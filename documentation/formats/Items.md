# 物品（Item）

物品定义可以放在你的物品栏里、拿在手中的事物。

物品定义放在 thing 包的 `item` 目录中。

例如：
```
/things/examplepack/item/cheese_stick.json
```

## JSON 文件的基本结构

```json
{
  "parent": "minecraft:string",
  "type": "plain",
  "max_stack_size": 64,
  "max_damage": 50,
  "food": {
    "saturation": 5
  },
  "container": "bowl",
  "fire_resistant": true,
  "group": "decorations",
  "creative_menu_stacks": [
    {
      "nbt": {}
    }
  ],
  "attribute_modifiers": [
    {
      "nbt": {}
    }
  ],
  "color_handler": "foliage",
  "lore": [
    "Hello",
    {"text": "Hi", "italic": true, "color": "gray" }
  ],
  "tool_actions": ["shovel_dig"],
  "burn_duration": 20,
  "delayed_use": {
    "duration": 20,
    "animation": "EAT",
    "on_complete": "USE_ITEM"
  }
}
```

## "parent"

指定从另一个物品继承属性。

可选。默认：无父级。

必须是资源位置字符串，如 `"string"`，或 `"minecraft:stick"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

子级中显式指定的字段会覆盖父级中的同名值。

## "type"

指定要构造的物品类型。每种类型都带有额外属性。

可选。默认：不带额外属性的普通（plain）类型。

必须是资源位置字符串，如 `"block"`，或 `"minecraft:sword"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

可用的物品类型见 [物品类型](./ItemTypes.md) 页面。

## "max_stack_size"

定义物品的堆叠上限。

可选。默认：由物品类型决定（通常为 64）。

必须是介于 1 与 127 之间的正整数。大于 64 的值未良好定义，可能会出错。

## "max_damage"

定义物品（不计耐久附魔）损坏前可使用的次数。

可选。默认：0（无耐久条），工具与盔甲等物品类型除外。

必须是正整数或零。零表示物品不会被损坏。

## "group"

定义物品所属的创造模式标签页。

不能与 `"creative_menu_stacks"` 同时使用。

可选。默认：不出现在创造模式菜单中。

必须是创造模式标签页名称字符串。

## "creative_menu_stacks"

定义该物品加入创造模式菜单的堆叠。每个堆叠可以加入一个或多个标签页。

不能与 `"group"` 同时使用。

可选。默认：不出现在创造模式菜单中。

必须是包含 JSON 对象（`{}`）的 JSON 数组（`[]`）。

每个 JSON 对象对应一组标签页与该堆叠信息的组合：

```json
    {
      "tabs": [ "tools" ],
      "nbt": { "tmp": 0 }
    }
```

`"tabs"` 键为必填，必须是包含字符串的 JSON 数组（`[]`）。

对象中其余键定义该物品堆叠。

JSON 中物品堆叠的定义细节见 [物品堆叠定义](./ItemStack.md) 页面。

## "container"

定义合成后留在合成格中、或燃料耗尽后留在熔炉中的物品。

可选。默认：无容器物品。

## "food"

定义该物品可食用。

可选。默认：不可食用。

定义食物所用的取值见 [食物定义](./Food.md) 页面。

## "attribute_modifiers"

定义装备该物品时应用的属性变化。例如：+20% 速度。

可选。默认：无属性修饰符。

必须是包含 JSON 对象（`{}`）的 JSON 数组（`[]`）。每个对象描述一个属性修饰符。

属性修饰符的语法见 [属性修饰符](./AttributeModifiers.md) 页面。

## "color_handler"

定义物品堆叠的颜色处理器。颜色处理器基于上下文提供着色值。

可选。默认：无着色。

必须是资源位置字符串，如 `"foliage"`，或 `"minecraft:tall_grass"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

默认只定义了 2 个颜色处理器：`"foliage"` 与 `"tall_grass"`。更多可通过模组代码定义，未来可能支持通过脚本定义。

## "lore"

定义要显示在提示框中的描述文本行列表。

可选。默认：无描述文本。

必须是包含字符串与 JSON 文本组件的 JSON 数组（`[]`），其写法与 `/tellraw` 等命令中的一致。

## "tool_actions"

定义向游戏声明的一组工具动作。这并不会让物品真的能执行这些动作，除非另有脚本逻辑；在没有脚本的情况下，它只是把物品声明为某种工具。

可选。默认：取决于物品类型。

必须是包含字符串的 JSON 数组（`[]`）。

## "burn_duration"

定义该物品作为熔炉燃料时可燃烧的时间（刻）。

可选。默认：使用原版默认处理（取决于物品类型），或继承父级设置的值。

必须是大于等于 0 的整数。值为 0 表示该物品不作为燃料，可用来显式覆盖父级继承的燃烧时间。

> 说明：0 = 不作为燃料的语义对应 Forge `IForgeItem#getBurnTime`（返回 0 表示不作为燃料）与 Fabric `FuelRegistry`（`add(item, 0)` 等价于移除燃料）。

## "fire_resistant"

定义该物品能否在火焰与岩浆中存留而不被烧毁。

可选。默认：false（物品会在岩浆与火焰中烧毁）。

必须是布尔值（`false` 或 `true`）。

## "delayed_use"

实验性。

定义该物品是否可以持续使用（按住使用并经过一段时间后触发完成动作，类似弓的蓄力）。

可选。

`"duration"` 必填，定义需要使用多少刻。
`"animation"` 必填。允许的值：
    NONE、
    EAT、
    DRINK、
    BLOCK、
    BOW、
    SPEAR、
    CROSSBOW、
    SPYGLASS、
    TOOT_HORN、
    BRUSH、
    CUSTOM
`"on_complete"` 可选。允许的值：
  USE_ITEM        （类似食物：使用完成后消耗物品）
  CONTINUE        （类似弓：可以持续使用）
