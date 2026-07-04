package com.lexis.module.modules.player;

import com.lexis.module.Category;
import com.lexis.module.Module;
import com.lexis.setting.settings.BooleanSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    public final BooleanSetting packetMode = new BooleanSetting("Packet Mode", true);

    public NoFall() {
        super("NoFall", Category.PLAYER, 0); // Kategori PLAYER yapıldı reis
        this.getSettings().add(packetMode);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.fallDistance > 1.5F) {
            if (packetMode.isEnabled()) {
                if (mc.getNetworkHandler() != null) {
                    double groundY = mc.player.getY();
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX(), groundY, mc.player.getZ(), true, mc.player.horizontalCollision
                    ));
                }
            } else {
                mc.player.setOnGround(true);
            }
            mc.player.fallDistance = 0.0F;
        }
    }
}