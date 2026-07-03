"""Quick check: is soffice/LibreOffice available?"""
import shutil, subprocess, sys

soffice = shutil.which("soffice")
if soffice:
    print(f"soffice found: {soffice}")
    try:
        r = subprocess.run([soffice, "--version"], capture_output=True, text=True, timeout=10)
        print(f"version: {r.stdout.strip() or r.stderr.strip()}")
    except Exception as e:
        print(f"error checking version: {e}")
else:
    print("soffice not found in PATH")
    print(f"PATH={shutil.which('python')}")
    # Check in common locations
    for p in [r"C:\Program Files\LibreOffice\program\soffice.exe",
              r"C:\Program Files (x86)\LibreOffice\program\soffice.exe"]:
        if shutil.which(p):
            print(f"Found at: {p}")
            break
    else:
        print("LibreOffice not installed or not in PATH")

# Show Python info
print(f"\nPython: {sys.executable}")
print(f"Python version: {sys.version}")
