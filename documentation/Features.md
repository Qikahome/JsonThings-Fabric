# JsonThings 功能清单

## 已完成（可用，计划内的功能均已实现……大概吧）

* Voxel 形状"模型"
* 食物定义
* 物品等级（Tier）
* 方块材质（Block Material）
* 护甲材料
* 附魔

## 可用（主要功能已实现，额外功能开发中）

* 物品
  * 也许还漏了些物品类型
  * 自定义物品属性，通过 NBT 指定，可供模型使用。
* 方块
  * 矿石
  * 也许还漏了些方块类型
* 流体
  * 基础
  * 流动（含实体交互：游泳、溺水等；详见 FabricatedForgeFluid）
* 文档

## 开发中（可用但不完整）

* 脚本系统（Scripting）
  * 移植版已包含基于 Rhino 的脚本系统（`things/scripting/rhino`），已测试可用，仍在持续完善中。

## 发布前待办

[ 目前没有 ]

## 发布后计划（FUTURE）

* 界面布局
* 容器类型
* 数据生成（Datagen）

## 可能实现（MAYBE）

* 结构（Structure）与拼图块（Jigsaw）
* 世界生成修改（在 BiomeLoadingEvent 中执行的系列追加）

## 已否决

* 1.17+ 实体模型
  * 原因：已有其他模组实现。https://www.curseforge.com/minecraft/mc-mods/entity-model-json
