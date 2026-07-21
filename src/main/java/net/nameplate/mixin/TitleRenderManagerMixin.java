package net.nameplate.mixin;

import com.yungnickyoung.minecraft.travelerstitles.render.TitleRenderManager;
import com.yungnickyoung.minecraft.travelerstitles.render.TitleRenderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.nameplate.NameplateMain;
import net.nameplate.network.packet.TitlePacket;

@Environment(EnvType.CLIENT)
@Mixin(TitleRenderManager.class)
public class TitleRenderManagerMixin {

    @Shadow(remap = false)
    @Mutable
    @Final
    public TitleRenderer<Biome> biomeTitleRenderer;

    @Inject(method = "updateBiomeTitle", at = @At(value = "INVOKE", target = "Lcom/yungnickyoung/minecraft/travelerstitles/render/TitleRenderer;addRecentEntry(Ljava/lang/Object;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private void updateBiomeTitleMixin(Level world, BlockPos playerPos, Player player, boolean isPlayerUnderground, CallbackInfo info, Holder<?> biomeHolder, boolean isUndergroundBiome,
            ResourceLocation biomeBaseKey, String overrideBiomeNameKey, String normalBiomeNameKey, Component biomeTitle) {
        if (NameplateMain.CONFIG.levelTitle) {
            ClientPlayNetworking.send(new TitlePacket(0));
        }
    }
}
