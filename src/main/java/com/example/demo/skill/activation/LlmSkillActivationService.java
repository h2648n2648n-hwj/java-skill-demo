package com.example.demo.skill.activation;

import com.example.demo.skill.SkillDefinition;
import com.example.demo.skill.discovery.SkillDiscoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LlmSkillActivationService implements SkillActivationService {
    private static final int ROUTER_CANDIDATE_LIMIT = 5;
    private static final Logger log = LoggerFactory.getLogger(LlmSkillActivationService.class);

    private final ChatClient chatClient;
    private final SkillDiscoveryService discoveryService;
    private final ObjectMapper objectMapper;
    private final SkillCandidateSelector candidateSelector;

    public LlmSkillActivationService(ChatClient.Builder builder,
                                     SkillDiscoveryService discoveryService,
                                     ObjectMapper objectMapper,
                                     SkillCandidateSelector candidateSelector) {
        this.chatClient = builder.build();
        this.discoveryService = discoveryService;
        this.objectMapper = objectMapper;
        this.candidateSelector = candidateSelector;
    }

    @Override
    public SkillRouteResult activate(String task) {
        if (discoveryService.list().isEmpty()) {
            throw new IllegalStateException("No skills loaded.");
        }

        // 第一步：先用本地 BM25 召回器选出少量候选技能。

        List<SkillDefinition> candidates = candidateSelector.select(
                task,
                discoveryService.list(),
                ROUTER_CANDIDATE_LIMIT
        );
        if (candidates.isEmpty()) {
            candidates = List.copyOf(discoveryService.list());
        }

        // 第二步：只把候选技能目录发给大模型，让大模型做最终选择。

        //String skillCatalog = candidates.stream()
        //  全部交给LLM
        String skillCatalog =  discoveryService.list().stream()
                .map(skill -> """
                        - name: %s
                          description: %s
                          when_to_use: %s
                        """.formatted(skill.name(), skill.description(), skill.whenToUse()))
                .collect(Collectors.joining("\n"));

        String prompt = """
                You are a skill router for a simplified Skills Runtime.

                Your job is activation only. Choose exactly one skill for the user task.

                Rules:
                - Only choose from the provided skill catalog.
                - Use name, description, and when_to_use to decide.
                - Do not execute the skill.
                - Do not call tools.
                - Return JSON only, no markdown, no explanation, no analysis.
                - Your first character must be { and your last character must be }.
                - If no skill is suitable, return {"skillName":"","reason":"..."}.

                Skill catalog:
                %s

                User task:
                %s

                JSON schema:
                {"skillName":"skill-name","reason":"short reason"}
                """.formatted(skillCatalog, task);

        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        log.info("LLM skill activation result: {}", content);

        SkillRouteResult routeResult = parseRouteResult(content);
        if (routeResult.skillName() == null || routeResult.skillName().isBlank()) {
            return routeResult;
        }

        SkillDefinition skill = discoveryService.get(routeResult.skillName());
        return withDirectoryName(routeResult, skill);
    }

    private SkillRouteResult parseRouteResult(String content) {
        String json = extractJson(content);
        try {
            return objectMapper.readValue(json, SkillRouteResult.class);
        } catch (Exception e) {
            return fallbackRouteFromText(content)
                    .orElseThrow(() -> new IllegalStateException("Skill router returned invalid JSON: " + content, e));
        }
    }

    private String extractJson(String content) {
        if (content == null) {
            return "{}";
        }
        String trimmed = content.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }

    /**
     * 降级策略：如果大模型没有严格返回 JSON，但文本里提到了某个已知 skill 名称，
     * 就选择这个 skill，避免因为格式问题直接失败。
     */
    private Optional<SkillRouteResult> fallbackRouteFromText(String content) {
        if (content == null || content.isBlank()) {
            return Optional.empty();
        }

        for (SkillDefinition skill : discoveryService.list()) {
            if (content.contains(skill.name())) {
                return Optional.of(new SkillRouteResult(
                        skill.name(),
                        "Router did not return JSON; fallback selected skill mentioned in response.",
                        directoryName(skill)
                ));
            }
        }

        return Optional.empty();
    }

    private SkillRouteResult withDirectoryName(SkillRouteResult routeResult, SkillDefinition skill) {
        return new SkillRouteResult(
                routeResult.skillName(),
                routeResult.reason(),
                directoryName(skill)
        );
    }

    private String directoryName(SkillDefinition skill) {
        return skill.rootDir().getFileName().toString();
    }
}
