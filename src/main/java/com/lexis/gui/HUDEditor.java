package com.lexis.gui;

import com.lexis.module.modules.render.CustomFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HUDEditor extends Screen {
    public static int wmX = 10, wmY = 10;
    // İlk açılışta sıfır ise ekranın en sağına yasla reis
    public static int alX = 0, alY = 10;

    private boolean draggingWM = false;
    private boolean draggingAL = false;
    private int dragX = 0, dragY = 0;

    public HUDEditor() {
        super(Text.of("HUD Editor"));
        // Ekran genişliğini alıp ArrayList kutusunu sağ üst köşeye hizalıyoruz
        if (alX == 0) {
            alX = MinecraftClient.getInstance().getWindow().getScaledWidth() - 110;
        }
    }

    private Text getCustomText(String text) {
        MutableText node = Text.literal(text);
        if (CustomFont.isFontEnabled) {
            return node.setStyle(Style.EMPTY.withFont(Identifier.of("lexis", "default")));
        }
        return node;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float scale = (float) ClickGUI.getTextScale();

        if (ClickGUI.isWatermarkEnabled()) {
            int wmW = (int) (textRenderer.getWidth("Lexis Premium [1.21.4]") * scale) + 8;
            int wmH = (int) (12 * scale) + 6;
            context.fill(wmX - 4, wmY - 3, wmX + wmW, wmY + wmH, 0x443A86FF);

            context.getMatrices().push();
            context.getMatrices().translate(wmX, wmY, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.drawText(textRenderer, getCustomText("Lexis Premium [1.21.4] (Sürükle)"), 0, 0, 0xFFFFFFFF, true);
            context.getMatrices().pop();
        }

        if (ClickGUI.isArrayListEnabled()) {
            int alW = 100; int alH = 60;
            context.fill(alX - 4, alY - 3, alX + alW, alY + alH, 0x448338EC);

            context.getMatrices().push();
            context.getMatrices().translate(alX, alY, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.drawText(textRenderer, getCustomText("[ArrayList Bölgesi]"), 0, 0, 0xFFFFFFFF, true);
            context.getMatrices().pop();
        }

        int btnW = 120; int btnH = 22;
        int btnX = (this.width - btnW) / 2; int btnY = this.height - 40;
        boolean hoverSave = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        context.fill(btnX, btnY, btnX + btnW, btnY + btnH, hoverSave ? 0xFF2ECC71 : 0xFF27AE60);
        context.drawText(textRenderer, getCustomText("Ayarları Kaydet"), btnX + 22, btnY + 7, 0xFFFFFFFF, false);

        if (draggingWM) { wmX = mouseX - dragX; wmY = mouseY - dragY; }
        if (draggingAL) { alX = mouseX - dragX; alY = mouseY - dragY; }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scale = (float) ClickGUI.getTextScale();
        if (ClickGUI.isWatermarkEnabled()) {
            int wmW = (int) (textRenderer.getWidth("Lexis Premium [1.21.4]") * scale) + 8;
            int wmH = (int) (12 * scale) + 6;
            if (mouseX >= wmX - 4 && mouseX <= wmX + wmW && mouseY >= wmY - 3 && mouseY <= wmY + wmH) {
                draggingWM = true; dragX = (int) mouseX - wmX; dragY = (int) mouseY - wmY; return true;
            }
        }
        if (ClickGUI.isArrayListEnabled()) {
            if (mouseX >= alX - 4 && mouseX <= alX + 100 && mouseY >= alY - 3 && mouseY <= alY + 60) {
                draggingAL = true; dragX = (int) mouseX - alX; dragY = (int) mouseY - alY; return true;
            }
        }
        int btnW = 120; int btnH = 22; int btnX = (this.width - btnW) / 2; int btnY = this.height - 40;
        if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
            client.setScreen(new ClickGUI()); return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingWM = false; draggingAL = false; return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}