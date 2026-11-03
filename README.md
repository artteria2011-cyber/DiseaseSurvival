# Disease Survival

Fabric mod for **Minecraft Java Edition 1.20.1**.

## Features
- Every 10 minutes each online player receives a random disease.
- Six diseases apply different vanilla status effects.
- Effects refresh while the disease is active.
- Drinking a milk bucket cures the current disease.
- Chat messages announce the disease and its cure.

## Build
Requires Java 17 and Gradle 8.7:
```bash
gradle build
```
The JAR is created at `build/libs/disease-survival-1.0.0.jar`.

## Install
1. Install Fabric Loader for Minecraft 1.20.1.
2. Install Fabric API for Minecraft 1.20.1.
3. Download the JAR from the **Actions → Build Disease Survival → Artifacts** section.
4. Put the JAR in the server's `mods` folder (and optionally the clients' `mods` folders).

The build runs automatically on pushes to `main`, and can also be started manually from the Actions tab.
