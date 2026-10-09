# Experiment

Auto Experimentation Table solver for Hypixel SkyBlock. Supports Chronomatron, Ultrasequencer, and Superpairs, with a Serums mode that auto-stops after capping out on Metaphysical Serums.

![Minecraft](https://img.shields.io/badge/Minecraft-26.1.x-green)
![Fabric](https://img.shields.io/badge/Fabric%20Loader-0.19.0%2B-blue)
![Downloads](https://img.shields.io/github/downloads/LiuLei-L/experiment/total?label=downloads)
![Release](https://img.shields.io/github/v/release/LiuLei-L/experiment)
![License](https://img.shields.io/github/license/LiuLei-L/experiment)

## Features

- **Chronomatron** - Remembers the glowing sequence and clicks it in order on your turn
- **Ultrasequencer** - Scans dye numbers and clicks them from lowest to highest
- **Superpairs** - Auto-matches identical items (disabled by default)
- **Serums mode** - Automatically stops and closes the GUI once you've gotten 3 Metaphysical Serums

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) and the [Fabric API](https://modrinth.com/mod/fabric-api)
2. Place `experiment-0.1.0.jar` in your `.minecraft/mods/` folder
3. Launch the game

## Commands

All commands start with `/experiment`:

| Command | Description |
|---------|-------------|
| `/experiment` | Show current status |
| `/experiment help` | Show help and GitHub link |
| `/experiment on` / `off` | Enable / disable the mod |
| `/experiment delay <ms>` | Set click delay, default 250ms |
| `/experiment superpairs on` / `off` | Enable / disable Superpairs |
| `/experiment serums on` / `off` | Enable / disable Serums mode |

## Serums Mode

When enabled, the mod automatically stops and exits the Experimentation Table once you've received 3 Metaphysical Serums, since no further serums will be granted.

- Chronomatron: stops after round 9
- Ultrasequencer: stops after round 6
- Superpairs: not affected

Use this when farming Serums for maximum efficiency.

## Building

```bash
./gradlew build
```

Output jars are in `build/libs/`.

## Configuration

Config file: `.minecraft/config/experiment.json`

```json
{
  "delay": 250,
  "enabled": true,
  "superpairs": false,
  "serums": false
}
```

All settings can also be changed in-game via commands.

## Disclaimer

This is a client-side mod that only reads container contents and simulates slot clicks. Use at your own risk and follow server rules.

## License

MIT
