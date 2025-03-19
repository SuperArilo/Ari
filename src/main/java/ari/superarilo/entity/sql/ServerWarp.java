package ari.superarilo.entity.sql;

import lombok.Data;

@Data
public class ServerWarp {
    private Long id;
    private String warpId;
    private String warpName;
    private String createBy;
    private String location;
    private String showMaterial;
    private String permission;
    private Integer cost;

}
