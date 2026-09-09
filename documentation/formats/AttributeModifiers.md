# 属性修饰符（Attribute Modifier）定义

属性修饰符定义没有具名形式，而是包含在其他 JSON 文件中。

使用示例见 [物品](./Items.md#attribute_modifiers)。

## JSON 文件的基本结构

```json
{
  "slot": "mainhand",
  "attribute": "minecraft:attack_damage",
  "uuid": "6b4ecf78-9f10-4328-bf80-e48a4a5228d8",
  "name": "testpack:modifier_name",
  "amount": 0.5,
  "operation": "addition"
}
```

## "slot"

定义该修饰符生效时物品所在的装备槽。

必填。

必须是有效的装备槽名称字符串，可为：`mainhand`、`offhand`、`head`、`chest`、`legs`、`feet`。

## "attribute"

定义要应用修饰符的属性。

必填。

必须是资源位置字符串，如 `"attack_damage"`，或 `"minecraft:attack_damage"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "uuid"

定义修饰符使用的 UUID。

UUID 唯一标识一个修饰符，使其可以被精确地添加或移除，而不会造成重复或影响同类型的其他修饰符。

可选。默认：自动生成随机 UUID。

自动生成的 UUID 在不同运行之间不一致，不应在持久化修饰符中使用。

必须是包含合法 UUID 的字符串。可以使用类似 [Online UUID Generator](https://www.uuidgenerator.net/version4) 的网站生成，也有离线工具可用。

## "name"

定义修饰符的名称。同名修饰符会互相替代而不是叠加。

必填。

必须是字符串。

## "amount"

定义要对属性值修改的量。

必填。

必须是数字。允许小数。

## "operation"

定义应用修饰符时使用的运算。

必填。

可以是数字或以下字符串之一：

* `0` 或 `"addition"`：将 amount 直接加到累计值上。
  * 公式：`accumulated = accumulated + amount`
* `1` 或 `"multiply_base"`：将 amount 乘以属性的基础值，再加到累计值上。
  * 公式：`accumulated = accumulated + amount * base_value`
* `2` 或 `"multiply_total"`：将累计值乘以 `(1 + amount)`。
  * 公式：`accumulated = accumulated * (1 + amount)`

amount 取负值可用来按百分比削减总量。例如：operation 为 "multiply_total" 且 amount 为 `-0.2` 时，实际效果是总量减少 20%，即总量的 80%。

**注意：** 修饰符中的数值不会按原样显示。乘法型修饰符会以百分比显示（乘以 100）。加法型修饰符原样显示，但击退抗性例外，它要乘以 10。因此 0.1 的击退抗性会显示为 "1"。
