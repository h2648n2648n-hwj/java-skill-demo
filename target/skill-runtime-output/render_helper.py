#!/usr/bin/env python3
"""Run render_docx.py from the skill root on the output document."""
import subprocess, sys, os

# Paths
skill_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
render_script = os.path.join(skill_root, "render_docx.py")
docx_path = r"D:\project\java_project\skill_demo\target\skill-runtime-output\Spring_AI_Skill_Routing.docx"
out_dir = r"D:\project\java_project\skill_demo\target\skill-runtime-output\render_out"

os.makedirs(out_dir, exist_ok=True)

if not os.path.exists(render_script):
    print(f"render_docx.py not found at {render_script}")
    sys.exit(1)

res = subprocess.run(
    [sys.executable, render_script, docx_path, "--output_dir", out_dir, "--emit_pdf"],
    capture_output=True, text=True, timeout=120
)
print("STDOUT:", res.stdout[:1000])
print("STDERR:", res.stderr[:2000])
print("Return:", res.returncode)

if os.listdir(out_dir):
    print("Output files:")
    for f in sorted(os.listdir(out_dir)):
        print(f"  {f}")
else:
    print("No output files generated")
