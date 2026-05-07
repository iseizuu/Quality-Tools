# Quality Tools

**Quality Tools** is a Fabric mod for Minecraft 1.21.11 that introduces a **weapon quality system** and a custom **Reforge Table** block. Every weapon you craft receives a random quality tier that permanently affects its damage and durability — making every craft feel meaningful.

> ⚠️ This mod must be installed on **both client and server**.

---

## Features

- **Weapon Quality System** — Every weapon crafted automatically receives a random quality tier (AWFUL → MYTHIC), affecting damage and durability permanently.
- **Custom Reforge Table** — A craftable block with a custom GUI to reforge existing weapons into a new random quality.
- **Fully Configurable** — Server admins can tune every quality's damage multiplier, durability multiplier, weight (rarity), forge material, and forge cost via a JSON config file.
- **Hot Reload** — Change config and reload in-game without restarting the server via `/qualitytools reload`.
- **Broad Mod Compatibility** — Automatically supports any modded weapon that uses the standard `ATTACK_DAMAGE` attribute.

---

## Requirements

| Dependency | Version            |
|---|--------------------|
| Minecraft | 1.21.11            |
| Fabric Loader | ≥ 0.19.2           |
| Fabric API | Latest for 1.21.11 |

---

## Crafting the Reforge Table

Craft the Reforge Table using the following recipe:

<img src="https://i.ibb.co.com/LDLPwstx/crafting-recipe.png" alt="crafting-recipe" border="0">

*(Alternatively: `/give @p qualitytools:reforge_table`)*

---

## How to Use

### Automatic Quality on Craft
Simply craft any weapon — it will automatically receive a random quality tier based on the configured weights. The quality and its effects are permanently stored on the item itself.

### Reforge Table
1. Place the **Reforge Table** block.
2. Right-click to open the reforging GUI.
3. Insert the weapon you want to reforge into the slot.
4. Make sure you have enough of the required forge material in your inventory (default: 3 Diamonds).
5. Click **Reforge** — the weapon receives a brand new random quality.

---

## Quality Tiers

Qualities are rolled using a weighted random system. Higher weight = more common.

| Quality | Color | Damage Multiplier | Durability Multiplier | Weight (default) |
|---|---|---|---|------------------|
| AWFUL | Gray | 0.70x | 4.0x penalty | 5                |
| TRASH | Red | 0.85x | 2.5x penalty | 12               |
| FINE | Aqua | 1.00x | 1.0x (normal) | 30               |
| GREAT | Green | 1.15x | 0.7x (better) | 15               |
| LEGENDARY | Gold | 1.30x | 0.5x (much better) | 8                |
| MYTHIC | Light Purple | 1.60x | 0.3x (RNG based) | 2                |

> All values above are **defaults** and can be changed in the config file.

---

## How Stats Work

### Damage
The weapon's base attack damage is permanently scaled at the moment the quality is applied, then stored on the item.

```
Final Damage = Math.round(Base Damage × damageMultiplier × 10.0) / 10.0
```

**Example:** Diamond Sword (7.0 base) + MYTHIC (1.6x) = **11.2 damage**

### Durability
Durability is intercepted every time the weapon takes damage, split into two behaviors:

**Penalized qualities** (`durabilityMultiplier >= 1.0`) — multiplies durability loss directly:
```
Durability lost = delta × durabilityMultiplier
```
*Example: AWFUL (4.0x) loses 4 durability per hit instead of 1.*

**Buffed qualities** (`durabilityMultiplier < 1.0`) — uses RNG (like vanilla Unbreaking):
```
Chance to lose 1 durability = durabilityMultiplier
```
*Example: MYTHIC (0.3x) = 30% chance to lose 1 durability, 70% chance to ignore damage.*

---

## Configuration

Config file is auto-generated at:
```
config/qualitytools.json
```

### Full Config Example
```json
{
  "forgeMaterial": "minecraft:diamond",
  "forgeCost": 3,
  "qualities": {
    "AWFUL": {
      "damageMultiplier": 0.7,
      "weight": 5,
      "durabilityMultiplier": 4.0
    },
    "TRASH": {
      "damageMultiplier": 0.85,
      "weight": 12,
      "durabilityMultiplier": 2.5
    },
    "FINE": {
      "damageMultiplier": 1.0,
      "weight": 30,
      "durabilityMultiplier": 1.0
    },
    "GREAT": {
      "damageMultiplier": 1.15,
      "weight": 15,
      "durabilityMultiplier": 0.7
    },
    "LEGENDARY": {
      "damageMultiplier": 1.3,
      "weight": 8,
      "durabilityMultiplier": 0.5
    },
    "MYTHIC": {
      "damageMultiplier": 1.6,
      "weight": 2,
      "durabilityMultiplier": 0.3
    }
  }
}
```

### Config Fields

| Field | Description |
|---|---|
| `forgeMaterial` | Item required to reforge (any valid item ID) |
| `forgeCost` | Amount of material consumed per reforge |
| `damageMultiplier` | Multiplier applied to base weapon damage |
| `durabilityMultiplier` | Multiplier applied to durability loss per hit |
| `weight` | Rarity weight — higher = more common |

> ⚠️ **Do not rename quality keys** (e.g. renaming `TRASH` to `BAD`) — the mod uses these names to map internal enum values. Renaming will cause errors.

---

## Commands

All commands require **operator (OP) level** permission.

| Command | Description |
|---|---|
| `/qualitytools reload` | Reloads the config file and resets all weapon stats in every online player's inventory to reflect new values |

---

## Compatibility

### ✅ Supported
- All vanilla melee weapons (Swords, Axes, Tridents, Mace)
- Any modded weapon that uses the standard `ATTACK_DAMAGE` attribute modifier (e.g. spears, katanas, custom broadswords)

### ❌ Not Supported
- **Bows & Crossbows** — ranged weapons use projectile entity damage, not item attack attributes
- **Armor & Tools** — quality system targets weapons only

---

## ⚠️ Important Notes

- **World Safety** — If you remove the mod from an existing world, weapons keep their modified damage values (baked into the item). The quality NBT tag is simply ignored by vanilla. Your world will not break.
- **Config Changes** — After editing the config, use `/qualitytools reload` to apply changes. This resets all weapon damage in online players' inventories so new multipliers take effect correctly.
- **Quality is permanent** — Once a quality is assigned to a weapon, it stays on that item forever unless reforged.
