package com.tty.entity.menu;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDataMenu extends BaseMenu {
    private DataItems dataItems;
}
