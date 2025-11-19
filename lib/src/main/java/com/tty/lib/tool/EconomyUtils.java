package com.tty.lib.tool;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class EconomyUtils {

    private static Economy ECONOMY;

    public static Economy getInstance() {
        return ECONOMY;
    }

    public static void setInstance(Economy economy) {
        ECONOMY = economy;
    }

    /**
     * 为指定玩家增长指定的金钱
     * @param player 玩家
     * @param cost 增加金钱数量
     * @return 返沪 EconomyResponse
     */
    public EconomyResponse depositPlayer(Player player, double cost) {
        if (!isNull()) return ECONOMY.depositPlayer(player, cost);
        return null;
    }

    /**
     * 为指定玩家扣除相应的金钱
     * @param player 指定玩家
     * @param cost 扣除的金钱数量
     * @return 返沪 EconomyResponse
     */
    public static EconomyResponse withdrawPlayer(Player player, double cost) {
        if(!isNull()) return ECONOMY.withdrawPlayer(player, cost);
        return null;
    }

    /**
     * 获取指定玩家的存款数量
     * @param player 指定玩家
     * @return 返回该玩家的存款
     */
    public static Double getBalance(Player player) {
        if (!isNull()) return ECONOMY.getBalance(player);
        return 0.0;
    }

    /**
     * 检查指定玩家是否有足够的金钱
     * @param player 玩家
     * @param cost 花费的金额
     * @return true 足够，false 不足够
     */
    public static boolean hasEnoughBalance(Player player, double cost) {
        if(isNull()) return true;
        return getBalance(player) >= cost;
    }

    public static boolean isNull() {
        return ECONOMY == null;
    }

    /**
     * 获取复数形式的货币名称
     * @return 复数形式
     */
    public static String getNamePlural() {
        return ECONOMY.currencyNamePlural();
    }

    /**
     * 获取单数形式的货币名称
     * @return 单数形式
     */
    public static String getNameSingular() {
        return ECONOMY.currencyNameSingular();
    }
}
