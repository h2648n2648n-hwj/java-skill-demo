package com.example.demo.skill.execution;

import com.example.demo.skill.SkillDefinition;
import com.example.demo.skill.SkillTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class LlmSkillExecutionService implements SkillExecutionService {

    private static final Logger log = LoggerFactory.getLogger(LlmSkillExecutionService.class);
    private final ChatClient chatClient;
    private final SkillTools skillTools;

    public LlmSkillExecutionService(ChatClient.Builder builder, SkillTools skillTools) {
        this.chatClient = builder.build();
        this.skillTools = skillTools;
    }

    // 执行技能
    @Override
    public String execute(SkillExecutionRequest request) {
        SkillDefinition skill = request.skill();
        String prompt = """
                You are running a simplified Skills Runtime demo.

                Current skill name: %s

                Activation reason:
                %s

                Base directory for this skill:
                %s

                SKILL.md content:
                %s

                Runtime rules:
                - SKILL.md is already loaded into this prompt.
                - The current skill has already been activated for this task.
                - references/ and scripts/ are not preloaded.
                - If you need auxiliary files, call listSkillFiles or readSkillFile.
                - If you need a helper script, call runSkillScript.
                - - If the current skill provides a helper script for Word generation, prefer runSkillScript.
                - For docx-report-generator, use runSkillScript with scripts/docx_ops.py to create Word reports.
                - Use createDocxDocument only when the selected skill does not provide a suitable script.
                - If you need to persist intermediate JSON or text config, call writeTextFile.
                - Never assume a reference or script content before reading it.

                User task:
                %s
                """.formatted(
                skill.name(),
                request.activationReason(),
                skill.rootDir(),
                skill.skillMarkdown(),
                request.task()
        );

        log.info("调用大模型执行任务: {}", skill.name());

        String result = chatClient.prompt()
                .user(prompt)
                .tools(skillTools)     //调用  工具
                .call()
                .content();

        log.info("大模型输出内容:\n{}", result);

        return result;
    }
}
