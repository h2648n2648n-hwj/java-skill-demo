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

    //返回当前系统里已发现的所有 SkillDefinition，也就是技能列表。
    @GetMapping
    public Collection<SkillDefinition> list() {
        return discoveryService.list();
    }

    //重新扫描/加载技能目录；
    @PostMapping("/reload")
    public String reload() throws IOException {
        discoveryService.reload();
        return "ok";
    }

    //按指定技能名执行任务。
    @PostMapping("/{skillName}/run")
    public String run(@PathVariable String skillName, @RequestBody RunSkillRequest request) {
        return runtimeService.run(skillName, request.task());
    }

    // 根据 task 自动判断应该路由到哪个技能。返回的是“匹配结果
    @PostMapping("/auto/match")
    public SkillRouteResult autoMatch(@RequestBody RunSkillRequest request) {
        return activationService.activate(request.task());
    }

    // 先自动匹配技能，再直接执行任务
    @PostMapping("/auto/run")
    public String autoRun(@RequestBody RunSkillRequest request) {
        return runtimeService.runAuto(request.task());
    }

    public record RunSkillRequest(String task) {
    }
}
