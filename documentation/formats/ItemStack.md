# 物品堆叠（Item Stack）定义

物品堆叠将物品与数量及可选的 NBT 关联起来，是物品被使用时的基本对象。

物品堆叠定义没有具名形式，而是内嵌在其他 JSON 文件中。

使用示例见 [物品](./Items.md#creative_menu_stacks)。

## JSON 文件的基本结构

```json
{
  "item": "minecraft:stick",
  "count": 10,
  "nbt": { }
}
```

## "item"

定义堆叠中使用的物品。

在物品的 "creative_menu_stacks" 中不支持。

在支持的场合为必填。默认：由上下文提供的物品。

必须是资源位置字符串，如 `"string"`，或 `"minecraft:stick"`。与配方 JSON 以及其他原版文件一致，
省略命名空间（冒号前的部分）时默认使用 "minecraft"。

## "count"

定义堆叠中的物品数量。

在物品的 "creative_menu_stacks" 中不支持。创造模式堆叠应始终为 1。

可选。默认：1。

必须是大于零的正整数。

## "nbt"

定义附加到堆叠上的 NBT 标签。

可选。默认：无 NBT。

可以有 2 种格式：

* 字符串，使用游戏内 `/give` 命令的标签格式。
    ```
      "{Damage:10b}"
    ```
* JSON 对象（`{}`），显式定义标签层级。
    ```json
      {
        "Damage": 10
      }
    ```

字符串形式允许指定显式类型，在某些情况下可能是必要的。
