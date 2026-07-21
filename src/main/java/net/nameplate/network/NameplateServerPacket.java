package net.nameplate.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.nameplate.network.packet.LevelPacket;

public class NameplateServerPacket {

    // Removed: the TitlePacket handler that used to live here was purely
    // compat glue for the original "Traveler's Titles" mod (yungnickyoung) —
    // a different, unrelated mod with no 26.2 build that we don't install
    // anyway. Our own TravelerZ fork queries mob level independently.

    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(LevelPacket.PACKET_ID, LevelPacket.PACKET_CODEC);
    }

}
