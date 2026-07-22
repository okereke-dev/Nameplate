package net.nameplate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.nameplate.NameplateMain;
import net.nameplate.access.MobEntityAccess;

// Prepends "[Lvl N] " to the vanilla name-tag Component instead of drawing a
// custom floating label. 26.x replaced the old immediate-mode
// PoseStack/RenderSystem entity rendering with an extract-state/submit
// pipeline (GpuDevice/RenderPass based) — rather than reimplement custom
// world-space text against that fast-moving internal API, we ride vanilla's
// own name-tag submission (EntityRenderer#submitNameDisplay), which already
// handles scale/position/visibility correctly and will keep working across
// future rendering-pipeline churn.
//
// Vanilla only calls getNameTag() at all if shouldShowName() already said
// yes, and vanilla's own shouldShowName() is true only for players or
// entities with a custom name (item name tag) being looked at — an ordinary
// unnamed mob never passes that gate. So we also have to force the gate open
// ourselves for mobs we want to label, and fall back to the entity type's
// default display name (e.g. "Cow") when vanilla's own getNameTag() is null.
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererNameTagMixin {

    @Inject(method = "shouldShowName", at = @At("RETURN"), cancellable = true)
    private void nameplateForceShowName(Entity entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }
        if (eligibleMob(entity) == null) {
            return;
        }
        if (distanceToCameraSq > NameplateMain.CONFIG.squaredDistance) {
            return;
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true)
    private void nameplateAppendLevel(Entity entity, CallbackInfoReturnable<Component> cir) {
        Mob mob = eligibleMob(entity);
        if (mob == null) {
            return;
        }
        MobEntityAccess access = (MobEntityAccess) mob;
        Component original = cir.getReturnValue();
        if (original == null) {
            original = mob.getType().getDescription();
        }
        Component levelText = Component.translatable("text.nameplate.level", access.getMobRpgLevel());
        cir.setReturnValue(levelText.copy().append(" ").append(original));
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
