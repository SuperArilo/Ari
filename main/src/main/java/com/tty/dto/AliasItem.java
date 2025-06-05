package com.tty.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AliasItem {
    @SerializedName("enable")
    private boolean enable;
    @SerializedName("tab-complete")
    private boolean tabComplete;
}
