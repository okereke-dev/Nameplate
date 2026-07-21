package net.nameplate.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.nameplate.NameplateMain;
import net.nameplate.access.MobEntityAccess;
import net.nameplate.mixin.DrawContextAccessor;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class NameplateRender {

    private static final ResourceLocation ICONS = ResourceLocation.parse("nameplate:textures/icons.png");

    public static void renderNameplate(EntityRenderer<?> entityRenderer, Mob mobEntity, PoseStack matrices, MultiBufferSource vertexConsumers, EntityRenderDispatcher dispatcher,
                                       Font textRenderer, boolean isVisible, int i) {
        if (Minecraft.renderNames() && NameplateMain.CONFIG.showLevel && dispatcher.distanceToSqr(mobEntity) <= NameplateMain.CONFIG.squaredDistance && !mobEntity.isVehicle())
            if (isVisible && ((MobEntityAccess) mobEntity).showMobRpgLabel()) {
                if (!NameplateMain.CONFIG.showNameplateIfObstructed && !Minecraft.getInstance().player.hasLineOfSight(mobEntity)) {
                    return;
                }
                matrices.pushPose();
                matrices.translate(0.0D, (double) mobEntity.getBbHeight() + NameplateMain.CONFIG.nameplateHeight, 0.0D);
                matrices.mulPose(dispatcher.cameraOrientation());
                matrices.scale(-NameplateMain.CONFIG.nameplateSize, NameplateMain.CONFIG.nameplateSize, -0.025F);

                if (NameplateMain.CONFIG.healthBar) {
                    matrices.pushPose();
                    matrices.scale(1.5f, 1.5f, 1f);

                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.enableBlend();

                    RenderSystem.defaultBlendFunc();
                    RenderSystem.enableDepthTest();

                    RenderSystem.enablePolygonOffset();
                    RenderSystem.polygonOffset(3.0F, 3.0F);

                    Minecraft client = Minecraft.getInstance();
                    GuiGraphics context = DrawContextAccessor.getDrawContext(client, matrices, client.renderBuffers().bufferSource());
                    context.blit(ICONS, -20, 0, 0, 0, 40, 6, 256, 256);
                    float health = mobEntity.getHealth() / mobEntity.getMaxHealth();
                    matrices.translate(0.0D, 0.0D, -0.01D);
                    context.blit(ICONS, -20, 0, 0, 6, Math.round(40 * health), 6, 256, 256);
                    RenderSystem.polygonOffset(0.0F, 0.0F);
                    RenderSystem.disablePolygonOffset();

                    matrices.popPose();
                    matrices.translate(0.0D, -9D, 0.0D);
                    RenderSystem.disableBlend();

                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                }

                Matrix4f matrix4f = matrices.last().pose();
                float o = dispatcher.options.getBackgroundOpacity(NameplateMain.CONFIG.backgroundOpacity);
                int j = (int) (o * 255.0F) << 24;
                String string = mobEntity.hasCustomName() ? mobEntity.getCustomName().getString() : mobEntity.getName().getString();
                if (NameplateMain.CONFIG.showHealth) {
                    string = string + " " + Component.translatable("text.nameplate.health", Math.round(mobEntity.getHealth()), Math.round(mobEntity.getMaxHealth())).getString();
                }
                String levelString = Component.translatable("text.nameplate.level", ((MobEntityAccess) mobEntity).getMobRpgLevel()).getString();
                string = levelString + " " + Component.translatable("text.nameplate.name", string).getString();
                Component text = Component.nullToEmpty(string);

                float h = (float) (-textRenderer.width(text) / 2);
                textRenderer.drawInBatch(text, h, 0.0F, NameplateMain.CONFIG.nameColor, false, matrix4f, vertexConsumers, Font.DisplayMode.SEE_THROUGH, j, i);
                textRenderer.drawInBatch(text, h, 0.0F, NameplateMain.CONFIG.backgroundColor, false, matrix4f, vertexConsumers, Font.DisplayMode.NORMAL, 0, i);
                matrices.popPose();
            }
    }
}
