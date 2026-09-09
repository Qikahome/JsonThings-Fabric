# 护甲材料（Armor Material）

护甲材料定义盔甲物品的等级与材料属性。

具名的护甲材料定义放在 thing 包的 `armor_material` 目录中。

例如：
```
/things/examplepack/armor_material/rubber.json
```

## JSON 文件的基本结构

```json
{
  "toughness": 1.0,
  "knockback_resistance": 1.0,
  "enchantment_value": 5,
  "equip_sound": "minecraft:item.armor.equip_chain",
  "repair_ingredient": {
    "item": "clay_ball"
  },
  "durability": {
    "feet": 20,
    "legs": 20,
    "chest": 20,
    "head": 20
  },
  "armor": {
    "feet": 2,
    "legs": 3,
    "chest": 4,
    "head": 2
  }
}
```

## "toughness"

定义该护甲提供的护甲韧性。韧性会降低高伤害攻击的减免衰减。

必填。

必须是大于等于零的数字。允许小数。

## "knockback_resistance"

定义穿戴该护甲时受到的击退被削减的比例。

必填。

必须是大于等于零的数字。允许小数。

## "equip_sound"

定义穿戴该护甲时播放的声音。

必填。

必须是资源位置字符串，如 `"item.armor.equip_chain"`，或 `"minecraft:item.armor.equip_chain"`。
与模型 JSON 以及其他原版文件一致，省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "enchantment_value"

定义该护甲的附魔能力。值越高，能同时附上的魔咒越好。

必填。

必须是大于等于零的整数。

## "repair_ingredient"

定义用于修复该护甲材料的原料。

必填。

必须是 JSON 对象（`{}`），定义方式见 [原料定义](./Ingredient.md)。

## "durability"

定义基于穿戴槽位的护甲耐久。每种槽位类型对应一个耐久值。

必填。

可以是 JSON 对象（`{}`），键为槽位名（`head`、`chest`、`legs`、`feet`），值为对应槽位的耐久（大于等于零的整数）；
也可以是单个整数，此时所有槽位使用相同的耐久值。

各槽位键可选。缺省的槽位耐久为 0，护甲在对应槽位首次受到伤害就会损坏。

## "armor"

定义基于穿戴槽位的护甲值（防御点数）。每种槽位类型对应一个护甲值。

必填。

可以是 JSON 对象（`{}`），键为槽位名（`head`、`chest`、`legs`、`feet`），值为对应槽位的护甲值（大于等于零的整数）；
也可以是单个整数，此时所有槽位使用相同的护甲值。

各槽位键可选。缺省的槽位护甲值为 0，护甲在对应槽位不会提供保护。

示例：
```json
{
  "armor": {
    "feet": 2,
    "legs": 5,
    "chest": 6,
    "head": 3
  }
}
```
