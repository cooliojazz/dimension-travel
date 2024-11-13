package com.up.dt.dimension;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 *
 * @author Ricky
 */
public class RealityCoordinate {
    
    private final short[] vector;

    public RealityCoordinate(short... vector) {
        this.vector = vector;
    }
    
    private static final String chars = "defghijlmnopqrstuvwxyz0123456789";
    private static final int COORD_SIZE = 8;
    private static final int PACK_SIZE = 5;
    
    public String toString() {
        String s = chars.charAt(vector.length / 32) + "" + chars.charAt(vector.length % 32);
        for (int i = 0; i < vector.length / (double)PACK_SIZE; i++) {
            long merged = 0;
            for (int j = 0; j < PACK_SIZE; j++) {
                if (i * PACK_SIZE + j < vector.length) merged += (long)(vector[i * PACK_SIZE + j] & 0xFF) << (PACK_SIZE - 1 - j) * COORD_SIZE;
            }
            // Wasting a bit becausse 6 fits better than 5 but there's not 64 availabe chars in a resourse id
            
            for (int j = 0; j < COORD_SIZE; j++) {
                s += chars.charAt((int)((merged >> (COORD_SIZE - 1 - j) * 5) & 0x1F));
            }
        }
        return s;
    }
    
    public short get(int i) {
        return vector[i];
    }
    
    public static RealityCoordinate parse(String coord) {
        short[] vector = new short[chars.indexOf(coord.charAt(0)) * 32 + chars.indexOf(coord.charAt(1))];
        for (int i = 0; i < vector.length / (double)PACK_SIZE; i++) {
            long merged = 0;
            for (int j = 0; j < COORD_SIZE; j++) {
                merged += (long)chars.indexOf(coord.charAt(i * PACK_SIZE + 2 + j)) << (COORD_SIZE - 1 - j) * PACK_SIZE;
            }
            for (int j = 0; j < PACK_SIZE; j++) {
                if (i * PACK_SIZE + j < vector.length) vector[i * PACK_SIZE + j] = (short)((merged >> (PACK_SIZE - 1 - j) * 8) & 0xFF);
            }
        }
        return new RealityCoordinate(vector);
    }
    
    public static RealityCoordinate random(int length) {
        short[] vector = new short[length];
        for (int i = 0; i < length; i++) {
            vector[i] = (short)(Math.random() * 255);
        }
        return new RealityCoordinate(vector);
    }
    
    public static final StreamCodec<ByteBuf, RealityCoordinate> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, RealityCoordinate::toString, RealityCoordinate::parse);
    
}
