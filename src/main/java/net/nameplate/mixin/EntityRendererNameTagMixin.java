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
@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public class EntityRendererNameTagMixin {

    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true)
    private void nameplateAppendLevel(Entity entity, CallbackInfoReturnable<Component> cir) {
        if (!NameplateMain.CONFIG.showLevel) {
            return;
        }
        if (!(entity instanceof Mob mob) || mob.isVehicle()) {
            return;
        }
        MobEntityAccess access = (MobEntityAccess) mob;
        if (!access.showMobRpgLabel()) {
            return;
        }
        Component original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        Component levelText = Component.translatable("text.nameplate.level", access.getMobRpgLevel());
        cir.setReturnValue(levelText.copy().append(" ").append(original));
    }
}
