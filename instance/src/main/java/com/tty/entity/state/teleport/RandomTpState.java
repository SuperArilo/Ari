package com.tty.entity.state.teleport;

import com.tty.Ari;
import com.tty.entity.state.State;
import com.tty.enumType.FilePath;
import com.tty.enumType.TeleportType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RandomTpState extends State {

    @Getter
    private final World world;
    @Getter
    private final TeleportType type = TeleportType.RTP;

    @Getter
    @Setter
    private Location trueLocation;

    public RandomTpState(Entity owner, @NotNull World world) {
        super(owner, Ari.C_INSTANCE.getValue("rtp.search-count", FilePath.FunctionConfig, Integer.class, 10));
        this.world = world;
    }
}
