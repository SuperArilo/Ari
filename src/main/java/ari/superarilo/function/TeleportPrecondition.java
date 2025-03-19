package ari.superarilo.function;

import ari.superarilo.entity.TeleportStatus;
import ari.superarilo.enumType.AriCommand;
import ari.superarilo.enumType.TeleportType;
import ari.superarilo.function.impl.TeleportPreconditionImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TeleportPrecondition {
    /**
     * 玩家传送到玩家时进行的传送检查，例如：是否满足传送条件，是否已经发送过了。如果满足则发送对应的传送消息到玩家
     * @param sender 被传送玩家
     * @param targetPlayer 目标玩家
     * @param ariCommand 传送类型 TPA TPAHERE
     */
    void preCheckStatus(Player sender, Player targetPlayer, AriCommand ariCommand);

    /**
     * 检查接收消息点击是否有效
     * @param sender 发送者，这里是被传送玩家
     * @param targetPlayer 目标玩家
     * @return 返回一个传送状态
     */
    TeleportStatus checkStatusV(Player sender, Player targetPlayer);
    static TeleportPrecondition create() {
        return new TeleportPreconditionImpl();
    }
}
