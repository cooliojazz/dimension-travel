package com.up.dt.dimension;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import java.util.Arrays;

/**
 * Should this be called RealityVector? I really like that name instead for it's mathematical accuracy, but RealityCoordinate I think more clearly communicates what it is colloquially.
 * @author Ricky
 */
public class RealityCoordinate {
    
    // TODO: This really needs to become immutable now for the codec stuff
    
    public static final StreamCodec<ByteBuf, RealityCoordinate> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, RealityCoordinate::toString, RealityCoordinate::parse);
    public static final Codec<RealityCoordinate> CODEC = Codec.STRING.comapFlatMap(
            s -> { 
                try {
                    return DataResult.success(RealityCoordinate.parse(s));
                } catch (Exception e) {
                    return DataResult.error(() -> s + " is not a reality coordinate.");
                }
            },
            RealityCoordinate::toString);
    
//    private static final String chars = "0123456789abcdefghijklmnopqrstuv"; // Simpler for testing
//    private static final String chars = "bcdfghjlmnopqrstuvwxyz0123456789";
    private static final String chars = "ugf9ldb2rwmnt3860phy5vqxzc7ojs41"; // Shuffled for fun to obscure it more
    private static final int COORD_SIZE = 8;
    private static final int PACK_SIZE = 5;
    
    private final short[] vector;

    public RealityCoordinate(short... vector) {
        this.vector = vector;
    }
    
    public short get(int i) {
        return vector[i];
    }
    
    // Probably should make these return new inst4ead of mutate?
    public RealityCoordinate with(RealityDirection dir, short value) {
        vector[dir.ordinal()] = (short)(value & 0xFF);
        return this;
    }
    
    public RealityCoordinate offset(RealityDirection dir, short value) {
        vector[dir.ordinal()] = (short)((vector[dir.ordinal()] + value) & 0xFF);
        return this;
    }
    
    public String toString() {
        String s = chars.charAt(vector.length / 32) + "" + chars.charAt(vector.length % 32);
        for (int i = 0; i < vector.length / (double)PACK_SIZE; i++) {
            long merged = 0;
            for (int j = 0; j < PACK_SIZE; j++) {
                if (i * PACK_SIZE + j < vector.length) merged += (long)(vector[i * PACK_SIZE + j] & 0xFF) << (PACK_SIZE - 1 - j) * COORD_SIZE;
            }
            // Wasting a bit becausse 6 fits better than 5 but there's not 64 availabe chars in a resourse id
            
            for (int j = 0; j < COORD_SIZE; j++) {
                s += chars.charAt((int)((merged >> (COORD_SIZE - 1 - j) * PACK_SIZE) & 0x1F));
            }
        }
        return s;
    }
    
    public RealityCoordinate clone() {
        return new RealityCoordinate(vector.clone());
    }
    
    public static RealityCoordinate parse(String coord) {
        int length = chars.indexOf(coord.charAt(0)) * 32 + chars.indexOf(coord.charAt(1));
        if (length != RealityDirection.size()) throw new IndexOutOfBoundsException("Reality coordinate does not match number of reality directions");
        short[] vector = new short[length];
        for (int i = 0; i < vector.length / (double)PACK_SIZE; i++) {
            long merged = 0;
            for (int j = 0; j < COORD_SIZE; j++) {
                merged += (long)chars.indexOf(coord.charAt(i * COORD_SIZE + 2 + j)) << (COORD_SIZE - 1 - j) * PACK_SIZE;
            }
            for (int j = 0; j < PACK_SIZE; j++) {
                if (i * PACK_SIZE + j < vector.length) vector[i * PACK_SIZE + j] = (short)((merged >> (PACK_SIZE - 1 - j) * COORD_SIZE) & 0xFF);
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RealityCoordinate)) return false;
        return Arrays.equals(vector, ((RealityCoordinate)obj).vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }
}
