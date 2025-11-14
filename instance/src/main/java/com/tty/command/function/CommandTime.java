package com.tty.command.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.function.TimeManager;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.enum_type.TimePeriod;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTime implements CommandTabsList {

    private final Player player;

    public CommandTime(Player sender) {
        this.player = sender;
    }

    public void control(String timePeriod) {
        TimePeriod period;
        try {
            period = TimePeriod.valueOf(timePeriod.toUpperCase());
        } catch (Exception e) {
            this.player.sendMessage(ConfigUtils.t("server.time.not-exist-period", LangType.PERIOD.getType(), timePeriod));
            return;
        }
        World world = this.player.getWorld();
        if (!world.isBedWorks()) {
            this.player.sendMessage(ConfigUtils.t("server.time.not-allowed-world"));
            return;
        }
        TimeManager.build(world).timeSet(period.getStart());
        String value = Ari.C_INSTANCE.getValue("server.time.tips", FilePath.Lang);
        if (value == null) {
            this.player.sendMessage("no content " + timePeriod + "in lang");
            return;
        }
        value = value.replace(LangType.TIME.getType(), Ari.C_INSTANCE.getValue("server.time.name." + period.getDescription(), FilePath.Lang));
        this.player.sendMessage(ComponentUtils.text(value));
    }

    @Override
    public List<String> getTabs(int line) {
        List<String> list = new ArrayList<>();
        switch (line) {
            case 1 -> {
                for (TimePeriod timePeriod : TimePeriod.values()) {
                    list.add(timePeriod.getDescription());
                }
            }
        }
        return list;
    }
}
