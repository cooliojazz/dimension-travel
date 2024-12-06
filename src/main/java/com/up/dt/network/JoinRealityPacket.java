package com.up.dt.network;

import com.up.dt.DimensionTravelMod;
import com.up.dt.dimension.math.AttractedRealityVector;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


/**
 *
 * @author Ricky
 */
public record JoinRealityPacket(AttractedRealityVector coordinate) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<JoinRealityPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "join-reality"));
    public static final StreamCodec<ByteBuf, JoinRealityPacket> STREAM_CODEC = StreamCodec.composite(AttractedRealityVector.STREAM_CODEC, JoinRealityPacket::coordinate, JoinRealityPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
