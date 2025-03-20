# Ari - 基于 Folia 的现代化 Minecraft 服务器插件

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange)](https://adoptium.net/)
[![Folia 1.20+](https://img.shields.io/badge/Folia-1.20%2B-brightgreen)](https://papermc.io/folia)
[![Vault Dependency](https://img.shields.io/badge/Vault-Required-blue)](https://github.com/MilkBowl/Vault)

专为现代 Minecraft 服务器设计的全功能基础插件，完美适配 Folia 多线程架构，提供稳定高效的玩家交互体验。

## ✨ 功能概览

### 🏠 家园系统
| 命令                 | 描述      | 权限节点                     |
|--------------------|---------|--------------------------|
| `/sethome <名称>`    | 设置个人家园  | `ari.command.sethome`    |
| `/home <名称>`       | 传送至指定家园 | `ari.command.home`       |
| `/deletehome <名称>` | 删除家园    | `ari.command.deletehome` |

### 🌍 地标系统
| 命令                 | 描述     | 权限节点                     |
|--------------------|--------|--------------------------|
| `/setwarp <名称>`    | 创建公共地标 | `ari.command.setwarp`    |
| `/warp <名称>`       | 传送至地标  | `ari.command.warp`       |
| `/deletewarp <名称>` | 删除地标   | `ari.command.deletewarp` |

### 📨 即时传送
| 命令              | 描述       | 权限节点                    |
|-----------------|----------|-------------------------|
| `/tpa <玩家>`     | 请求传送到玩家  | `ari.command.tpa`       |
| `/tpahere <玩家>` | 请求玩家传送到你 | `ari.command.tpahere`   |
| `/tpaaccept`    | 接受传送请求   | `ari.command.tpaaccept` |
| `/tparefuse`    | 拒绝传送请求   | `ari.command.tparefuse` |

### ⏮️ 位置回溯
| 命令      | 描述     | 权限节点               |
|---------|--------|--------------------|
| `/back` | 返回上次位置 | `ari.command.back` |

## 🛠️ 安装指南

### 环境要求
- Folia 1.20.x 服务器核心
- Java 17 或更高版本
- [Vault](https://github.com/MilkBowl/Vault) 经济系统

### 安装步骤
1. 将插件放入 `plugins/` 目录
2. 重启服务器
3. 首次运行后编辑生成的 `plugins/Ari/config.yml`
4. 配置经济系统（如需地标创建费用）