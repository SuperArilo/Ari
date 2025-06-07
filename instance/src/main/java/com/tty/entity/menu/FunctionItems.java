package com.tty.entity.menu;

import com.tty.lib.enum_type.FunctionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FunctionItems extends BaseItem {
    private FunctionType type;
}
