package ari.superarilo.enumType;

public enum TeleportType {
    POINT("定点传送"),
    PLAYER("玩家传送"),
    BACK("返回上一个地点"),
    DBACK("返回上一个死亡地点"),
    RANDOM("随机传送");

    private final String dis;

    TeleportType(String dis) {
        this.dis = dis;
    }

    public String getDis() {
        return dis;
    }
}
