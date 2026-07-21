package net.nameplate.waila;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.nameplate.NameplateClient;
import net.nameplate.access.MobEntityAccess;

public class NameplateWailaMobInfo extends NameplateFeature implements IEntityComponentProvider {

    @Override
    public void initialize(IRegistrar registrar) {
        registrar.addConfig(NameplateClient.MOB_LEVEL_INFO, true);
        registrar.addComponent(this, TooltipPosition.BODY, Mob.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        IEntityComponentProvider.super.appendBody(tooltip, accessor, config);
        if (config.getBoolean(NameplateClient.MOB_LEVEL_INFO) && ((MobEntityAccess) accessor.getEntity()).showMobRpgLabel()) {
            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG,
                    Component.translatable("text.nameplate.level", String.valueOf("§e" + ((MobEntityAccess) accessor.getEntity()).getMobRpgLevel()), accessor.getEntity().getName()));
        }
    }

}
