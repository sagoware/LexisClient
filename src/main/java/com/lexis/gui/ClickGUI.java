package com.lexis.gui;

import com.lexis.module.Category;
import com.lexis.module.Module;
import com.lexis.module.ModuleManager;
import com.lexis.module.modules.movement.Fly;
import com.lexis.module.modules.render.CustomFont;
import com.lexis.setting.Setting;
import com.lexis.setting.settings.BooleanSetting;
import com.lexis.setting.settings.ModeSetting;
import com.lexis.setting.settings.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ClickGUI extends Screen {
    private static final Map<Category, Integer> categoryX = new HashMap<>();
    private static final Map<Category, Integer> categoryY = new HashMap<>();

    private Category draggingCategory = null;
    private int dragStartX = 0;
    private int dragStartY = 0;

    private int currentTab = 0;
    private Module selectedModule = null;
    private Module bindingModule = null;

    public static boolean whiteMode = false;
    private static boolean watermarkEnabled = true;
    private static boolean arrayListEnabled = true;
    private static double textScale = 1.0;
    private static int hudColorMode = 2;

    public ClickGUI() {
        super(Text.of("Lexis Premium"));
        if (categoryX.isEmpty()) {
            int startX = 40;
            for (Category c : Category.values()) {
                categoryX.put(c, startX);
                categoryY.put(c, 100);
                startX += 155;
            }
        }
    }

    public static boolean isWatermarkEnabled() { return watermarkEnabled; }
    public static boolean isArrayListEnabled() { return arrayListEnabled; }
    public static double getTextScale() { return textScale; }
    public static int getHudColorMode() { return hudColorMode; }

    private Text getCustomText(String rawText) {
        MutableText textNode = Text.literal(rawText);
        if (CustomFont.isFontEnabled) {
            return textNode.setStyle(Style.EMPTY.withFont(Identifier.of("lexis", "default")));
        }
        return textNode;
    }

    private int getRainbowColor(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 12.0);
        rainbowState %= 360;
        return java.awt.Color.getHSBColor((float) (rainbowState / 360.0f), 0.75f, 0.95f).getRGB();
    }

    private int getHudColor(int delay) {
        return switch (hudColorMode) {
            case 1 -> 0xFF8338EC;
            case 2 -> getRainbowColor(delay);
            default -> 0xFF3A86FF;
        };
    }

    private int getBgColor() { return whiteMode ? 0xF5F4F4F9 : 0xF5111116; }
    private int getPanelBgColor() { return whiteMode ? 0xFAFFFFFF : 0xFA0A0A0F; }
    private int getTextColor() { return whiteMode ? 0xFF1C1C1E : 0xFFE5E5EA; }
    private int getSecTextColor() { return whiteMode ? 0xFF636366 : 0xFF8E8E93; }

    // Kenarları pürüzsüzleştiren özel soft gövde metodu reis
    private void drawSoftRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int alpha = (color >> 24) & 0xFF;
        int softAlpha = (int)(alpha * 0.4f) << 24;
        int softColor = (softAlpha) | (color & 0x00FFFFFF);

        context.fill(x1 - 1, y1, x1, y2, softColor);
        context.fill(x2, y1, x2 + 1, y2, softColor);
        context.fill(x1, y1 - 1, x2, y1, softColor);
        context.fill(x1, y2, x2, y2 + 1, softColor);
        context.fill(x1, y1, x2, y2, color);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (draggingCategory != null) {
            categoryX.put(draggingCategory, mouseX - dragStartX);
            categoryY.put(draggingCategory, mouseY - dragStartY);
        }

        int themeBtnX = 20, themeBtnY = 20;
        drawSoftRect(context, themeBtnX, themeBtnY, themeBtnX + 90, themeBtnY + 22, whiteMode ? 0xFFE5E5EA : 0xFF2C2C35);
        context.drawText(textRenderer, getCustomText(whiteMode ? "☀ Dark Mode" : "🌙 White Mode"), themeBtnX + 10, themeBtnY + 7, getTextColor(), false);

        String tab1 = "Modules";
        String tab2 = "HUD Designer";
        int w1 = textRenderer.getWidth(tab1);
        int w2 = textRenderer.getWidth(tab2);
        int menuWidth = w1 + w2 + 60;
        int menuX = (this.width - menuWidth) / 2;
        int menuY = 20;

        drawSoftRect(context, menuX, menuY, menuX + menuWidth, menuY + 26, getBgColor());
        context.fill(menuX + 8, menuY, menuX + menuWidth - 8, menuY + 2, getHudColor(0));

        int drawX = menuX + 20;
        int colorTab1 = (currentTab == 0) ? getHudColor(0) : getSecTextColor();
        context.drawText(textRenderer, getCustomText(tab1), drawX, menuY + 9, colorTab1, false);
        if (currentTab == 0) context.fill(drawX - 2, menuY + 22, drawX + w1 + 2, menuY + 24, getHudColor(0));

        drawX += w1 + 20;
        int colorTab2 = (currentTab == 1) ? getHudColor(0) : getSecTextColor();
        context.drawText(textRenderer, getCustomText(tab2), drawX, menuY + 9, colorTab2, false);
        if (currentTab == 1) context.fill(drawX - 2, menuY + 22, drawX + w2 + 2, menuY + 24, getHudColor(0));

        if (currentTab == 0) {
            renderModernClickGui(context, mouseX, mouseY);
        } else if (currentTab == 1) {
            renderModernHud(context, mouseX, mouseY);
        }
    }

    private void renderModernClickGui(DrawContext context, int mouseX, int mouseY) {
        for (Category c : Category.values()) {
            int x = categoryX.get(c);
            int y = categoryY.get(c);
            int width = 140;

            List<Module> categoryModules = ModuleManager.getModulesByCategory(c);
            int totalHeight = 24 + (categoryModules.size() * 22) + 6;

            drawSoftRect(context, x, y, x + width, y + totalHeight, getBgColor());
            context.fill(x + 6, y + 20, x + width - 6, y + 21, whiteMode ? 0xFFD1D1D6 : 0xFF2C2C35);
            context.drawText(textRenderer, getCustomText(c.getName()), x + 10, y + 7, getTextColor(), false);

            context.fill(x + width - 15, y + 8, x + width - 8, y + 13, getHudColor(c.ordinal() * 120));

            int modY = y + 24;
            for (Module m : categoryModules) {
                boolean isHovered = mouseX >= x + 6 && mouseX <= x + width - 6 && mouseY >= modY && mouseY <= modY + 18;
                int btnColor = m.isEnabled() ? (whiteMode ? 0x25000000 : 0x25FFFFFF) : (isHovered ? (whiteMode ? 0x15000000 : 0x15FFFFFF) : 0x00000000);

                if (btnColor != 0) {
                    drawSoftRect(context, x + 6, modY, x + width - 6, modY + 18, btnColor);
                }

                if (m.isEnabled()) {
                    context.fill(x + 6, modY + 2, x + 8, modY + 16, getHudColor(modY));
                }

                String text = m.getName();
                if (m instanceof Fly fly) {
                    text += " [" + fly.getModeName() + "]";
                } else if (m.getKey() != 0) {
                    text += " [" + m.getKeyName() + "]";
                }

                int textColor = m.isEnabled() ? getHudColor(modY) : getTextColor();
                if (bindingModule == m) text += " [...]";

                context.drawText(textRenderer, getCustomText(text), x + 14, modY + 5, textColor, false);
                modY += 22;
            }
        }

        if (selectedModule != null && !selectedModule.getSettings().isEmpty()) {
            int sx = 670; int sy = 100; int sw = 150;
            int sh = 26 + (selectedModule.getSettings().size() * 24) + 6;

            drawSoftRect(context, sx, sy, sx + sw, sy + sh, getPanelBgColor());
            context.fill(sx, sy, sx + 2, sy + sh, getHudColor(50));

            context.drawText(textRenderer, getCustomText(selectedModule.getName()), sx + 12, sy + 8, getHudColor(0), false);
            context.fill(sx + 10, sy + 21, sx + sw - 10, sy + 22, whiteMode ? 0xFFE5E5EA : 0xFF2C2C35);

            int sY = sy + 26;
            for (Setting s : selectedModule.getSettings()) {
                if (s instanceof BooleanSetting bs) {
                    int checkColor = bs.isEnabled() ? getHudColor(sY) : (whiteMode ? 0xFFD1D1D6 : 0xFF3A3A3C);
                    context.fill(sx + 12, sY + 5, sx + 22, sY + 15, checkColor);
                    context.drawText(textRenderer, getCustomText(bs.getName()), sx + 28, sY + 6, getTextColor(), false);
                }
                else if (s instanceof NumberSetting ns) {
                    context.drawText(textRenderer, getCustomText(ns.getName() + ": " + Math.round(ns.getValue() * 10.0) / 10.0), sx + 12, sY + 6, getTextColor(), false);
                }
                else if (s instanceof ModeSetting ms) {
                    context.drawText(textRenderer, getCustomText(ms.getName() + ": [" + ms.getMode() + "]"), sx + 12, sY + 6, getHudColor(sY), false);
                }
                sY += 24;
            }
        }
    }

    private void renderModernHud(DrawContext context, int mouseX, int mouseY) {
        int x = (this.width - 230) / 2; int y = 80; int w = 230; int h = 160;
        drawSoftRect(context, x, y, x + w, y + h, getBgColor());
        drawSoftRect(context, x, y, x + w, y + 20, getHudColor(100));
        context.drawText(textRenderer, getCustomText("HUD Kontrol Merkezi"), x + 12, y + 6, 0xFFFFFFFF, false);

        String wmText = watermarkEnabled ? "✔ Logo & Watermark" : "✖ Logo & Watermark";
        context.drawText(textRenderer, getCustomText(wmText), x + 15, y + 35, watermarkEnabled ? getHudColor(y) : getSecTextColor(), false);

        String alText = arrayListEnabled ? "✔ Aktif Moduller (ArrayList)" : "✖ Aktif Moduller (ArrayList)";
        context.drawText(textRenderer, getCustomText(alText), x + 15, y + 55, arrayListEnabled ? getHudColor(y+20) : getSecTextColor(), false);

        String colorDesc = switch (hudColorMode) {
            case 1 -> "Mod: PURPLE";
            case 2 -> "Mod: RAINBOW ✦";
            default -> "Mod: NEON BLUE";
        };
        context.drawText(textRenderer, getCustomText("Renk Temasi: < " + colorDesc + " >"), x + 15, y + 80, getTextColor(), false);
        context.drawText(textRenderer, getCustomText("Yazi Olcegi: < " + Math.round(textScale * 10.0) / 10.0 + "x >"), x + 15, y + 105, getTextColor(), false);

        int editBtnX = x + 15; int editBtnY = y + 130;
        boolean hoverEdit = mouseX >= editBtnX && mouseX <= editBtnX + 200 && mouseY >= editBtnY && mouseY <= editBtnY + 20;
        context.fill(editBtnX, editBtnY, editBtnX + 200, editBtnY + 20, hoverEdit ? 0xAA3A86FF : 0x553A86FF);
        context.drawText(textRenderer, getCustomText("  ➔ HUD'u Düzenle (Blur Kaldır)"), editBtnX, editBtnY + 6, 0xFFFFFFFF, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= 20 && mouseX <= 110 && mouseY >= 20 && mouseY <= 42) {
            whiteMode = !whiteMode; return true;
        }

        String tab1 = "Modules"; int w1 = textRenderer.getWidth(tab1);
        int menuWidth = w1 + textRenderer.getWidth("HUD Designer") + 60;
        int menuX = (this.width - menuWidth) / 2; int menuY = 20;

        if (mouseY >= menuY && mouseY <= menuY + 26) {
            if (mouseX >= menuX && mouseX <= menuX + w1 + 30) { currentTab = 0; return true; }
            else if (mouseX >= menuX + w1 + 30 && mouseX <= menuX + menuWidth) { currentTab = 1; return true; }
        }

        if (currentTab == 0) {
            for (Category c : Category.values()) {
                int x = categoryX.get(c); int y = categoryY.get(c); int width = 140;

                if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 20) {
                    draggingCategory = c;
                    dragStartX = (int) mouseX - x; dragStartY = (int) mouseY - y;
                    return true;
                }

                List<Module> categoryModules = ModuleManager.getModulesByCategory(c);
                int modY = y + 24;
                for (Module m : categoryModules) {
                    if (mouseX >= x + 6 && mouseX <= x + width - 6 && mouseY >= modY && mouseY <= modY + 18) {
                        if (button == 0) {
                            if (m instanceof Fly fly) {
                                fly.flyMode.cycle();
                            } else {
                                m.toggle();
                            }
                        }
                        else if (button == 1) {
                            if (!m.getSettings().isEmpty()) selectedModule = (selectedModule == m) ? null : m;
                            else selectedModule = null;
                        }
                        else if (button == 2) bindingModule = (bindingModule == m) ? null : m;
                        return true;
                    }
                    modY += 22;
                }
            }

            if (selectedModule != null) {
                int sx = 670; int sY = 126;
                for (Setting s : selectedModule.getSettings()) {
                    if (mouseX >= sx + 8 && mouseX <= sx + 142 && mouseY >= sY && mouseY <= sY + 20) {
                        if (s instanceof BooleanSetting bs) {
                            bs.toggle();
                        } else if (s instanceof NumberSetting ns) {
                            if (button == 0) ns.setValue(ns.getValue() + 0.1);
                            else if (button == 1) ns.setValue(ns.getValue() - 0.1);
                        } else if (s instanceof ModeSetting ms) {
                            ms.cycle();
                        }
                        return true;
                    }
                    sY += 24;
                }
            }
        }
        else if (currentTab == 1) {
            int x = (this.width - 230) / 2; int y = 80;
            if (mouseX >= x + 15 && mouseX <= x + 215) {
                if (mouseY >= y + 35 && mouseY <= y + 50) { watermarkEnabled = !watermarkEnabled; return true; }
                if (mouseY >= y + 55 && mouseY <= y + 70) { arrayListEnabled = !arrayListEnabled; return true; }
                if (mouseY >= y + 80 && mouseY <= y + 95) {
                    hudColorMode = (button == 0) ? (hudColorMode + 1) % 3 : ((hudColorMode == 0) ? 2 : hudColorMode - 1);
                    return true;
                }
                if (mouseY >= y + 105 && mouseY <= y + 120) {
                    if (button == 0 && textScale < 2.0) textScale += 0.1;
                    else if (button == 1 && textScale > 0.6) textScale -= 0.1;
                    return true;
                }
                if (mouseY >= y + 130 && mouseY <= y + 150) { client.setScreen(new HUDEditor()); return true; }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingCategory = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindingModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) bindingModule.setKey(0);
            else bindingModule.setKey(keyCode);
            bindingModule = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}