#!/usr/bin/env python3
"""
Town Hitbox Mod - Pure Python Build Script
Compiles Java without any external build systems or cmd.exe dependency
"""

import os
import subprocess
import sys
import shutil
import zipfile
from pathlib import Path

# Configuration
PROJECT_ROOT = Path("c:/Users/alexander.jones.26/Desktop/Coding/TownHitboxMod")
SRC_DIR = PROJECT_ROOT / "src/main/java"
RESOURCES_DIR = PROJECT_ROOT / "src/main/resources"
BUILD_DIR = PROJECT_ROOT / "build"
CLASSES_DIR = BUILD_DIR / "classes"
DIST_DIR = BUILD_DIR / "dist"
JAR_NAME = "townhitboxmod-1.0.0.jar"

def print_section(title):
    """Print a formatted section header"""
    print("\n" + "="*60)
    print("  " + title)
    print("="*60 + "\n")

def find_java_files():
    """Find all Java source files"""
    java_files = list(SRC_DIR.rglob("*.java"))
    return [str(f) for f in java_files]

def compile_java():
    """Compile Java source files"""
    print_section("Compiling Java Files")
    
    java_files = find_java_files()
    if not java_files:
        print("[ERROR] No Java files found!")
        return False
    
    print(f"Found {len(java_files)} Java files to compile")
    
    # Create build directory
    CLASSES_DIR.mkdir(parents=True, exist_ok=True)
    
    # Gather classpath from lib directory
    lib_dir = PROJECT_ROOT / "lib"
    classpath_jars = list(lib_dir.glob("*.jar")) if lib_dir.exists() else []
    
    if classpath_jars:
        print(f"Found {len(classpath_jars)} dependency JARs")
    else:
        print("[WARNING] No dependency JARs found in lib/")
        print("Run: python download_deps.py")
    
    # Build classpath
    classpath = ";".join([str(jar) for jar in classpath_jars]) if classpath_jars else ""
    
    # Compile with javac
    cmd = [
        "javac",
        "-d", str(CLASSES_DIR),
        "--release", "17"
    ]
    
    # Add classpath if dependencies exist
    if classpath:
        cmd.extend(["-cp", classpath])
    
    cmd.extend(java_files)
    
    print(f"Running: javac -d {CLASSES_DIR} --release 17 [files]")
    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=False)
        
        if result.returncode != 0:
            print("[ERROR] Compilation failed!")
            print(f"\nErrors:\n{result.stderr}")
            return False
        
        if result.stderr:
            print(f"Warnings:\n{result.stderr}")
        
        print("[OK] Compilation successful!")
        return True
        
    except FileNotFoundError:
        print("[ERROR] javac not found. Please ensure JDK 17+ is installed.")
        return False

def copy_resources():
    """Copy resource files to build directory"""
    print_section("Copying Resources")
    
    if not RESOURCES_DIR.exists():
        print("[INFO] No resources directory found, skipping")
        return True
    
    try:
        count = 0
        for resource_file in RESOURCES_DIR.rglob("*"):
            if resource_file.is_file():
                rel_path = resource_file.relative_to(RESOURCES_DIR)
                dest_path = CLASSES_DIR / rel_path
                dest_path.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(resource_file, dest_path)
                count += 1
        
        print(f"[OK] Resources copied! ({count} files)")
        return True
    except Exception as e:
        print(f"[ERROR] Error copying resources: {e}")
        return False

def create_jar():
    """Create JAR file using jar command"""
    print_section("Creating JAR File")
    
    DIST_DIR.mkdir(parents=True, exist_ok=True)
    jar_path = DIST_DIR / JAR_NAME
    
    # Create manifest
    manifest_path = BUILD_DIR / "MANIFEST.MF"
    manifest_content = """Manifest-Version: 1.0
Main-Class: com.townhitbox.TownHitboxMod
Implementation-Title: townhitboxmod
Implementation-Version: 1.0.0
"""
    manifest_path.write_text(manifest_content)
    
    # Try using jar command
    cmd = [
        "jar",
        "cfm",
        str(jar_path),
        str(manifest_path),
        "-C", str(CLASSES_DIR), "."
    ]
    
    print(f"Running: jar cfm {jar_path} ...")
    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=False)
        
        if result.returncode == 0:
            print(f"[OK] JAR created successfully!")
            print(f"     Location: {jar_path}")
            return True
        else:
            print(f"[WARNING] jar command failed: {result.stderr}")
            print("[INFO] Attempting fallback: creating JAR with Python zipfile...")
            return create_jar_fallback()
            
    except FileNotFoundError:
        print("[WARNING] jar command not found, using Python fallback...")
        return create_jar_fallback()

def create_jar_fallback():
    """Fallback: Create JAR as a ZIP file using Python"""
    print_section("Creating JAR (Python Fallback)")
    
    DIST_DIR.mkdir(parents=True, exist_ok=True)
    jar_path = DIST_DIR / JAR_NAME
    
    try:
        with zipfile.ZipFile(jar_path, 'w', zipfile.ZIP_DEFLATED) as jar:
            # Add manifest
            manifest_path = BUILD_DIR / "MANIFEST.MF"
            if manifest_path.exists():
                jar.write(manifest_path, "META-INF/MANIFEST.MF")
            
            # Add all class files and resources
            count = 0
            for file_path in CLASSES_DIR.rglob("*"):
                if file_path.is_file():
                    arcname = file_path.relative_to(CLASSES_DIR)
                    jar.write(file_path, arcname)
                    count += 1
        
        print(f"[OK] JAR created successfully! ({count} files)")
        print(f"     Location: {jar_path}")
        return True
        
    except Exception as e:
        print(f"[ERROR] Error creating JAR: {e}")
        return False

def clean():
    """Clean build directory"""
    print_section("Cleaning Build Directory")
    
    if BUILD_DIR.exists():
        shutil.rmtree(BUILD_DIR)
        print("[OK] Build directory cleaned")
    else:
        print("[INFO] Build directory doesn't exist")

def main():
    """Main build function"""
    print("\n" + "="*60)
    print("  Town Hitbox Mod - Python Build System")
    print("="*60)
    print(f"Project Root: {PROJECT_ROOT}")
    print(f"Source Dir: {SRC_DIR}")
    print(f"Build Dir: {BUILD_DIR}")
    
    # Check if project structure exists
    if not SRC_DIR.exists():
        print(f"\n[ERROR] Source directory not found: {SRC_DIR}")
        return 1
    
    # Build steps
    if not compile_java():
        return 1
    
    if not copy_resources():
        return 1
    
    if not create_jar():
        return 1
    
    print_section("Build Complete!")
    print(f"[OK] Your JAR is ready at: {DIST_DIR / JAR_NAME}")
    print(f"     Java Version: 17")
    print(f"     Minecraft Version: 1.20.4 (Fabric)")
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
