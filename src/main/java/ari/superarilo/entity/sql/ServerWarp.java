package ari.superarilo.entity.sql;

public class ServerWarp {
    private Long id;
    private String warpId;
    private String warpName;
    private String createBy;
    private Double x;
    private Double y;
    private Double z;
    private String world;
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
}
