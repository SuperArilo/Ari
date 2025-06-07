package com.tty.dto;

import com.tty.lib.enum_type.TitleInputType;
import lombok.Data;

@Data
public class OnEdit {
    private CustomInventoryHolder holder;
    private TitleInputType type;

    public static OnEdit build(CustomInventoryHolder holder, TitleInputType type) {
        OnEdit edit = new OnEdit();
        edit.setHolder(holder);
        edit.setType(type);
        return edit;
    }
}
