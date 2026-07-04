package com.lexis.setting.settings;

import com.lexis.setting.Setting;
import java.util.List;

public class ModeSetting extends Setting {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, List<String> modes) {
        super(name);
        this.modes = modes;
        this.index = modes.indexOf(defaultMode);
        if (this.index == -1) this.index = 0;
    }

    public String getMode() {
        return modes.get(index);
    }

    public void cycle() {
        index = (index + 1) % modes.size();
    }

    public List<String> getModes() {
        return modes;
    }
}