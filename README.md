# Experiment

Hypixel SkyBlock 实验台自动求解模组，支持 Chronomatron、Ultrasequencer 和 Superpairs，自带 Serums 药水模式自动停手。

## 功能

- **Chronomatron** - 自动记忆闪烁顺序，玩家回合按顺序点击
- **Ultrasequencer** - 扫描染料编号，按从小到大顺序点击
- **Superpairs** - 自动配对相同物品（默认关闭）
- **Serums 模式** - 吃满 3 瓶形而上药水后自动停手并退出界面

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/use/installer/) 和 [Fabric API](https://modrinth.com/mod/fabric-api)
2. 下载 `experiment-0.1.0.jar` 放入 `.minecraft/mods/` 文件夹
3. 启动游戏

## 命令

所有命令以 `/experiment` 开头：

| 命令 | 说明 |
|------|------|
| `/experiment` | 查看当前状态 |
| `/experiment help` | 查看帮助和 GitHub 链接 |
| `/experiment on` / `off` | 开启 / 关闭模组 |
| `/experiment delay <ms>` | 设置点击延迟，默认 250ms |
| `/experiment superpairs on` / `off` | 开启 / 关闭 Superpairs |
| `/experiment serums on` / `off` | 开启 / 关闭 Serums 模式 |

## Serums 模式

开启后，模组会在拿满 3 瓶 Metaphysical Serum 的那一轮自动停手，并自动退出实验台。

- Chronomatron：第 9 轮后停止
- Ultrasequencer：第 6 轮后停止
- Superpairs：不受影响

适合刷经验药水的时候使用，效率最高。

## 构建

```bash
./gradlew build
```

构建产物在 `build/libs/` 目录下。

## 配置

配置文件：`.minecraft/config/experiment.json`

```json
{
  "delay": 250,
  "enabled": true,
  "superpairs": false,
  "serums": false
}
```

也可以直接在游戏内用命令修改。

## 免责声明

本模组仅为客户端辅助工具，所有操作均在本地完成。使用风险由使用者自行承担，请遵守服务器规则。

## License

MIT
