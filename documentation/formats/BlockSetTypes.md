# 方块集类型（Block Set Type）定义

实验性。

方块集类型定义用于为门、活板门、压力板、按钮等需要成套音效与行为的方块指定一组属性。木头方块应声明为 `is_wood: true`，并额外提供栅栏门与悬挂式告示牌的声音。

方块集类型定义放在 thing 包的 `block_set_type` 目录中。

例如：
```
/things/examplepack/block_set_type/scream.json
```

## JSON 文件的基本结构

```json
{
  "sound_type": "some:sound_type",
  "door_close": "some:sound_event",
  "door_open": "some:sound_event",
  "trapdoor_close": "some:sound_event",
  "trapdoor_open": "some:sound_event",
  "pressure_plate_off": "some:sound_event",
  "pressure_plate_on": "some:sound_event",
  "button_off": "some:sound_event",
  "button_on": "some:sound_event",
  "is_wood": true,
  "hanging_sign_sound_type": "some:sound_type",
  "fence_gate_close": "some:sound_event",
  "fence_gate_open": "some:sound_event",
  "can_open_by_hand": true
}
```

## "sound_type"

定义该方块集使用的 [声音类型](./SoundTypes.md)。

可选。默认：wood。

必须是资源位置字符串，如 `"wood"`，或 `"minecraft:wood"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "door_close"

定义门关闭时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_door_close。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "door_open"

定义门打开时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_door_open。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "trapdoor_close"

定义活板门关闭时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_trapdoor_close。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "trapdoor_open"

定义活板门打开时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_trapdoor_open。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "pressure_plate_off"

定义压力板被释放时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_pressure_plate_click_off。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "pressure_plate_on"

定义有东西踩上压力板时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_pressure_plate_click_on。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "button_off"

定义按钮弹起（关闭）时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_button_click_off。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "button_on"

定义按钮按下（开启）时使用的 [音效事件](./SoundEvents.md)。

可选。默认：wooden_button_click_on。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "is_wood"

定义该方块集类型是否代表一种木头类型。为 true 时，会额外向游戏注册对应的 WoodType（木头类型）。

可选。默认：false。

必须是布尔值（`true` 或 `false`）。

## "hanging_sign_sound_type"

定义悬挂式告示牌使用的 [声音类型](./SoundTypes.md)。

可选。默认：hanging_sign。

必须是资源位置字符串，如 `"wood"`，或 `"minecraft:wood"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "fence_gate_close"

定义栅栏门关闭时使用的 [音效事件](./SoundEvents.md)。

可选。默认：fence_gate_close。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "fence_gate_open"

定义栅栏门打开时使用的 [音效事件](./SoundEvents.md)。

可选。默认：fence_gate_open。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "can_open_by_hand"

定义该方块集对应的门等方块能否直接用手打开（无需按钮等红石信号）。

可选。默认：true。

必须是布尔值（`true` 或 `false`）。
