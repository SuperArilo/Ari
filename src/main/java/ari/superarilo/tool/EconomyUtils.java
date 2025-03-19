package ari.superarilo.tool;

import ari.superarilo.exception.NoEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyUtils {

    public Economy economy;

    public EconomyUtils(Economy economy) {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null) {
                this.economy = provider.getProvider();
                Log.info("Loaded economy plugin: " + this.economy.getName());
            } else {
                Log.error("No economy plugin is loaded");
            }
        } else {
            Log.warning("no vault, no Economy");
        }
    }

    public EconomyResponse depositPlayer(Player player, double cost) throws NoEconomy {
        if (this.isNull()) throw new NoEconomy("no vault, no Economy");
        return this.economy.depositPlayer(player, cost);
    }

    public EconomyResponse withdrawPlayer(Player player, double cost) throws NoEconomy {
        if (this.isNull()) throw new NoEconomy("no vault, no Economy");
        return this.economy.withdrawPlayer(player, cost);
    }

    public double getBalance(Player player) throws NoEconomy {
        if (this.isNull()) throw new NoEconomy("no vault, no Economy");
        return this.economy.getBalance(player);
    }

    private boolean isNull() {
        return this.economy == null;
    }
}
