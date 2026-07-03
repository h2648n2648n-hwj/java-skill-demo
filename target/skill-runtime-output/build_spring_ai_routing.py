"""
Build Spring AI Skill Routing DOCX with proper styles.
Preset: standard_business_brief
Writes to the runtime output directory.
"""
import os, sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

try:
    from docx import Document
    from docx.shared import Pt, Inches, RGBColor
    from docx.enum.text import WD_ALIGN_PARAGRAPH
    from docx.enum.table import WD_TABLE_ALIGNMENT
    from docx.oxml.ns import qn, nsdecls
    from docx.oxml import parse_xml
except ImportError:
    print("ERROR: python-docx not available")
    sys.exit(1)

# ── Helpers ──────────────────────────────────────────────────────────────

def set_run(run, name="Calibri", size=None, color=None, bold=None, italic=None):
    if name:
        run.font.name = name
        rPr = run._r.get_or_add_rPr()
        rFonts = rPr.find(qn('w:rFonts'))
        if rFonts is None:
            rFonts = parse_xml(f'<w:rFonts {nsdecls("w")} w:ascii="{name}" w:hAnsi="{name}" w:cs="{name}"/>')
            rPr.insert(0, rFonts)
        else:
            for a in ("ascii", "hAnsi", "cs"):
                rFonts.set(qn(f'w:{a}'), name)
    if size:
        run.font.size = Pt(size)
    if color:
        h = color.lstrip("#")
        run.font.color.rgb = RGBColor(*tuple(int(h[i:i+2], 16) for i in (0, 2, 4)))
    if bold is not None:
        run.font.bold = bold
    if italic is not None:
        run.font.italic = italic

def spacing(p, before=0, after=0, line=None):
    pf = p.paragraph_format
    if before is not None:
        pf.space_before = Pt(before)
    if after is not None:
        pf.space_after = Pt(after)
    if line:
        pf.line_spacing = line

def body(doc, text, before=0, after=6):
    p = doc.add_paragraph(text)
    p.style = doc.styles["Normal"]
    spacing(p, before=before, after=after, line=1.10)
    for r in p.runs:
        set_run(r, size=11)
    return p

def heading(doc, text, level=1):
    h = doc.add_heading(text, level=level)
    if level == 1:
        spacing(h, before=16, after=8)
        for r in h.runs:
            set_run(r, size=16, color="#2E74B5", bold=True)
    elif level == 2:
        spacing(h, before=12, after=6)
        for r in h.runs:
            set_run(r, size=13, color="#2E74B5", bold=True)
    elif level == 3:
        spacing(h, before=8, after=4)
        for r in h.runs:
            set_run(r, size=12, color="#1F4D78", bold=True)
    return h

def bullet(doc, text):
    p = doc.add_paragraph(text, style="List Bullet")
    spacing(p, before=0, after=8, line=1.167)
    for r in p.runs:
        set_run(r, size=11)
    return p

def set_cell_margins(cell, t=80, b=80, s=120, e=120):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    tcMar = parse_xml(
        f'<w:tcMar {nsdecls("w")}>'
        f'<w:top w:w="{t}" w:type="dxa"/>'
        f'<w:bottom w:w="{b}" w:type="dxa"/>'
        f'<w:start w:w="{s}" w:type="dxa"/>'
        f'<w:end w:w="{e}" w:type="dxa"/>'
        f'</w:tcMar>'
    )
    tcPr.append(tcMar)

def cell_vcenter(cell):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    tcPr.append(parse_xml(f'<w:vAlign {nsdecls("w")} w:val="center"/>'))

def table_styled(doc, headers, rows, cw):
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.LEFT
    table.autofit = False

    tbl = table._tbl
    tblPr = tbl.tblPr if tbl.tblPr is not None else parse_xml(f'<w:tblPr {nsdecls("w")}/>')
    for child in list(tblPr):
        if child.tag in (qn("w:tblW"), qn("w:tblInd")):
            tblPr.remove(child)
    tblPr.append(parse_xml(f'<w:tblW {nsdecls("w")} w:w="9360" w:type="dxa"/>'))
    tblPr.append(parse_xml(f'<w:tblInd {nsdecls("w")} w:w="120" w:type="dxa"/>'))

    tblPr.append(parse_xml(
        f'<w:tblBorders {nsdecls("w")}>'
        '<w:top w:val="single" w:sz="4" w:space="0" w:color="B0B0B0"/>'
        '<w:left w:val="single" w:sz="4" w:space="0" w:color="B0B0B0"/>'
        '<w:bottom w:val="single" w:sz="4" w:space="0" w:color="B0B0B0"/>'
        '<w:right w:val="single" w:sz="4" w:space="0" w:color="B0B0B0"/>'
        '<w:insideH w:val="single" w:sz="4" w:space="0" w:color="B0B0B0"/>'
        '<w:insideV w:val="single" w:sz="4" w:space="0" w:color="B0B0B0"/>'
        '</w:tblBorders>'
    ))

    tblGrid = parse_xml(f'<w:tblGrid {nsdecls("w")}/>')
    for w in cw:
        tblGrid.append(parse_xml(f'<w:gridCol {nsdecls("w")} w:w="{w}"/>'))
    eg = tbl.find(qn("w:tblGrid"))
    if eg is not None:
        tbl.remove(eg)
    tbl.insert(0, tblGrid)

    hfill = "F2F4F7"
    for j, ht in enumerate(headers):
        cell = table.cell(0, j)
        cell.text = ""
        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.LEFT
        r = p.add_run(ht)
        set_run(r, size=10, bold=True, color="#1F3A5F")
        spacing(p, before=0, after=0, line=1.0)
        set_cell_margins(cell)
        cell_vcenter(cell)
        cell._tc.get_or_add_tcPr().append(parse_xml(f'<w:shd {nsdecls("w")} w:fill="{hfill}" w:val="clear"/>'))

    for i, row in enumerate(rows):
        for j, txt in enumerate(row):
            cell = table.cell(i + 1, j)
            cell.text = ""
            p = cell.paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            r = p.add_run(str(txt))
            set_run(r, size=10)
            spacing(p, before=0, after=0, line=1.0)
            set_cell_margins(cell)
            cell_vcenter(cell)

    return table


# ══════════════════════════════════════════════════════════════════════════
#  BUILD
# ══════════════════════════════════════════════════════════════════════════

doc = Document()

# Page
sec = doc.sections[0]
sec.page_width = Inches(8.5)
sec.page_height = Inches(11)
sec.top_margin = Inches(1.0)
sec.bottom_margin = Inches(1.0)
sec.left_margin = Inches(1.0)
sec.right_margin = Inches(1.0)
sec.header_distance = Inches(0.492)
sec.footer_distance = Inches(0.492)

# Normal
n = doc.styles["Normal"]
n.font.name = "Calibri"
n.font.size = Pt(11)
n.paragraph_format.space_before = Pt(0)
n.paragraph_format.space_after = Pt(6)
n.paragraph_format.line_spacing = 1.10

# ── Title ────────────────────────────────────────────────────────────────
t = doc.add_paragraph()
spacing(t, before=0, after=6)
t.alignment = WD_ALIGN_PARAGRAPH.LEFT
r = t.add_run("Spring AI Skill Routing")
set_run(r, size=22, color="#2E74B5", bold=True)

sub = doc.add_paragraph()
spacing(sub, before=0, after=12)
r = sub.add_run("Technical Brief \u2014 Intelligent Request Routing in Spring AI")
set_run(r, size=11, color="#555555", italic=True)

# ── Body ─────────────────────────────────────────────────────────────────
body(doc,
    "Spring AI Skill Routing is a capability within the Spring AI framework that "
    "dynamically directs incoming AI requests to the most appropriate processing "
    "skill, model, or tool based on request characteristics, context, and configured "
    "routing policies. This enables modular, extensible AI service architectures "
    "where specialized handlers own distinct capabilities."
)

heading(doc, "Overview", 1)

body(doc,
    "Skill routing addresses a common challenge in AI-powered applications: as the "
    "range of supported capabilities grows\u2014from simple chat and summarization to "
    "code generation, data analysis, and domain-specific reasoning\u2014a monolithic "
    "handler becomes difficult to maintain, test, and evolve. Routing decouples "
    "the entry point from the execution logic."
)

body(doc,
    "The router examines the incoming request (prompt content, metadata, model "
    "preferences, or explicit skill tags) and selects one or more registered "
    "skills to handle it. The result is a clean separation of concerns, better "
    "testability, and the ability to add, remove, or update skills without "
    "touching the routing infrastructure."
)

heading(doc, "Core Concepts", 1)

heading(doc, "Skill", 2)
body(doc,
    "A skill is a self-contained processing unit that handles a specific category "
    "of AI request. Each skill declares its capabilities, expected input format, "
    "and output schema. Examples include a summarization skill, a code-generation "
    "skill, and a data-extraction skill."
)

heading(doc, "Router", 2)
body(doc,
    "The router is the central dispatch component. It evaluates incoming requests "
    "against routing rules and selects the best-matching skill. Routers can be "
    "configured with strategies such as content-based routing, tag-based routing, "
    "or model-capability routing."
)

heading(doc, "Routing Policy", 2)
body(doc,
    "A routing policy defines the logic for matching requests to skills. Policies "
    "range from simple keyword matching to LLM-based intent classification. "
    "Multiple policies can be composed to handle complex decision trees."
)

heading(doc, "Routing Strategies", 1)

table_styled(
    doc,
    headers=["Strategy", "Mechanism", "Best Use Case"],
    rows=[
        ["Content-Based", "Prompt text analysis via keywords, embeddings, or LLM classifiers", "When request context alone determines the appropriate handler"],
        ["Tag-Based", "Explicit skill tags or metadata in the request payload", "Multi-tenant systems or explicit skill selection by callers"],
        ["Model-Capability", "Routes based on required model features (multimodal, code execution, etc.)", "When available models have heterogeneous capabilities"],
        ["Fallback Chain", "Attempts skills in priority order until one succeeds", "Reliability-critical paths with redundant skill implementations"],
        ["Hybrid / Composite", "Combines two or more strategies with configurable precedence", "Complex production systems requiring adaptive routing"],
    ],
    cw=[2000, 4360, 3000],
)

heading(doc, "Configuration Approach", 1)

body(doc,
    "Spring AI skill routing follows the framework\u2019s convention-over-configuration "
    "philosophy. Skills are registered as Spring beans, and the router is configured "
    "through a combination of annotations, Java configuration, and optional "
    "externalized routing rules."
)

body(doc, "A typical setup involves three steps:", before=6, after=4)

bullet(doc, "Define skill beans annotated with @Skill or implementing a Skill interface, each specifying its routing criteria or supported intent categories.")
bullet(doc, "Configure a Router bean with the desired routing strategy (content-based, tag-based, or hybrid).")
bullet(doc, "Inject the router into the application entry point and call router.route(request) to dispatch.")

heading(doc, "Benefits", 1)
body(doc, "Adopting skill routing in Spring AI applications yields several practical advantages:")

bullet(doc, "Modularity: Skills are independently developed, tested, and deployed, reducing regression risk across the AI stack.")
bullet(doc, "Extensibility: New capabilities are added by registering new skills without modifying existing routing logic.")
bullet(doc, "Observability: Each routing decision is traceable, enabling monitoring dashboards and A/B testing of routing policies.")
bullet(doc, "Resilience: Fallback chains and dynamic reconfiguration prevent single-skill failures from cascading into full-service outages.")
bullet(doc, "Performance: Skills are invoked only when needed, avoiding unnecessary model calls and reducing latency.")

heading(doc, "Design Considerations", 1)
body(doc, "When implementing skill routing, teams should account for the following factors:")

bullet(doc, "Routing Latency: Complex content-based policies (LLM-based intent classification) add per-request overhead. Cache or pre-compute where possible.")
bullet(doc, "Ambiguity: Requests that match multiple skills require a tie-breaking policy\u2014priority ordering, confidence thresholds, or explicit conflict resolution.")
bullet(doc, "Skill Contract Evolution: As skills evolve, their input/output contracts may change. Versioning the routing criteria or the skill interface helps manage transitions.")
bullet(doc, "Observability Investment: Logging and metrics at the router level are essential for debugging misrouted requests and tuning policies.")

heading(doc, "Summary", 1)
body(doc,
    "Spring AI skill routing provides a structured, extensible foundation for "
    "building AI services that handle diverse request types. By decoupling request "
    "dispatch from skill execution, teams gain modularity, resilience, and the "
    "flexibility to evolve their AI capabilities independently. The framework\u2019s "
    "support for multiple routing strategies\u2014content-based, tag-based, "
    "model-capability, and fallback chains\u2014makes it suitable for a wide range "
    "of production AI workloads."
)

# ── Save to output ──────────────────────────────────────────────────────
out_dir = r"D:\project\java_project\skill_demo\target\skill-runtime-output"
os.makedirs(out_dir, exist_ok=True)
out_path = os.path.join(out_dir, "Spring_AI_Skill_Routing.docx")
doc.save(out_path)
print(f"OK  saved  {out_path}")
