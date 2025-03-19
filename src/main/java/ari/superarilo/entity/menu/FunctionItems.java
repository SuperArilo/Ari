package ari.superarilo.entity.menu;

import ari.superarilo.enumType.FunctionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FunctionItems extends BaseItem {
    private FunctionType type;
}
