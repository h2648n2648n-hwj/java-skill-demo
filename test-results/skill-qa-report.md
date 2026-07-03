# skill_qa.jsonl 路由评测报告

## 总览

- 用例总数: 20
- 通过数: 19
- 失败数: 1
- 通过率: 0.950
- 期望 skill 准确率: 0.941

### 指标解释

本报告逐行读取 `datasets/skill_qa.jsonl`，把每条数据的 `turns` 合并为用户请求，调用 `activationService.activate()` 做真实 LLM 路由评测。正例要求实际 skill 等于 `expected_skill`；负例要求不要误触发 `docx-report-generator`，因此 Render、天气、代码审查等请求只要没有被路由到 Word 报告 skill 就算通过。

## Word Skill 混淆矩阵

- TP: 16
- TN: 3
- FP: 0
- FN: 1
- Accuracy: 0.950
- Precision: 1.000
- Recall: 0.941

### 指标解释

这里把 `docx-report-generator` 当成目标 skill 来计算二分类指标。TP 表示应该触发 Word skill 且实际触发；TN 表示不应该触发 Word skill 且实际没有触发；FP 表示误触发 Word skill；FN 表示应该触发但没有触发。

## Token 消耗

- Min: 33
- Max: 116
- Avg: 90.250
- Total: 1805
- P50: 100.000
- P95: 109.000

### 指标解释

Token 是测试脚本估算值，统计内容为用户请求、实际 skill、路由原因和错误信息，不是 API 返回的真实 token usage。

## 延迟（毫秒）

- Min: 1807
- Max: 5528
- Avg: 3310.650
- Total: 66213
- P50: 3011.000
- P95: 4911.000

### 指标解释

延迟使用 `System.nanoTime()` 统计每条 JSONL 用例从调用路由器到返回结果的耗时，主要反映真实 LLM 路由阶段的响应速度。

## 按 Category 分组

| 名称 | 执行次数 | 平均 Token | 平均延迟（毫秒） | 成功率 |
|---|---:|---:|---:|---:|
| document_generation | 2 | 100.000 | 3317.741 | 1.000 |
| template_fill | 1 | 104.000 | 2512.769 | 1.000 |
| mail_merge | 1 | 88.000 | 2988.841 | 1.000 |
| toc_generation | 1 | 69.000 | 2662.637 | 1.000 |
| pdf_export | 1 | 56.000 | 3028.682 | 0.000 |
| business_plan | 1 | 109.000 | 3910.021 | 1.000 |
| certificate_generation | 1 | 100.000 | 2945.330 | 1.000 |
| table_report | 1 | 106.000 | 5528.059 | 1.000 |
| style_theme | 1 | 101.000 | 4096.055 | 1.000 |
| multi_turn_document_generation | 1 | 105.000 | 2798.988 | 1.000 |
| missing_filename | 1 | 104.000 | 3019.947 | 1.000 |
| template_syntax_guidance | 1 | 97.000 | 3837.316 | 1.000 |
| config_generation | 1 | 116.000 | 2823.152 | 1.000 |
| chart_embedding | 1 | 102.000 | 3820.193 | 1.000 |
| contract_document | 1 | 90.000 | 3011.451 | 1.000 |
| non_activation_weather | 1 | 33.000 | 1806.764 | 1.000 |
| non_activation_deployment | 1 | 73.000 | 2243.890 | 1.000 |
| non_activation_code_review | 1 | 47.000 | 3632.618 | 1.000 |
| ambiguous_followup | 1 | 105.000 | 4911.212 | 1.000 |

## 按实际 Skill 分组

| 名称 | 执行次数 | 平均 Token | 平均延迟（毫秒） | 成功率 |
|---|---:|---:|---:|---:|
| docx-report-generator | 16 | 99.750 | 3468.841 | 1.000 |
| pdf | 1 | 56.000 | 3028.682 | 0.000 |
| None | 2 | 40.000 | 2719.691 | 1.000 |
| render-deploy | 1 | 73.000 | 2243.890 | 1.000 |

## 逐次执行明细

| 用例 | Category | 期望激活 | 期望 Skill | 实际 Skill | 是否通过 | Token | 延迟（毫秒） | 原因 | 错误 |
|---|---|---:|---|---|---:|---:|---:|---|---|
| QA-WORD-001 | document_generation | true | docx-report-generator | docx-report-generator | true | 91 | 2945.453 | 用户需要生成一份项目周报Word报告，符合docx-report-generator的触发条件——自动生成专业Word文档报告。 | None |
| QA-WORD-002 | document_generation | true | docx-report-generator | docx-report-generator | true | 109 | 3690.030 | 用户要求生成包含季度指标表和图表说明的销售总结docx文件，该技能专门用于自动生成专业Word报告，支持表格和图表插入，完全匹配需求。 | None |
| QA-WORD-003 | template_fill | true | docx-report-generator | docx-report-generator | true | 104 | 2512.769 | 用户需要基于合同模板填充占位符生成Word文档，该技能专为模板填充和Word报告生成设计。 | None |
| QA-WORD-004 | mail_merge | true | docx-report-generator | docx-report-generator | true | 88 | 2988.841 | 用户需要根据Word模板和CSV批量生成会议通知，该技能专门支持Word模板填充和批量生成。符合其应用场景。 | None |
| QA-WORD-005 | toc_generation | true | docx-report-generator | docx-report-generator | true | 69 | 2662.637 | 用户需要为现有Word文档添加自动目录，该技能支持目录生成和Word文档操作。 | None |
| QA-WORD-006 | pdf_export | true | docx-report-generator | pdf | false | 56 | 3028.682 | 需要将Word文档导出为PDF，适合使用PDF相关工具进行格式转换。 | None |
| QA-WORD-007 | business_plan | true | docx-report-generator | docx-report-generator | true | 109 | 3910.021 | 用户需要生成商业计划书Word文档，该技能专门用于Word报告自动生成，支持模板填充、目录、图表等，符合任务要求。 | None |
| QA-WORD-008 | certificate_generation | true | docx-report-generator | docx-report-generator | true | 100 | 2945.330 | 用户需要基于模板批量生成结业证书，替换姓名、课程、编号，docx-report-generator 专为 Word 模板填充和证书批量生成设计。 | None |
| QA-WORD-009 | table_report | true | docx-report-generator | docx-report-generator | true | 106 | 5528.059 | 用户需要生成包含表格和要点的运营日报Word文档，该技能专门用于自动生成专业Word报告，支持表格插入和要点列表，完全匹配需求。 | None |
| QA-WORD-010 | style_theme | true | docx-report-generator | docx-report-generator | true | 101 | 4096.055 | 用户需要生成一份管理层汇报Word文档，要求蓝色商务风格、页眉页脚设置，符合docx-report-generator的‘生成Word报告、制作商业计划书’场景。 | None |
| QA-WORD-011 | multi_turn_document_generation | true | docx-report-generator | docx-report-generator | true | 105 | 2798.988 | 用户需要生成一份Word格式的研发月报，包含指定章节，docx-report-generator专门用于自动生成专业Word报告，完全匹配该需求。 | None |
| QA-WORD-012 | missing_filename | true | docx-report-generator | docx-report-generator | true | 104 | 3019.947 | 用户需要生成一份客户拜访报告，包含拜访对象、沟通纪要、机会点和后续动作，属于Word报告自动生成场景，该技能专门用于生成专业Word文档报告。 | None |
| QA-WORD-013 | template_syntax_guidance | true | docx-report-generator | docx-report-generator | true | 97 | 3837.316 | 用户询问Word模板占位符写法和data.json示例，属于模板填充和文档生成需求，docx-report-generator专门处理这类任务。 | None |
| QA-WORD-014 | config_generation | true | docx-report-generator | docx-report-generator | true | 116 | 2823.152 | 用户需要生成一份包含背景、目标达成、问题清单（表格）和改进动作的Word报告，符合docx-report-generator的Word报告自动生成功能。 | None |
| QA-WORD-015 | chart_embedding | true | docx-report-generator | docx-report-generator | true | 102 | 3820.193 | 用户需要生成财务分析报告Word文档，包含图表和明细表，该技能专门用于自动生成专业Word报告，支持图表插入和模板填充。 | None |
| QA-WORD-016 | contract_document | true | docx-report-generator | docx-report-generator | true | 90 | 3011.451 | 用户需要生成一份Word版采购合同，包含合同典型条款，符合该技能自动生成Word文档的专长。 | None |
| QA-WORD-017 | non_activation_weather | false | None | None | true | 33 | 1806.764 | No skill in the catalog handles weather queries or clothing recommendations. | None |
| QA-WORD-018 | non_activation_deployment | false | None | render-deploy | true | 73 | 2243.890 | 用户要求将Node.js服务部署到Render并生成render.yaml及环境变量说明，与render-deploy技能的描述完全匹配。 | None |
| QA-WORD-019 | non_activation_code_review | false | None | None | true | 47 | 3632.618 | No skill in the catalog is designed for Java code review focusing on concurrency and exception handling. | None |
| QA-WORD-020 | ambiguous_followup | true | docx-report-generator | docx-report-generator | true | 105 | 4911.212 | 用户需要将汇报整理成专业的Word文档，包含摘要、关键指标、风险和决策事项，该技能专门用于自动生成Word报告。 | None |
