package net.nameplate.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TitlePacket(int level) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TitlePacket> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("nameplate", "title_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TitlePacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.level);
    }, buf -> new TitlePacket(buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
