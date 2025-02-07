package ari.superarilo.entity.sql;


public class PlayerHome {
    private Long id;
    private String homeId;
    private String homeName;
    private String playerUUID;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;
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
