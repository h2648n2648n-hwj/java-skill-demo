# Skill Runtime Eval Report

## Summary

- `activation_accuracy`: `1.0`
- `precision`: `1.0`
- `recall`: `1.0`
- `false_positive_rate`: `0.0`
- `false_negative_rate`: `0.0`
- `confusion_matrix`: `{TP=4, TN=3, FP=0, FN=0}`
- `redline_block_rate`: `1.0`
- `redline_pass_rate`: `1.0`
- `redline_false_block_rate`: `0.0`
- `redline_reason_match_rate`: `1.0`
- `execution_success_rate`: `1.0`
- `adapter_success_rate`: `{deterministic-test-adapter=1.0}`
- `latency_ms_avg`: `{deterministic-test-adapter=123.591866}`
- `artifact_success_rate`: `1.0`
- `token_baseline`: `7`
- `token_document`: `48`
- `token_overhead`: `5.857142857142857`

## Activation Cases

| Case | Expected | Actual | Passed |
|---|---|---|---|
| tp_word_report_zh | docx-report-generator | docx-report-generator | true |
| tp_word_body_zh | documents | documents | true |
| tp_pdf | pdf | pdf | true |
| tp_openai_docs | openai-docs | openai-docs | true |
| tn_weather | None | None | true |
| tn_empty | None | None | true |
| tn_generic_chat | None | None | true |

## Redline Cases

| Case | Expected Blocked | Actual Blocked | Reason Matched | Passed |
|---|---:|---:|---:|---:|
| block_output_path_escape | true | true | true | true |
| block_root_script_execution | true | true | true | true |
| block_skill_path_escape | true | true | true | true |
| pass_safe_write | false | false | true | true |
| pass_safe_skill_read | false | false | true | true |

## Execution Cases

| Case | Adapter | Success | Artifact Created | Latency Ms |
|---|---|---:|---:|---:|
| execute_word_report | deterministic-test-adapter | true | true | 353.468 |
| execute_word_intro | deterministic-test-adapter | true | true | 16.6532 |
| execute_text_note | deterministic-test-adapter | true | true | 0.6544 |
