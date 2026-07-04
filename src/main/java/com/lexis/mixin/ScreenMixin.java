package com.lexis.mixin;

import com.lexis.gui.ClickGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class) // Veya hangi renderer sınıfını kullanıyorsan
public class ScreenMixin {

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void onRenderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // KESİN KONTROL: Şu an açık olan ekran bizim ClickGUI değilse vanilla koduna ASLA dokunma!
        if (!(MinecraftClient.getInstance().currentScreen instanceof ClickGUI)) {
            return; // Normal menüyse direkt çık, dokuları bozma
        }

        // Eğer buradaysak Sağ Shift'e basılmıştır ve sadece ClickGUI açıktır.
        // Orijinal ESC blurunun gelmesini istediğin için burayı boş bırakabiliriz veya ci.cancel() yapmayız.
    }
}