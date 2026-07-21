package net.nameplate.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.nameplate.util.NameplateRender;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.ClientUtil;

@Environment(EnvType.CLIENT)
@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {

    public GeoEntityRendererMixin(Context ctx) {
        super(ctx);
    }

    @Inject(method = "renderFinal", at = @At("HEAD"), remap = false)
    private void renderFinalMixin(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour, CallbackInfo info) {
        if (animatable instanceof Mob) {
            NameplateRender.renderNameplate(this, (Mob) animatable, poseStack, bufferSource, entityRenderDispatcher, this.getFont(), animatable == null || !animatable.isInvisibleTo(ClientUtil.getClientPlayer()), packedLight);
        }
    }


    @Inject(method = "shouldShowName", at = @At("RETURN"), cancellable = true)
    private void hasLabelMixin(T entity, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue() && entity instanceof Mob) {
            info.setReturnValue(false);
        }
    }

}
