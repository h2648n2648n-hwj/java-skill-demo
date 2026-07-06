# Test Report

- Total: 51
- Passed: 21
- Failed: 30
- Total duration ms: 473.498

| Case | Dimension | Type | Adapter | Expected | Actual | Passed | Latency ms | Tokens | Error |
|---|---|---|---|---|---|---:|---:|---:|---|
| A-01 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [docx-report-generator] | None | false | 0.193 | 15 | IllegalStateException: No skills loaded. |
| A-02 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [documents, docx-report-generator] | None | false | 0.070 | 19 | IllegalStateException: No skills loaded. |
| A-03 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [pdf] | None | false | 0.013 | 16 | IllegalStateException: No skills loaded. |
| A-04 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [openai-docs] | None | false | 0.008 | 17 | IllegalStateException: No skills loaded. |
| A-05 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [render-deploy] | None | false | 0.008 | 18 | IllegalStateException: No skills loaded. |
| A-06 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [cloudflare-deploy] | None | false | 0.009 | 15 | IllegalStateException: No skills loaded. |
| A-07 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [security-best-practices] | None | false | 0.008 | 14 | IllegalStateException: No skills loaded. |
| A-08 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [chatgpt-apps] | None | false | 0.008 | 15 | IllegalStateException: No skills loaded. |
| A-09 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [figma-generate-library, figma-use] | None | false | 0.007 | 16 | IllegalStateException: No skills loaded. |
| A-10 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [notion-knowledge-capture, notion-research-documentation] | None | false | 0.010 | 13 | IllegalStateException: No skills loaded. |
| A-11 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [speech] | None | false | 0.008 | 12 | IllegalStateException: No skills loaded. |
| A-12 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [] | None | false | 0.008 | 14 | IllegalStateException: No skills loaded. |
| A-13 | A-routing-accuracy | ROUTING | spring-ai-openai-compatible | [] | None | false | 0.007 | 6 | IllegalStateException: No skills loaded. |
| B-01 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {filename=weekly-report.docx} | {filename=weekly-report.docx} | true | 0.497 | 16 | None |
| B-02 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {template_name=product-review} | {template_name=product-review} | true | 0.074 | 18 | None |
| B-03 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {filename=q2-summary.docx, template_name=q2-summary} | {filename=q2-summary.docx, template_name=q2-summary} | true | 0.076 | 29 | None |
| B-04 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {} | {} | true | 0.003 | 2 | None |
| B-05 | B-field-extraction | FIELD_EXTRACTION | java-regex-field-extractor | {} | {} | true | 0.090 | 9 | None |
| C-01 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | schema rejected | Unknown skill: missing-skill | true | 0.110 | 27 | None |
| C-02 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | schema rejected | should_call must be boolean | true | 0.008 | 24 | None |
| C-03 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | schema rejected | confidence must be number | true | 0.006 | 20 | None |
| C-04 | C-llm-error-tolerance | SCHEMA | decision-schema-validator | JsonProcessingException | JsonParseException | true | 7.336 | 5 | None |
| D-01 | D-execution-chain | EXECUTION | spring-ai-openai-compatible-tools | non-empty execution response | None | false | 0.025 | 15 | IllegalStateException: No skills loaded. |
| D-02 | D-execution-chain | EXECUTION | spring-ai-openai-compatible-tools | non-empty execution response | None | false | 0.006 | 17 | IllegalStateException: No skills loaded. |
| D-03 | D-execution-chain | EXECUTION | spring-ai-openai-compatible-tools | non-empty execution response | None | false | 0.005 | 16 | IllegalStateException: No skills loaded. |
| D-04 | D-execution-chain | EXECUTION | spring-ai-prompt-builder | true | None | false | 0.013 | 15 | IllegalArgumentException: Unknown skill: docx-report-generator |
| D-05 | D-execution-chain | EXECUTION | latency-recorder | true | true | true | 0.003 | 4 | None |
| D-06 | D-execution-chain | EXECUTION | token-estimator | true | true | true | 0.007 | 5 | None |
| E-01 | E-adapter-tool-coverage | ADAPTER | SkillTools.createDocxDocument | true | true | true | 462.000 | 4 | None |
| E-02 | E-adapter-tool-coverage | ADAPTER | SkillTools.writeTextFile | true | true | true | 1.537 | 4 | None |
| E-03 | E-adapter-tool-coverage | ADAPTER | SkillTools.listSkillFiles | true | None | false | 0.034 | 10 | IllegalArgumentException: Unknown skill: documents |
| E-04 | E-adapter-tool-coverage | ADAPTER | SkillTools.readSkillFile | true | None | false | 0.009 | 9 | IllegalArgumentException: Unknown skill: documents |
| E-05 | E-adapter-tool-coverage | ADAPTER | SkillTools.runSkillScript | true | None | false | 0.008 | 10 | IllegalArgumentException: Unknown skill: documents |
| E-06 | E-adapter-tool-coverage | ADAPTER | SkillTools.writeTextFile | true | true | true | 0.295 | 6 | None |
| E-07 | E-adapter-tool-coverage | ADAPTER | SkillTools.readSkillFile | true | true | true | 0.145 | 6 | None |
| E-08 | E-adapter-tool-coverage | ADAPTER | SkillTools.runSkillScript | true | None | false | 0.011 | 11 | IllegalArgumentException: Unknown skill: documents |
| F-01 | F-boundary-robustness | BOUNDARY | DefaultSkillDiscoveryService | true | false | false | 0.012 | 7 | None |
| F-02 | F-boundary-robustness | BOUNDARY | DefaultSkillDiscoveryService | true | true | true | 0.082 | 5 | None |
| F-03 | F-boundary-robustness | BOUNDARY | SkillLoader | true | true | true | 0.236 | 6 | None |
| F-04 | F-boundary-robustness | BOUNDARY | SkillLoader | true | true | true | 0.161 | 6 | None |
| F-05 | F-boundary-robustness | BOUNDARY | SkillLoader | true | true | true | 0.107 | 8 | None |
| F-06 | F-boundary-robustness | BOUNDARY | SkillTools.writeTextFile | true | true | true | 0.085 | 6 | None |
| F-07 | F-boundary-robustness | BOUNDARY | SkillTools.readSkillFile | true | true | true | 0.119 | 6 | None |
| G-01 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | None | false | 0.010 | 7 | IllegalArgumentException: Unknown skill: documents |
| G-02 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | None | false | 0.005 | 15 | IllegalArgumentException: Unknown skill: docx-report-generator |
| G-03 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | None | false | 0.005 | 7 | IllegalArgumentException: Unknown skill: pdf |
| G-04 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | None | false | 0.005 | 11 | IllegalArgumentException: Unknown skill: openai-docs |
| G-05 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | None | false | 0.005 | 11 | IllegalArgumentException: Unknown skill: render-deploy |
| G-06 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | None | false | 0.005 | 11 | IllegalArgumentException: Unknown skill: cloudflare-deploy |
| G-07 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | loaded | None | false | 0.005 | 15 | IllegalArgumentException: Unknown skill: security-best-practices |
| G-08 | G-discovery-completeness | DISCOVERY | DefaultSkillDiscoveryService | word-report-generator-1.0.0 | None | false | 0.006 | 18 | IllegalArgumentException: Unknown skill: docx-report-generator |
