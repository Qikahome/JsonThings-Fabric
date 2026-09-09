# 流体（Fluid）

流体定义允许你定义可以装进桶及其他流体容器、和/或放置在世界中的液体。

流体定义放在 thing 包的 `fluid` 目录中。

例如：
```
/things/examplepack/fluid/mud.json
```

## JSON 文件的基本结构

```json
{
  "parent": "another:fluid",
  "type": "plain",
  "fluid_type": "some:fluid_type",
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
  "bucket": true
}
```

> `"fluid_type"` 除了写成资源位置字符串引用已注册的流体类型外，也可以直接内联一个流体类型定义对象（见 [流体类型](./FluidTypes.md)）。两种情况只能选其一。

## "parent"

指定从另一个流体定义继承属性。

可选。默认：无父级。

必须是资源位置字符串，如 `"water"`，或 `"minecraft:lava"`。与战利品表 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

子级中显式指定的字段会覆盖父级中的同名值。

## "type"

指定用于构造流体的流体工厂（fluid factory）。每种工厂可以带有额外属性。

可选。默认：不带额外属性的普通（plain）类型。

必须是资源位置字符串，如 `"plain"`。与标签 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

可用的流体工厂类型见 [流体工厂](./FluidFactories.md) 页面。

## "fluid_type"

指定流体类型。流体类型是独立的对象，定义流体的属性（如贴图、温度等）。

必填。

可以写成 JSON 对象 `{}`（内联定义），也可以是资源位置字符串，如 `"some:fluid_type"`。与标签 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

流体类型的可用字段见 [流体类型](./FluidTypes.md) 页面。

## "properties"

定义流体包含的 FluidState 属性。

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

## "bucket"

定义流体的桶物品。生成的桶物品会自动命名为 `<流体注册名>_bucket`，例如 `mud` 流体对应的桶物品为 `mud_bucket`。

可选。默认：无桶物品。若不使用，流体将没有对应的桶；其他流体容器仍然可用。

可以是 2 种形式之一：
* 布尔值：若为 `true`，桶物品将使用全部默认属性。
* JSON 对象（`{}`）：包含物品定义的各字段（见 [物品](./Items.md) 页面）。内联的桶定义中不允许包含 `"fluid"` 字段。

> 注意：`"bucket"` 只是为流体创建桶**物品**。流体方块本身能否放置在世界中，由 `"type"` 选择的工厂（如 `flowing`）及其 `"block"` 参数决定，见 [流体工厂](./FluidFactories.md)。
