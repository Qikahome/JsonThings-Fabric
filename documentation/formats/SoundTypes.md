# 声音类型（Sound Type）定义

实验性。

声音类型定义用于为新的方块材质指定一组声音。

声音类型定义放在 thing 包的 `sound_type` 目录中。

例如：
```
/things/examplepack/sound_type/scream.json
```

## JSON 文件的基本结构

```json
{
  "volume": 1.0,
  "pitch": 1.0,
  "break_sound": "sound:location",
  "step_sound": "sound:location",
  "hit_sound": "sound:location",
  "fall_sound": "sound:location"
}
```

## "volume"

定义声音的响度。

可选。默认：1.0。

必须是介于 0 与 1 之间的数字。

## "pitch"

定义声音的音高。

可选。默认：1.0。

必须是大于零的数字。1.0 使用声音文件中的原始音高。

## "break_sound"

定义方块被破坏时使用的 [音效事件](./SoundEvents.md)。

必填。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "step_sound"

定义有东西踩在方块上时使用的 [音效事件](./SoundEvents.md)。

必填。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "hit_sound"

定义玩家或实体用手或工具敲击方块时使用的 [音效事件](./SoundEvents.md)。

必填。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "fall_sound"

定义有东西摔落在方块上时使用的 [音效事件](./SoundEvents.md)。

必填。

必须是资源位置字符串，如 `"block.anvil.break"`，或 `"minecraft:block.amethyst_cluster.break"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。
