# AntiHealthIndicator

[![Build](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/gradle.yml/badge.svg)](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/gradle.yml)
[![CodeQL](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/codeql.yml/badge.svg)](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/codeql.yml)
![GitHub Release](https://img.shields.io/github/release/Bram1903/AntiHealthIndicator.svg)

[![SpigotMC](https://img.shields.io/badge/-SpigotMC-blue?style=for-the-badge&logo=SpigotMC)](https://www.spigotmc.org/resources/antihealthindicator.114851/)
[![Modrinth](https://img.shields.io/badge/-Modrinth-green?style=for-the-badge&logo=Modrinth)](https://modrinth.com/plugin/antihealthindicator)

## Overview

AntiHealthIndicator is a robust Minecraft plugin
designed to foster fair gameplay by preventing cheaters and modders from gaining unfair advantages.
This lightweight solution modifies specific data packets
to ensure players cannot access critical information such as health or player status,
thereby maintaining a level playing field for all participants.

## Showcase

![Demo](showcase/AntiHealthIndicator.gif)

## Key Features

- **Asynchronous Design**: Utilizes asynchronous packet modifications to ensure server performance remains unaffected.
- **Compatibility**: Supports a wide range of Minecraft versions from 1.8.8 to 1.20.4, along with popular server
  platforms including Spigot, Paper, and Folia.
- **Customizable Settings**: Highly configurable plugin settings enable server administrators to tailor the experience
  according to their preferences.
- **Update Notifications**: Automatically checks for updates upon startup and notifies administrators of available
  upgrades.
- **Permission System**: Implements a permission system to grant specific privileges, including bypassing spoofing
  features.

## Spoofers

The plugin includes a set of spoofing features, including:

- Health
- Air Ticks
- Player Absorption
- Player Saturation
- Player XP
- Item Durability
- Item Enchantments
- Item Stack Amount
- World Seed

## Permission Nodes

Players that are OP (Operators) have these permissions by default.

- `AntiHealthIndicator.Notify` Notifies the player when a new update is available.
- `AntiHealthIndicator.Bypass` Prevents the player from receiving spoofed data (if enabled in the config).
- `AntiHealthIndicator.Debug` Allows the player to receive debug messages periodically in-game when debugging is enabled
  in the config.

## Installation

1. **Prerequisites**: Ensure the [PacketEvents library](https://ci.codemc.io/job/retrooper/job/packetevents/)
   is installed.
2. **Download**: Get the latest release from
   the [GitHub release page](https://github.com/Bram1903/AntiHealthIndicator/releases/latest).
3. **Installation**: Move the downloaded plugin to your server's plugins directory.
4. **Configuration**: Customize settings in the `config.yml` file to match server preferences.
5. **Restart**: Restart the server for changes to take effect.

## Compiling From Source

### Prerequisites

- Java Development Kit (JDK) version 8 or higher

### Steps:

1. **Clone the Repository**:
   Clone the repository containing the AntiHealthIndicator source code to your local machine using Git:
   ```bash
   git clone https://github.com/Bram1903/AntiHealthIndicator.git
   ```

2. **Navigate to Project Directory**:
   Change your current directory to the root directory of the cloned project:
   ```bash
   cd AntiHealthIndicator
   ```

3. **Compile the Source Code**:
   Use the Gradle wrapper to compile the source code and generate the plugin JAR file:
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

Without the following people, this project wouldn't be half as good as it is right now!

- **[@Retrooper](https://github.com/retrooper)**: Author of
  the [PacketEvents library](https://github.com/retrooper/packetevents) and just a great guy overall!
- **[@Tofaa](https://github.com/Tofaa2)**:
  Really nice person who helped me with the major part of the project infrastructure and logic!
  Check out his [EntityLib](https://github.com/Tofaa2/EntityLib)!

## License

This project is licensed under the [GPL3 License](LICENSE).