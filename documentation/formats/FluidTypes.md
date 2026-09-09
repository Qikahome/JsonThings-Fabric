# 流体类型（Fluid Type）

自 1.19 起，流体需要一个单独注册的 FluidType 对象来描述其属性。为方便起见，Json Things 允许你在流体定义中直接声明流体类型。

FluidType 包含了流体的基本属性，例如贴图与温度等。

具名的 FluidType 定义放在 thing 包的 `fluid_type` 目录中。

例如：
```
/things/examplepack/fluid_type/mud.json
```

## JSON 文件的基本结构

```json
{
  "parent": "another:fluid_type",
  "still_texture": "minecraft:block/water_still",
  "flowing_texture": "minecraft:block/water_flow",
  "side_texture": "minecraft:block/water_overlay",
  "rarity": "epic",
  "color": [255,0,255],
  "density": 100,
  "luminosity": 15,
  "temperature": 100,
  "viscosity": 100,
  "gaseous": false,

  "motion_scale": 1.0,
  "fall_distance_modifier": 1.0,

  "can_push_entity": true,
  "can_swim": true,
  "can_drown": true,
  "can_extinguish": false,
  "can_hydrate": false,
  "can_convert_to_source": false,
  "supports_boating": false,

  "tag": "testpack:test",

  "sounds": {
    "bucket_fill": "minecraft:item.bucket.fill_water",
    "bucket_empty": "minecraft:item.bucket.empty_water",
    "fluid_vaporize": "minecraft:block.fire.extinguish"
  }
}
```

## "parent"

指定从另一个流体类型继承属性。

可选。默认：无父级。

必须是资源位置字符串，如 `"water"`，或 `"minecraft:lava"`。与标签（tag）JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

子级中显式指定的字段会覆盖父级中的同名值。

## "still_texture"

定义源方块顶面以及处于平衡状态的流动方块所使用的贴图。也用于动态桶及其他流体容器中的展示。

必填。

必须是资源位置字符串，如 `"water"`，或 `"minecraft:block/water_still"`。与标签 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "flowing_texture"

定义流向更低处的流动方块顶面所使用的贴图。也用于动态桶及其他流体容器中的展示。

必填。

必须是资源位置字符串，如 `"water"`，或 `"minecraft:block/water_flow"`。与标签 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "side_texture"

定义透过非实心方块观察流体方块时其侧面所使用的贴图。有时也称为 overlay（覆盖）贴图。

可选。默认：无侧面贴图。

必须是资源位置字符串，如 `"water"`，或 `"minecraft:block/water_overlay"`。与标签 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "translation_key"

覆盖流体名称的本地化键。未指定时使用默认的本地化键。

可选。默认：无。

必须是字符串。

## "rarity"

定义流体名称显示时所用的稀有度颜色。

可选。默认："common"。

必须是以下字符串之一：

* "common"
* "uncommon"
* "rare"
* "epic"

## "color"

定义将与贴图颜色相乘的颜色着色。

可选。默认：白色（无着色）。

可以是：
1. 一个介于 0 与 2^32-1 之间的数字（ARGB 格式，如 `0xFFFFFFFF` 表示不透明白色）。
2. 一个包含十六进制颜色代码的字符串，例如 `"#FFAABBCC"`（AARRGGBB 顺序）。不支持短格式颜色（"#ABC"）。
3. 一个 JSON 数组，按 `[a,r,g,b]` 或 `[r,g,b]` 顺序包含各元素。
4. 一个 JSON 对象，包含 "r"、"g"、"b" 键以及可选的 "a" 键。

   例如：
```json
{ "r": 255, "g": 0, "b": 255 }
```

## "luminosity"

定义流体的光照等级。原版流体方块不使用该值，但其他机制（如实体所处的光照计算）可能使用。

可选。默认：0。

必须是整数。

## "temperature"

定义流体的温度值。原版流体流动机制不使用该值，但部分机制（如与岩浆或水有关的交互）可能使用。

可选。默认：300。

必须是整数。

## "density"

定义流体的密度值。原版流体流动机制不使用该值，但部分机制（如实体升降）可能使用。

> **注意**：该值是判定"比空气轻"（气态）的依据。Forge 与 Porting Lib 均以 `density <= 0` 判定流体比空气轻（`isLighterThanAir()`），例如 0 是 Forge 约定中空气的"标准"密度。
>
> 但当前 Fabric 端尚未支持"气体"的实际表现：Forge 中比空气轻的流体会把桶模型上下翻转、流体方块顶点反转，而 Porting Lib 没有实现流体方块顶点反转，桶模型翻转也只在模型显式请求且 `flip_gas` 为 true 时才生效（JsonThings-Fabric 当前的桶模型并未启用该行为）。因此在 Fabric 端把密度设为小于等于 0，目前不会产生可感知的气体效果。

可选。默认：1000。

必须是整数。

## "viscosity"

定义流体的粘稠度值。原版流体流动机制不使用该值。

可选。默认：1000。

必须是整数。

## "gaseous"

定义该流体是否应被视为气态。

> **注意**：该字段会被解析但不会产生任何效果——上游 JsonThings（含 Forge 版）同样未启用它，且 Forge / Porting Lib 的流体属性中没有独立的"气态"属性。气态是通过 `"density"` 字段设为小于等于 0 来表达的（见上文 `"density"` 说明）。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "motion_scale"

实体浸入该流体时所受运动阻尼的比例。该值越小，实体在其中移动越艰难。

可选。默认：0.014。

必须是数字。

## "fall_distance_modifier"

实体在该流体上方坠落时，坠落距离的乘数。该值越小，坠落伤害越低。

可选。默认：0.5。

必须是数字。

## "can_push_entity"

定义流体是否推动其中的实体。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "can_swim"

定义实体是否能在该流体中游泳（进入游泳状态）。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "can_drown"

定义实体在该流体中是否会溺水（氧气耗尽）。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "can_extinguish"

定义该流体能否扑灭实体身上的火焰。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "can_hydrate"

定义该流体能否湿润实体或泥土等方块。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "can_convert_to_source"

定义在满足条件时，该流体能否形成新的源方块。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "supports_boating"

定义该流体能否承载船。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "tag"

本移植版特有的实体交互桥接流体标签。实体交互（推动/游泳/溺水等）按此标签查找匹配的流体。

可选。默认：null（即使用本流体类型的注册名 `<ns>:<path>`）。

数据包中需要让对应流体实际挂上该标签（例如 `data/<ns>/tags/fluids/<path>.json`），否则实体交互不会命中。

必须是资源位置字符串，如 `"testpack:test"`。

## "sounds"

定义流体相关的音效。所有键都是可选的。

必须是一个 JSON 对象，可包含以下键：

```json
{
  "bucket_fill": "minecraft:item.bucket.fill_water",
  "bucket_empty": "minecraft:item.bucket.empty_water",
  "fluid_vaporize": "minecraft:block.fire.extinguish"
}
```

### "bucket_fill"

定义用桶装取该流体时播放的音效。

可选。默认：无。

必须是资源位置字符串，指向已注册的音效事件。

### "bucket_empty"

定义从桶中倒出该流体时播放的音效。

可选。默认：无。

必须是资源位置字符串，指向已注册的音效事件。

### "fluid_vaporize"

定义该流体蒸发（例如水与岩浆接触）时播放的音效。

可选。默认：无。

必须是资源位置字符串，指向已注册的音效事件。
