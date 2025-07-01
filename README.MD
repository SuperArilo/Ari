# Ari - 基于现代化 Minecraft 服务器插件

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange)](https://adoptium.net/)
[![Folia 1.21+](https://img.shields.io/badge/Folia-1.21%2B-brightgreen)](https://papermc.io/folia)
[![PaperMC](https://img.shields.io/badge/PaperMC-1.21%2B-brightgreen)](https://papermc.io/)
[![Vault Dependency](https://img.shields.io/badge/Vault-Required-blue)](https://github.com/MilkBowl/Vault)


✨ 一个高度兼容的Minecraft插件，完美支持 **PaperMC** 和 **Folia** 核心，兼容 1.21 至最新版本  
📦 提供家园传送、地标系统、玩家交互等核心功能 + 实用游戏工具
## ✨ 核心特性
| 功能             | 描述                         |
|----------------|----------------------------|
| ✅ **双核兼容**     | 同时支持 PaperMC 和 Folia 服务端核心 |
| 🗺️ **GUI 系统** | 全功能可视化界面，支持自定义物品图标/名称/排序权重 |
| 💾 **多存储**     | 支持 SQLite 和 MySQL 数据库存储    |
| ⚙️ **深度定制**    | 所有功能均可通过配置文件高度自定义          |

## 🚀 功能列表
### 传送系统
| 功能         | 描述                            |
|------------|-------------------------------|
| 🏠 `Home`  | 多家园系统 + GUI管理，支持图标/名称/权重自定义   |
| 🗺️ `Warp` | 地标系统 + GUI管理，支持传送权限/经济消耗/图标定制 |
| ↩️ `Back`  | 传送点或死亡位置返回                    |
| 📡 `Tpa`   | 玩家间传送请求                       |
| 🧭 `Spawn` | 世界出生点传送                       |
| 🎲 `Rtp`   | 随机安全传送                        |

### 游戏工具
| 功能               | 描述               |
|------------------|------------------|
| ⏳ `Time Control` | 实时调整游戏时间流速       |
| 🌙 `Skip Night`  | 多人睡眠加速跳夜         |
| 📋 `Tab`         | 完全自定义玩家Tab列表显示格式 |
## 🛠️ 安装指南

### 环境要求
- Folia 1.21.x 服务器核心
- Java 17 或更高版本
- [Vault](https://github.com/MilkBowl/Vault) 经济系统

### 安装步骤
1. 将插件放入 `plugins/` 目录
2. 重启服务器
3. 首次运行后编辑生成的 `plugins/Ari/config.yml`
4. 配置经济系统（如需地标创建费用）

## ❓ 常见问题
```bash
Q: 是否支持 Folia 的异步线程？
A: ✅ 完全兼容 Folia 的线程模型，所有操作均线程安全
```
```bash
Q: 如何自定义GUI布局？
A: 编辑 /module/带有 gui 标志的 yml即可
```