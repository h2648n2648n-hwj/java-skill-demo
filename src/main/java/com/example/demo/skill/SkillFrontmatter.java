package com.example.demo.skill;

import java.util.ArrayList;
import java.util.List;

public record SkillFrontmatter(
        String name,
        String description,
        String whenToUse,
        List<String> allowedTools
) {
    public static SkillFrontmatter empty(String fallbackName) {
        return new SkillFrontmatter(fallbackName, "", "", new ArrayList<>());
    }
}
