package com.example.demo.skill.discovery;

import com.example.demo.skill.SkillDefinition;

import java.io.IOException;
import java.util.Collection;

public interface SkillDiscoveryService {

    // 加载所有技能
    void reload() throws IOException;
    //获取单个技能
    SkillDefinition get(String skillName);
    // 列出所有技能
    Collection<SkillDefinition> list();
}
