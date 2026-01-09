
import os
import re
from fontTools.subset import main as subset_main

def extract_chars():
    chars = set()
    # Relevant files to scan
    files_to_scan = [
        r'd:\pattern\src\main\resources\templates\admin.html',
        r'd:\pattern\src\main\resources\templates\gallery.html',
        r'd:\pattern\src\main\resources\templates\login.html',
        r'd:\pattern\src\main\resources\templates\index.html',
        r'd:\pattern\src\main\resources\data.sql'
    ]
    
    for file_path in files_to_scan:
        if os.path.exists(file_path):
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                # Find all Chinese characters, punctuation, etc.
                found = re.findall(r'[\u4e00-\u9fa5\u3000-\u303f\uff00-\uffef]', content)
                chars.update(found)
    
    # Add common ASCII characters for safety
    for i in range(32, 127):
        chars.add(chr(i))
        
    return "".join(sorted(list(chars)))

def subset_font(input_name, output_name, text):
    input_path = os.path.join(r'd:\pattern\src\main\resources\static\fonts', input_name)
    output_path = os.path.join(r'd:\pattern\src\main\resources\static\fonts', output_name)
    
    text_file = "chars_v4.txt"
    with open(text_file, 'w', encoding='utf-8') as f:
        f.write(text)
    
    # Run subsetting
    try:
        subset_main([
            input_path,
            f"--text-file={text_file}",
            "--flavor=woff2",
            f"--output-file={output_path}",
            "--layout-features=*",
            "--notdef-outline",
            "--no-hinting",
            "--desubroutinize"
        ])
        print(f"Successfully created {output_name}")
    except Exception as e:
        print(f"Error subsetting {input_name}: {e}")

if __name__ == "__main__":
    text = extract_chars()
    print(f"Extracted {len(text)} unique characters.")
    
    fonts = [
        ("HYYongZiShanHeLi95J.ttf", "HYYongZiShanHeLi95J_v4.woff2"),
        ("HYYongZiShanHeLi95F.ttf", "HYYongZiShanHeLi95F_v4.woff2"),
        ("HYYongZiShanHeLi85J.ttf", "HYYongZiShanHeLi85J_v4.woff2"),
        ("HYYongZiShanHeLi85W.ttf", "HYYongZiShanHeLi85W_v4.woff2")
    ]
    
    for src, dest in fonts:
        subset_font(src, dest, text)
