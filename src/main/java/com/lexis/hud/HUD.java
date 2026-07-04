package com.lexis.hud;

import com.lexis.gui.ClickGUI;
import com.lexis.gui.HUDEditor;
import com.lexis.module.Module;
import com.lexis.module.ModuleManager;
import com.lexis.module.modules.render.CustomFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HUD {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static int getRainbowColor(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 12.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.75f, 0.95f).getRGB();
    }

    private static int getCurrentColor(int delay) {
        return switch (ClickGUI.getHudColorMode()) {
            case 1 -> 0xFF8338EC; // Premium Mor
            case 2 -> getRainbowColor(delay); // Canlı Akıcı Rainbow
            default -> 0xFF3A86FF; // Neon Mavi
        };
    }

    private static Text getFormattedText(String text) {
        MutableText textNode = Text.literal(text);
        if (CustomFont.isFontEnabled) {
            return textNode.setStyle(Style.EMPTY.withFont(Identifier.of("lexis", "default")));
        }
        return textNode;
    }

    public static void render(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.options.hudHidden) return;

        float scale = (float) ClickGUI.getTextScale();
        int colorCount = 0;

        // -----------------------------------------------------------------
        // 1. LOGO & WATERMARK ÇİZİMİ
        // -----------------------------------------------------------------
        if (ClickGUI.isWatermarkEnabled()) {
            context.getMatrices().push();
            context.getMatrices().translate(HUDEditor.wmX, HUDEditor.wmY, 0);
            context.getMatrices().scale(scale, scale, 1.0f);
            context.drawText(mc.textRenderer, getFormattedText("Lexis Premium [1.21.4]"), 0, 0, getCurrentColor(0), true);
            context.getMatrices().pop();
        }

        // -----------------------------------------------------------------
        // 2. AKTİF MODÜLLER (ARRAYLIST ÇİZİMİ)
        // -----------------------------------------------------------------
        if (ClickGUI.isArrayListEnabled()) {
            List<Module> activeModules = ModuleManager.getModules().stream()
                    .filter(Module::isEnabled)
                    .sorted(Comparator.comparingInt(m -> -mc.textRenderer.getWidth(m.getName())))
                    .collect(Collectors.toList());

            int yOffset = HUDEditor.alY;

            for (Module m : activeModules) {
                String modName = m.getName();
                int textWidth = (int) (mc.textRenderer.getWidth(modName) * scale);
                int drawX = HUDEditor.alX - textWidth;

                context.getMatrices().push();
                context.getMatrices().translate(drawX, yOffset, 0);
                context.getMatrices().scale(scale, scale, 1.0f);

                int modColor = getCurrentColor(colorCount * 90);
                context.drawText(mc.textRenderer, getFormattedText(modName), 0, 0, modColor, true);

                context.getMatrices().pop();

                yOffset += (int) (11 * scale);
                colorCount++;
            }
        }
    }
}