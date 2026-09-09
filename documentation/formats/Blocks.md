# 方块（Block）定义

方块定义可以存在于世界地形网格中的事物。

方块定义放在 thing 包的 `block` 目录中。

例如：
```
/things/examplepack/block/mud.json
```

## JSON 文件的基本结构

```json
{
  "parent": "another:block",
  "type": "plain",
  "map_color": "dirt",
  "properties": {
    "facing": "horizontal_facing",
    "powered": { "type": "boolean" },
    "str": { "type": "string", "values": [ "value1", "value2" ] },
    "num": { "type": "int", "min": 0, "max": 5 }
  },
  "default_state": {
    "facing": "east",
    "powered": false,
    "str": "value1",
    "num": 0
  },
  "shape_rotation": "facing",
  "shape": [
    {
      "when": {
        "facing": "east"
      },
      "shape": [2,2,2,14,14,14]
    },
    [0,0,0,16,2,16],
    [5,2,0,11,16,11]
  ],
  "item": {
    "group": "decorations"
  }
}
```

## "parent"

指定从另一个方块定义继承属性。

可选。默认：无父级。

必须是资源位置字符串，如 `"stone"`，或 `"minecraft:dirt"`。与战利品表 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

> 注意：父级必须指向另一个 Json Things 方块定义。本移植版不支持从原版方块继承属性。

子级中显式指定的字段会覆盖父级中的同名值。

## "type"

指定要构造的方块类型。每种类型都带有额外属性。

可选。默认：不带额外属性的普通（plain）类型。

必须是资源位置字符串，如 `"plain"`，或 `"minecraft:stairs"`。与标签 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

可用的方块类型见 [方块类型](./BlockTypes.md) 页面。

## "map_color"

定义方块在地图上显示的颜色，取自地图颜色调色板。

可选。默认：无特殊地图颜色。

可以是字符串，也可以是介于 0 与 63（含）之间的正整数。

## "properties"

定义方块包含的 BlockState 属性。

可选。默认：无属性（单一状态）。

必须是 JSON 对象 `{}`，键为属性名。

对象中的值可以是 2 种形式：
* 字符串：原版提供的库存属性名。新的库存属性可通过模组代码添加。
* JSON 对象 `{}`，可包含以下键：
    ```json
      {
        "type": "string",
        "values": []
      }
    ```
  * `"type"`：属性实现类型之一。必填。
    * `"boolean"`：值为 `false` 和 `true`。
    * `"int"`：值为一组整数。合法范围须通过 `min` 与 `max` 键指定。
    * `"string"`：值为一组字符串。合法字符串须通过 `values` 列表指定。
    * `"direction"`：值为四个基本方向。可通过 `values` 列表指定方向的子集。
    * `"enum"`：值为 `class` 键所给枚举中的值。可通过 `values` 列表指定取值的子集。
  * `"values"`：对 `string`、`direction` 和 `enum` 属性，为包含允许值的 JSON 数组（`[]`）。仅对 `string` 属性为必填。
  * `"min"` 与 `"max"`：对 `int` 属性，为整数的取值范围。仅对 `int` 属性为必填。
  * `"class"`：对 `enum` 属性，为枚举类的全限定名。仅对 `enum` 属性为必填。

## "default_state"

定义各属性在未显式给出值时的默认值。

可选。默认：该属性的第一个合法值。此默认行为可能随时变化，不建议依赖。

必须是 JSON 对象（`{}`），键为属性名，值为对应属性的合法取值。

```json
{
  "facing": "east",
  "age": 3
}
```

## "shape_rotation"

定义用于旋转碰撞与命中检测所用 Voxel 形状的属性名。

可选。默认：不使用朝向属性。

必须是字符串，对应已定义属性之一，或方块类型默认属性之一。

被引用的属性必须是 `direction` 类型。

## "shape"

定义方块的一般形状，应大致与模型的形状一致。

形状由轴对齐盒体组成，不能任意旋转。这是原版的限制。

可选。默认：使用该方块类型的默认形状。

语法细节见 [Voxel 形状](./VoxelShapes.md)。

## "collision_shape"

定义方块的碰撞形状，用于实体与玩家的移动碰撞。

形状由轴对齐盒体组成，不能任意旋转。这是原版的限制。

可选。默认：一般形状；若未定义一般形状，则使用该方块类型的默认碰撞形状。

语法细节见 [Voxel 形状](./VoxelShapes.md)。

## "raytrace_shape"

定义方块的射线检测形状，用于命中检测与视线检测。

形状由轴对齐盒体组成，不能任意旋转。这是原版的限制。

可选。默认：一般形状；若未定义一般形状，则使用该方块类型的默认射线检测形状。

语法细节见 [Voxel 形状](./VoxelShapes.md)。

## "render_shape"

定义方块的渲染形状，用于决定何时可以进行相邻面剔除。

形状由轴对齐盒体组成，不能任意旋转。这是原版的限制。

可选。默认：一般形状；若未定义一般形状，则使用该方块类型的默认渲染形状。

语法细节见 [Voxel 形状](./VoxelShapes.md)。

## "render_layer"

**已在 1.19 移除**：请在模型 JSON 中使用 "render_type" 值。

## "not_solid"

定义方块是否可透视，以及是否应跳过相邻面剔除。

可选。默认：false（实心）。

必须是布尔值（`false` 或 `true`）。

## "requires_tool_for_drops"

定义方块是否必须使用正确的工具才能掉落战利品。

可选。默认：不要求工具。

必须是布尔值（`false` 或 `true`）。

## "is_air"

定义方块是否等价于空气。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "has_collision"

定义方块是否会阻挡实体穿过碰撞盒。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "ticks_randomly"

定义方块是否接收随机刻，使脚本/事件等逻辑可以响应随机刻。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "light_emission"

定义方块发出的光照等级。

可选。默认：0。

必须是介于 0 与 15（含）之间的整数。

## "explosion_resistance"

定义破坏方块所需的爆炸威力。

可选。默认：0.0。

必须是大于等于零的数字。

## "destroy_time"

定义不用工具挖掘方块所需的时间。

可选。默认：0.0。

必须是大于等于零的数字。

## "friction"

定义作用于移动变化的摩擦系数。

取值可能反直觉：接近 0 表示变化快（高摩擦），接近 1 表示变化慢（低摩擦）。

可选。默认：0.6。

必须是介于 0 与 1 之间的数字。允许小数。

## "speed_factor"

定义作用于最大移动速度的速度系数。值为 1 表示默认速度。

可选。默认：1。

必须是介于 0 与 1 之间的数字。允许小数。

## "jump_factor"

定义作用于最大跳跃高度的跳跃系数。值为 1 表示默认跳跃高度。

可选。默认：1。

必须是介于 0 与 1 之间的数字。允许小数。

## "color_handler"

定义方块的颜色处理器。颜色处理器基于上下文提供着色值。

可选。默认：无着色。

必须是资源位置字符串，如 `"foliage"`，或 `"minecraft:tall_grass"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

默认只定义了 2 个颜色处理器：`"foliage"` 与 `"tall_grass"`。更多可通过模组代码定义，未来可能支持通过脚本定义。

## "sound_type"

定义方块被放置、破坏或踩踏时播放的声音事件所使用的声音类型。

可选。默认：木头声音。

必须是资源位置字符串，如 `"wood"`，或 `"minecraft:powder_snow"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "item"

定义方块的物品形态。

可选。默认：无物品。若不使用，方块将完全无法存在于物品栏中：没有创造模式条目、没有配方、也不作为战利品。

可以是 2 种形式之一：
* 布尔值：若为 `true`，方块物品将使用全部默认属性。
* JSON 对象（`{}`）：包含物品定义的各字段（见 [物品](./Items.md) 页面）。

## "ignited_by_lava"

定义方块是否会被附近的岩浆点燃。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "force_solid"

定义方块是否必须被视为实心（无论其 Voxel 形状如何）。若为 true，则忽略 "blocks_motion" 选项。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "blocks_motion"

若为 false，定义方块必须被视为非实心（无论其 Voxel 形状如何）。若 force_solid 为 true，则忽略此选项。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "replaceable"

定义在相同位置放置其他方块时，该方块是否可被替换。默认情况下会阻止方块放置。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "push_reaction"

定义方块被活塞推动时的反应。

可选。默认：normal。

必须是以下字符串之一：

* `"normal"`：方块可被活塞推动和拉动。
* `"block"`：方块不能被活塞推动。
* `"destroy"`：方块被活塞推动时会被破坏并掉落战利品。
* `"push_only"`：方块可被活塞推动，但不能被拉动。
