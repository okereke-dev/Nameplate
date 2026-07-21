package net.nameplate.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.nameplate.util.NameplateRender;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

@Environment(EnvType.CLIENT)
@SuppressWarnings("rawtypes")
@Mixin(GeoReplacedEntityRenderer.class)
public abstract class GeoReplacedEntityRendererMixin extends EntityRenderer {

    @Shadow
    protected Entity currentEntity;

    public GeoReplacedEntityRendererMixin(Context ctx) {
        super(ctx);
    }

    @Inject(method = "actuallyRender", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Mob;getLeashHolder()Lnet/minecraft/world/entity/Entity;"))
    private void actuallyRenderMixin(PoseStack poseStack, GeoAnimatable animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour, CallbackInfo info) {
        NameplateRender.renderNameplate(this, (Mob) currentEntity, poseStack, bufferSource, entityRenderDispatcher, this.getFont(), !currentEntity.isInvisible(), packedLight);
    }

    @Inject(method = "shouldShowName", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    protected void hasLabelMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof Mob) {
            info.setReturnValue(false);
        }
    }

}
