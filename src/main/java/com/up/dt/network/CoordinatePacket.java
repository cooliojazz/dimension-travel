package com.up.dt.network;

import com.up.dt.DimensionTravelMod;
import com.up.dt.dimension.RealityCoordinate;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


/**
 *
 * @author Ricky
 */
public record CoordinatePacket(RealityCoordinate coordinate) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CoordinatePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "coordinate"));
    public static final StreamCodec<ByteBuf, CoordinatePacket> STREAM_CODEC = StreamCodec.composite(RealityCoordinate.STREAM_CODEC, CoordinatePacket::coordinate, CoordinatePacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
