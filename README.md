# 🛠️ QualityTools

**QualityTools** is a Fabric mod for Minecraft 1.21.11 that introduces a brand new **Reforge Table**. This mod allows players to reforge their tools and weapons to get better stats or qualities, perfect for enhancing the late-game survival experience on server.

## ✨ Features
* **Custom Reforge Table:** A brand new functional block with a custom UI.
* **Material-Based Reforging:** Reforging requires specific materials (configurable).
* **Server-Side Configuration:** Server admins can easily configure the required materials and costs using a JSON configuration file.
* **Immersive Feedback:** Custom sound effects (Mob/Anvil sounds) and clean, formatted chat messages during the reforging process.

---

## 📖 How to Get the Block

Players can craft the Reforge Table in survival mode using standard vanilla materials.

### Crafting Recipe
[IMAGE PLACEHOLDER]

*(Alternatively, server admins can grant the block using: `/give @p qualitytools:reforge_table`)*

---

## 🎮 How to Use

1. **Place the Reforge Table:** Place the block anywhere in the world.
2. **Open the UI:** Right-click the Reforge Table to open the custom reforging menu.
3. **Insert the Tool:** Place the tool or weapon you want to reforge into the designated slot.
4. **Prepare Materials:** Ensure you have enough of the required material in your inventory (e.g., Diamonds).
5. **Click Reforge:** Click the Reforge button in the UI.

---

## 🧮 How Stats are Calculated

When you craft weapon or looting from nowhere, this plugin will add random quality.

* **Damage Calculation (`damageMultiplier`)**
  The base attack damage of the weapon is multiplied by the quality's multiplier, then rounded to the nearest decimal point.
    * **Formula:** `Final Damage = Math.round(Base Damage * damageMultiplier * 10.0) / 10.0`
    * *Example:* A weapon with `7.0` base damage receiving the **MYTHIC** quality (`1.6x`) will be permanently modified to deal `11.2` damage per hit.

* **Durability Calculation (`durabilityMultiplier`)**
  Durability is dynamically intercepted every time the weapon takes damage. The logic splits based on whether the quality is a "buff" or a "debuff":

  **1. Penalized Qualities (Multiplier >= 1.0):**
  Qualities like **AWFUL (4.0x)** multiply the durability loss directly.
    * **Formula:** Takes `Damage * durabilityMultiplier` per hit.
    * *Example:* One hit will drain `4` durability points instead of `1`, breaking the weapon 4x faster.

  **2. Buffed Qualities (Multiplier < 1.0):**
  Qualities like **MYTHIC (0.3x)** use an RNG system similar to Minecraft's Vanilla *Unbreaking* enchantment. The multiplier becomes the **chance** to take 1 point of durability damage.
    * **Formula:** `30% chance` to lose 1 durability, `70% chance` to ignore the damage completely.
    * *Example:* A MYTHIC sword will absorb the vast majority of durability loss, making it last significantly longer than an ordinary sword!

---

## The JSON Configuration File
When the mod runs, it generates (or reads) a configuration file. You can modify this to balance the economy and adjust the RNG (Random Number Generation) mechanics of your server.

**Example Config Structure:**
```json
{
  "forgeMaterial": "minecraft:diamond",
  "forgeCost": 3,
  "qualities": {
    "AWFUL": {
      "damageMultiplier": 0.7,
      "weight": 10,
      "durabilityMultiplier": 4.0
    },
    "TRASH": {
      "damageMultiplier": 0.85,
      "weight": 20,
      "durabilityMultiplier": 2.5
    },
    "FINE": {
      "damageMultiplier": 1.0,
      "weight": 20,
      "durabilityMultiplier": 1.0
    },
    "GREAT": {
      "damageMultiplier": 1.15,
      "weight": 7,
      "durabilityMultiplier": 0.7
    },
    "LEGENDARY": {
      "damageMultiplier": 1.3,
      "weight": 5,
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
*notes: renaming item quality isnt support for now, it will return error, example you want to rename `TRASH` to `BAD` its not recommended atleast for now*

---

## 🤝 Compatibility & Limitations

QualityTools is dynamically built to support not just Vanilla gear, but also custom weapons from other mods!

**✅ Supported Items**
The mod will automatically apply qualities to any item that utilizes the standard `ATTACK_DAMAGE` attribute. This includes:
* Vanilla melee weapons (Swords, Axes, Tridents).
* Custom modded melee weapons (e.g., Spears, Katanas, custom broadswords) from other mods.

**❌ Not Supported (Yet)**
* **Ranged Weapons:** Bows and Crossbows will not receive qualities. In Minecraft, ranged weapons rely on projectile entity damage rather than standard item-based attack attributes.
* **Armor & Non-Weapon Tools:** Currently, the reforging system specifically targets items functioning primarily as weapons.