package com.lexis.module.modules.combat;

import com.lexis.module.Category;
import com.lexis.module.Module;
import com.lexis.setting.settings.BooleanSetting;
import com.lexis.setting.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class KillAura extends Module {
    public final NumberSetting range = new NumberSetting("Range", 4.2, 1.0, 6.0, 0.1);
    public final BooleanSetting players = new BooleanSetting("Players", true);
    public final BooleanSetting animals = new BooleanSetting("Animals", false);
    public final BooleanSetting monsters = new BooleanSetting("Monsters", true);
    // Bu ayar açıkken sadece vuruş barı %100 dolduğunda vurur (Maksimum Hasar)
    public final BooleanSetting criticalOnly = new BooleanSetting("Critical Only", true);

    public KillAura() {
        super("KillAura", Category.COMBAT, 0);
        addSettings(range, players, animals, monsters, criticalOnly);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity target && entity != mc.player) {
                if (!target.isAlive() || target.isDead()) continue;

                if (target instanceof PlayerEntity && !players.isEnabled()) continue;
                if (target instanceof AnimalEntity && !animals.isEnabled()) continue;
                if (target instanceof Monster && !monsters.isEnabled()) continue;

                if (mc.player.distanceTo(target) <= range.getValue()) {

                    // Vuruş barının dolma oranını alıyoruz (0.0 ile 1.0 arası döner)
                    float cooldownProgress = mc.player.getAttackCooldownProgress(0.0f);

                    if (criticalOnly.isEnabled()) {
                        // Eğer bar tam dolmadıysa (1.0'dan küçükse) bu tick'i es geç, barın dolmasını bekle!
                        if (cooldownProgress < 1.0f) {
                            continue;
                        }
                    }

                    // Bar dolduysa veya ayar kapalıysa direkt yapıştır
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                    break;
                }
            }
        }
    }
}