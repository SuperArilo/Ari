package ari.superarilo.entity.sql;


public class PlayerHome {
    private Long id;
    private String homeId;
    private String homeName;
    private String playerUUID;
    private Double x;
    private Double y;
    private Double z;
    private String world;
    private String showMaterial;

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getShowMaterial() {
        return showMaterial;
    }

    public void setShowMaterial(String showMaterial) {
        this.showMaterial = showMaterial;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public Long getId() {
        return id;
    }
}
