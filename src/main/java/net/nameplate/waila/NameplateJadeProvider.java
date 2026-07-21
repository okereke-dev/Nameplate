package net.nameplate.waila;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.nameplate.NameplateClient;
import net.nameplate.access.MobEntityAccess;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum NameplateJadeProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return NameplateClient.MOB_LEVEL_INFO;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (((MobEntityAccess) accessor.getEntity()).showMobRpgLabel()) {
            tooltip.append(0, IElementHelper.get().text(Component.translatable("text.nameplate.jade.level", String.valueOf("§e" + ((MobEntityAccess) accessor.getEntity()).getMobRpgLevel()))));
        }
    }

}
