package com.tty.lib.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AliasItem {
    @SerializedName("enable")
    private boolean enable;
    @SerializedName("usage")
    private String usage;
}
