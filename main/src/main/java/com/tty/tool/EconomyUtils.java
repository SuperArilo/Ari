package com.tty.tool;

import com.tty.lib.tool.Log;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyUtils {

    public Economy economy;

    public EconomyUtils() {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null) {
                this.economy = provider.getProvider();
                Log.info("Loaded economy plugin: " + this.economy.getName());
            } else {
                Log.warning("No economy plugin is loaded");
            }
        } else {
            Log.warning("no vault, no Economy");
        }
    }

    /**
     * 为指定玩家增长指定的金钱
     * @param player 玩家
     * @param cost 增加金钱数量
     * @return 返沪 EconomyResponse
     */
    public EconomyResponse depositPlayer(Player player, double cost) {
        if (!this.isNull()) return this.economy.depositPlayer(player, cost);
        return null;
    }

    /**
     * 为指定玩家扣除相应的金钱
     * @param player 指定玩家
     * @param cost 扣除的金钱数量
     * @return 返沪 EconomyResponse
     */
    public EconomyResponse withdrawPlayer(Player player, double cost) {
        if(!this.isNull()) return this.economy.withdrawPlayer(player, cost);
        return null;
    }

    /**
     * 获取指定玩家的存款数量
     * @param player 指定玩家
     * @return 返回该玩家的存款
     */
    public Double getBalance(Player player) {
        if (!this.isNull()) return this.economy.getBalance(player);
        return null;
    }

    /**
     * 检查指定玩家是否有足够的金钱
     * @param player 玩家
     * @param cost 花费的金额
     * @return true 足够，false 不足够
     */
    public boolean hasEnoughBalance(Player player, double cost) {
        if(this.isNull()) return false;
        return this.getBalance(player) >= cost;
    }

    public boolean isNull() {
        return this.economy == null;
    }

    /**
     * 获取复数形式的货币名称
     * @return 复数形式
     */
    public String getNamePlural() {
        return this.economy.currencyNamePlural();
    }

    /**
     * 获取单数形式的货币名称
     * @return 单数形式
     */
    public String getNameSingular() {
        return this.economy.currencyNameSingular();
    }
}
