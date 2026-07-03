package com.example.demo.controller;

import com.example.demo.skill.SkillDefinition;
import com.example.demo.skill.SkillRuntimeService;
import com.example.demo.skill.activation.SkillActivationService;
import com.example.demo.skill.activation.SkillRouteResult;
import com.example.demo.skill.discovery.SkillDiscoveryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillDiscoveryService discoveryService;
    private final SkillRuntimeService runtimeService;
    private final SkillActivationService activationService;

    public SkillController(SkillDiscoveryService discoveryService,
                           SkillRuntimeService runtimeService,
                           SkillActivationService activationService) {
        this.discoveryService = discoveryService;
        this.runtimeService = runtimeService;
        this.activationService = activationService;
    }

    @GetMapping
    public Collection<SkillDefinition> list() {
        return discoveryService.list();
    }

    @PostMapping("/reload")
    public String reload() throws IOException {
        discoveryService.reload();
        return "ok";
    }

    @PostMapping("/{skillName}/run")
    public String run(@PathVariable String skillName, @RequestBody RunSkillRequest request) {
        return runtimeService.run(skillName, request.task());
    }

    @PostMapping("/auto/match")
    public SkillRouteResult autoMatch(@RequestBody RunSkillRequest request) {
        return activationService.activate(request.task());
    }

    @PostMapping("/auto/run")
    public String autoRun(@RequestBody RunSkillRequest request) {
        return runtimeService.runAuto(request.task());
    }

    public record RunSkillRequest(String task) {
    }
}
