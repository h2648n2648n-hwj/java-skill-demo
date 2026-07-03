#!/usr/bin/env python3
"""Create a concise Word document about Spring AI Skill Routing."""

from docx import Document
from docx.shared import Pt, Inches, Cm, RGBColor, Twips
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn, nsdecls
import os

OUTPUT = "Spring_AI_Skill_Routing.docx"

# ── Preset: standard_business_brief ──────────────────────────────────
# Page geometry
PAGE_W = 9360  # DXA (6.5 in)
MARGIN = 1440  # DXA (1 in)

# Typography
BASE_FONT = "Calibri"
BASE_SIZE = 11  # pt

# Heading tokens
H1_SIZE = 16
H1_COLOR = "2E74B5"
H1_BEFORE = 320   # 16pt * 20
H1_AFTER = 160    # 8pt * 20

H2_SIZE = 13
H2_COLOR = "2E74B5"
H2_BEFORE = 240   # 12pt
H2_AFTER = 120    # 6pt

H3_SIZE = 12
H3_COLOR = "1F4D78"
H3_BEFORE = 160   # 8pt
H3_AFTER = 80     # 4pt

BODY_AFTER = 120  # 6pt
BODY_LINE = 264   # 1.10 x 240

# Table tokens
TABLE_W = 9360
TABLE_INDENT = 120
CELL_MARGINS = {"top": 80, "bottom": 80, "start": 120, "end": 120}
HEADER_FILL = "F2F4F7"

# ── Helpers ──────────────────────────────────────────────────────────

def set_run_font(run, name=BASE_FONT, size=BASE_SIZE, color=None, bold=None, italic=None):
    run.font.name = name
    run.font.size = Pt(size)
    if color:
        run.font.color.rgb = RGBColor.from_string(color)
    if bold is not None:
        run.bold = bold
    if italic is not None:
        run.italic = italic

def set_paragraph_spacing(paragraph, before=0, after=BODY_AFTER, line=BODY_LINE):
    pf = paragraph.paragraph_format
    pf.space_before = Pt(before / 20) if before else Pt(0)
    pf.space_after = Pt(after / 20) if after else Pt(0)
    pf.line_spacing = line / 240

def add_heading(doc, text, level=1):
    """Add a paragraph with heading-level formatting (not built-in style)."""
    p = doc.add_paragraph()
    run = p.add_run(text)
    if level == 1:
        set_run_font(run, size=H1_SIZE, color=H1_COLOR, bold=True)
        set_paragraph_spacing(p, before=H1_BEFORE, after=H1_AFTER, line=BODY_LINE)
    elif level == 2:
        set_run_font(run, size=H2_SIZE, color=H2_COLOR, bold=True)
        set_paragraph_spacing(p, before=H2_BEFORE, after=H2_AFTER, line=BODY_LINE)
    elif level == 3:
        set_run_font(run, size=H3_SIZE, color=H3_COLOR, bold=True)
        set_paragraph_spacing(p, before=H3_BEFORE, after=H3_AFTER, line=BODY_LINE)
    p.style = doc.styles['Normal']
    return p

def add_body(doc, text):
    p = doc.add_paragraph(text)
    p.style = doc.styles['Normal']
    set_paragraph_spacing(p, before=0, after=BODY_AFTER, line=BODY_LINE)
    for run in p.runs:
        set_run_font(run)
    return p

def add_bullet(doc, text, level=0):
    """Add a bullet paragraph with proper indentation."""
    p = doc.add_paragraph(text)
    p.style = doc.styles['Normal']
    set_paragraph_spacing(p, before=0, after=80, line=BODY_LINE)
    pf = p.paragraph_format
    indent = 0.25 + 0.25 * level
    pf.left_indent = Inches(0.25 + 0.25 * level)
    pf.first_line_indent = Inches(-0.25)
    for run in p.runs:
        set_run_font(run)
    return p

def add_bold_body(doc, bold_part, normal_part):
    p = doc.add_paragraph()
    p.style = doc.styles['Normal']
    set_paragraph_spacing(p, before=0, after=BODY_AFTER, line=BODY_LINE)
    run_b = p.add_run(bold_part)
    set_run_font(run_b, bold=True)
    run_n = p.add_run(normal_part)
    set_run_font(run_n)
    return p

def set_cell_text(cell, text, bold=False, size=BASE_SIZE, color=None, alignment=None):
    cell.text = ""
    p = cell.paragraphs[0]
    run = p.add_run(text)
    set_run_font(run, size=size, bold=bold, color=color)
    if alignment:
        p.alignment = alignment
    set_paragraph_spacing(p, before=0, after=0, line=240)  # single

def shade_cell(cell, color_hex):
    shading = OxmlElement('w:shd')
    shading.set(qn('w:fill'), color_hex)
    shading.set(qn('w:val'), 'clear')
    cell._tc.get_or_add_tcPr().append(shading)

def set_cell_margins(cell, margins=None):
    if margins is None:
        margins = CELL_MARGINS
    tc_pr = cell._tc.get_or_add_tcPr()
    tc_mar = OxmlElement('w:tcMar')
    for side, val in margins.items():
        m = OxmlElement(f'w:{side}')
        m.set(qn('w:w'), str(val))
        m.set(qn('w:type'), 'dxa')
        tc_mar.append(m)
    tc_pr.append(tc_mar)

def apply_table_geometry(table, col_widths_dxa, indent_dxa=TABLE_INDENT):
    table.autofit = False
    table.alignment = WD_TABLE_ALIGNMENT.LEFT
    tbl = table._tbl

    # tblW
    tbl_pr = tbl.tblPr
    tblW = OxmlElement('w:tblW')
    tblW.set(qn('w:type'), 'dxa')
    tblW.set(qn('w:w'), str(int(sum(col_widths_dxa))))
    tbl_pr.append(tblW)

    # tblInd
    tblInd = OxmlElement('w:tblInd')
    tblInd.set(qn('w:type'), 'dxa')
    tblInd.set(qn('w:w'), str(indent_dxa))
    tbl_pr.append(tblInd)

    # tblLayout fixed
    layout = OxmlElement('w:tblLayout')
    layout.set(qn('w:type'), 'fixed')
    tbl_pr.append(layout)

    # tblGrid
    grid = tbl.tblGrid
    for child in list(grid):
        grid.remove(child)
    for w in col_widths_dxa:
        gc = OxmlElement('w:gridCol')
        gc.set(qn('w:w'), str(int(w)))
        grid.append(gc)

    # Cell widths
    for row in table.rows:
        for i, cell in enumerate(row.cells):
            cell.width = Twips(col_widths_dxa[i])
            tc_pr = cell._tc.get_or_add_tcPr()
            tcW = OxmlElement('w:tcW')
            tcW.set(qn('w:type'), 'dxa')
            tcW.set(qn('w:w'), str(int(col_widths_dxa[i])))
            tc_pr.append(tcW)
            set_cell_margins(cell)

# ── Build Document ──────────────────────────────────────────────────

doc = Document()

# Page setup
section = doc.sections[0]
section.page_width = Inches(8.5)
section.page_height = Inches(11)
section.top_margin = Inches(1)
section.bottom_margin = Inches(1)
section.left_margin = Inches(1)
section.right_margin = Inches(1)

# Remove the empty default paragraph
if doc.paragraphs:
    p = doc.paragraphs[0]
    p._element.getparent().remove(p._element)

# ── Normal style definition ──
style = doc.styles['Normal']
font = style.font
font.name = BASE_FONT
font.size = Pt(BASE_SIZE)
font.color.rgb = RGBColor(0, 0, 0)
pf = style.paragraph_format
pf.space_before = Pt(0)
pf.space_after = Pt(6)
pf.line_spacing = 1.10

# ── Title ──
title = doc.add_paragraph()
title.alignment = WD_ALIGN_PARAGRAPH.LEFT
run = title.add_run("Spring AI Skill Routing")
set_run_font(run, size=26, bold=False)
run.font.color.rgb = RGBColor(0, 0, 0)
set_paragraph_spacing(title, before=0, after=120, line=240)

# ── Subtitle / Lead ──
subtitle = doc.add_paragraph()
run = subtitle.add_run("A Concise Overview of Intelligent Request Dispatch in Spring AI")
set_run_font(run, size=12, color="555555", bold=False)
set_paragraph_spacing(subtitle, before=0, after=160, line=240)

# ── Section 1: Introduction ──
add_heading(doc, "Introduction", level=1)
add_body(doc,
    "Spring AI Skill Routing is a pattern for directing natural-language or structured requests "
    "to the most appropriate AI-driven handler — a \"skill\" — within a Spring-based application. "
    "Rather than hard-coding a single AI invocation path, a skill router introspects the incoming "
    "request, evaluates available skills, and dispatches execution to the best-matched handler."
)
add_body(doc,
    "This approach enables modular, extensible AI architectures where new capabilities can be added "
    "without rewriting existing orchestration logic. It is especially valuable in enterprise "
    "applications where different tasks require different models, prompts, tools, or vector stores."
)

# ── Section 2: Core Concepts ──
add_heading(doc, "Core Concepts", level=1)

add_heading(doc, "Skill", level=2)
add_body(doc,
    "A skill is a self-contained unit of AI functionality. Each skill defines a name, description, "
    "input schema, and an execution handler that may invoke a language model, run a retrieval chain, "
    "execute a tool, or call an external API. Skills are registered with the routing infrastructure "
    "at application startup."
)

add_heading(doc, "Router", level=2)
add_body(doc,
    "The router is the central dispatch component. It receives a request — typically a user query or "
    "a structured intent — and matches it against the set of registered skills. Matching may use "
    "semantic similarity, keyword patterns, LLM-based classification, or a combination of strategies."
)

add_heading(doc, "Routing Strategies", level=2)
add_bullet(doc, "Semantic routing — embed the request and find the skill with the closest description vector.")
add_bullet(doc, "LLM-based routing — ask a language model to select the appropriate skill from a candidate list.")
add_bullet(doc, "Keyword / regex routing — match predefined patterns to fixed skills.")
add_bullet(doc, "Hybrid routing — combine multiple strategies with fallback logic.")

# ── Section 3: Comparison Table ──
add_heading(doc, "Routing Strategy Comparison", level=1)

table = doc.add_table(rows=5, cols=4)
table.autofit = False
table.alignment = WD_TABLE_ALIGNMENT.LEFT

col_widths = [1800, 2700, 2700, 2160]  # total = 9360

headers = ["Strategy", "Strength", "Limitation", "Best For"]
data = [
    ["Semantic", "Handles varied phrasing; no manual rules", "Requires embedding model; higher latency", "Open-ended Q&A, discovery"],
    ["LLM-based", "Flexible reasoning; context-aware", "Token cost; slower; model-dependent", "Complex or ambiguous intents"],
    ["Keyword / Regex", "Fast; deterministic; low cost", "Brittle; maintenance overhead", "Well-defined commands, buttons"],
    ["Hybrid", "Balances speed and accuracy; fallback", "More moving parts; tuning effort", "Production systems with mixed traffic"],
]

for j, h in enumerate(headers):
    cell = table.rows[0].cells[j]
    set_cell_text(cell, h, bold=True, size=11, color="FFFFFF")
    shade_cell(cell, "2E74B5")
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER

for i, row_data in enumerate(data, start=1):
    for j, val in enumerate(row_data):
        cell = table.rows[i].cells[j]
        set_cell_text(cell, val, bold=(j == 0))
        cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER
        if i % 2 == 0:
            shade_cell(cell, "F2F4F7")

apply_table_geometry(table, col_widths)

# ── Section 4: Implementation Sketch ──
add_heading(doc, "Implementation Overview", level=1)
add_body(doc,
    "In a typical Spring AI application, skill routing is wired through a central dispatcher bean. "
    "Skills are registered as Spring beans implementing a common interface. The router, itself a bean, "
    "collects all skill beans at injection time and exposes a dispatch method."
)

add_bold_body(doc, "Key steps: ",
    "Define a Skill interface with canHandle(request) and execute(request) methods. "
    "Register concrete skill beans. Configure the router with a matching strategy. "
    "Expose a REST or message endpoint that delegates to the router."
)

add_body(doc,
    "Spring AI's auto-configuration and model abstraction layer make it straightforward to "
    "integrate skill routing with existing Spring Boot applications, supporting OpenAI, "
    "Azure OpenAI, Ollama, and other model providers interchangeably."
)

# ── Section 5: Benefits ──
add_heading(doc, "Key Benefits", level=1)
add_bullet(doc, "Modularity — each skill is independently developed, tested, and deployed.")
add_bullet(doc, "Extensibility — new skills plug in without altering existing dispatch logic.")
add_bullet(doc, "Observability — routing decisions can be logged, traced, and monitored per skill.")
add_bullet(doc, "Cost control — expensive LLM paths are reserved for requests that actually need them.")
add_bullet(doc, "Graceful degradation — fallback skills handle unmatched requests instead of failing.")

# ── Section 6: Conclusion ──
add_heading(doc, "Conclusion", level=1)
add_body(doc,
    "Spring AI Skill Routing provides a clean, extensible foundation for building AI-powered "
    "applications that can handle diverse request types through specialized handlers. By separating "
    "routing logic from skill execution, teams can evolve their AI capabilities incrementally while "
    "maintaining a consistent entry point for clients."
)

# ── Save ──
doc.save(OUTPUT)
print(f"Created {OUTPUT}")
