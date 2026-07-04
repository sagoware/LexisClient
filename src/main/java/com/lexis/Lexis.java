package com.lexis;

import com.lexis.module.ModuleManager;
import net.fabricmc.api.ModInitializer;

public class Lexis implements ModInitializer {
    @Override
    public void onInitialize() {
        // Listenin ilk açılışta dolmasını kesinleştiriyoruz
        ModuleManager.init();
    }
}