package com.lexis.module;

import com.lexis.setting.Setting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final Category category;
    private boolean enabled;
    private int key; // Atanan tuşun kodu (Örn: GLFW.GLFW_KEY_R)
    private final List<Setting> settings = new ArrayList<>();
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, Category category, int defaultKey) {
        this.name = name;
        this.category = category;
        this.key = defaultKey;
    }

    public void addSettings(Setting... settingsIn) {
        for (Setting s : settingsIn) this.settings.add(s);
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }

    // Tuşun adını yazı olarak dönen metot (Örn: "R" veya "NONE")
    public String getKeyName() {
        if (key == 0) return "NONE";
        String name = GLFW.glfwGetKeyName(key, 0);
        return name != null ? name.toUpperCase() : "KEY " + key;
    }

    public List<Setting> getSettings() { return settings; }

    public void toggle() {
        this.enabled = !this.enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
}