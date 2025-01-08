package ari.superarilo.function;

import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.function.impl.TeleportPreconditionImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TeleportPrecondition {
    void preCheckStatus(Player sender, Player targetPlayer, AriCommand ariCommand);
    TeleportStatus preCheckStatus(Player sender, Location targetLocation);
    TeleportStatus checkStatusV(Player sender, Player targetPlayer);
    static TeleportPrecondition create() {
        return new TeleportPreconditionImpl();
    }
}
