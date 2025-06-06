package com.tty.entity.menu;

import com.tty.enumType.FunctionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FunctionItems extends BaseItem {
    private FunctionType type;
}
