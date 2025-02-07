package ari.superarilo.entity.sql;

public class ServerWarp {
    private Long id;
    private String warpId;
    private String warpName;
    private String createBy;
    private String location;
    private String showMaterial;
    private String permission;
    private Integer cost;

    public Long getId() {
        return id;
    }

    public String getWarpId() {
        return warpId;
    }

    public void setWarpId(String warpId) {
        this.warpId = warpId;
    }

    public String getWarpName() {
        return warpName;
    }

    public void setWarpName(String warpName) {
        this.warpName = warpName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getShowMaterial() {
        return showMaterial;
    }

    public void setShowMaterial(String showMaterial) {
        this.showMaterial = showMaterial;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
