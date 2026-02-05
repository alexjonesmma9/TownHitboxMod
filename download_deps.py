#!/usr/bin/env python3
"""
Download Fabric Minecraft mod dependencies without any external build tools
"""

import os
import urllib.request
import sys
from pathlib import Path

# Configuration
LIB_DIR = Path("c:/Users/alexander.jones.26/Desktop/Coding/TownHitboxMod/lib")

# Maven Central URLs for dependencies
DEPENDENCIES = {
    "gson-2.10.1.jar": "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar",
    "fabric-loader-0.14.24.jar": "https://maven.fabricmc.net/net/fabricmc/fabric-loader/0.14.24/fabric-loader-0.14.24.jar",
    "minecraft-1.20.4.jar": "https://launcher.mojang.com/v1/objects/8e5c12db6e0f47b5da0d3bfc3a8c1b1b9abb5c1c/client.jar",
}

def download_file(url, dest_path):
    """Download a file with progress"""
    try:
        print(f"Downloading: {dest_path.name}")
        urllib.request.urlretrieve(url, dest_path)
        print(f"✅ Downloaded: {dest_path}")
        return True
    except Exception as e:
        print(f"❌ Failed to download {dest_path.name}: {e}")
        return False

def main():
    """Download all dependencies"""
    print("\nDownloading Fabric Minecraft Mod Dependencies\n")
    
    LIB_DIR.mkdir(parents=True, exist_ok=True)
    
    success_count = 0
    for filename, url in DEPENDENCIES.items():
        dest_path = LIB_DIR / filename
        
        if dest_path.exists():
            print(f"✅ Already exists: {filename}")
            success_count += 1
        else:
            if download_file(url, dest_path):
                success_count += 1
    
    print(f"\n{'='*60}")
    print(f"Downloaded {success_count}/{len(DEPENDENCIES)} dependencies")
    
    if success_count == len(DEPENDENCIES):
        print("✅ All dependencies ready!")
        return 0
    else:
        print("⚠️  Some dependencies failed to download")
        print("You may need to download them manually from Maven Central")
        return 1

if __name__ == "__main__":
    sys.exit(main())
