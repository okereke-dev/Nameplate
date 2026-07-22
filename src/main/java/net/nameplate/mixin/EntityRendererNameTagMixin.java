package net.nameplate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.nameplate.NameplateMain;
import net.nameplate.access.MobEntityAccess;

// The original 1.21 mod drew its own fully independent floating label
// (NameplateRender.renderNameplate, gated only by distance/config) and force
// -disabled vanilla's own name-tag check entirely (hasLabel -> always false)
// so vanilla's restrictive "must be named + looked at" gate never got a say.
//
// 26.x splits label rendering into extract (main thread) and submit (render
// thread) phases. EntityRenderer#extractNameTags(Entity, EntityRenderState,
// float, double, double) is the single, final (non-overridable) choke point
// where state.nameTag gets set from getNameTag(), gated on shouldShowName().
// Rather than fight shouldShowName() (which MobRenderer overrides with an
// extra AND-condition that a base-class mixin can't reach), we do what the
// original did: bypass the gate entirely by overwriting state.nameTag
// directly after vanilla's own logic has already run. Vanilla's unmodified
// submitNameDisplay() then just draws whatever's in that field.
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererNameTagMixin {

    @Inject(method = "extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FDD)V", at = @At("TAIL"))
    private void nameplateForceNameTag(Entity entity, EntityRenderState state, float partialTick, double nameTagDistance, double belowNameDistance, CallbackInfo info) {
        Mob mob = eligibleMob(entity);
        if (mob == null) {
            return;
        }
        if (state.distanceToCameraSq > NameplateMain.CONFIG.squaredDistance) {
            return;
        }
        MobEntityAccess access = (MobEntityAccess) mob;
        Component baseName = mob.hasCustomName() ? mob.getCustomName() : mob.getDisplayName();
        Component levelText = Component.translatable("text.nameplate.level", access.getMobRpgLevel());
        state.nameTag = levelText.copy().append(" ").append(baseName);
    }

    private static Mob eligibleMob(Entity entity) {
        if (!NameplateMain.CONFIG.showLevel) {
            return null;
        }
        if (!(entity instanceof Mob mob) || mob.isVehicle()) {
            return null;
        }
        MobEntityAccess access = (MobEntityAccess) mob;
        if (!access.showMobRpgLabel()) {
            return null;
        }
        return mob;
    }
}
