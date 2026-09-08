# JsonThings-Fabric (1.20.1)

JsonThings 的 Fabric 1.20.1 移植（上游 Forge 版同源）。

## 流体系统（FabricatedForgeFluid）

流体实体交互（推动 / 游泳 / 溺水 / 灭火 / 落距）与客户端渲染注册由
[FabricatedForgeFluid](https://github.com/Qikahome/FabricatedForgeFluid)（jar-in-jar 内嵌）提供，
JsonThings 只负责声明流体（`fluid` / `fluid_type` 数据包）并把视觉数据交给
`ExtendedFluidType`（继承 `FabricatedFluidType`）。

### fluid_type 的 `tag` 字段（可选）

实体交互按 **fluid tag** 桥接：`ExtendedFluidType` 构造时传入的 `TagKey<Fluid>` 会登记进
`FabricatedFluidType.tagToTypeMap`，实体接触流体时通过 `state.getTags()` 反查命中。

- **默认**：取该 `fluid_type` 的注册名（`<ns>:<path>`）作为 tag id
- **覆盖**：可在 fluid_type json 里显式指定：
  ```json
  {
    "type": "jsonthings:fluid_type",
    "tag": "minecraft:water"
  }
  ```
- **注意**：要让实体交互生效，流体必须**真实挂上对应 tag**，即数据包需提供
  `data/<ns>/tags/fluids/<path>.json`。tag 的 values 需同时包含 **source 与 flowing** 两个形态：
  流动流体的 flowing 注册名固定为 `<path>_flowing`（后缀式）。例如 `fluid/test.json`：
  ```json
  {
    "replace": false,
    "values": [
      "testpack:test",
      "testpack:test_flowing"
    ]
  }
  ```
  否则只挂 source 的话，flowing 形态下 `state.getTags()` 查不到、交互不会命中。

### 构建

依赖 PL（`modImplementation`，不 include）与 `qikahome:FabricatedForgeFluid-1.20.1`（jar-in-jar）。
GitHub Packages 仓库需 `gpr.user` / `gpr.key` 凭据（gradle.properties 或环境变量）。

## 许可证

本项目为**双许可证**：
- 源自上游 JsonThings（Forge）的部分（`dev.gigaherz.jsonthings.*` 代码基底）采用 **BSD 3-Clause**，保留原作者 David Quintana 版权声明。
- 本 Fabric 移植的新增代码（工程骨架、入口、资源包处理、mixin、FabricatedForgeFluid 对接等）采用 **MIT**。

详见 [LICENSE.txt](LICENSE.txt)。
