# 方块类型（Block Type）

方块有多种类型。一些常见方块需要特殊处理，例如在代码中使用特殊的超类。

为了支持这些特殊方块，JSON 中可以指定多种方块类型。

未来可以根据需要添加更多类型。

## "plain"

默认方块类型。

默认渲染层：solid。

无默认 BlockState 属性。

## "falling"

下方无支撑方块时就会下落的普通方块（类似沙砾）。

默认渲染层：cutout。

无默认 BlockState 属性。

参数：
* `"dust_color"`：当方块失去支撑但尚未下落时产生的灰尘粒子的颜色。可以是整数或对象；对象须包含 `"r"`、`"g"`、`"b"` 键以及可选的 `"a"` 键。
    * 可选。默认：白色（r=g=b=255）。

## "directional"

可以朝多个方向（包括上下）放置的方块。

默认渲染层：solid。

默认 BlockState 属性：facing

## "horizontal_directional"

可以朝多个水平方向放置的方块。

默认渲染层：solid。

默认 BlockState 属性：facing

## "rotated_pillar"

可以沿轴向放置的方块，如原木与柱子；相对的两个水平方向外观相同。

默认渲染层：solid。

默认 BlockState 属性：axis

## "slab"

具有台阶（slab）属性的方块，包括含水（waterlogging）。

默认渲染层：solid。

默认 BlockState 属性：type、waterlogged

## "stairs"

具有楼梯（stair）属性的方块，包括含水。

默认渲染层：solid。

默认 BlockState 属性：facing、half、shape、waterlogged

注意：该类型需要父级方块。可通过方块 JSON 的 `"parent"` 键指定，也可用参数 `"stairs_parent"` 单独指定用作楼梯基础形状的方块。

## "wall"

具有墙（wall）属性的方块，包括含水。

默认渲染层：solid。

默认 BlockState 属性：up、east_wall、north_wall、south_wall、west_wall、waterlogged

## "fence"

具有栅栏（fence）属性的方块，包括含水。

默认渲染层：solid。

默认 BlockState 属性：east、north、south、west、waterlogged

## "fence_gate"

具有栅栏门（fence gate）属性的方块。

默认渲染层：solid。

默认 BlockState 属性：open、powered、in_wall

参数：
* `"wood_type"`：使用的 WoodType（木头类型）名称，决定声音等属性。
    * 必填。

## "leaves"

具有树叶（leaves）属性的方块。

默认渲染层：cutout_mipped。默认也不视为实心。

默认 BlockState 属性：distance、persistent

## "door"

具有木门属性的方块。

默认渲染层：cutout。默认也不视为实心。

默认 BlockState 属性：facing、open、hinge、powered、half

参数：
* `"block_set_type"`：使用的 BlockSetType（方块集类型）名称，决定声音等属性。
    * 必填。

## "trapdoor"

具有木活板门属性的方块，包括含水。

默认渲染层：cutout。默认也不视为实心。

默认 BlockState 属性：open、half、powered、waterlogged

参数：
* `"block_set_type"`：使用的 BlockSetType（方块集类型）名称，决定声音等属性。
    * 必填。

## "sapling"

树的树苗。仅支持单树苗生长，不支持 2x2 的"巨树"。

默认渲染层：cutout。

无默认 BlockState 属性。

参数：
* `"tree_feature"`：树苗长大时生成的地物（feature）注册名。
    * 必填。

## "liquid"

表示填充在方块网格中的流体的方块。

默认渲染层：translucent。默认视为可透视。

默认 BlockState 属性：level。

参数：
* `"fluid"`：所包含流体的注册名。
    * 必填。
