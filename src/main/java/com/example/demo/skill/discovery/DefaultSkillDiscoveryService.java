package com.example.demo.skill.discovery;

import com.example.demo.skill.SkillDefinition;
import com.example.demo.skill.SkillLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultSkillDiscoveryService implements SkillDiscoveryService {

    private final SkillLoader skillLoader;
    private final Map<String, SkillDefinition> skills = new LinkedHashMap<>();

    public DefaultSkillDiscoveryService(SkillLoader skillLoader) {
        this.skillLoader = skillLoader;
    }

    @PostConstruct
    public void initialize() throws IOException {
        //Bean 创建并完成依赖注入之后立即执行
        reload();
    }

    @Override
    public synchronized void reload() throws IOException {
        skills.clear();
        // 从文件系统加载,扫描 skills/ 目录下的所有子目录
        for (SkillDefinition skill : skillLoader.loadSkills()) {
            skills.put(skill.name(), skill);
        }
    }

    @Override
    public synchronized SkillDefinition get(String skillName) {
        SkillDefinition skill = skills.get(skillName);
        if (skill == null) {
            throw new IllegalArgumentException("Unknown skill: " + skillName);
        }
        return skill;
    }

    @Override
    public synchronized Collection<SkillDefinition> list() {
        return List.copyOf(skills.values());
    }
}
