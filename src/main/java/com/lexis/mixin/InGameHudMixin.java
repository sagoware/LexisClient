package com.lexis.mixin;

import com.lexis.hud.HUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    // 1.21.4 uyumlu en güncel render enjeksiyon noktası
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Oyun içi ekran çizilirken bizim HUD sınıfındaki render'ı çağırıp
        // ClickGUI'deki tüm ölçek, font ve rainbow ayarlarını ekrana basıyoruz reisim
        HUD.render(context, tickCounter.getTickDelta(true));
    }
}