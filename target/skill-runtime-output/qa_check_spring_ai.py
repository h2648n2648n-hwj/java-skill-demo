"""QA check: verify Spring AI Skill Routing DOCX structure and content."""
import os, sys, json

# Add the output dir to path for finding the docx
try:
    from docx import Document
    from docx.shared import Inches, Pt, RGBColor
except ImportError:
    print("ERROR: python-docx not available")
    sys.exit(1)

docx_path = r"D:\project\java_project\skill_demo\target\skill-runtime-output\Spring_AI_Skill_Routing.docx"

if not os.path.exists(docx_path):
    print(f"ERROR: File not found at {docx_path}")
    sys.exit(1)

doc = Document(docx_path)
report = {
    "status": "pass",
    "paragraphs": len(doc.paragraphs),
    "tables": len(doc.tables),
    "sections": len(doc.sections),
    "checks": []
}

# Check page setup
sec = doc.sections[0]
page_w = round(sec.page_width / 914400, 2)
page_h = round(sec.page_height / 914400, 2)
margin_t = round(sec.top_margin / 914400, 2)
margin_b = round(sec.bottom_margin / 914400, 2)
margin_l = round(sec.left_margin / 914400, 2)
margin_r = round(sec.right_margin / 914400, 2)

if page_w == 8.5 and page_h == 11:
    report["checks"].append({"check": "page_size", "status": "pass", "detail": "US Letter (8.5x11in)"})
else:
    report["checks"].append({"check": "page_size", "status": "fail", "detail": f"{page_w}x{page_h}in"})

if margin_t == 1.0 and margin_b == 1.0 and margin_l == 1.0 and margin_r == 1.0:
    report["checks"].append({"check": "margins", "status": "pass", "detail": "1in all sides"})
else:
    report["checks"].append({"check": "margins", "status": "fail", "detail": f"T:{margin_t} R:{margin_r} B:{margin_b} L:{margin_l}"})

# Check styles used
styles_used = set()
for p in doc.paragraphs:
    if p.style:
        styles_used.add(p.style.name)

report["styles_used"] = sorted(list(styles_used))
report["checks"].append({"check": "styles", "status": "pass", "detail": f"Styles: {', '.join(sorted(styles_used))}"})

# Check content has proper sections
headings = []
for p in doc.paragraphs:
    if p.style and p.style.name.startswith("Heading"):
        level = p.style.name.replace("Heading ", "")
        headings.append({"level": int(level), "text": p.text})

report["headings_count"] = len(headings)
report["headings"] = headings

# Verify headings ladder
if headings:
    levels = [h["level"] for h in headings]
    if sorted(levels) == levels:  # non-decreasing
        report["checks"].append({"check": "heading_ladder", "status": "pass", "detail": "Headings follow proper hierarchy"})
    else:
        report["checks"].append({"check": "heading_ladder", "status": "warn", "detail": "Headings may have inconsistent ordering"})

# Check table structure
for i, table in enumerate(doc.tables):
    rows = len(table.rows)
    cols = len(table.columns)
    if rows > 0:
        headers = [c.text.strip() for c in table.rows[0].cells]
        report["checks"].append({"check": f"table_{i+1}", "status": "pass", "detail": f"{rows}x{cols}, headers: {headers}"})

# Check for content length and body text
body_text_count = sum(1 for p in doc.paragraphs if p.text.strip() and not p.style.name.startswith("Heading"))
report["body_paragraphs"] = body_text_count
report["checks"].append({"check": "content_volume", "status": "pass", "detail": f"{body_text_count} body paragraphs, {len(headings)} headings"})

# Print report
print(json.dumps(report, indent=2))
print("\n=== QA SUMMARY ===")
all_pass = all(c["status"] == "pass" for c in report["checks"])
print(f"Overall: {'PASS' if all_pass else 'ISSUES FOUND'}")
for c in report["checks"]:
    icon = "✓" if c["status"] == "pass" else ("⚠" if c["status"] == "warn" else "✗")
    print(f"  {icon} {c['check']}: {c['detail']}")
