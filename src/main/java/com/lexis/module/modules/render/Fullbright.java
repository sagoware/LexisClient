package com.lexis.module.modules.render;

import com.lexis.module.Category;
import com.lexis.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", Category.RENDER, 0);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        // Süreyi her karede 100000 tick'e zorluyoruz. Parçacıkları ve ambient modunu tamamen kapatıyoruz reis.
        StatusEffectInstance permanentBright = new StatusEffectInstance(
                StatusEffects.NIGHT_VISION, 100000, 0, false, false, false
        );
        mc.player.addStatusEffect(permanentBright);
    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;
        mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}