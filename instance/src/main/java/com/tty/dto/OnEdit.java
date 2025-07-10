package com.tty.dto;

import com.tty.lib.enum_type.FunctionType;
import lombok.Data;

@Data
public class OnEdit {

    private CustomInventoryHolder holder;
    private FunctionType type;

    public static OnEdit build(CustomInventoryHolder holder, FunctionType type) {
        OnEdit edit = new OnEdit();
        edit.setHolder(holder);
        edit.setType(type);
        return edit;
    }
}
