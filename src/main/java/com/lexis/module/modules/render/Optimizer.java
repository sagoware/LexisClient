package com.lexis.module.modules.render;

import com.lexis.module.Category;
import com.lexis.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;

public class Optimizer extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private CloudRenderMode oldClouds = CloudRenderMode.FANCY;
    private Boolean oldAo = true;

    public Optimizer() {
        super("Optimizer", Category.RENDER, 0);
    }

    @Override
    public void onEnable() {
        if (mc.options == null) return;

        oldClouds = mc.options.getCloudRenderMode().getValue();
        oldAo = mc.options.getAo().getValue();

        mc.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        mc.options.getAo().setValue(false);

        if (mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }

    @Override
    public void onDisable() {
        if (mc.options == null) return;

        mc.options.getCloudRenderMode().setValue(oldClouds);
        mc.options.getAo().setValue(oldAo);

        if (mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }
}