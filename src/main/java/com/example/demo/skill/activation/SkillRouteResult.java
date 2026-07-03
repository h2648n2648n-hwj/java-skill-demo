package com.example.demo.skill.activation;

public record SkillRouteResult(String skillName, String reason, String directoryName) {

    public SkillRouteResult {
        skillName = skillName == null ? "" : skillName;
        reason = reason == null ? "" : reason;
        directoryName = directoryName == null ? "" : directoryName;
    }
}
