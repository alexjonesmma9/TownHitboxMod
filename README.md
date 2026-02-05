# Town Hitbox Mod

A lightweight Fabric client mod for Minecraft 1.20+ that colors entity hitboxes based on town names found in player nametags.

## Features

- **Town-Based Coloring**: Renders enemy town players' hitboxes red, friendly towns green
- **Configurable Towns**: Add/remove town names via in-game settings (press L)
- **Nametag Scanning**: Efficient substring matching scans player nametags
- **Lightweight**: Minimal impact with smart UUID caching
- **Cross-Platform**: Works on Windows, Linux, Mac

## Installation

### From .JAR (Easiest)
1. Have Fabric Loader + Fabric API installed
2. Download the compiled JAR from Releases 
3. Place in your `.minecraft/mods/` folder
4. Launch Minecraft

### Build from Source
1. Clone this repository
2. Run from PowerShell: `.\gradlew.bat build`
3. JAR will be at: `build/libs/townhitboxmod-1.0.0.jar`

## Configuration

Config file: `.minecraft/config/townhitboxmod.json`

```json
{
  "enemy_towns": ["EnemyTown"],
  "friendly_towns": ["AllyTown"],
  "scan_interval_ticks": 10,
  "hitbox_thickness": 2.0,
  "hitbox_red": [1.0, 0.0, 0.0],
  "hitbox_green": [0.0, 1.0, 0.0]
}
```

**In-Game**: Press L to open settings and manage towns directly

## Settings

- **enemy_towns**: Town names to highlight RED
- **friendly_towns**: Town names to highlight GREEN  
- **scan_interval_ticks**: Update frequency (10 = 2 scans/second, lower = more frequent)
- **hitbox_thickness**: Line width (1.0-5.0)
- **hitbox_red/green**: RGB color (0.0-1.0 per channel)

## Keybindings

- **L** - Open settings screen

## Building

**Requirements**: Java 17+

**Command**:
```powershell
.\gradlew.bat build
```

**Output**: `build/libs/townhitboxmod-1.0.0.jar`

## How It Works

1. Every N ticks, scans visible players for nametags
2. Checks if town name appears in nametag (substring match)
3. Caches result per player UUID
4. Renders hitboxes in configured colors

## Compatibility

- Minecraft 1.20.x, 1.21.x
- Fabric Loader 0.14+
- Fabric API 0.90+

## File Structure

```
src/main/java/com/townhitbox/
├── TownHitboxMod.java          (main mod class)
├── config/ConfigManager.java   (JSON config system)
├── keybind/ModKeybindings.java (L key binding)
├── renderer/HitboxRenderer.java (rendering logic)
├── scanner/NametaxScanner.java (nametag detection)
└── screen/TownSettingsScreen.java (settings GUI)
```

## License

See LICENSE file
│   ├── TownHitboxMod.java          (Main entry point)
│   ├── config/ConfigManager.java   (Configuration system)
│   ├── keybind/ModKeybindings.java (Keybinding registration)
│   ├── scanner/NametaxScanner.java (Nametag scanning logic)
│   ├── renderer/HitboxRenderer.java (Hitbox rendering)
│   └── screen/TownSettingsScreen.java (In-game GUI)
├── build.gradle                     (Build configuration)
└── src/main/resources/fabric.mod.json (Mod metadata)
```

## License

MIT License - See [LICENSE](LICENSE) file for details
