package net.nameplate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
// We bypass that gate the same way the original did, by overwriting
// state.nameTag directly after vanilla's own logic has already run.
//
// Vanilla only computes state.nameTagAttachment (the Vec3 the text actually
// draws at) inside that SAME gated branch - when the gate is false (the
// normal case for any unnamed mob), nameTag gets set to null but
// nameTagAttachment is left untouched, i.e. null/stale. Overwriting nameTag
// alone therefore produces a label with nowhere valid to draw - silently
// nothing renders. Recompute nameTagAttachment ourselves the same way
// vanilla does, unconditionally, whenever we override nameTag.
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
        Vec3 attachment = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTick));
        if (attachment == null) {
            return;
        }
        if (!NameplateMain.CONFIG.showNameplateIfObstructed && isObstructed(entity, attachment)) {
            return;
        }
        MobEntityAccess access = (MobEntityAccess) mob;
        Component baseName = mob.hasCustomName() ? mob.getCustomName() : mob.getDisplayName();
        Component levelText = Component.translatable("text.nameplate.level", access.getMobRpgLevel());
        state.nameTag = levelText.copy().append(" ").append(baseName);
        state.nameTagAttachment = attachment;
    }

    private static boolean isObstructed(Entity entity, Vec3 attachment) {
        Vec3 camPos = Minecraft.getInstance().gameRenderer.mainCamera().position();
        BlockHitResult hit = entity.level().clip(new ClipContext(camPos, attachment, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
        return hit.getType() == HitResult.Type.BLOCK;
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
