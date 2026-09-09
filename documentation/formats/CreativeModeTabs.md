# 创造模式标签页（Creative Mode Tab）定义

创造模式标签页定义允许向创造模式菜单中添加新的标签页。

创造模式标签页定义放在 thing 包的 `creative_mode_tab` 目录中。

物品定义中的 ["group"](./Items.md#group) 与 ["creative_menu_stacks"](./Items.md#creative_menu_stacks) 用法示例，可参见物品定义相关页面。

## JSON 文件的基本结构

```json
{
  "icon": "minecraft:stick",
  "translation_key": "namespace:tab_name",
  "right_side": false,
  "items": ["minecraft:apple"],
  "before": "",
  "after": ""
}
```

## "icon"

定义用作标签页图标的物品。

必填。

可以是资源位置字符串，如 `"string"`，或 `"minecraft:stick"`；也可以是包含堆叠定义的 JSON 对象（`{}`）。

## "translation_key"

该标签页标题使用的翻译键。

可选。

默认值：`<模组命名空间>.<标签页 id>`（即定义文件的位置，如 `namespace.tab_name`）。

## "right_side"

定义该标签页是否对齐到创造菜单界面的右侧栏。

可选。默认：false。

必须是布尔值（`false` 或 `true`）。

## "items"

定义标签页直接包含的物品。其他通过 JSON 定义的物品也可以借助各自的 group / creative_menu_stacks 字段加入本标签页。

可选。默认：空。

必须是物品堆叠列表。每一项可以是资源位置字符串，如 `"string"`，或 `"minecraft:stick"`；也可以是包含堆叠定义的 JSON 对象（`{}`）。

## "before"

列出应放置于本标签页右侧（即本标签页排在其前面）的创造模式标签页。

可选。

必须是资源位置字符串列表，如 `"string"`，或 `"minecraft:stick"`。与模型 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

> 注意：当前 Fabric 移植版尚不支持相对其他标签页的排序（新标签页一律追加在末尾），`"before"` 与 `"after"` 会被解析但不会产生排序效果。

## "after"

列出应放置于本标签页左侧（即本标签页排在其后面）的创造模式标签页。

可选。

必须是资源位置字符串列表，如 `"string"`，或 `"minecraft:stick"`。与模型 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

> 注意：当前 Fabric 移植版尚不支持相对其他标签页的排序（新标签页一律追加在末尾），`"before"` 与 `"after"` 会被解析但不会产生排序效果。
