package com.example.demo.skill;

import com.example.demo.skill.activation.SkillActivationService;
import com.example.demo.skill.activation.SkillRouteResult;
import com.example.demo.skill.discovery.SkillDiscoveryService;
import com.example.demo.skill.execution.SkillExecutionRequest;
import com.example.demo.skill.execution.SkillExecutionService;
import org.springframework.stereotype.Service;

@Service
public class SkillRuntimeService {

    private final SkillDiscoveryService discoveryService;
    private final SkillActivationService activationService;
    private final SkillExecutionService executionService;

    public SkillRuntimeService(SkillDiscoveryService discoveryService,
                               SkillActivationService activationService,
                               SkillExecutionService executionService) {
        this.discoveryService = discoveryService;
        this.activationService = activationService;
        this.executionService = executionService;
    }

    public String runAuto(String task) {
        SkillRouteResult routeResult = activationService.activate(task);
        if (routeResult.skillName() == null || routeResult.skillName().isBlank()) {
            return "No suitable skill found. Reason: " + routeResult.reason();
        }

        SkillDefinition skill = discoveryService.get(routeResult.skillName());
        return executionService.execute(new SkillExecutionRequest(skill, task, routeResult.reason()));
    }

    public String run(String skillName, String task) {
        SkillDefinition skill = discoveryService.get(skillName);
        return executionService.execute(new SkillExecutionRequest(skill, task, "manual activation"));
    }
}
