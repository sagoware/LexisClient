package com.lexis.module.modules.movement;

import com.lexis.module.Category;
import com.lexis.module.Module;
import com.lexis.setting.settings.BooleanSetting;
import com.lexis.setting.settings.ModeSetting;
import com.lexis.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import java.util.Arrays;

public class Fly extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public final ModeSetting flyMode = new ModeSetting("Mode", "Vanilla", Arrays.asList("Vanilla", "Motion", "Glide", "Bypass"));
    // Derleyicinin istediği gibi 5 parametreye sabitledik usta
    public final NumberSetting speed = new NumberSetting("Speed", 0.1, 5.0, 1.0, 0.1);
    public final BooleanSetting antiKick = new BooleanSetting("Anti-Kick", true);

    private int teleportTicks = 0;

    public Fly() {
        super("Fly", Category.MOVEMENT, 0);
        // Hatalı addSetting yerine doğrudan listeye pushluyoruz reis
        this.getSettings().add(flyMode);
        this.getSettings().add(speed);
        this.getSettings().add(antiKick);
    }

    @Override
    public void onEnable() {
        teleportTicks = 0;
    }

    public void onTick() {
        if (mc.player == null) return;

        double flySpeed = speed.getValue();
        String currentMode = flyMode.getMode();

        if (antiKick.isEnabled() && mc.player.age % 20 == 0) {
            Vec3d vel = mc.player.getVelocity();
            mc.player.setVelocity(vel.x, -0.04, vel.z);
        }

        switch (currentMode) {
            case "Vanilla" -> {
                mc.player.getAbilities().flying = true;
                mc.player.getAbilities().setFlySpeed((float) (flySpeed * 0.05f));
            }
            case "Motion" -> {
                mc.player.getAbilities().flying = false;
                Vec3d look = mc.player.getRotationVector().multiply(flySpeed);
                double yVelocity = 0;

                if (mc.options.jumpKey.isPressed()) yVelocity = flySpeed * 0.5;
                else if (mc.options.sneakKey.isPressed()) yVelocity = -flySpeed * 0.5;

                mc.player.setVelocity(look.x, yVelocity, look.z);
            }
            case "Glide" -> {
                mc.player.getAbilities().flying = false;
                Vec3d velocity = mc.player.getVelocity();
                if (!mc.player.isOnGround() && velocity.y < 0) {
                    mc.player.setVelocity(velocity.x, -0.12, velocity.z);
                }
            }
            case "Bypass" -> {
                mc.player.getAbilities().flying = false;
                mc.player.setVelocity(0, 0, 0);
                teleportTicks++;

                if (teleportTicks >= 2) {
                    Vec3d forward = Vec3d.fromPolar(0, mc.player.getYaw()).multiply(flySpeed * 0.4);
                    mc.player.setPosition(mc.player.getX() + forward.x, mc.player.getY(), mc.player.getZ() + forward.z);
                    teleportTicks = 0;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                mc.player.getAbilities().flying = false;
            }
        }
    }

    // Üst sınıfta bu metot tanımlı olmadığı için @Override kaldırıldı, ClickGUI'den sorunsuz okunur reis
    public String getModeName() {
        return flyMode.getMode();
    }
}