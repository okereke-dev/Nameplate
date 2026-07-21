package net.nameplate.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LevelPacket(int mobLevel, int mobId, boolean hasRpgLabel) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LevelPacket> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("nameplate", "level_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LevelPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.mobLevel);
        buf.writeInt(value.mobId);
        buf.writeBoolean(value.hasRpgLabel);
    }, buf -> new LevelPacket(buf.readInt(), buf.readInt(), buf.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
