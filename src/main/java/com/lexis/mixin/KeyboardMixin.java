package com.lexis.mixin;

import com.lexis.gui.ClickGUI;
import com.lexis.module.Module;
import com.lexis.module.ModuleManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKeyInject(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player != null && action == GLFW.GLFW_PRESS) {
            // Sağ Shift ile GUI'yi açma
            if (key == GLFW.GLFW_KEY_RIGHT_SHIFT && mc.currentScreen == null) {
                mc.setScreen(new ClickGUI());
                return;
            }

            // Normal oyunda kısayol tuşlarını tetikleme
            if (mc.currentScreen == null) {
                for (Module module : ModuleManager.getModules()) {
                    if (module.getKey() != 0 && module.getKey() == key) {
                        module.toggle();
                    }
                }
            }
        }
    }
}