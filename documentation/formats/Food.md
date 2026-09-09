# 食物（Food）定义

食物定义用于定义食物物品的属性。可以具名定义，也可以直接内联在物品的 `"food"` 键中。

具名的食物定义放在 thing 包的 `food` 目录中。

例如：
```
/things/examplepack/food/stick.json
```

注意：食物定义**不会**自动创建物品。你必须定义一个使用该食物定义的物品，它才会出现在游戏中。

## JSON 文件的基本结构

```json
{
  "nutrition": 1,
  "saturation": 1.0,
  "meat": false,
  "fast": false,
  "always_eat": false,
  "effects": [

  ]
}
```

## "nutrition"

定义该食物恢复多少饥饿值。

必填。

必须是大于零的正整数。

## "saturation"

定义该食物提供多少饱和度。饱和度是饥饿值停止下降（重新开始消耗）之前的缓冲值。

必填。

必须是大于等于零的数字。允许小数。

## "meat"

定义该食物是否算作肉。肉对狼等肉食动物有吸引力。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "fast"

定义该食物能否快速食用（食用所需时间更短）。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "always_eat"

定义该食物即使在饥饿值满时也能食用。应只用于零食类食物，而非正餐。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "effects"

定义食用该食物时施加的效果列表。

可选。默认：无效果。

必须是包含一系列 JSON 对象（`{}`）的 JSON 数组（`[]`），格式如下。

每个条目的格式如下：

```json
{
  "effects": [
    {
      "probability": 1.0,
      "effect": "minecraft:poison",
      "duration": 5,
      "amplifier": 0,
      "visible": true,
      "show_particles": true,
      "show_icon": true
    }
  ]
}
```

`"probability"` 可选，定义该效果被施加的概率，取值介于 0 与 1 之间。默认：1.0。

其余取值的含义见 [效果实例](./EffectInstances.md) 文档页面。
