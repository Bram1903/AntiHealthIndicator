<div align="center">
  <h1>AntiHealthIndicator</h1>
  <img alt="Build" src="https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/gradle.yml/badge.svg">
  <img alt="CodeQL" src="https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/codeql.yml/badge.svg">
  <img alt="GitHub Release" src="https://img.shields.io/github/release/Bram1903/AntiHealthIndicator.svg">
  <br>
  <a href="https://www.spigotmc.org/resources/antihealthindicator.114851/"><img alt="SpigotMC" src="https://img.shields.io/badge/-SpigotMC-blue?style=for-the-badge&logo=SpigotMC"></a>
  <a href="https://modrinth.com/plugin/antihealthindicator"><img alt="Modrinth" src="https://img.shields.io/badge/-Modrinth-green?style=for-the-badge&logo=Modrinth"></a>
  <br>
  <a href="https://discord.gg/Sd4bd6s99z"><img alt="Discord" src="https://img.shields.io/badge/-Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"></a>
</div>

## Overview

The **AntiHealthIndicator** plugin prevents hackers and modders from seeing other players' health. Designed to be
lightweight with minimal server performance impact, it modifies packets directly, making it impossible to bypass. Logic
executes asynchronously to minimize server load. Additional features include hiding durability, enchantments, item stack
amounts, player saturation, absorption, and XP.

### Requires PacketEvents

Ensure the [PacketEvents](https://modrinth.com/plugin/packetevents) library is installed on your server.

## Table of Contents

- [Overview](#overview)
    - [Requires PacketEvents](#requires-packetevents)
- [Showcase](#showcase)
- [Supported Platforms & Versions](#supported-platforms--versions)
- [Spoofers](#spoofers)
- [Commands](#commands)
- [Permission Nodes](#permission-nodes)
- [Installation](#installation)
- [Compiling From Source](#compiling-from-source)
    - [Prerequisites](#prerequisites)
    - [Steps](#steps)
- [Credits](#credits)
- [License](#license)

## Showcase

![Demo](docs/showcase/AntiHealthIndicator.gif)

## Supported Platforms & Versions

| Platform                           | Supported Versions |
|------------------------------------|--------------------|
| Bukkit (Spigot, Paper, Folia etc.) | 1.8.8 - 1.20.6     |
| Velocity                           | Latest Major       |
| BungeeCord (or any forks)          | Latest Major       |

## Spoofers

The plugin includes various spoofing features:

- Health
- Air Ticks
- Player Absorption
- Player Saturation
- Player XP
- Item Durability
- Item Enchantments
- Item Stack Amount
- World Seed

## Commands

- `/antihealthindicator` or `/ahi` - Displays the plugin version.

## Permission Nodes

Operators (OPs) have these permissions by default:

- `AntiHealthIndicator.Notify` - Notifies player of updates.
- `AntiHealthIndicator.Bypass` - Prevents receiving spoofed data (if enabled in config).
- `AntiHealthIndicator.Debug` - Receives debug messages if debugging is enabled.

## Installation

1. **Prerequisites**: Install [PacketEvents](https://modrinth.com/plugin/packetevents).
2. **Download**: Get the latest release from
   the [GitHub release page](https://github.com/Bram1903/AntiHealthIndicator/releases/latest).
3. **Installation**: Move the downloaded plugin to your server's plugins directory.
4. **Configuration**: Customize settings in `config.yml`.
5. **Restart**: Restart the server for changes to take effect.

## Compiling From Source

### Prerequisites

- Java Development Kit (JDK) version 21 or higher
- [Git](https://git-scm.com/downloads)

### Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Bram1903/AntiHealthIndicator.git
   ```

2. **Navigate to Project Directory**:
   ```bash
   cd AntiHealthIndicator
   ```

3. **Compile the Source Code**:
   Use the Gradle wrapper to compile and generate the plugin JAR file:

   <details>
   <summary><strong>Linux / macOS</strong></summary>

   ```bash
   ./gradlew build
   ```
   </details>
   <details>
   <summary><strong>Windows</strong></summary>

   ```cmd
   .\gradlew build
   ```
   </details>

## Credits

Special thanks to:

- **[@Retrooper](https://github.com/retrooper)**: Author of [PacketEvents](https://github.com/retrooper/packetevents).
- **[@Tofaa](https://github.com/Tofaa2)**: Helped design the project infrastructure and logic. Check out
  his [EntityLib](https://github.com/Tofaa2/EntityLib).
- **[@Booky10](https://github.com/booky10)**: Helped with various complex matters.
- **[@Abhigya](https://github.com/AbhigyaKrishna)**: Designed the Gradle Build Chain.

## License

This project is licensed under the [GPL3 License](LICENSE).
