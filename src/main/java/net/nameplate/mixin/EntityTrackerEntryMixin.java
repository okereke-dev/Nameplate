package net.nameplate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.nameplate.util.NameplateTracker;

@Mixin(value = ServerEntity.class, priority = 1001)
public class EntityTrackerEntryMixin {

    @Shadow
    private final Entity entity;

    public EntityTrackerEntryMixin(Entity entity) {
        this.entity = entity;
    }

    @Inject(method = "addPairing", at = @At(value = "TAIL"))
    public void startTrackingMixin(ServerPlayer serverPlayer, CallbackInfo info) {
        if (entity instanceof Mob) {
            NameplateTracker.startTracking((Mob) entity, serverPlayer);
        }
    }

}
