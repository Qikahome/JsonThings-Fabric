# 从方块材质（Block Material）迁移

方块材质过去用于定义方块的地图颜色与一些基础属性。

它们在 1.20 中已被移除。已有的材质定义需要迁移到方块 JSON 中。

## "map_color"

改用方块中的地图颜色属性。格式相同。

## "liquid"

决定不为此提供替代。它已隐含在 liquid 方块类型中。

## "flammable"

改用方块的 "ignited_by_lava" 属性。

## "replaceable"

该属性原样迁移到了方块中。

## "solid"

若原材质为非实心，将方块的 "not_solid" 属性设为 true。

## "blocks_motion"

该属性原样迁移到了方块中。

## "solid_blocking"

此属性没有对应物：它由 Voxel 形状决定，并可被 "force_solid" 与 "blocks_motion" 属性覆盖。

## "push_reaction"

该属性原样迁移到了方块中。
