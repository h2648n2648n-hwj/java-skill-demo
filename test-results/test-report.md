# Test Report

- Total: 51
- Passed: 49
- Failed: 2
- Total duration ms: 403201.374

| Case | Dimension | Type | Adapter | Expected | Actual | Passed | Latency ms | Tokens | Error |
|---|---|---|---|---|---|---:|---:|---:|---|
| A-01 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [docx-report-generator] | docx-report-generator | true | 3059.905 | 14 | None |
| A-02 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [documents, docx-report-generator] | documents | true | 3693.580 | 14 | None |
| A-03 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [pdf] | pdf | true | 1927.248 | 11 | None |
| A-04 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [openai-docs] | openai-docs | true | 1845.287 | 14 | None |
| A-05 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [render-deploy] | render-deploy | true | 2070.694 | 15 | None |
| A-06 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [cloudflare-deploy] | cloudflare-deploy | true | 1656.277 | 12 | None |
| A-07 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [security-best-practices] | security-best-practices | true | 2722.031 | 13 | None |
| A-08 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [chatgpt-apps] | chatgpt-apps | true | 1864.807 | 12 | None |
| A-09 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [figma-generate-library, figma-use] | figma-generate-library | true | 2107.537 | 15 | None |
| A-10 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [notion-knowledge-capture, notion-research-documentation] | notion-knowledge-capture | true | 2845.751 | 12 | None |
| A-11 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [speech] | None | false | 2075.777 | 6 | None |
| A-12 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [] | None | true | 3198.348 | 8 | None |
| A-13 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [] | None | true | 1572.234 | 0 | None |
| B-01 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {filename=weekly-report.docx} | {filename=weekly-report.docx} | true | 0.398 | 16 | None |
| B-02 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {template_name=product-review} | {template_name=product-review} | true | 0.090 | 18 | None |
| B-03 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {template_name=q2-summary, filename=q2-summary.docx} | {filename=q2-summary.docx, template_name=q2-summary} | true | 0.117 | 29 | None |
| B-04 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {} | {} | true | 0.003 | 2 | None |
| B-05 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {} | {} | true | 0.039 | 9 | None |
| C-01 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | schema rejected | Unknown skill: missing-skill | true | 0.106 | 27 | None |
| C-02 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | schema rejected | should_call must be boolean | true | 0.007 | 24 | None |
| C-03 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | schema rejected | confidence must be number | true | 0.006 | 20 | None |
| C-04 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | JsonProcessingException | JsonParseException | true | 0.236 | 5 | None |
| D-01 | D-execution-chain | EXECUTION | spring-ai-openai-compatible-tools | non-empty execution response | 实习报告已成功生成！📄<br><br>**文件路径：** `internship_report.docx`<br><br>报告包含以下完整内容：<br><br>\| 章节 \| 内容概要 \|<br>\|------\|----------\|<br>\| 📋 **基本信息** \| 姓名、学号、专业、实习单位、岗位、时间、导师 \|<br>\| 🎯 **实习目的与背景** \| 理论应用于实践，提升工程能力和职业素养 \|<br>\| 🏢 **实习单位简介** \| 星辰科技有限公司概况 \|<br>\| 🛠️ **实习主要内容** \| 6个工作模块：用户服务、订单优化、商品搜索、消息推送、单元测试、API文档 \|<br>\| 💡 **关键技术实践** \| 微服务架构设计、数据库性能优化（800ms→120ms）、全文检索集成 \|<br>\| 📊 **实习成果数据** \| 接口24个、代码12,000行、测试156个(覆盖率92%)、Bug修复47个 \|<br>\| ✨ **实习心得** \| 技术能力、工程思维、团队协作、问题解决、职业素养五大提升 \|<br>\| 🔭 **总结与展望** \| 未来学习计划与职业目标 \|<br>\| 🙏 **致谢** \| 感谢公司、导师和团队 \|<br><br>文档已使用 Word 格式创建，包含标题、正文段落、列表等基础排版。如需进一步美化（添加目录、表格、图表或自定义样式），我还可以继续使用脚本工具进行增强处理。是否需要我进一步优化？ | true | 73795.359 | 479 | None |
| D-02 | D-execution-chain | EXECUTION | spring-ai-openai-compatible-tools | non-empty execution response | The document has been created successfully. Here's a summary of what was delivered:<br><br>---<br><br>## ✅ Spring AI Skill Routing — DOCX Delivered<br><br>**File:** `Spring_AI_Skill_Routing.docx`<br><br>**Design Preset:** `standard_business_brief` — Calibri-based, professional typography with blue heading hierarchy (#2E74B5), clean body spacing, and consistent table styling.<br><br>**Document Structure:**<br><br>\| Section \| Content \|<br>\|---\|---\|<br>\| **Title** \| Spring AI Skill Routing (22pt bold blue, with italic subtitle) \|<br>\| **Overview** \| What skill routing is and why it matters in Spring AI \|<br>\| **Core Concepts** \| Skill, Router, and Routing Policy explained \|<br>\| **Routing Strategies** \| Table comparing 5 strategies (content-based, tag-based, model-capability, fallback chain, hybrid) \|<br>\| **Configuration Approach** \| How to set up skills, routers, and dispatch in Spring AI \|<br>\| **Benefits** \| Modularity, extensibility, observability, resilience, performance \|<br>\| **Design Considerations** \| Latency, ambiguity, contract evolution, observability investment \|<br>\| **Summary** \| Key takeaways \|<br><br>**Quality Checks:**<br>- ✅ Page setup: US Letter, 1in margins<br>- ✅ Table geometry: verified — `tblW`, `tblInd`, `tblGrid`, and `tcW` all match (9360 DXA full-width)<br>- ✅ Content: 7 major sections, 1 comparison table, bullet lists<br>- ⚠️ Visual render QA was not completed (LibreOffice/`soffice` unavailable in this environment)<br><br>The document provides a concise yet thorough technical brief on Spring AI skill routing — covering the what, why, and how of intelligent request dispatch within the Spring AI framework. | true | 286630.341 | 389 | None |
| D-03 | D-execution-chain | EXECUTION | spring-ai-openai-compatible-tools | non-empty execution response | No suitable skill found. Reason: No skill in catalog matches the task of writing a generic text draft about runtime evaluation metrics. | true | 12118.641 | 35 | None |
| D-04 | D-execution-chain | EXECUTION | spring-ai-prompt-builder | true | true | true | 0.285 | 6 | None |
| D-05 | D-execution-chain | EXECUTION | latency-recorder | true | true | true | 0.005 | 4 | None |
| D-06 | D-execution-chain | EXECUTION | token-estimator | true | true | true | 0.011 | 5 | None |
| E-01 | E-adapter-tool-coverage | ADAPTER | SkillTools.createDocxDocument | true | true | true | 11.482 | 4 | None |
| E-02 | E-adapter-tool-coverage | ADAPTER | SkillTools.writeTextFile | true | true | true | 0.663 | 4 | None |
| E-03 | E-adapter-tool-coverage | ADAPTER | SkillTools.listSkillFiles | true | true | true | 0.292 | 5 | None |
| E-04 | E-adapter-tool-coverage | ADAPTER | SkillTools.readSkillFile | true | true | true | 0.604 | 4 | None |
| E-05 | E-adapter-tool-coverage | ADAPTER | SkillTools.runSkillScript | true | true | true | 0.364 | 5 | None |
| E-06 | E-adapter-tool-coverage | ADAPTER | SkillTools.writeTextFile | true | true | true | 0.223 | 6 | None |
| E-07 | E-adapter-tool-coverage | ADAPTER | SkillTools.readSkillFile | true | true | true | 0.099 | 6 | None |
| E-08 | E-adapter-tool-coverage | ADAPTER | SkillTools.runSkillScript | true | false | false | 0.140 | 6 | None |
| F-01 | F-boundary-robustness | BOUNDARY | DefaultSkillDiscoveryService | true | true | true | 0.018 | 7 | None |
| F-02 | F-boundary-robustness | BOUNDARY | DefaultSkillDiscoveryService | true | true | true | 0.105 | 5 | None |
| F-03 | F-boundary-robustness | BOUNDARY | SkillLoader | true | true | true | 0.141 | 6 | None |
| F-04 | F-boundary-robustness | BOUNDARY | SkillLoader | true | true | true | 1.144 | 6 | None |
| F-05 | F-boundary-robustness | BOUNDARY | SkillLoader | true | true | true | 0.138 | 8 | None |
| F-06 | F-boundary-robustness | BOUNDARY | SkillTools.writeTextFile | true | true | true | 0.099 | 6 | None |
| F-07 | F-boundary-robustness | BOUNDARY | SkillTools.readSkillFile | true | true | true | 0.111 | 6 | None |
| G-01 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | loaded | true | 0.241 | 2 | None |
| G-02 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | loaded | true | 0.077 | 6 | None |
| G-03 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | loaded | true | 0.062 | 2 | None |
| G-04 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | loaded | true | 0.056 | 4 | None |
| G-05 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | loaded | true | 0.066 | 4 | None |
| G-06 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | loaded | true | 0.061 | 4 | None |
| G-07 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | loaded | true | 0.061 | 6 | None |
| G-08 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | word-report-generator-1.0.0 | word-report-generator-1.0.0 | true | 0.010 | 19 | None |
