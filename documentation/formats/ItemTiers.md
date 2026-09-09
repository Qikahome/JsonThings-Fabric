# 物品等级（Item Tier）

物品等级定义工具的材质等级。

具名的物品等级定义放在 thing 包的 `item_tier` 目录中。

例如：
```
/things/examplepack/item_tier/clay.json
```

## JSON 文件的基本结构

```json
{
  "uses": 10,
  "speed": 1.0,
  "attack_damage_bonus": 1,
  "enchantment_value": 1,
  "tag": "minecraft:needs_iron_tool",
  "repair_ingredient": {
    "item": "string"
  },
  "sort_after": [],
  "sort_before": []
}
```

## "uses"

定义该工具（不计耐久附魔）损坏前可使用的次数。

必填。

必须是大于零的正整数。

## "speed"

定义该工具的开采速度。

必填。

必须是大于等于 1 的数字。允许小数。

## "attack_damage_bonus"

定义该工具攻击敌人时附加的攻击伤害加成。

必填。

必须是大于等于 1 的数字。允许小数。

## "enchantment_value"

定义该工具的附魔能力。值越高，能同时附上的魔咒越好。

必填。

必须是大于等于 1 的整数。

## "tag"

定义该工具等级能够正确开采（产生掉落物）的方块 tag。用于表示"需要该等级工具"的方块集合，例如原版中的 `minecraft:needs_iron_tool`。

必填。

必须是资源位置字符串，如 `"needs_iron_tool"`，或 `"minecraft:needs_iron_tool"`。与模型 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "repair_ingredient"

定义用于修复该工具等级的原料。

必填。

必须是 JSON 对象（`{}`），定义方式见 [原料定义](./Ingredient.md)。

## "sort_before" 与 "sort_after"

定义该等级相对于其他等级的排序。

Sort After：定义被视为低于本等级的等级，它们必须排在列表前面；本等级排在它们之后。

Sort Before：定义被视为高于本等级的等级，它们必须排在列表后面；本等级排在它们之前。

可选。默认：无依赖。

必须是包含字符串的 JSON 数组（`[]`）。
