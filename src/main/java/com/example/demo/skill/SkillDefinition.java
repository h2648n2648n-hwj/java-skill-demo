package com.example.demo.skill;

import java.nio.file.Path;
import java.util.List;

public record SkillDefinition(
        String name,
        String description,
        String whenToUse,
        List<String> allowedTools,
        Path rootDir,
        String skillMarkdown
) {
}
