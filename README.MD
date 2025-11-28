# Ari - 基于现代化 Minecraft 服务器插件

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange)](https://adoptium.net/)
[![Folia](https://img.shields.io/badge/Folia-1.21.3%2B-brightgreen)](https://papermc.io/folia)
[![PaperMC](https://img.shields.io/badge/PaperMC-1.21.3%2B-brightgreen)](https://papermc.io/)
[![Vault Dependency](https://img.shields.io/badge/Vault-Required-blue)](https://github.com/MilkBowl/Vault)


✨ 一个高度兼容的Minecraft插件，完美支持 **PaperMC** 和 **Folia** 核心，兼容 1.21.3+  
📦 提供家园传送、地标系统、玩家交互等核心功能 + 实用游戏工具
## ✨ 核心特性
| 功能             | 描述                         |
|----------------|----------------------------|
| ✅ **双核兼容**     | 同时支持 PaperMC 和 Folia 服务端核心 |
| 🗺️ **GUI 系统** | 全功能可视化界面，支持自定义物品图标/名称/排序权重 |
| 💾 **多存储**     | 支持 SQLite 和 MySQL 数据库存储    |
| ⚙️ **深度定制**    | 所有功能均可通过配置文件高度自定义          |
## 🚀 功能列表
| 功能               | 描述                            |
|------------------|-------------------------------|
| 🏠 `Home`        | 多家园系统 + GUI管理，支持图标/名称/权重自定义   |
| 🗺️ `Warp`       | 地标系统 + GUI管理，支持传送权限/经济消耗/图标定制 |
| ↩️ `Back`        | 传送点或死亡位置返回                    |
| 📡 `Tpa`         | 玩家间传送请求                       |
| 🧭 `Spawn`       | 世界出生点传送                       |
| 🎲 `Rtp`         | 随机安全传送                        |
| ⏳ `时间控制`         | 实时调整游戏时间流速                    |
| 🌙 `夜晚加速`        | 多人睡眠加速跳夜                      |
| 📋 `Tab自定义`      | 完全自定义玩家Tab列表显示格式              |
| 📃 `白名单功能(Zako)` | 自定义服务器白名单管理方式                 |
| 📃 `物品属性修改`      | 可快捷修改物品名称和lore属性              |
## 🗯️ 权限列表
| 功能                      | 描述              |
|-------------------------|-----------------|
| `ari.command.home`      | 打开自己的家列表        |
| `ari.command.sethome`   | 设置家位置           |
| `ari.command.warp`      | 打开服务器公共传送点      |
| `ari.command.setwarp`   | 设置公共传送点         |
| `ari.command.back`      | 返回上一次传送位置       |
| `ari.command.tpa`       | 传送到其他玩家         |
| `ari.command.tpahere`   | 请求其他玩家传送到自己     |
| `ari.command.tpaaccept` | 接受传送请求          |
| `ari.command.tparefuse` | 拒绝传送请求          |
| `ari.command.spawn`     | 传送至出生点          |
| `ari.command.setspawn`  | 设置出生点           |
| `ari.command.rtp`       | 随机传送            |
| `ari.command.time`      | 控制时间流逝          |
| `ari.command.itemname`  | 修改物品名称          |
| `ari.command.itemlore`  | 修改物品描述          |
| `ari.command.zako`      | 白名单设置           |
| `ari.command.reload`    | 重载插件配置          |
| `ari.count.home.<数量>`   | 设置玩家可拥有的最大家数量限制 |
| `ari.count.warp.<数量>`   | 设置地标的最大数量限制     |
| `ari.keepinventory`     | 玩家死亡时保留背包内的物品   |
| `ari.keepexperience`    | 玩家死亡时保留经验值和等级   |
| `ari.cooldown.<传送类型>`   | 指定某种传送类型没有CD时间  |
## 🛠️ 安装指南

### 环境要求
- Folia 1.21.x 服务器核心
- Java 17 或更高版本
- [Vault](https://github.com/MilkBowl/Vault) 前置插件

### 安装步骤
1. 将插件放入 `plugins/` 目录
2. 重启服务器
3. 首次运行后编辑生成的 `plugins/Ari/config.yml`
4. 配置经济系统（如需地标创建费用）