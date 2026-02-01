# LTS Auth Mod

A server-side authentication mod for Minecraft Forge 1.20.1.

## Features

- **Simple Authentication**: Players can register and login using simple commands.
- **Player Restrictions**: Unauthenticated players are frozen in place and cannot interact with the world.
- **Auto-Kick**: Players are automatically kicked if they fail to authenticate within 60 seconds.
- **JSON Storage**: Player data is stored in a simple JSON file for easy management.
- **Server-Side Only**: Clients do not need to install the mod to join the server.

## Commands

- `/register <password>`: Register a new account.
- `/login <password>`: Login to an existing account.

## Configuration

The mod stores player data in `config/lts_auth/players.json`.

## Installation

1. Download the mod JAR file.
2. Place it in the `mods` folder of your Forge 1.20.1 server.
3. Restart the server.

## Development

This mod was built using Forge 1.20.1 and Java 17.

### Building from source

```bash
./gradlew build
```

The built JAR will be in `build/libs/`.
