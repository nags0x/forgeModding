# 🦖 Big Ass Dino

A chaotic Minecraft mod that brings loud, fast, and aggressive dinos into your world — because vanilla mobs are too polite.  
Built with love (and caffeine) for a fun Minecraft modding challenge.

---

## 🚨 Why This Dino Exists

Because Minecraft needed something more intense than creepers.  
We wanted to create a fast, enraged, knockback-happy beast that could chase players like a heat-seeking missile — and we did.  
Roaring, stomping, kicking dinos that turn Minecraft into a survival horror moment.

---

## 🔥 Feature List

- **Randomized dino spawning across the Overworld** 🌍  
- **Custom sound effects** (roars borrowed from the Ender Dragon 🔊)  
- **Roars every 10s** to slow nearby players *(movement debuff)*  
- **Becomes enraged when hit** → gains speed + rage particles  
- **Melee attack goal + aggressive targeting** (follows you across the land)  
- **Knocks back players** if within 4 blocks *(like a raging bull)*  
- **Ambient, hurt, and death sounds** (because vibes)  
- **Animation system ready** (idle animations coming soon)  
- **Attribute system configured**: HP, damage, speed  
- **Correct spawn placements** using `ON_GROUND` logic  

---

## TechStack
```
Modding Tech/
├── Minecraft Forge 1.20.x
├── Java (17+)
├── GeckoLib (for animations)
├── IntelliJ IDEA (or VSCode)
├── Gradle (build tool)
```
---

## ProjectStructure

```
big-ass-dino-mod/
├── src/
│   ├── main/
│   │   ├── java/net/nags/tutorialmod/
│   │   │   ├── entity/
│   │   │   │   ├── custom/DinoEntity.java
│   │   │   │   └── client/DinoModel.java
│   │   │   ├── event/ModEventBusEvents.java
│   │   │   └── TutorialMod.java
│   │   └── resources/
│   │       ├── assets/tutorialmod/
│   │       │   ├── sounds/
│   │       │   ├── textures/
│   │       │   └── models/
├── build.gradle
└── README.md
```
---

## 📜 License
This project is under the MIT License — but the Dino? He’s lawless.
> **TL;DR:** Dino exists. It’s loud. It’s fast. It’s beautiful. Minecraft just went Jurassic. 🌋
---
