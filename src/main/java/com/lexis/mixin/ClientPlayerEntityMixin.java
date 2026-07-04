package com.lexis.mixin;

import com.lexis.module.Module;
import com.lexis.module.ModuleManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickInject(CallbackInfo ci) {
        // Tüm aktif modüllerin oyun içi döngüsünü tetikliyoruz
        for (Module m : ModuleManager.getModules()) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }
}