# AntiHealthIndicator

[![Build](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/gradle.yml/badge.svg)](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/gradle.yml)
[![CodeQL](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/codeql.yml/badge.svg)](https://github.com/Bram1903/AntiHealthIndicator/actions/workflows/codeql.yml)
![GitHub Release](https://img.shields.io/github/release/Bram1903/AntiHealthIndicator.svg)

## Showcase

![Demo](showcase/AntiHealthIndicator.gif)

## Supported Versions

This plugin supports every Minecraft version from 1.8.8 to 1.20.4.
Besides that, the plugin also supports Spigot, Paper, and Folia.

Technically, it should also work on any Spigot or Paper fork, but I can't guarantee that statement.

## Features

- **Completely Asynchronous** - The plugin is designed to be as lightweight as possible.
  All packet modifications are done asynchronously, so the main thread is never blocked.
- **Folia Support** - The plugin integrates with [Folia](https://papermc.io/software/folia), which is a Paper fork that
  adds regionised multithreading to the server.
- **Configurable** - The plugin is highly configurable, allowing you to adjust the settings to your liking.
- **Update Checker** - The plugin automatically checks for updates on startup.
  If a new version is available, a message will be sent to the console.
- **Stand Alone** - The plugin is completely stand alone, meaning it doesn't require any other plugins to function.
- **Permission Bypass** - The plugin allows you to bypass the spoofing features with the `AntiHealthIndicator.Bypass`
  permission (can be enabled in the config).

### Spoofers

> The spoofer features is a collection of features that modify the data packets sent to the client or other clients,
> which prevents them from being used to gain an unfair advantage.

- **Health**
- **Air Ticks**
- **Player Absorption**
- **Player Saturation**
- **Player XP**
- **Item Durability**
- **Item Enchantments**
- **Item Stack Amount**
- **World Seed**
- **Enchantment Seed**

## Permission Nodes

Players that are OP (Operators) have these permissions by default.

- `AntiHealthIndicator.Notify` Notifies the player when a new update is available.
- `AntiHealthIndicator.Bypass` Prevents the player from receiving spoofed data.

## Installation Guide

1. [Download the latest release](https://github.com/Bram1903/AntiHealthIndicator/releases/latest) from the release
   page.

2. Move the downloaded plugin to the plugins directory of your server.

3. Restart your server for the changes to be implemented.

4. Adjust the presets in the `config.yml` file to match your preferences. (Restart required)

5. You're good to go!

## Compiling Jar From Source

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
   Use Gradle to compile the source code and generate the jar file:
   ```bash
   ./gradlew build
   ```
   or for Windows CMD:
   ```cmd
   .\gradlew build
   ```

4. **Locate the Jar File**:
   Upon successful compilation,
   the generated jar file can be found in the `build/libs/` directory within the project root.

By following these steps, you will compile the AntiHealthIndicator plugin from its source code.

## Credits

- **[Retrooper](https://github.com/retrooper)** for helping me with multiple questions, and for writing the amazing
  library
  [PacketEvents](https://github.com/retrooper/packetevents)
  which this plugin uses, to get easy read and write access to the Minecraft protocol!
- A few people in the PacketEvents [Discord server](https://discord.me/packetevents) for helping me with a bunch of
  questions I had!
