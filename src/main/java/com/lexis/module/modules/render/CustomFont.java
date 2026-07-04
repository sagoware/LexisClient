package com.lexis.module.modules.render;

import com.lexis.module.Category;
import com.lexis.module.Module;

public class CustomFont extends Module {
    public static boolean isFontEnabled = true;

    public CustomFont() {
        super("Use Font", Category.RENDER, 0);
        // İsteğin üzerine oyun başında otomatik açık geliyor
        if (!this.isEnabled()) {
            this.toggle();
        }
    }

    @Override
    public void onEnable() {
        isFontEnabled = true;
    }

    @Override
    public void onDisable() {
        isFontEnabled = false;
    }
}