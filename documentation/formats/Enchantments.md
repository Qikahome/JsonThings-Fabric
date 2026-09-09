# 附魔（Enchantment）

附魔定义允许你定义新的附魔。

附魔定义放在 thing 包的 `enchantment` 目录中。

例如：
```
/things/examplepack/enchantment/deadly.json
```

## JSON 文件的基本结构

```json
{
  "max_level": 3,
  "disallow_enchants": [
    "minecraft:smite", "minecraft:bane_of_arthropods"
  ]
}
```

## "rarity"

定义附魔的稀有度。

可选。默认：common。

必须是以下字符串之一：`"common"`、`"uncommon"`、`"rare"`、`"very_rare"`。

## "type"

定义附魔的类型（可作用的物品类别）。

可选。默认：breakable。

必须是以下字符串之一：`"breakable"`、`"armor"`、`"armor_feet"`、`"armor_legs"`、`"armor_chest"`、`"armor_head"`、`"weapon"`、`"digger"`、`"fishing_rod"`、`"trident"`、`"bow"`、`"wearable"`、`"crossbow"`、`"vanishable"`。

## "min_level"

定义附魔出现在创造模式菜单与附魔台中的最低等级。

可选。默认：1。

必须是大于零的正整数。

## "max_level"

定义附魔出现在创造模式菜单与附魔台中的最高等级。

可选。默认：1。

必须是大于零的正整数。必须大于或等于 `"min_level"`。

## "base_cost"

定义该附魔的基础附魔成本。数值越大，该附魔与其他附魔同时获得的难度越高。

可选。默认：1。

必须是非负整数。

## "per_level_cost"

定义该附魔每级的附魔成本增幅。数值越大，该附魔与其他附魔同时获得的难度越高。

可选。默认：10。

必须是非负整数。

## "random_cost"

定义该附魔成本的随机浮动范围。数值越大，该附魔与其他附魔同时获得的难度越高。

可选。默认：5。

必须是非负整数。

## "item_compatibility"

定义与该附魔兼容的物品谓词（item predicate）。

可选。默认：允许所有物品。

参见原版关于物品谓词的文档——它同时用于进度条件与战利品表。

## "disallow_enchants"

定义不能与该附魔同时存在的附魔列表。

可选。默认：所有不禁用本附魔的附魔都允许。

必须是包含其他附魔资源位置字符串的 JSON 数组（`[]`）。

## "treasure"

定义该附魔是否为宝藏附魔。若为 true，它只能通过战利品获得，不会出现在附魔台中。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "curse"

定义该附魔是否为诅咒附魔。若为 true，它在提示框中显示为红色。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "tradeable"

定义该附魔可否交易。若为 true，它会出现在村民交易中。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "discoverable"

定义该附魔可否被发现在战利品中。若为 true，它会出现在战利品中。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "allow_on_books"

定义该附魔是否允许附在书上。若为 true，它会出现在给书附魔时。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。
