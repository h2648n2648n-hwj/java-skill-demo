# 量化报告

## 分类指标

- TP: 10
- TN: 2
- FP: 0
- FN: 1
- 准确率: 0.923
- 精确率: 1.000
- 召回率: 0.909

### 指标解释

这一部分只统计路由类用例，也就是测试脚本中用于验证“用户请求是否被正确分配到某个 skill”的用例。它不会统计字段提取、工具调用、边界检查和发现阶段检查。

- `TP`：期望系统激活某个 skill，实际也选中了可接受的 skill。
- `TN`：期望系统不激活 skill，实际也没有选择 skill。
- `FP`：期望系统不激活 skill，但实际误选了某个 skill。
- `FN`：期望系统激活 skill，但实际没有选中，或者选错了 skill。

准确率表示整体判断正确的比例；精确率表示系统一旦选择 skill 时有多少是真的选对；召回率表示所有应该被激活的请求里，系统成功识别出了多少。本次报告中有 1 个 FN，对应 `A-11`：期望 `speech`，实际没有选中 skill。

## Token 消耗

- 最小值: 0
- 最大值: 479
- 平均值: 26.373
- 总量: 1345
- P50: 6.000
- P95: 35.000

### 指标解释

这里的 Token 是测试脚本内部估算出来的数量，不是模型服务接口返回的真实计费 Token。测试脚本会把“输入内容、实际输出、错误信息”拼在一起，再用本地估算方法计算 Token。

- 最小值：单条用例中最小的估算 Token 数。
- 最大值：单条用例中最大的估算 Token 数。
- 平均值：所有用例的平均估算 Token 数。
- 总量：所有用例估算 Token 的总和。
- `P50`：50% 分位数，表示一半用例的 Token 消耗不超过这个值。
- `P95`：95% 分位数，用于观察高消耗用例的 Token 水平。

本次报告中，真实执行链路返回内容较长，因此执行类用例的 Token 会明显高于本地校验类用例。

## 延迟（毫秒）

- 最小值: 0
- 最大值: 286630
- 平均值: 7905.843
- 总耗时: 403198
- P50: 0.000
- P95: 12119.000

### 指标解释

延迟表示每条用例从开始执行到得到结果的耗时，单位是毫秒。测试脚本使用系统纳秒计时，再转换成毫秒写入报告。

- 最小值：单条用例最短耗时。
- 最大值：单条用例最长耗时。
- 平均值：所有用例的平均耗时。
- 总耗时：所有用例耗时的总和。
- `P50`：50% 分位延迟，表示一半用例的耗时不超过这个值。
- `P95`：95% 分位延迟，用于观察慢请求水平。

这个报告混合了真实模型调用和本地逻辑校验。本地校验通常接近 0 毫秒，真实模型调用通常是秒级甚至更久，所以会出现中位数很低、最大值和平均值较高的情况。

## 按 Skill 分组

| 名称 | 执行次数 | 平均 Token | 平均延迟（毫秒） | 成功率 |
|---|---:|---:|---:|---:|
| docx-report-generator | 4 | 129.500 | 19213.838 | 1.000 |
| documents | 3 | 135.000 | 96774.721 | 1.000 |
| pdf | 2 | 6.500 | 963.655 | 1.000 |
| openai-docs | 2 | 9.000 | 922.672 | 1.000 |
| render-deploy | 2 | 9.500 | 1035.380 | 1.000 |
| cloudflare-deploy | 2 | 8.000 | 828.169 | 1.000 |
| security-best-practices | 2 | 9.500 | 1361.046 | 1.000 |
| chatgpt-apps | 1 | 12.000 | 1864.807 | 1.000 |
| figma-generate-library | 1 | 15.000 | 2107.537 | 1.000 |
| notion-knowledge-capture | 1 | 12.000 | 2845.751 | 1.000 |
| None | 31 | 9.613 | 612.320 | 0.935 |

### 指标解释

这一部分按照实际记录到的 skill 进行分组统计。它可以看出每个 skill 被命中的次数、平均 Token、平均延迟和成功率。

- 名称：实际选中或记录到的 skill 名称。
- 执行次数：该 skill 在所有用例中出现的次数。
- 平均 Token：该 skill 分组内用例的平均估算 Token。
- 平均延迟：该 skill 分组内用例的平均耗时。
- 成功率：该 skill 分组内通过用例数除以总用例数。

`None` 不一定表示错误，它表示这条用例没有实际 skill，例如字段提取、结构校验、工具校验、边界测试，以及被正确拒绝的路由请求都可能归入 `None`。

## 按 Adapter 分组

| 名称 | 执行次数 | 平均 Token | 平均延迟（毫秒） | 成功率 |
|---|---:|---:|---:|---:|
| spring-ai-openai-compatible | 13 | 11.231 | 2356.883 | 0.923 |
| java-regex-field-extractor | 5 | 14.800 | 0.129 | 1.000 |
| decision-schema-validator | 4 | 19.000 | 0.088 | 1.000 |
| spring-ai-openai-compatible-tools | 3 | 301.000 | 124181.447 | 1.000 |
| spring-ai-prompt-builder | 1 | 6.000 | 0.285 | 1.000 |
| latency-recorder | 1 | 4.000 | 0.005 | 1.000 |
| token-estimator | 1 | 5.000 | 0.011 | 1.000 |
| SkillTools.createDocxDocument | 1 | 4.000 | 11.482 | 1.000 |
| SkillTools.writeTextFile | 3 | 5.333 | 0.328 | 1.000 |
| SkillTools.listSkillFiles | 1 | 5.000 | 0.292 | 1.000 |
| SkillTools.readSkillFile | 3 | 5.333 | 0.271 | 1.000 |
| SkillTools.runSkillScript | 2 | 5.500 | 0.252 | 0.500 |
| DefaultSkillDiscoveryService | 10 | 5.900 | 0.076 | 1.000 |
| SkillLoader | 3 | 6.667 | 0.474 | 1.000 |

### 指标解释

这一部分按照测试脚本中给每条用例标记的 Adapter 分类进行统计。这里的 Adapter 更多是评测分类标签，不完全等同于生产代码中的类名。

- `spring-ai-openai-compatible`：真实模型路由用例。
- `spring-ai-openai-compatible-tools`：真实执行链路用例。
- `java-regex-field-extractor`：本地字段提取用例。
- `decision-schema-validator`：本地结构校验和异常容错用例。
- `SkillTools.*`：直接调用工具方法的覆盖用例。
- `DefaultSkillDiscoveryService` 和 `SkillLoader`：发现阶段和边界场景用例。

通过这个分组可以看出不同链路的稳定性和耗时差异。比如真实模型相关 Adapter 的延迟通常高于本地逻辑 Adapter。

## 逐次执行明细

| 用例 | 维度 | Skill | Adapter | 是否通过 | Token | 延迟（毫秒） | 期望结果 | 实际结果 | 错误信息 |
|---|---|---|---|---:|---:|---:|---|---|---|
| A-01 | A-routing-accuracy | docx-report-generator | spring-ai-openai-compatible | true | 14 | 3059.905 | [docx-report-generator] | docx-report-generator | None |
| A-02 | A-routing-accuracy | documents | spring-ai-openai-compatible | true | 14 | 3693.580 | [documents, docx-report-generator] | documents | None |
| A-03 | A-routing-accuracy | pdf | spring-ai-openai-compatible | true | 11 | 1927.248 | [pdf] | pdf | None |
| A-04 | A-routing-accuracy | openai-docs | spring-ai-openai-compatible | true | 14 | 1845.287 | [openai-docs] | openai-docs | None |
| A-05 | A-routing-accuracy | render-deploy | spring-ai-openai-compatible | true | 15 | 2070.694 | [render-deploy] | render-deploy | None |
| A-06 | A-routing-accuracy | cloudflare-deploy | spring-ai-openai-compatible | true | 12 | 1656.277 | [cloudflare-deploy] | cloudflare-deploy | None |
| A-07 | A-routing-accuracy | security-best-practices | spring-ai-openai-compatible | true | 13 | 2722.031 | [security-best-practices] | security-best-practices | None |
| A-08 | A-routing-accuracy | chatgpt-apps | spring-ai-openai-compatible | true | 12 | 1864.807 | [chatgpt-apps] | chatgpt-apps | None |
| A-09 | A-routing-accuracy | figma-generate-library | spring-ai-openai-compatible | true | 15 | 2107.537 | [figma-generate-library, figma-use] | figma-generate-library | None |
| A-10 | A-routing-accuracy | notion-knowledge-capture | spring-ai-openai-compatible | true | 12 | 2845.751 | [notion-knowledge-capture, notion-research-documentation] | notion-knowledge-capture | None |
| A-11 | A-routing-accuracy | None | spring-ai-openai-compatible | false | 6 | 2075.777 | [speech] | None | None |
| A-12 | A-routing-accuracy | None | spring-ai-openai-compatible | true | 8 | 3198.348 | [] | None | None |
| A-13 | A-routing-accuracy | None | spring-ai-openai-compatible | true | 0 | 1572.234 | [] | None | None |
| B-01 | B-field-extraction | None | java-regex-field-extractor | true | 16 | 0.398 | {filename=weekly-report.docx} | {filename=weekly-report.docx} | None |
| B-02 | B-field-extraction | None | java-regex-field-extractor | true | 18 | 0.090 | {template_name=product-review} | {template_name=product-review} | None |
| B-03 | B-field-extraction | None | java-regex-field-extractor | true | 29 | 0.117 | {template_name=q2-summary, filename=q2-summary.docx} | {filename=q2-summary.docx, template_name=q2-summary} | None |
| B-04 | B-field-extraction | None | java-regex-field-extractor | true | 2 | 0.003 | {} | {} | None |
| B-05 | B-field-extraction | None | java-regex-field-extractor | true | 9 | 0.039 | {} | {} | None |
| C-01 | C-llm-error-tolerance | None | decision-schema-validator | true | 27 | 0.106 | schema rejected | Unknown skill: missing-skill | None |
| C-02 | C-llm-error-tolerance | None | decision-schema-validator | true | 24 | 0.007 | schema rejected | should_call must be boolean | None |
| C-03 | C-llm-error-tolerance | None | decision-schema-validator | true | 20 | 0.006 | schema rejected | confidence must be number | None |
| C-04 | C-llm-error-tolerance | None | decision-schema-validator | true | 5 | 0.236 | JsonProcessingException | JsonParseException | None |
| D-01 | D-execution-chain | docx-report-generator | spring-ai-openai-compatible-tools | true | 479 | 73795.359 | non-empty execution response | 瀹炰範鎶ュ憡宸叉垚鍔熺敓鎴愶紒馃搫<br><br>**鏂囦欢璺緞锛?* `internship_report.docx`<br><br>鎶ュ憡鍖呭惈浠ヤ笅瀹屾暣鍐呭锛?br><br>\| 绔犺妭 \| 鍐呭姒傝 \|<br>\|------\|----------\|<br>\| 馃搵 **鍩烘湰淇℃伅** \| 濮撳悕銆佸鍙枫€佷笓涓氥€佸疄涔犲崟浣嶃€佸矖浣嶃€佹椂闂淬€佸甯?\|<br>\| 馃幆 **瀹炰範鐩殑涓庤儗鏅?* \| 鐞嗚搴旂敤浜庡疄璺碉紝鎻愬崌宸ョ▼鑳藉姏鍜岃亴涓氱礌鍏?\|<br>\| 馃彚 **瀹炰範鍗曚綅绠€浠?* \| 鏄熻景绉戞妧鏈夐檺鍏徃姒傚喌 \|<br>\| 馃洜锔?**瀹炰範涓昏鍐呭** \| 6涓伐浣滄ā鍧楋細鐢ㄦ埛鏈嶅姟銆佽鍗曚紭鍖栥€佸晢鍝佹悳绱€佹秷鎭帹閫併€佸崟鍏冩祴璇曘€丄PI鏂囨。 \|<br>\| 馃挕 **鍏抽敭鎶€鏈疄璺?* \| 寰湇鍔℃灦鏋勮璁°€佹暟鎹簱鎬ц兘浼樺寲锛?00ms鈫?20ms锛夈€佸叏鏂囨绱㈤泦鎴?\|<br>\| 馃搳 **瀹炰範鎴愭灉鏁版嵁** \| 鎺ュ彛24涓€佷唬鐮?2,000琛屻€佹祴璇?56涓?瑕嗙洊鐜?2%)銆丅ug淇47涓?\|<br>\| 鉁?**瀹炰範蹇冨緱** \| 鎶€鏈兘鍔涖€佸伐绋嬫€濈淮銆佸洟闃熷崗浣溿€侀棶棰樿В鍐炽€佽亴涓氱礌鍏讳簲澶ф彁鍗?\|<br>\| 馃敪 **鎬荤粨涓庡睍鏈?* \| 鏈潵瀛︿範璁″垝涓庤亴涓氱洰鏍?\|<br>\| 馃檹 **鑷磋阿** \| 鎰熻阿鍏徃銆佸甯堝拰鍥㈤槦 \|<br><br>鏂囨。宸蹭娇鐢?Word 鏍煎紡鍒涘缓锛屽寘鍚爣棰樸€佹鏂囨钀姐€佸垪琛ㄧ瓑鍩虹鎺掔増銆傚闇€杩涗竴姝ョ編鍖栵紙娣诲姞鐩綍銆佽〃鏍笺€佸浘琛ㄦ垨鑷畾涔夋牱寮忥級锛屾垜杩樺彲浠ョ户缁娇鐢ㄨ剼鏈伐鍏疯繘琛屽寮哄鐞嗐€傛槸鍚﹂渶瑕佹垜杩涗竴姝ヤ紭鍖栵紵 | None |
| D-02 | D-execution-chain | documents | spring-ai-openai-compatible-tools | true | 389 | 286630.341 | non-empty execution response | The document has been created successfully. Here's a summary of what was delivered:<br><br>---<br><br>## 鉁?Spring AI Skill Routing 鈥?DOCX Delivered<br><br>**File:** `Spring_AI_Skill_Routing.docx`<br><br>**Design Preset:** `standard_business_brief` 鈥?Calibri-based, professional typography with blue heading hierarchy (#2E74B5), clean body spacing, and consistent table styling.<br><br>**Document Structure:**<br><br>\| Section \| Content \|<br>\|---\|---\|<br>\| **Title** \| Spring AI Skill Routing (22pt bold blue, with italic subtitle) \|<br>\| **Overview** \| What skill routing is and why it matters in Spring AI \|<br>\| **Core Concepts** \| Skill, Router, and Routing Policy explained \|<br>\| **Routing Strategies** \| Table comparing 5 strategies (content-based, tag-based, model-capability, fallback chain, hybrid) \|<br>\| **Configuration Approach** \| How to set up skills, routers, and dispatch in Spring AI \|<br>\| **Benefits** \| Modularity, extensibility, observability, resilience, performance \|<br>\| **Design Considerations** \| Latency, ambiguity, contract evolution, observability investment \|<br>\| **Summary** \| Key takeaways \|<br><br>**Quality Checks:**<br>- 鉁?Page setup: US Letter, 1in margins<br>- 鉁?Table geometry: verified 鈥?`tblW`, `tblInd`, `tblGrid`, and `tcW` all match (9360 DXA full-width)<br>- 鉁?Content: 7 major sections, 1 comparison table, bullet lists<br>- 鈿狅笍 Visual render QA was not completed (LibreOffice/`soffice` unavailable in this environment)<br><br>The document provides a concise yet thorough technical brief on Spring AI skill routing 鈥?covering the what, why, and how of intelligent request dispatch within the Spring AI framework. | None |
| D-03 | D-execution-chain | None | spring-ai-openai-compatible-tools | true | 35 | 12118.641 | non-empty execution response | No suitable skill found. Reason: No skill in catalog matches the task of writing a generic text draft about runtime evaluation metrics. | None |
| D-04 | D-execution-chain | None | spring-ai-prompt-builder | true | 6 | 0.285 | true | true | None |
| D-05 | D-execution-chain | None | latency-recorder | true | 4 | 0.005 | true | true | None |
| D-06 | D-execution-chain | None | token-estimator | true | 5 | 0.011 | true | true | None |
| E-01 | E-adapter-tool-coverage | None | SkillTools.createDocxDocument | true | 4 | 11.482 | true | true | None |
| E-02 | E-adapter-tool-coverage | None | SkillTools.writeTextFile | true | 4 | 0.663 | true | true | None |
| E-03 | E-adapter-tool-coverage | None | SkillTools.listSkillFiles | true | 5 | 0.292 | true | true | None |
| E-04 | E-adapter-tool-coverage | None | SkillTools.readSkillFile | true | 4 | 0.604 | true | true | None |
| E-05 | E-adapter-tool-coverage | None | SkillTools.runSkillScript | true | 5 | 0.364 | true | true | None |
| E-06 | E-adapter-tool-coverage | None | SkillTools.writeTextFile | true | 6 | 0.223 | true | true | None |
| E-07 | E-adapter-tool-coverage | None | SkillTools.readSkillFile | true | 6 | 0.099 | true | true | None |
| E-08 | E-adapter-tool-coverage | None | SkillTools.runSkillScript | false | 6 | 0.140 | true | false | None |
| F-01 | F-boundary-robustness | None | DefaultSkillDiscoveryService | true | 7 | 0.018 | true | true | None |
| F-02 | F-boundary-robustness | None | DefaultSkillDiscoveryService | true | 5 | 0.105 | true | true | None |
| F-03 | F-boundary-robustness | None | SkillLoader | true | 6 | 0.141 | true | true | None |
| F-04 | F-boundary-robustness | None | SkillLoader | true | 6 | 1.144 | true | true | None |
| F-05 | F-boundary-robustness | None | SkillLoader | true | 8 | 0.138 | true | true | None |
| F-06 | F-boundary-robustness | None | SkillTools.writeTextFile | true | 6 | 0.099 | true | true | None |
| F-07 | F-boundary-robustness | None | SkillTools.readSkillFile | true | 6 | 0.111 | true | true | None |
| G-01 | G-discovery-completeness | documents | DefaultSkillDiscoveryService | true | 2 | 0.241 | loaded | loaded | None |
| G-02 | G-discovery-completeness | docx-report-generator | DefaultSkillDiscoveryService | true | 6 | 0.077 | loaded | loaded | None |
| G-03 | G-discovery-completeness | pdf | DefaultSkillDiscoveryService | true | 2 | 0.062 | loaded | loaded | None |
| G-04 | G-discovery-completeness | openai-docs | DefaultSkillDiscoveryService | true | 4 | 0.056 | loaded | loaded | None |
| G-05 | G-discovery-completeness | render-deploy | DefaultSkillDiscoveryService | true | 4 | 0.066 | loaded | loaded | None |
| G-06 | G-discovery-completeness | cloudflare-deploy | DefaultSkillDiscoveryService | true | 4 | 0.061 | loaded | loaded | None |
| G-07 | G-discovery-completeness | security-best-practices | DefaultSkillDiscoveryService | true | 6 | 0.061 | loaded | loaded | None |
| G-08 | G-discovery-completeness | docx-report-generator | DefaultSkillDiscoveryService | true | 19 | 0.010 | word-report-generator-1.0.0 | word-report-generator-1.0.0 | None |

### 指标解释

这一部分是逐条用例明细，每一行对应一次真实执行记录。它用于定位具体是哪条用例失败、实际选中了哪个 skill、耗时多少、输出是什么。

- 用例：测试用例编号。
- 维度：该用例所属测试维度。
- Skill：实际记录到的 skill。
- Adapter：该用例所属的评测链路或工具分类。
- 是否通过：该用例的断言结果。
- Token：该用例的估算 Token。
- 延迟：该用例耗时。
- 期望结果：测试脚本中定义的期望值。
- 实际结果：系统实际返回的结果。
- 错误信息：执行异常信息，没有异常时为 `None`。

从当前报告看，`A-11` 是路由失败用例，期望 `speech`，实际为 `None`；`E-08` 是工具覆盖失败用例，期望结果为 `true`，实际为 `false`。另外，`D-03` 虽然通过，但实际返回了“没有合适 skill”的信息，说明执行链路断言目前只检查了返回内容非空，后续可以进一步收紧。

