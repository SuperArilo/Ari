package ari.superarilo.entity.sql;

import lombok.Data;

@Data
public class PlayerHome {
    private Long id;
    private String homeId;
    private String homeName;
    private String playerUUID;
    private String location;
    private String showMaterial;
}
