# 效果实例（Effect Instance）定义

该格式适用于在其他 JSON 文件中定义的效果实例。目前效果实例没有具名定义。

使用示例见 [食物](./Food.md)。

## JSON 文件的基本结构

```json
{
  "effect": "minecraft:poison",
  "duration": 5,
  "amplifier": 0,
  "ambient": false,
  "visible": true,
  "show_particles": true,
  "show_icon": true
}
```

## "effect"

定义要施加的药水效果。

必填。

必须是资源位置字符串，如 `"poison"`，或 `"minecraft:blindness"`。与模型 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "duration"

定义效果持续的时间，单位为游戏刻（每秒 20 刻）。

必填。

必须是非负整数。

## "amplifier"

定义效果的强度。数值越高，效果的强度越大。

可选。默认：0。

0 表示不放大，即标准效果。

必须是非负整数。

## "ambient"

定义效果是否应被视为环境效果（例如来自信标的效果）。环境效果以不同的颜色显示，且没有倒计时。建议不要为食物等情境效果将 "ambient" 设为 true。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "visible"

定义效果是否显示在屏幕右上方的状态图标区域。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "show_particles"

定义效果是否在实体周围产生粒子。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。

## "show_icon"

定义效果是否在 HUD 右上方显示图标。

可选。默认：true。

必须是布尔值（`false` 或 `true`）。
