# Town Hitbox Mod

A simple Fabric client mod for Minecraft 1.20+ that colors entity hitboxes based on town names found in player nametags.

## Features

- **Configurable Enemy/Friendly Towns**: Add town names to detect as enemies (red) or friendlies (green)
- **Nametag Scanning**: Scans player nametags every 10 ticks (configurable) to avoid lag
- **Custom Colors**: Fully configurable hitbox colors via config file
- **Lightweight**: Minimal performance impact with smart caching
- **In-Game Settings**: Press L to open settings menu and manage towns directly

## Installation

1. Install Fabric Loader and Fabric API for Minecraft 1.20.x or 1.21.x
2. Place the compiled JAR in your `mods` folder
3. Launch Minecraft with the Fabric profile

## Configuration

The mod creates a config file at `.minecraft/config/townhitboxmod.json`

### Default Config Example

```json
{
  "enemy_towns": [
    "EnemyTown",
    "RivalClan"
  ],
  "friendly_towns": [
    "AllyTown",
    "MyAlliance"
  ],
  "scan_interval_ticks": 10,
  "hitbox_thickness": 2.0,
  "hitbox_red": [1.0, 0.0, 0.0],
  "hitbox_green": [0.0, 1.0, 0.0]
}
```

### Config Options

- **enemy_towns**: List of town names to highlight in RED
- **friendly_towns**: List of town names to highlight in GREEN
- **scan_interval_ticks**: How often to scan nametags (lower = more frequent, higher = less lag)
- **hitbox_thickness**: Line thickness of the hitbox outline
- **hitbox_red**: RGB color for enemy hitboxes (0.0-1.0)
- **hitbox_green**: RGB color for friendly hitboxes (0.0-1.0)

## Keybindings

- **L** - Open mod settings screen (configurable in-game)

## How It Works

1. Every N ticks, the mod scans all visible players' display names
2. If a town name from your config is found in a player's nametag, it's cached
3. When rendering, enemy players get red hitboxes, friendlies get green
4. Unknown players are not highlighted

## Building

```bash
powershell -ExecutionPolicy Bypass -File build.ps1
```

The compiled JAR will be in `build/libs/townhitboxmod-1.0.0.jar`

## Compatibility

- Minecraft 1.20.x and 1.21.x
- Fabric Loader 0.14+
- Fabric API 0.90+

## Project Structure

```
TownHitboxMod/
├── src/main/java/com/townhitbox/
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
