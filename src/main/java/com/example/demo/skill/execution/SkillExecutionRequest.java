package com.example.demo.skill.execution;

import com.example.demo.skill.SkillDefinition;

public record SkillExecutionRequest(
        SkillDefinition skill,
        String task,
        String activationReason
) {
}
