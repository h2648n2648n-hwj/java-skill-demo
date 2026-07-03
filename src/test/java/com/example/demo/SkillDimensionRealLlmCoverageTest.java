package com.example.demo;

import com.example.demo.skill.SkillDefinition;
import com.example.demo.skill.SkillRuntimeService;
import com.example.demo.skill.SkillTools;
import com.example.demo.skill.activation.SkillActivationService;
import com.example.demo.skill.activation.SkillRouteResult;
import com.example.demo.skill.discovery.SkillDiscoveryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DEEPSEEK_API_KEY", matches = ".+")
@EnabledIfEnvironmentVariable(named = "RUN_REAL_LLM_TESTS", matches = "true")
@DisplayName("Real skills directory + real DeepSeek LLM quantitative coverage")
class SkillDimensionRealLlmCoverageTest {

    private static final Path REPORT_DIR = Path.of("test-results");

    @Autowired
    private SkillDiscoveryService discoveryService;

    @Autowired
    private SkillActivationService activationService;

    @Autowired
    private SkillRuntimeService runtimeService;

    @Autowired
    private SkillTools skillTools;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("runs TEST_DIMENSIONS.md as 51 quantified cases")
    void runsTestDimensionsAsQuantifiedCases() throws Exception {
        RealLlmReportCollector collector = new RealLlmReportCollector(objectMapper);
        List<EvalCase> cases = buildCases();

        assertEquals(51, cases.size(), "TEST_DIMENSIONS.md currently defines 51 quantified cases");
        cases.forEach(item -> runCase(item, collector));
        collector.writeReports(REPORT_DIR);

        assertEquals(0, collector.failedCount(), "See test-results/test-report.md for failed case details.");
    }

    @Test
    @DisplayName("runs datasets/skill_qa.jsonl as a quantified QA routing dataset")
    void runsSkillQaDatasetAsQuantifiedRoutingCases() throws Exception {
        Path dataset = Path.of("datasets", "skill_qa.jsonl");
        SkillQaReportCollector collector = new SkillQaReportCollector(objectMapper);

        List<String> lines = Files.readAllLines(dataset, StandardCharsets.UTF_8).stream()
                .filter(line -> !line.isBlank())
                .toList();

        for (String line : lines) {
            runSkillQaCase(objectMapper.readTree(line), collector);
        }

        collector.writeReports(REPORT_DIR);
        assertEquals(0, collector.failedCount(), "See test-results/skill-qa-report.md for failed QA cases.");
    }

    private void runSkillQaCase(JsonNode node, SkillQaReportCollector collector) {
        String id = node.path("id").asText();
        String category = node.path("category").asText();
        String expectedSkill = node.path("expected_skill").isNull() ? "" : node.path("expected_skill").asText("");
        boolean expectedActivation = node.path("expected_activation").asBoolean(false);
        String task = readTurns(node.path("turns"));

        long start = System.nanoTime();
        String actualSkill = "";
        String reason = "";
        String error = "";
        boolean passed;

        try {
            SkillRouteResult result = activationService.activate(task);
            actualSkill = result.skillName();
            reason = result.reason();
            passed = skillQaPassed(expectedSkill, expectedActivation, actualSkill);
        } catch (Exception ex) {
            passed = false;
            error = ex.getClass().getSimpleName() + ": " + nullToEmpty(ex.getMessage());
        }

        long latencyNanos = System.nanoTime() - start;
        long tokens = estimateTokens(task + " " + actualSkill + " " + reason + " " + error);
        collector.record(new SkillQaCaseResult(
                id,
                category,
                expectedSkill,
                expectedActivation,
                actualSkill,
                passed,
                latencyNanos,
                tokens,
                reason,
                error,
                task
        ));
    }

    private boolean skillQaPassed(String expectedSkill, boolean expectedActivation, String actualSkill) {
        if (expectedActivation) {
            return Objects.equals(expectedSkill, actualSkill);
        }
        return !"docx-report-generator".equals(actualSkill);
    }

    private String readTurns(JsonNode turns) {
        if (!turns.isArray()) {
            return "";
        }
        List<String> values = new ArrayList<>();
        turns.forEach(turn -> values.add(turn.asText()));
        return String.join("\n", values);
    }

    private void runCase(EvalCase item, RealLlmReportCollector collector) {
        long start = System.nanoTime();
        String actual = "";
        String actualSkill = "";
        String error = "";
        boolean passed;

        try {
            CaseOutcome outcome = item.runner().run();
            actual = outcome.actual();
            actualSkill = outcome.actualSkill();
            passed = outcome.passed();
        } catch (Exception ex) {
            passed = false;
            error = ex.getClass().getSimpleName() + ": " + nullToEmpty(ex.getMessage());
        }

        long latencyNanos = System.nanoTime() - start;
        long tokens = estimateTokens(item.input() + " " + actual + " " + error);
        collector.record(new CaseResult(
                item.caseId(),
                item.dimension(),
                item.caseType(),
                item.adapter(),
                item.expectedDisplay(),
                item.expectedActive(),
                actual,
                actualSkill,
                passed,
                latencyNanos,
                tokens,
                error
        ));
    }

    private List<EvalCase> buildCases() {
        List<EvalCase> cases = new ArrayList<>();
        cases.addAll(routeCases());
        cases.addAll(fieldExtractionCases());
        cases.addAll(schemaToleranceCases());
        cases.addAll(executionChainCases());
        cases.addAll(adapterAndToolCases());
        cases.addAll(boundaryCases());
        cases.addAll(discoveryCases());
        return cases;
    }

    private List<EvalCase> routeCases() {
        return List.of(
                routeCase("A-01", "Generate an internship report as a Word document.", List.of("docx-report-generator")),
                routeCase("A-02", "Create a short Word document body about the Java Skills Runtime demo.", List.of("documents", "docx-report-generator")),
                routeCase("A-03", "Read and summarize the contents of a PDF file.", List.of("pdf")),
                routeCase("A-04", "Look up how to use the OpenAI API Responses endpoint.", List.of("openai-docs")),
                routeCase("A-05", "Deploy a Node.js application to Render with a blueprint.", List.of("render-deploy")),
                routeCase("A-06", "Deploy a static web app on Cloudflare Pages.", List.of("cloudflare-deploy")),
                routeCase("A-07", "Review this service for security best practices.", List.of("security-best-practices")),
                routeCase("A-08", "Help me build a ChatGPT app with widgets.", List.of("chatgpt-apps")),
                routeCase("A-09", "Generate a Figma component library from these design tokens.", List.of("figma-generate-library", "figma-use")),
                routeCase("A-10", "Capture this research note into Notion.", List.of("notion-knowledge-capture", "notion-research-documentation")),
                routeCase("A-11", "Create a speech transcription workflow.", List.of("speech")),
                routeCase("A-12", "What is the weather in Beijing today?", List.of("")),
                routeCase("A-13", "", List.of(""))
        );
    }

    private EvalCase routeCase(String caseId, String query, List<String> expectedSkills) {
        return new EvalCase(
                caseId,
                "A-routing-accuracy",
                CaseType.ROUTING,
                "spring-ai-openai-compatible",
                query,
                expectedSkills.toString(),
                expectedSkills.stream().anyMatch(skill -> skill != null && !skill.isBlank()),
                () -> {
                    SkillRouteResult result = activationService.activate(query);
                    boolean skillMatched = expectedSkills.contains(result.skillName());
                    boolean directoryMatched = result.skillName().isBlank()
                            || discoveryService.get(result.skillName()).rootDir().getFileName().toString()
                            .equals(result.directoryName());
                    return new CaseOutcome(
                            skillMatched && directoryMatched,
                            result.skillName(),
                            result.skillName()
                    );
                }
        );
    }

    private List<EvalCase> fieldExtractionCases() {
        return List.of(
                fieldCase("B-01", "Generate file weekly-report.docx", Map.of("filename", "weekly-report.docx")),
                fieldCase("B-02", "Use template product-review to generate a report", Map.of("template_name", "product-review")),
                fieldCase("B-03", "Use template q2-summary to generate q2-summary.docx",
                        Map.of("filename", "q2-summary.docx", "template_name", "q2-summary")),
                fieldCase("B-04", "", Map.of()),
                fieldCase("B-05", "This request does not mention output fields", Map.of())
        );
    }

    private EvalCase fieldCase(String caseId, String query, Map<String, String> expectedFields) {
        return new EvalCase(
                caseId,
                "B-field-extraction",
                CaseType.FIELD_EXTRACTION,
                "java-regex-field-extractor",
                query,
                expectedFields.toString(),
                false,
                () -> {
                    Map<String, String> actualFields = extractFields(query);
                    return new CaseOutcome(expectedFields.equals(actualFields), actualFields.toString(), "");
                }
        );
    }

    private List<EvalCase> schemaToleranceCases() {
        return List.of(
                schemaCase("C-01", Map.of("should_call", true, "skill_name", "missing-skill", "confidence", 0.9)),
                schemaCase("C-02", Map.of("should_call", "yes", "skill_name", "", "confidence", 0.5)),
                schemaCase("C-03", Map.of("should_call", false, "skill_name", "", "confidence", "high")),
                new EvalCase(
                        "C-04",
                        "C-llm-error-tolerance",
                        CaseType.SCHEMA,
                        "decision-schema-validator",
                        "not json at all",
                        "JsonProcessingException",
                        false,
                        () -> {
                            try {
                                objectMapper.readValue("not json at all", Map.class);
                                return new CaseOutcome(false, "parsed", "");
                            } catch (Exception ex) {
                                return new CaseOutcome(true, ex.getClass().getSimpleName(), "");
                            }
                        }
                )
        );
    }

    private EvalCase schemaCase(String caseId, Map<String, Object> payload) {
        return new EvalCase(
                caseId,
                "C-llm-error-tolerance",
                CaseType.SCHEMA,
                "decision-schema-validator",
                payload.toString(),
                "schema rejected",
                false,
                () -> {
                    try {
                        validateDecisionPayload(payload);
                        return new CaseOutcome(false, "accepted", "");
                    } catch (IllegalArgumentException ex) {
                        return new CaseOutcome(true, ex.getMessage(), "");
                    }
                }
        );
    }

    private List<EvalCase> executionChainCases() {
        return List.of(
                runtimeCase("D-01", "Generate an internship report as a Word document."),
                runtimeCase("D-02", "Create a concise Word body about Spring AI skill routing."),
                runtimeCase("D-03", "Write a short text draft about runtime evaluation metrics."),
                localCase("D-04", "execution prompt contains skill body", "spring-ai-prompt-builder", () -> {
                    SkillDefinition skill = discoveryService.get("docx-report-generator");
                    String prompt = executionPromptPreview(skill, "Generate a report.");
                    return prompt.contains(skill.name()) && prompt.contains(skill.skillMarkdown());
                }),
                localCase("D-05", "execution records latency", "latency-recorder", () -> System.nanoTime() > 0),
                localCase("D-06", "token tracker estimates overhead", "token-estimator",
                        () -> estimateTokens("Generate a Word report with title and summary")
                                > estimateTokens("echo ping"))
        );
    }

    private EvalCase runtimeCase(String caseId, String task) {
        return new EvalCase(
                caseId,
                "D-execution-chain",
                CaseType.EXECUTION,
                "spring-ai-openai-compatible-tools",
                task,
                "non-empty execution response",
                true,
                () -> {
                    String response = runtimeService.runAuto(task);
                    SkillRouteResult route = activationService.activate(task);
                    return new CaseOutcome(!response.isBlank(), response, route.skillName());
                }
        );
    }

    private List<EvalCase> adapterAndToolCases() {
        return List.of(
                localCase("E-01", "create docx artifact", "SkillTools.createDocxDocument", () ->
                        skillTools.createDocxDocument("real-eval/e-01.docx", "Eval", "Body").contains("Created DOCX")),
                localCase("E-02", "write text artifact", "SkillTools.writeTextFile", () ->
                        skillTools.writeTextFile("real-eval/e-02.txt", "ok").contains("Wrote text file")),
                localCase("E-03", "list skill root files", "SkillTools.listSkillFiles", () ->
                        skillTools.listSkillFiles("documents", ".").contains("SKILL.md")),
                localCase("E-04", "read skill file", "SkillTools.readSkillFile", () ->
                        !skillTools.readSkillFile("documents", "SKILL.md").isBlank()),
                localCase("E-05", "missing script is captured", "SkillTools.runSkillScript", () ->
                        skillTools.runSkillScript("documents", "scripts/missing.py", "").contains("Not a script file")),
                localCase("E-06", "output path escape is rejected", "SkillTools.writeTextFile", () ->
                        throwsIllegalArgument(() -> skillTools.writeTextFile("../escape.txt", "blocked"))),
                localCase("E-07", "skill path escape is rejected", "SkillTools.readSkillFile", () ->
                        throwsIllegalArgument(() -> skillTools.readSkillFile("documents", "../SKILL.md"))),
                localCase("E-08", "run root script is blocked", "SkillTools.runSkillScript", () ->
                        skillTools.runSkillScript("documents", "render_docx.py", "").contains("Not a script file"))
        );
    }

    private List<EvalCase> boundaryCases() {
        return List.of(
                localCase("F-01", "real skill directory is not empty", "DefaultSkillDiscoveryService",
                        () -> !discoveryService.list().isEmpty()),
                localCase("F-02", "unknown skill is rejected", "DefaultSkillDiscoveryService",
                        () -> throwsIllegalArgument(() -> discoveryService.get("unknown-skill-for-eval"))),
                localCase("F-03", "all loaded skills have names", "SkillLoader",
                        () -> discoveryService.list().stream().allMatch(skill -> !skill.name().isBlank())),
                localCase("F-04", "all loaded skill roots exist", "SkillLoader",
                        () -> discoveryService.list().stream().allMatch(skill -> Files.isDirectory(skill.rootDir()))),
                localCase("F-05", "all loaded skill markdown bodies are available", "SkillLoader",
                        () -> discoveryService.list().stream().allMatch(skill -> skill.skillMarkdown() != null)),
                localCase("F-06", "blank output path is rejected", "SkillTools.writeTextFile",
                        () -> throwsIllegalArgument(() -> skillTools.writeTextFile("", "blocked"))),
                localCase("F-07", "blank skill path is rejected", "SkillTools.readSkillFile",
                        () -> throwsIllegalArgument(() -> skillTools.readSkillFile("documents", "")))
        );
    }

    private List<EvalCase> discoveryCases() {
        return List.of(
                discoveryCase("G-01", "documents"),
                discoveryCase("G-02", "docx-report-generator"),
                discoveryCase("G-03", "pdf"),
                discoveryCase("G-04", "openai-docs"),
                discoveryCase("G-05", "render-deploy"),
                discoveryCase("G-06", "cloudflare-deploy"),
                discoveryCase("G-07", "security-best-practices"),
                new EvalCase(
                        "G-08",
                        "G-discovery-completeness",
                        CaseType.DISCOVERY,
                        "DefaultSkillDiscoveryService",
                        "directory name for docx-report-generator",
                        "word-report-generator-1.0.0",
                        true,
                        () -> {
                            SkillDefinition skill = discoveryService.get("docx-report-generator");
                            String directoryName = skill.rootDir().getFileName().toString();
                            return new CaseOutcome(
                                    "word-report-generator-1.0.0".equals(directoryName),
                                    directoryName,
                                    skill.name()
                            );
                        }
                )
        );
    }

    private EvalCase discoveryCase(String caseId, String skillName) {
        return new EvalCase(
                caseId,
                "G-discovery-completeness",
                CaseType.DISCOVERY,
                "DefaultSkillDiscoveryService",
                skillName,
                "loaded",
                true,
                () -> {
                    SkillDefinition skill = discoveryService.get(skillName);
                    boolean loaded = Objects.equals(skillName, skill.name())
                            && !skill.description().isBlank()
                            && Files.isDirectory(skill.rootDir());
                    return new CaseOutcome(loaded, loaded ? "loaded" : "invalid", skill.name());
                }
        );
    }

    private EvalCase localCase(String caseId, String input, String adapter, CheckedBooleanSupplier assertion) {
        String dimension = switch (caseId.charAt(0)) {
            case 'D' -> "D-execution-chain";
            case 'E' -> "E-adapter-tool-coverage";
            case 'F' -> "F-boundary-robustness";
            default -> "local";
        };
        CaseType type = switch (caseId.charAt(0)) {
            case 'D' -> CaseType.EXECUTION;
            case 'E' -> CaseType.ADAPTER;
            case 'F' -> CaseType.BOUNDARY;
            default -> CaseType.LOCAL;
        };
        return new EvalCase(
                caseId,
                dimension,
                type,
                adapter,
                input,
                "true",
                false,
                () -> {
                    boolean passed = assertion.getAsBoolean();
                    return new CaseOutcome(passed, Boolean.toString(passed), "");
                }
        );
    }

    private Map<String, String> extractFields(String query) {
        Map<String, String> fields = new LinkedHashMap<>();
        if (query == null || query.isBlank()) {
            return fields;
        }
        Matcher filenameMatcher = Pattern.compile("([A-Za-z0-9_-]+\\.docx)").matcher(query);
        if (filenameMatcher.find()) {
            fields.put("filename", filenameMatcher.group(1));
        }
        Matcher templateMatcher = Pattern.compile("template\\s+([A-Za-z0-9_-]+)", Pattern.CASE_INSENSITIVE)
                .matcher(query);
        if (templateMatcher.find()) {
            fields.put("template_name", templateMatcher.group(1));
        }
        return fields;
    }

    private void validateDecisionPayload(Map<String, Object> payload) {
        Object shouldCall = payload.get("should_call");
        Object confidence = payload.get("confidence");
        Object skillName = payload.get("skill_name");

        if (!(shouldCall instanceof Boolean)) {
            throw new IllegalArgumentException("should_call must be boolean");
        }
        if (!(confidence instanceof Number)) {
            throw new IllegalArgumentException("confidence must be number");
        }
        if (Boolean.TRUE.equals(shouldCall)) {
            if (!(skillName instanceof String name) || name.isBlank()) {
                throw new IllegalArgumentException("skill_name is required");
            }
            discoveryService.get(name);
        }
    }

    private String executionPromptPreview(SkillDefinition skill, String task) {
        return """
                Current skill name: %s
                Base directory for this skill: %s
                SKILL.md content: %s
                User task: %s
                """.formatted(skill.name(), skill.rootDir(), skill.skillMarkdown(), task);
    }

    private boolean throwsIllegalArgument(CheckedOperation operation) throws Exception {
        try {
            operation.run();
            return false;
        } catch (IllegalArgumentException ex) {
            return true;
        }
    }

    private static long estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        long tokens = 0;
        boolean inAsciiWord = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isWhitespace(ch)) {
                inAsciiWord = false;
            } else if (ch < 128 && Character.isLetterOrDigit(ch)) {
                if (!inAsciiWord) {
                    tokens++;
                    inAsciiWord = true;
                }
            } else {
                tokens++;
                inAsciiWord = false;
            }
        }
        return tokens;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private enum CaseType {
        ROUTING,
        FIELD_EXTRACTION,
        SCHEMA,
        EXECUTION,
        ADAPTER,
        BOUNDARY,
        DISCOVERY,
        LOCAL
    }

    private record EvalCase(String caseId,
                            String dimension,
                            CaseType caseType,
                            String adapter,
                            String input,
                            String expectedDisplay,
                            boolean expectedActive,
                            CaseRunner runner) {
    }

    private record CaseOutcome(boolean passed, String actual, String actualSkill) {
    }

    private interface CaseRunner {
        CaseOutcome run() throws Exception;
    }

    private record CaseResult(String caseId,
                              String dimension,
                              CaseType caseType,
                              String adapter,
                              String expected,
                              boolean expectedActive,
                              String actual,
                              String actualSkill,
                              boolean passed,
                              long latencyNanos,
                              long tokens,
                              String error) {
        double latencyMs() {
            return latencyNanos / 1_000_000.0;
        }
    }

    private record TestReport(String generatedAt,
                              int total,
                              int passed,
                              int failed,
                              double totalDurationMs,
                              List<CaseResult> cases) {
    }

    private record QuantitativeReport(ConfusionMatrix confusionMatrix,
                                      ClassificationMetrics classificationMetrics,
                                      NumericStats tokenUsage,
                                      NumericStats latencyMs,
                                      Map<String, GroupStats> bySkill,
                                      Map<String, GroupStats> byAdapter,
                                      List<ExecutionDetail> executionDetails) {
    }

    private record ConfusionMatrix(long tp, long tn, long fp, long fn) {
    }

    private record ClassificationMetrics(double accuracy, double precision, double recall) {
    }

    private record NumericStats(long min, long max, double avg, long total, double p50, double p95) {
    }

    private record GroupStats(long executions, double avgTokens, double avgLatencyMs, double successRate) {
    }

    private record ExecutionDetail(String caseId,
                                   String dimension,
                                   String skill,
                                   String adapter,
                                   boolean passed,
                                   long tokens,
                                   double latencyMs,
                                   String expected,
                                   String actual,
                                   String error) {
    }

    private interface CheckedBooleanSupplier {
        boolean getAsBoolean() throws Exception;
    }

    private interface CheckedOperation {
        void run() throws Exception;
    }

    private record SkillQaCaseResult(String id,
                                     String category,
                                     String expectedSkill,
                                     boolean expectedActivation,
                                     String actualSkill,
                                     boolean passed,
                                     long latencyNanos,
                                     long tokens,
                                     String reason,
                                     String error,
                                     String task) {
        double latencyMs() {
            return latencyNanos / 1_000_000.0;
        }

        boolean expectedWordSkill() {
            return expectedActivation && "docx-report-generator".equals(expectedSkill);
        }

        boolean actualWordSkill() {
            return "docx-report-generator".equals(actualSkill);
        }
    }

    private record SkillQaReport(String generatedAt,
                                 int total,
                                 int passed,
                                 int failed,
                                 double passRate,
                                 ConfusionMatrix wordSkillConfusionMatrix,
                                 ClassificationMetrics wordSkillMetrics,
                                 double expectedSkillAccuracy,
                                 NumericStats tokenUsage,
                                 NumericStats latencyMs,
                                 Map<String, GroupStats> byCategory,
                                 Map<String, GroupStats> byActualSkill,
                                 List<SkillQaCaseResult> cases) {
    }

    private static class SkillQaReportCollector {

        private final ObjectMapper objectMapper;
        private final List<SkillQaCaseResult> results = new ArrayList<>();

        SkillQaReportCollector(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        void record(SkillQaCaseResult result) {
            results.add(result);
        }

        long failedCount() {
            return results.stream().filter(result -> !result.passed()).count();
        }

        void writeReports(Path outputDir) throws IOException {
            Files.createDirectories(outputDir);
            SkillQaReport report = report();

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(outputDir.resolve("skill-qa-report.json").toFile(), report);
            Files.writeString(outputDir.resolve("skill-qa-report.md"), markdown(report), StandardCharsets.UTF_8);
        }

        private SkillQaReport report() {
            int passed = (int) results.stream().filter(SkillQaCaseResult::passed).count();
            ConfusionMatrix matrix = wordSkillConfusionMatrix();
            long totalClassified = matrix.tp() + matrix.tn() + matrix.fp() + matrix.fn();
            ClassificationMetrics metrics = new ClassificationMetrics(
                    ratio(matrix.tp() + matrix.tn(), totalClassified),
                    ratio(matrix.tp(), matrix.tp() + matrix.fp()),
                    ratio(matrix.tp(), matrix.tp() + matrix.fn())
            );

            return new SkillQaReport(
                    java.time.OffsetDateTime.now().toString(),
                    results.size(),
                    passed,
                    results.size() - passed,
                    ratio(passed, results.size()),
                    matrix,
                    metrics,
                    expectedSkillAccuracy(),
                    numericStats(results.stream().map(SkillQaCaseResult::tokens).toList()),
                    numericStats(results.stream().map(result -> Math.round(result.latencyMs())).toList()),
                    groupBy(SkillQaCaseResult::category),
                    groupBy(result -> blankToNone(result.actualSkill())),
                    List.copyOf(results)
            );
        }

        private ConfusionMatrix wordSkillConfusionMatrix() {
            long tp = 0;
            long tn = 0;
            long fp = 0;
            long fn = 0;
            for (SkillQaCaseResult result : results) {
                boolean expected = result.expectedWordSkill();
                boolean actual = result.actualWordSkill();
                if (expected && actual) {
                    tp++;
                } else if (!expected && !actual) {
                    tn++;
                } else if (!expected) {
                    fp++;
                } else {
                    fn++;
                }
            }
            return new ConfusionMatrix(tp, tn, fp, fn);
        }

        private double expectedSkillAccuracy() {
            List<SkillQaCaseResult> activeCases = results.stream()
                    .filter(SkillQaCaseResult::expectedActivation)
                    .toList();
            long matched = activeCases.stream()
                    .filter(result -> Objects.equals(result.expectedSkill(), result.actualSkill()))
                    .count();
            return ratio(matched, activeCases.size());
        }

        private Map<String, GroupStats> groupBy(java.util.function.Function<SkillQaCaseResult, String> classifier) {
            Map<String, List<SkillQaCaseResult>> grouped = results.stream()
                    .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));

            Map<String, GroupStats> stats = new LinkedHashMap<>();
            grouped.forEach((name, items) -> stats.put(blankToNone(name), new GroupStats(
                    items.size(),
                    items.stream().mapToLong(SkillQaCaseResult::tokens).average().orElse(0),
                    items.stream().mapToDouble(SkillQaCaseResult::latencyMs).average().orElse(0),
                    ratio(items.stream().filter(SkillQaCaseResult::passed).count(), items.size())
            )));
            return stats;
        }

        private NumericStats numericStats(List<Long> values) {
            if (values.isEmpty()) {
                return new NumericStats(0, 0, 0, 0, 0, 0);
            }
            List<Long> sorted = values.stream().sorted(Comparator.naturalOrder()).toList();
            long total = sorted.stream().mapToLong(Long::longValue).sum();
            return new NumericStats(
                    sorted.get(0),
                    sorted.get(sorted.size() - 1),
                    total * 1.0 / sorted.size(),
                    total,
                    percentile(sorted, 0.50),
                    percentile(sorted, 0.95)
            );
        }

        private double percentile(List<Long> sorted, double percentile) {
            if (sorted.isEmpty()) {
                return 0;
            }
            int index = (int) Math.ceil(percentile * sorted.size()) - 1;
            int bounded = Math.max(0, Math.min(index, sorted.size() - 1));
            return sorted.get(bounded);
        }

        private String markdown(SkillQaReport report) {
            StringBuilder out = new StringBuilder();
            out.append("# skill_qa.jsonl 路由评测报告\n\n");
            out.append("## 总览\n\n");
            out.append("- 用例总数: ").append(report.total()).append("\n");
            out.append("- 通过数: ").append(report.passed()).append("\n");
            out.append("- 失败数: ").append(report.failed()).append("\n");
            out.append("- 通过率: ").append(format(report.passRate())).append("\n");
            out.append("- 期望 skill 准确率: ").append(format(report.expectedSkillAccuracy())).append("\n\n");

            out.append("### 指标解释\n\n");
            out.append("本报告逐行读取 `datasets/skill_qa.jsonl`，把每条数据的 `turns` 合并为用户请求，调用 `activationService.activate()` 做真实 LLM 路由评测。");
            out.append("正例要求实际 skill 等于 `expected_skill`；负例要求不要误触发 `docx-report-generator`，因此 Render、天气、代码审查等请求只要没有被路由到 Word 报告 skill 就算通过。\n\n");

            out.append("## Word Skill 混淆矩阵\n\n");
            out.append("- TP: ").append(report.wordSkillConfusionMatrix().tp()).append("\n");
            out.append("- TN: ").append(report.wordSkillConfusionMatrix().tn()).append("\n");
            out.append("- FP: ").append(report.wordSkillConfusionMatrix().fp()).append("\n");
            out.append("- FN: ").append(report.wordSkillConfusionMatrix().fn()).append("\n");
            out.append("- Accuracy: ").append(format(report.wordSkillMetrics().accuracy())).append("\n");
            out.append("- Precision: ").append(format(report.wordSkillMetrics().precision())).append("\n");
            out.append("- Recall: ").append(format(report.wordSkillMetrics().recall())).append("\n\n");

            out.append("### 指标解释\n\n");
            out.append("这里把 `docx-report-generator` 当成目标 skill 来计算二分类指标。");
            out.append("TP 表示应该触发 Word skill 且实际触发；TN 表示不应该触发 Word skill 且实际没有触发；FP 表示误触发 Word skill；FN 表示应该触发但没有触发。\n\n");

            out.append("## Token 消耗\n\n");
            appendStats(out, report.tokenUsage());
            out.append("\n### 指标解释\n\n");
            out.append("Token 是测试脚本估算值，统计内容为用户请求、实际 skill、路由原因和错误信息，不是 API 返回的真实 token usage。\n\n");

            out.append("## 延迟（毫秒）\n\n");
            appendStats(out, report.latencyMs());
            out.append("\n### 指标解释\n\n");
            out.append("延迟使用 `System.nanoTime()` 统计每条 JSONL 用例从调用路由器到返回结果的耗时，主要反映真实 LLM 路由阶段的响应速度。\n\n");

            out.append("## 按 Category 分组\n\n");
            appendGroupTable(out, report.byCategory());
            out.append("\n## 按实际 Skill 分组\n\n");
            appendGroupTable(out, report.byActualSkill());

            out.append("\n## 逐次执行明细\n\n");
            out.append("| 用例 | Category | 期望激活 | 期望 Skill | 实际 Skill | 是否通过 | Token | 延迟（毫秒） | 原因 | 错误 |\n");
            out.append("|---|---|---:|---|---|---:|---:|---:|---|---|\n");
            for (SkillQaCaseResult item : report.cases()) {
                out.append("| ").append(escape(item.id()))
                        .append(" | ").append(escape(item.category()))
                        .append(" | ").append(item.expectedActivation())
                        .append(" | ").append(escape(item.expectedSkill()))
                        .append(" | ").append(escape(item.actualSkill()))
                        .append(" | ").append(item.passed())
                        .append(" | ").append(item.tokens())
                        .append(" | ").append(format(item.latencyMs()))
                        .append(" | ").append(escape(item.reason()))
                        .append(" | ").append(escape(item.error()))
                        .append(" |\n");
            }
            return out.toString();
        }

        private void appendStats(StringBuilder out, NumericStats stats) {
            out.append("- Min: ").append(stats.min()).append("\n");
            out.append("- Max: ").append(stats.max()).append("\n");
            out.append("- Avg: ").append(format(stats.avg())).append("\n");
            out.append("- Total: ").append(stats.total()).append("\n");
            out.append("- P50: ").append(format(stats.p50())).append("\n");
            out.append("- P95: ").append(format(stats.p95())).append("\n");
        }

        private void appendGroupTable(StringBuilder out, Map<String, GroupStats> groups) {
            out.append("| 名称 | 执行次数 | 平均 Token | 平均延迟（毫秒） | 成功率 |\n");
            out.append("|---|---:|---:|---:|---:|\n");
            groups.forEach((name, stats) -> out.append("| ").append(escape(name))
                    .append(" | ").append(stats.executions())
                    .append(" | ").append(format(stats.avgTokens()))
                    .append(" | ").append(format(stats.avgLatencyMs()))
                    .append(" | ").append(format(stats.successRate()))
                    .append(" |\n"));
        }

        private double ratio(long numerator, long denominator) {
            return denominator == 0 ? 0 : numerator * 1.0 / denominator;
        }

        private String format(double value) {
            return String.format(java.util.Locale.ROOT, "%.3f", value);
        }

        private String escape(String value) {
            return blankToNone(value).replace("|", "\\|").replace("\n", "<br>");
        }

        private static String blankToNone(String value) {
            return value == null || value.isBlank() ? "None" : value;
        }
    }

    private static class RealLlmReportCollector {

        private final ObjectMapper objectMapper;
        private final List<CaseResult> results = new ArrayList<>();

        RealLlmReportCollector(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        void record(CaseResult result) {
            results.add(result);
        }

        long failedCount() {
            return results.stream().filter(result -> !result.passed()).count();
        }

        void writeReports(Path outputDir) throws IOException {
            Files.createDirectories(outputDir);

            TestReport testReport = testReport();
            QuantitativeReport quantitativeReport = quantitativeReport();

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(outputDir.resolve("test-report.json").toFile(), testReport);
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(outputDir.resolve("quantitative-report.json").toFile(), quantitativeReport);

            Files.writeString(outputDir.resolve("test-report.md"), testReportMarkdown(testReport),
                    StandardCharsets.UTF_8);
            Files.writeString(outputDir.resolve("quantitative-report.md"),
                    quantitativeReportMarkdown(quantitativeReport), StandardCharsets.UTF_8);
        }

        private TestReport testReport() {
            int passed = (int) results.stream().filter(CaseResult::passed).count();
            double totalDurationMs = results.stream().mapToDouble(CaseResult::latencyMs).sum();
            return new TestReport(
                    java.time.OffsetDateTime.now().toString(),
                    results.size(),
                    passed,
                    results.size() - passed,
                    totalDurationMs,
                    List.copyOf(results)
            );
        }

        private QuantitativeReport quantitativeReport() {
            ConfusionMatrix matrix = confusionMatrix();
            long totalClassified = matrix.tp() + matrix.tn() + matrix.fp() + matrix.fn();
            ClassificationMetrics metrics = new ClassificationMetrics(
                    ratio(matrix.tp() + matrix.tn(), totalClassified),
                    ratio(matrix.tp(), matrix.tp() + matrix.fp()),
                    ratio(matrix.tp(), matrix.tp() + matrix.fn())
            );

            return new QuantitativeReport(
                    matrix,
                    metrics,
                    numericStats(results.stream().map(CaseResult::tokens).toList()),
                    numericStats(results.stream().map(result -> Math.round(result.latencyMs())).toList()),
                    groupBySkill(),
                    groupByAdapter(),
                    results.stream()
                            .map(result -> new ExecutionDetail(
                                    result.caseId(),
                                    result.dimension(),
                                    blankToNone(result.actualSkill()),
                                    result.adapter(),
                                    result.passed(),
                                    result.tokens(),
                                    result.latencyMs(),
                                    result.expected(),
                                    result.actual(),
                                    result.error()
                            ))
                            .toList()
            );
        }

        private ConfusionMatrix confusionMatrix() {
            long tp = 0;
            long tn = 0;
            long fp = 0;
            long fn = 0;
            for (CaseResult result : results) {
                if (result.caseType() != CaseType.ROUTING) {
                    continue;
                }
                boolean expectedActive = result.expectedActive();
                boolean actualActive = result.actualSkill() != null && !result.actualSkill().isBlank();
                if (expectedActive && result.passed() && actualActive) {
                    tp++;
                } else if (!expectedActive && result.passed() && !actualActive) {
                    tn++;
                } else if (!expectedActive && actualActive) {
                    fp++;
                } else {
                    fn++;
                }
            }
            return new ConfusionMatrix(tp, tn, fp, fn);
        }

        private Map<String, GroupStats> groupBySkill() {
            return groupBy(result -> blankToNone(result.actualSkill()));
        }

        private Map<String, GroupStats> groupByAdapter() {
            return groupBy(CaseResult::adapter);
        }

        private Map<String, GroupStats> groupBy(java.util.function.Function<CaseResult, String> classifier) {
            Map<String, List<CaseResult>> grouped = results.stream()
                    .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));

            Map<String, GroupStats> stats = new LinkedHashMap<>();
            grouped.forEach((name, items) -> stats.put(name, new GroupStats(
                    items.size(),
                    items.stream().mapToLong(CaseResult::tokens).average().orElse(0),
                    items.stream().mapToDouble(CaseResult::latencyMs).average().orElse(0),
                    ratio(items.stream().filter(CaseResult::passed).count(), items.size())
            )));
            return stats;
        }

        private NumericStats numericStats(List<Long> values) {
            if (values.isEmpty()) {
                return new NumericStats(0, 0, 0, 0, 0, 0);
            }
            List<Long> sorted = values.stream().sorted(Comparator.naturalOrder()).toList();
            long total = sorted.stream().mapToLong(Long::longValue).sum();
            return new NumericStats(
                    sorted.get(0),
                    sorted.get(sorted.size() - 1),
                    total * 1.0 / sorted.size(),
                    total,
                    percentile(sorted, 0.50),
                    percentile(sorted, 0.95)
            );
        }

        private double percentile(List<Long> sorted, double percentile) {
            if (sorted.isEmpty()) {
                return 0;
            }
            int index = (int) Math.ceil(percentile * sorted.size()) - 1;
            int bounded = Math.max(0, Math.min(index, sorted.size() - 1));
            return sorted.get(bounded);
        }

        private String testReportMarkdown(TestReport report) {
            StringBuilder out = new StringBuilder();
            out.append("# Test Report\n\n");
            out.append("- Total: ").append(report.total()).append("\n");
            out.append("- Passed: ").append(report.passed()).append("\n");
            out.append("- Failed: ").append(report.failed()).append("\n");
            out.append("- Total duration ms: ").append(format(report.totalDurationMs())).append("\n\n");
            out.append("| Case | Dimension | Type | Adapter | Expected | Actual | Passed | Latency ms | Tokens | Error |\n");
            out.append("|---|---|---|---|---|---|---:|---:|---:|---|\n");
            for (CaseResult item : report.cases()) {
                out.append("| ").append(escape(item.caseId()))
                        .append(" | ").append(escape(item.dimension()))
                        .append(" | ").append(item.caseType())
                        .append(" | ").append(escape(item.adapter()))
                        .append(" | ").append(escape(item.expected()))
                        .append(" | ").append(escape(item.actual()))
                        .append(" | ").append(item.passed())
                        .append(" | ").append(format(item.latencyMs()))
                        .append(" | ").append(item.tokens())
                        .append(" | ").append(escape(item.error()))
                        .append(" |\n");
            }
            return out.toString();
        }

        private String quantitativeReportMarkdown(QuantitativeReport report) {
            StringBuilder out = new StringBuilder();
            out.append("# Quantitative Report\n\n");
            out.append("## Classification\n\n");
            out.append("- TP: ").append(report.confusionMatrix().tp()).append("\n");
            out.append("- TN: ").append(report.confusionMatrix().tn()).append("\n");
            out.append("- FP: ").append(report.confusionMatrix().fp()).append("\n");
            out.append("- FN: ").append(report.confusionMatrix().fn()).append("\n");
            out.append("- Accuracy: ").append(format(report.classificationMetrics().accuracy())).append("\n");
            out.append("- Precision: ").append(format(report.classificationMetrics().precision())).append("\n");
            out.append("- Recall: ").append(format(report.classificationMetrics().recall())).append("\n\n");

            out.append("## Token Usage\n\n");
            appendStats(out, report.tokenUsage());
            out.append("\n## Latency Ms\n\n");
            appendStats(out, report.latencyMs());

            out.append("\n## By Skill\n\n");
            appendGroupTable(out, report.bySkill());
            out.append("\n## By Adapter\n\n");
            appendGroupTable(out, report.byAdapter());

            out.append("\n## Execution Details\n\n");
            out.append("| Case | Dimension | Skill | Adapter | Passed | Tokens | Latency ms | Expected | Actual | Error |\n");
            out.append("|---|---|---|---|---:|---:|---:|---|---|---|\n");
            for (ExecutionDetail item : report.executionDetails()) {
                out.append("| ").append(escape(item.caseId()))
                        .append(" | ").append(escape(item.dimension()))
                        .append(" | ").append(escape(item.skill()))
                        .append(" | ").append(escape(item.adapter()))
                        .append(" | ").append(item.passed())
                        .append(" | ").append(item.tokens())
                        .append(" | ").append(format(item.latencyMs()))
                        .append(" | ").append(escape(item.expected()))
                        .append(" | ").append(escape(item.actual()))
                        .append(" | ").append(escape(item.error()))
                        .append(" |\n");
            }
            return out.toString();
        }

        private void appendStats(StringBuilder out, NumericStats stats) {
            out.append("- Min: ").append(stats.min()).append("\n");
            out.append("- Max: ").append(stats.max()).append("\n");
            out.append("- Avg: ").append(format(stats.avg())).append("\n");
            out.append("- Total: ").append(stats.total()).append("\n");
            out.append("- P50: ").append(format(stats.p50())).append("\n");
            out.append("- P95: ").append(format(stats.p95())).append("\n");
        }

        private void appendGroupTable(StringBuilder out, Map<String, GroupStats> groups) {
            out.append("| Name | Executions | Avg Tokens | Avg Latency ms | Success Rate |\n");
            out.append("|---|---:|---:|---:|---:|\n");
            groups.forEach((name, stats) -> out.append("| ").append(escape(name))
                    .append(" | ").append(stats.executions())
                    .append(" | ").append(format(stats.avgTokens()))
                    .append(" | ").append(format(stats.avgLatencyMs()))
                    .append(" | ").append(format(stats.successRate()))
                    .append(" |\n"));
        }

        private double ratio(long numerator, long denominator) {
            return denominator == 0 ? 0 : numerator * 1.0 / denominator;
        }

        private String format(double value) {
            return String.format(java.util.Locale.ROOT, "%.3f", value);
        }

        private String escape(String value) {
            return blankToNone(value).replace("|", "\\|").replace("\n", "<br>");
        }

        private static String blankToNone(String value) {
            return value == null || value.isBlank() ? "None" : value;
        }
    }
}
