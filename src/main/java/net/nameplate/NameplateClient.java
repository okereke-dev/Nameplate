package net.nameplate;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.nameplate.network.NameplateClientPacket;

@Environment(EnvType.CLIENT)
public class NameplateClient implements ClientModInitializer {

    public static final ResourceLocation MOB_LEVEL_INFO = ResourceLocation.fromNamespaceAndPath("nameplate", "mob_level_info");

    @Override
    public void onInitializeClient() {
        NameplateClientPacket.init();
    }

}
