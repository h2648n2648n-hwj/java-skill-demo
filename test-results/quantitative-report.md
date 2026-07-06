# Quantitative Report

## Classification

- TP: 0
- TN: 0
- FP: 0
- FN: 13
- Accuracy: 0.000
- Precision: 0.000
- Recall: 0.000

## Token Usage

- Min: 2
- Max: 29
- Avg: 11.980
- Total: 611
- P50: 11.000
- P95: 24.000

## Latency Ms

- Min: 0
- Max: 462
- Avg: 9.235
- Total: 471
- P50: 0.000
- P95: 2.000

## By Skill

| Name | Executions | Avg Tokens | Avg Latency ms | Success Rate |
|---|---:|---:|---:|---:|
| None | 51 | 11.980 | 9.284 | 0.412 |

## By Adapter

| Name | Executions | Avg Tokens | Avg Latency ms | Success Rate |
|---|---:|---:|---:|---:|
| spring-ai-openai-compatible | 13 | 14.615 | 0.027 | 0.000 |
| java-regex-field-extractor | 5 | 14.800 | 0.148 | 1.000 |
| decision-schema-validator | 4 | 19.000 | 1.865 | 1.000 |
| spring-ai-openai-compatible-tools | 3 | 16.000 | 0.012 | 0.000 |
| spring-ai-prompt-builder | 1 | 15.000 | 0.013 | 0.000 |
| latency-recorder | 1 | 4.000 | 0.003 | 1.000 |
| token-estimator | 1 | 5.000 | 0.007 | 1.000 |
| SkillTools.createDocxDocument | 1 | 4.000 | 462.000 | 1.000 |
| SkillTools.writeTextFile | 3 | 5.333 | 0.639 | 1.000 |
| SkillTools.listSkillFiles | 1 | 10.000 | 0.034 | 0.000 |
| SkillTools.readSkillFile | 3 | 7.000 | 0.091 | 0.667 |
| SkillTools.runSkillScript | 2 | 10.500 | 0.009 | 0.000 |
| DefaultSkillDiscoveryService | 10 | 10.700 | 0.014 | 0.100 |
| SkillLoader | 3 | 6.667 | 0.168 | 1.000 |

## Execution Details

| Case | Dimension | Skill | Adapter | Passed | Tokens | Latency ms | Expected | Actual | Error |
|---|---|---|---|---:|---:|---:|---|---|---|
| A-01 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 15 | 0.193 | [docx-report-generator] | None | IllegalStateException: No skills loaded. |
| A-02 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 19 | 0.070 | [documents, docx-report-generator] | None | IllegalStateException: No skills loaded. |
| A-03 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 16 | 0.013 | [pdf] | None | IllegalStateException: No skills loaded. |
| A-04 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 17 | 0.008 | [openai-docs] | None | IllegalStateException: No skills loaded. |
| A-05 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 18 | 0.008 | [render-deploy] | None | IllegalStateException: No skills loaded. |
| A-06 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 15 | 0.009 | [cloudflare-deploy] | None | IllegalStateException: No skills loaded. |
| A-07 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 14 | 0.008 | [security-best-practices] | None | IllegalStateException: No skills loaded. |
| A-08 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 15 | 0.008 | [chatgpt-apps] | None | IllegalStateException: No skills loaded. |
| A-09 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 16 | 0.007 | [figma-generate-library, figma-use] | None | IllegalStateException: No skills loaded. |
| A-10 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 13 | 0.010 | [notion-knowledge-capture, notion-research-documentation] | None | IllegalStateException: No skills loaded. |
| A-11 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 12 | 0.008 | [speech] | None | IllegalStateException: No skills loaded. |
| A-12 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 14 | 0.008 | [] | None | IllegalStateException: No skills loaded. |
| A-13 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 6 | 0.007 | [] | None | IllegalStateException: No skills loaded. |
| B-01 | B-field-extraction | None | java-regex-field-extractor | true | 16 | 0.497 | {filename=weekly-report.docx} | {filename=weekly-report.docx} | None |
| B-02 | B-field-extraction | None | java-regex-field-extractor | true | 18 | 0.074 | {template_name=product-review} | {template_name=product-review} | None |
| B-03 | B-field-extraction | None | java-regex-field-extractor | true | 29 | 0.076 | {filename=q2-summary.docx, template_name=q2-summary} | {filename=q2-summary.docx, template_name=q2-summary} | None |
| B-04 | B-field-extraction | None | java-regex-field-extractor | true | 2 | 0.003 | {} | {} | None |
| B-05 | B-field-extraction | None | java-regex-field-extractor | true | 9 | 0.090 | {} | {} | None |
| C-01 | C-llm-error-tolerance | None | decision-schema-validator | true | 27 | 0.110 | schema rejected | Unknown skill: missing-skill | None |
| C-02 | C-llm-error-tolerance | None | decision-schema-validator | true | 24 | 0.008 | schema rejected | should_call must be boolean | None |
| C-03 | C-llm-error-tolerance | None | decision-schema-validator | true | 20 | 0.006 | schema rejected | confidence must be number | None |
| C-04 | C-llm-error-tolerance | None | decision-schema-validator | true | 5 | 7.336 | JsonProcessingException | JsonParseException | None |
| D-01 | D-execution-chain | None | spring-ai-openai-compatible-tools | false | 15 | 0.025 | non-empty execution response | None | IllegalStateException: No skills loaded. |
| D-02 | D-execution-chain | None | spring-ai-openai-compatible-tools | false | 17 | 0.006 | non-empty execution response | None | IllegalStateException: No skills loaded. |
| D-03 | D-execution-chain | None | spring-ai-openai-compatible-tools | false | 16 | 0.005 | non-empty execution response | None | IllegalStateException: No skills loaded. |
| D-04 | D-execution-chain | None | spring-ai-prompt-builder | false | 15 | 0.013 | true | None | IllegalArgumentException: Unknown skill: docx-report-generator |
| D-05 | D-execution-chain | None | latency-recorder | true | 4 | 0.003 | true | true | None |
| D-06 | D-execution-chain | None | token-estimator | true | 5 | 0.007 | true | true | None |
| E-01 | E-adapter-tool-coverage | None | SkillTools.createDocxDocument | true | 4 | 462.000 | true | true | None |
| E-02 | E-adapter-tool-coverage | None | SkillTools.writeTextFile | true | 4 | 1.537 | true | true | None |
| E-03 | E-adapter-tool-coverage | None | SkillTools.listSkillFiles | false | 10 | 0.034 | true | None | IllegalArgumentException: Unknown skill: documents |
| E-04 | E-adapter-tool-coverage | None | SkillTools.readSkillFile | false | 9 | 0.009 | true | None | IllegalArgumentException: Unknown skill: documents |
| E-05 | E-adapter-tool-coverage | None | SkillTools.runSkillScript | false | 10 | 0.008 | true | None | IllegalArgumentException: Unknown skill: documents |
| E-06 | E-adapter-tool-coverage | None | SkillTools.writeTextFile | true | 6 | 0.295 | true | true | None |
| E-07 | E-adapter-tool-coverage | None | SkillTools.readSkillFile | true | 6 | 0.145 | true | true | None |
| E-08 | E-adapter-tool-coverage | None | SkillTools.runSkillScript | false | 11 | 0.011 | true | None | IllegalArgumentException: Unknown skill: documents |
| F-01 | F-boundary-robustness | None | DefaultSkillDiscoveryService | false | 7 | 0.012 | true | false | None |
| F-02 | F-boundary-robustness | None | DefaultSkillDiscoveryService | true | 5 | 0.082 | true | true | None |
| F-03 | F-boundary-robustness | None | SkillLoader | true | 6 | 0.236 | true | true | None |
| F-04 | F-boundary-robustness | None | SkillLoader | true | 6 | 0.161 | true | true | None |
| F-05 | F-boundary-robustness | None | SkillLoader | true | 8 | 0.107 | true | true | None |
| F-06 | F-boundary-robustness | None | SkillTools.writeTextFile | true | 6 | 0.085 | true | true | None |
| F-07 | F-boundary-robustness | None | SkillTools.readSkillFile | true | 6 | 0.119 | true | true | None |
| G-01 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 7 | 0.010 | loaded | None | IllegalArgumentException: Unknown skill: documents |
| G-02 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 15 | 0.005 | loaded | None | IllegalArgumentException: Unknown skill: docx-report-generator |
| G-03 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 7 | 0.005 | loaded | None | IllegalArgumentException: Unknown skill: pdf |
| G-04 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 11 | 0.005 | loaded | None | IllegalArgumentException: Unknown skill: openai-docs |
| G-05 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 11 | 0.005 | loaded | None | IllegalArgumentException: Unknown skill: render-deploy |
| G-06 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 11 | 0.005 | loaded | None | IllegalArgumentException: Unknown skill: cloudflare-deploy |
| G-07 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 15 | 0.005 | loaded | None | IllegalArgumentException: Unknown skill: security-best-practices |
| G-08 | G-discovery-completeness | None | DefaultSkillDiscoveryService | false | 18 | 0.006 | word-report-generator-1.0.0 | None | IllegalArgumentException: Unknown skill: docx-report-generator |
