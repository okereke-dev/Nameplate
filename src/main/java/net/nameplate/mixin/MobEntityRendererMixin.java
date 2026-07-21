package net.nameplate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.nameplate.util.NameplateRender;

@Environment(EnvType.CLIENT)
@Mixin(MobRenderer.class)
public abstract class MobEntityRendererMixin<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public MobEntityRendererMixin(Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    public void render(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
        NameplateRender.renderNameplate(this, livingEntity, matrixStack, vertexConsumerProvider, this.entityRenderDispatcher, this.getFont(), this.isBodyVisible(livingEntity), i);
    }

    @Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
    protected void hasLabelMixin(T mobEntity, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }
}
