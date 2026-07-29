package io.github.suel_ki.uei;

import io.github.suel_ki.uei.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Uei {
    public static final String MOD_ID = "uei";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static void init() {
        Config.init();
    }
}
