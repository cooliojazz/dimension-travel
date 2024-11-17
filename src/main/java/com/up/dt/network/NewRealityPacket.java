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
public record NewRealityPacket(RealityCoordinate coordinate) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<NewRealityPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "new-reality"));
    public static final StreamCodec<ByteBuf, NewRealityPacket> STREAM_CODEC = StreamCodec.composite(RealityCoordinate.STREAM_CODEC, NewRealityPacket::coordinate, NewRealityPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
