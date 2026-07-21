package net.nameplate.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.nameplate.NameplateMain;
import net.nameplate.access.MobEntityAccess;
import net.nameplate.network.packet.LevelPacket;
import net.rpgdifficulty.access.EntityAccess;

public class NameplateTracker {

    public static void startTracking(Mob mobEntity, ServerPlayer serverPlayer) {
        // Send packet if entity should show
        if (((MobEntityAccess) mobEntity).showMobRpgLabel()) {
            int mobLevel = getMobLevel(mobEntity);

            ((MobEntityAccess) mobEntity).setMobRpgLevel(mobLevel);
            ServerPlayNetworking.send(serverPlayer, new LevelPacket(mobLevel, mobEntity.getId(), ((MobEntityAccess) mobEntity).showMobRpgLabel()));
        }
    }

    public static int getMobLevel(Mob mobEntity) {
        int level = 1;
        if (NameplateMain.isRpgDifficultyLoaded && NameplateMain.CONFIG.useRpgDifficultyLvl) {
            level = (int) (NameplateMain.CONFIG.levelMultiplier * ((EntityAccess) mobEntity).getMobHealthMultiplier() - NameplateMain.CONFIG.levelMultiplier);
        } else if (DefaultAttributeRegistryAccessor.getRegistry().get(mobEntity.getType()) != null) {
            level = (int) (NameplateMain.CONFIG.levelMultiplier * mobEntity.getAttributeBaseValue(Attributes.MAX_HEALTH)
                    / Math.abs(DefaultAttributeRegistryAccessor.getRegistry().get(mobEntity.getType()).getBaseValue(Attributes.MAX_HEALTH))) - NameplateMain.CONFIG.levelMultiplier + 1;
        }
        if (level < 1) {
            level = 1;
        }
        return level;
    }
}
