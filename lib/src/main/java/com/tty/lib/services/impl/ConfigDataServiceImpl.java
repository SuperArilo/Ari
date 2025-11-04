package com.tty.lib.services.impl;

import com.tty.lib.Lib;
import com.tty.lib.enum_type.FilePath;
import com.tty.lib.services.ConfigDataService;

public class ConfigDataServiceImpl implements ConfigDataService {
    @Override
    public String getValue(String keyPath) {
        return Lib.C_INSTANCE.getValue(keyPath, FilePath.Lang);
    }
}
