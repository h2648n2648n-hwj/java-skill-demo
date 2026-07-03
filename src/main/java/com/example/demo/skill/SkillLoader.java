package com.example.demo.skill;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class SkillLoader {

    //定义skill根目录字段
    private final Path skillsRoot;

    public SkillLoader(@Value("${skill.runtime.skills-root:skills}") String skillsRoot) {
        this.skillsRoot = Path.of(skillsRoot).toAbsolutePath().normalize();
    }

    public List<SkillDefinition> loadSkills() throws IOException {
        //判断skill根目录是否存在
        if (!Files.exists(skillsRoot)) {
            Files.createDirectories(skillsRoot);
            return List.of();
        }
        //对每个子目录调用 loadOne() 加载单个技能
        try (var dirs = Files.list(skillsRoot)) {
            return dirs.filter(Files::isDirectory)
                    .map(this::loadOne)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }

    //
    private SkillDefinition loadOne(Path skillDir) {
        //检查目录下是否存在 SKILL.md 文件
        Path skillFile = skillDir.resolve("SKILL.md");
        if (!Files.isRegularFile(skillFile)) {
            return null;
        }

        try {
            String raw = Files.readString(skillFile);
            //分离 frontmatter（元数据）和 body（正文
            ParsedSkillMarkdown parsed = parseSkillMarkdown(raw);
            String fallbackName = skillDir.getFileName().toString();
            SkillFrontmatter frontmatter = parseFrontmatter(parsed.frontmatter(), fallbackName);
            //构建 SkillDefinition 对象
            return new SkillDefinition(
                    blankToDefault(frontmatter.name(), fallbackName),
                    blankToDefault(frontmatter.description(), ""),
                    blankToDefault(frontmatter.whenToUse(), ""),
                    frontmatter.allowedTools(),
                    skillDir.toAbsolutePath().normalize(),
                    parsed.body()
            );
        } catch (IOException e) {
            return null;
        }
    }

    /*
    frontmatter：两个 --- 之间的元数据
    body：后面的 markdown 正文
    * */
    private ParsedSkillMarkdown parseSkillMarkdown(String raw) {
        if (!raw.startsWith("---")) {
            return new ParsedSkillMarkdown("", raw.trim());
        }

        int end = raw.indexOf("\n---", 3);
        if (end < 0) {
            return new ParsedSkillMarkdown("", raw.trim());
        }

        String frontmatter = raw.substring(3, end).trim();
        String body = raw.substring(end + 4).trim();
        return new ParsedSkillMarkdown(frontmatter, body);
    }

    /*
    解析 Frontmatter
    逐行处理 frontmatter 文本
     */
    private SkillFrontmatter parseFrontmatter(String frontmatter, String fallbackName) {
        if (frontmatter == null || frontmatter.isBlank()) {
            return SkillFrontmatter.empty(fallbackName);
        }

        String name = fallbackName;
        String description = "";
        String whenToUse = "";
        List<String> allowedTools = new ArrayList<>();
        boolean readingAllowedTools = false;

        for (String rawLine : frontmatter.split("\\R")) {
            String line = rawLine.stripTrailing();
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            if (trimmed.startsWith("- ") && readingAllowedTools) {
                allowedTools.add(unquote(trimmed.substring(2).trim()));
                continue;
            }

            readingAllowedTools = false;
            int colon = trimmed.indexOf(':');
            if (colon < 0) {
                continue;
            }

            String key = trimmed.substring(0, colon).trim();
            String value = unquote(trimmed.substring(colon + 1).trim());
            switch (key) {
                case "name" -> name = value;
                case "description" -> description = value;
                case "when_to_use" -> whenToUse = value;
                case "allowed-tools" -> {
                    readingAllowedTools = true;
                    if (!value.isBlank()) {
                        allowedTools.add(value);
                    }
                }
                default -> {
                    // Unknown frontmatter is ignored in this demo.
                }
            }
        }

        return new SkillFrontmatter(name, description, whenToUse, allowedTools);
    }

    private String unquote(String value) {
        if ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private record ParsedSkillMarkdown(String frontmatter, String body) {
    }
}
