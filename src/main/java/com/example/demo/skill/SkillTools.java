package com.example.demo.skill;

import com.example.demo.skill.discovery.SkillDiscoveryService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Value;

import java.io.OutputStream;
import java.nio.file.StandardOpenOption;

@Component
public class SkillTools {

    private final SkillDiscoveryService discoveryService;


    private final Path outputRoot;

    public SkillTools(
            SkillDiscoveryService discoveryService,
            @Value("${skill.runtime.output-root:target/skill-runtime-output}") String outputRoot
    ) {
        this.discoveryService = discoveryService;
        this.outputRoot = Path.of(outputRoot).toAbsolutePath().normalize();
    }

    // 写入文本文件
    @Tool(description = "Write a UTF-8 text file under the runtime output directory. Use this for JSON configs, markdown drafts, or temporary text files.")
    public String writeTextFile(String relativePath, String content) throws IOException {
        Path target = resolveInsideOutput(relativePath);
        Files.createDirectories(target.getParent());
        Files.writeString(
                target,
                content == null ? "" : content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
        return "Wrote text file: " + target;
    }
    // 创建 Word 文档
    @Tool(description = "Create a simple Word .docx document under the runtime output directory from a title and body text.")
    public String createDocxDocument(String relativePath, String title, String body) throws IOException {
        String safePath = relativePath == null || relativePath.isBlank()
                ? "document.docx"
                : relativePath;

        if (!safePath.toLowerCase().endsWith(".docx")) {
            safePath = safePath + ".docx";
        }

        Path target = resolveInsideOutput(safePath);
        Files.createDirectories(target.getParent());

        try (XWPFDocument doc = new XWPFDocument()) {
            if (title != null && !title.isBlank()) {
                XWPFParagraph titleParagraph = doc.createParagraph();
                titleParagraph.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setBold(true);
                titleRun.setFontSize(16);
                titleRun.setText(title);
            }

            String text = body == null ? "" : body;
            for (String paragraphText : text.split("\\R\\s*\\R|\\R")) {
                if (paragraphText.isBlank()) {
                    continue;
                }
                XWPFParagraph paragraph = doc.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setFontSize(12);
                run.setText(paragraphText.trim());
            }

            try (OutputStream out = Files.newOutputStream(target)) {
                doc.write(out);
            }
        }

        return "Created DOCX document: " + target;
    }

    // 列出技能文件
    @Tool(description = "List files under a skill subdirectory. Examples: references, scripts, or empty string for the skill root.")
    public String listSkillFiles(String skillName, String relativeDir) throws IOException {
        SkillDefinition skill = discoveryService.get(skillName);
        Path dir = resolveInsideSkill(skill, blankToDot(relativeDir));
        if (!Files.isDirectory(dir)) {
            return "Not a directory: " + relativeDir;
        }

        try (var stream = Files.list(dir)) {
            return stream
                    .map(path -> skill.rootDir().relativize(path).toString().replace('\\', '/'))
                    .sorted()
                    .collect(Collectors.joining("\n"));
        }
    }

    // 读取技能文件
    @Tool(description = "Read a text file under a skill directory, such as references/rules.md or scripts/helper.py.")
    public String readSkillFile(String skillName, String relativePath) throws IOException {
        SkillDefinition skill = discoveryService.get(skillName);
        Path target = resolveInsideSkill(skill, relativePath);
        if (!Files.isRegularFile(target)) {
            return "Not a file: " + relativePath;
        }
        return Files.readString(target, StandardCharsets.UTF_8);
    }

    //执行脚本
    @Tool(description = "Run a helper script under the skill scripts directory. Supports .py, .js, and .jar demo scripts.")
    public String runSkillScript(String skillName, String relativeScriptPath, String args) throws IOException, InterruptedException {
        SkillDefinition skill = discoveryService.get(skillName);
        Path script = resolveInsideSkill(skill, relativeScriptPath);
        if (!Files.isRegularFile(script)) {
            return "Not a script file: " + relativeScriptPath;
        }
        if (!relativeScriptPath.replace('\\', '/').startsWith("scripts/")) {
            return "Only scripts under the scripts/ directory can be executed.";
        }

        List<String> command = buildScriptCommand(script, splitArgs(args));
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(skill.rootDir().toFile());
        builder.redirectErrorStream(true);

        Process process = builder.start();
        boolean finished = process.waitFor(Duration.ofSeconds(20).toMillis(), TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            return "Script timed out after 20 seconds.";
        }

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return "exitCode=" + process.exitValue() + "\n" + output;
    }

    private Path resolveInsideOutput(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("Path cannot be blank");
        }

        Path target = outputRoot.resolve(relativePath).normalize();

        if (!target.startsWith(outputRoot)) {
            throw new IllegalArgumentException("Path escapes output directory: " + relativePath);
        }

        return target;
    }

    private Path resolveInsideSkill(SkillDefinition skill, String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("Path cannot be blank");
        }
        Path target = skill.rootDir().resolve(relativePath).normalize();
        if (!target.startsWith(skill.rootDir())) {
            throw new IllegalArgumentException("Path escapes skill directory: " + relativePath);
        }
        return target;
    }

    private String blankToDot(String value) {
        return value == null || value.isBlank() ? "." : value;
    }

    private List<String> buildScriptCommand(Path script, List<String> args) {
        String fileName = script.getFileName().toString().toLowerCase();
        List<String> command = new ArrayList<>();
        if (fileName.endsWith(".py")) {
            command.add("python");
            command.add(script.toString());
        } else if (fileName.endsWith(".js")) {
            command.add("node");
            command.add(script.toString());
        } else if (fileName.endsWith(".jar")) {
            command.add("java");
            command.add("-jar");
            command.add(script.toString());
        } else {
            throw new IllegalArgumentException("Unsupported script type: " + fileName);
        }
        command.addAll(args);
        return command;
    }

    private List<String> splitArgs(String args) {
        if (args == null || args.isBlank()) {
            return List.of();
        }
        return List.of(args.trim().split("\\s+"));
    }
}
