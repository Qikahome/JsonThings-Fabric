# 原料（Ingredient）定义

原料没有具名形式，而是用在其他 JSON 文件内部。

由于初始化时机的原因，_Things_ 中使用的原料不能是自定义（模组定义）原料。只支持 `item` 与 `tag` 两种原料。

物品原料包含 `"item"` 键，值为指示物品注册名的资源位置。

```json
{
  "item": "minecraft:clay"
}
```

标签原料包含 `"tag"` 键，值为指示物品标签的资源位置。

```json
{
  "tag": "minecraft:string"
}
```
