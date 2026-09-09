# 音效事件（Sound Event）定义

实验性。

音效事件定义允许你定义一个新的可播放声音。

音效事件定义放在 thing 包的 `sound_event` 目录中。

例如：
```
/things/examplepack/sound_event/scream.json
```

## JSON 文件的基本结构

```json
{
  "range": 16
}
```

## "range"

定义声音可被听到的最大距离。

可选。默认：使用旧版声音机制。

必须是正数。
