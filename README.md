# AntiHealthIndicator

![GitHub Release](https://img.shields.io/github/release/Bram1903/AntiHealthIndicator.svg)

## Introduction

Minecraft, by its very nature, sends a wide array of data packets to the player's client,
many of which aren't used in typical gameplay.
Unfortunately, some players take advantage of this extra information to gain an upper hand against others.

### Gaining an Unfair Advantage

Players can gain an unfair advantage by accessing details such as the health value of their opponents.
This can help them identify the weakest or the strongest players in a fight.
They can also gain insights into any enchantments on opponents' armor, or how durable their armor is,
further enhancing their advantage.

### Solving the Issue

This is where this plugin comes in.
The plugin is designed to prevent these unfair advantages.
It achieves this by modifying these data packets so they no longer reveal this kind of information,
while still keeping the client functioning as normal.
By implementing this plugin, the gameplay can remain fair and true to its intended design.

## Supported Versions

This plugin supports every Minecraft version from 1.8.8 to 1.20.4.
Besides that, the plugin also supports Spigot, Paper, and Folia.

Technically, it should also work on any Spigot or Paper fork, but I can't guarantee that statement.

## Features

- **Completely Asynchronous** - The plugin is designed to be as lightweight as possible.
  All packet modifications are done asynchronously, so the main thread is never blocked.
- **Folia Support** - The plugin integrates with [Folia](https://papermc.io/software/folia), which is a Paper fork that adds regionised multithreading to the server.
- **Configurable** - The plugin is highly configurable, allowing you to adjust the settings to your liking.
- **Update Checker** - The plugin automatically checks for updates on startup.
  If a new version is available, a message will be sent to the console.
- **Stand Alone** - The plugin is completely stand alone, meaning it doesn't require any other plugins to function.
- **Permission Bypass** - The plugin allows you to bypass the spoofing features with the `AntiHealthIndicator.Bypass`
  permission (can be disabled in the config).

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

> Prerequisites

- Java 17
- Maven

To compile the jar from source, run the following command in the directory of your project root (i.e., where the pom.xml
file resides).

```bash
mvn clean package
```

The produced jar can be located in the /target/ directory within your project root.

## Credits

- **[Retrooper](https://github.com/retrooper)** for helping me with multiple questions, and for writing the amazing
  library
  [PacketEvents](https://github.com/retrooper/packetevents)
  which this plugin uses, to get easy read and write access to the Minecraft protocol!
- A few people in the PacketEvents [Discord server](https://discord.me/packetevents) for helping me with a bunch of
  questions I had!
