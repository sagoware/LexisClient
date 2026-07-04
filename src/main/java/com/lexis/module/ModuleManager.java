package com.lexis.module;

import com.lexis.module.modules.combat.*;
import com.lexis.module.modules.movement.*;
import com.lexis.module.modules.render.*;
import com.lexis.module.modules.player.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return; // Çift tetiklenmeyi önle

        // Modülleri listeye ekle
        modules.add(new KillAura());
        modules.add(new CustomFont());
        modules.add(new Fullbright());
        modules.add(new NoFall());
        modules.add(new Fly());
        modules.add(new Optimizer());


        initialized = true;
    }

    public static List<Module> getModules() {
        return modules;
    }

    // Filtreleme mantığını for döngüsü ile sağlama aldık
    public static List<Module> getModulesByCategory(Category c) {
        List<Module> categoryModules = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == c) {
                categoryModules.add(m);
            }
        }
        return categoryModules;
    }

    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
}