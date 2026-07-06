Java Skill Demo

**问答集的数据集位于"datasets\skill_qa_generated_20.jsonl"**


**最新的测试结果位于"test-results\skill-qa-report-name to LLM.md"**

**每部分的功能测试""test-results\test-report.md""**



一个基于 Spring Boot + Spring AI 的 Skills Runtime 示例项目。

项目会扫描 skills/ 目录下的 SKILL.md，把每个 skill 加载成可路由、可执行的能力；收到用户任务后，先进行 skill 匹配，再把命中的 SKILL.md 内容交给大模型执行。

核心功能

- 自动发现 skills/*/SKILL.md
- 解析 skill 的 frontmatter：name、description、when_to_use、allowed-tools
- 使用 BM25 做本地候选召回
- 使用 OpenAI-compatible Chat API 做最终 skill 路由
- 支持按 skill 手动执行任务，也支持自动匹配后执行
- 提供工具函数读取 skill 文件、运行 skill 脚本、写入运行产物
- 提供真实 LLM 路由测试和 QA 数据集测试

项目结构

    .
    ├── src/main/java/com/example/demo
    │   ├── controller/SkillController.java        # REST API
    │   └── skill/
    │       ├── SkillLoader.java                   # 加载 skills 目录
    │       ├── SkillRuntimeService.java           # 手动/自动执行入口
    │       ├── SkillTools.java                    # 暴露给 LLM 的工具
    │       ├── activation/                        # BM25 + LLM 路由
    │       ├── discovery/                         # skill 注册与查询
    │       └── execution/                         # LLM 执行 skill
    ├── src/main/resources/application.yaml        # 模型与运行配置
    ├── skills/                                    # skill 示例目录
    ├── datasets/                                  # QA 测试数据
    └── test-results/                              # 测试报告输出目录

环境要求

- JDK 17+
- Maven 3.8+
- 可访问 OpenAI-compatible Chat API

默认配置使用 DeepSeek 兼容接口：

    spring.ai.openai.api-key: ${DEEPSEEK_API_KEY}
    spring.ai.openai.base-url: ${DEEPSEEK_BASE_URL:https://api.deepseek.com}
    spring.ai.openai.chat.completions-path: ${DEEPSEEK_COMPLETIONS_PATH:/chat/completions}
    spring.ai.openai.chat.options.model: ${DEEPSEEK_MODEL:deepseek-v4-flash}

启动

先设置环境变量：

    $env:DEEPSEEK_API_KEY="你的 API Key"

启动应用：

    mvn spring-boot:run

默认服务地址：

    http://localhost:8080

API 示例

查看已加载的 skills：

    Invoke-RestMethod -Method Get http://localhost:8080/skills

重新扫描 skills/ 目录：

    Invoke-RestMethod -Method Post http://localhost:8080/skills/reload

自动匹配 skill：

    Invoke-RestMethod `
      -Method Post `
      -Uri http://localhost:8080/skills/auto/match `
      -ContentType "application/json" `
      -Body '{"task":"帮我生成一份 Word 项目周报"}'

自动匹配并执行：

    Invoke-RestMethod `
      -Method Post `
      -Uri http://localhost:8080/skills/auto/run `
      -ContentType "application/json" `
      -Body '{"task":"帮我生成一份 Word 项目周报，文件名 weekly-report.docx"}'

指定 skill 执行：

    Invoke-RestMethod `
      -Method Post `
      -Uri http://localhost:8080/skills/docx-report-generator/run `
      -ContentType "application/json" `
      -Body '{"task":"生成一份项目周报"}'

Skill 格式

每个 skill 放在 skills/<skill-dir>/SKILL.md，推荐包含 frontmatter：

    ---
    name: example-skill
    description: 简短描述这个 skill 能做什么
    when_to_use: 说明什么场景下应该触发这个 skill
    allowed-tools:
      - readSkillFile
      - runSkillScript
    ---
    
    这里是 skill 的详细执行说明。

如果 skill 需要辅助材料，可以放在：

    skills/<skill-dir>/references/
    skills/<skill-dir>/scripts/
    skills/<skill-dir>/assets/

运行时产物默认写入：

    target/skill-runtime-output/

测试

普通测试：

    mvn test

真实 LLM 测试需要额外开启环境变量：

    $env:DEEPSEEK_API_KEY="你的 API Key"
    $env:RUN_REAL_LLM_TESTS="true"
    mvn test

QA 数据集默认读取：

    datasets/skill_qa_generated_20.jsonl

可以通过 Maven 参数指定其他数据集：

    mvn test -Dskill.qa.dataset=datasets/skill_qa.jsonl

测试报告输出到：

    test-results/

配置项

    skill:
      runtime:
        skills-root: skills
        output-root: target/skill-runtime-output

- skills-root：skill 根目录
- output-root：运行时生成文件的输出目录

适合阅读的入口

- SkillController：看对外 API
- SkillLoader：看 SKILL.md 如何被加载
- LlmSkillActivationService：看 LLM 如何选择 skill
- LlmSkillExecutionService：看 skill 如何被执行
- SkillTools：看 LLM 可以调用哪些工具

