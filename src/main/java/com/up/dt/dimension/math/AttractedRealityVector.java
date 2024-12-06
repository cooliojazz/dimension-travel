package com.up.dt.dimension.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;

/**
 *
 * @author Ricky
 */
public class AttractedRealityVector {

    public static final StreamCodec<ByteBuf, AttractedRealityVector> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, AttractedRealityVector::toString, AttractedRealityVector::parse);
    public static final Codec<AttractedRealityVector> CODEC = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return DataResult.success(AttractedRealityVector.parse(s));
                } catch (Exception e) {
                    return DataResult.error(() -> s + " is not a reality coordinate.");
                }
            },
            AttractedRealityVector::toString);

    private final short[] vector;

    public AttractedRealityVector(short... vector) {
        this.vector = vector;
    }
    
    public short get(int i) {
        return vector[i];
    }
    
    public short get(RealityDirection dir) {
        return get(dir.ordinal());
    }

    public AttractedRealityVector with(RealityDirection dir, short value) {
        short[] vec = Arrays.copyOf(vector, vector.length);
        vec[dir.ordinal()] = (short)(value & 0xFF);
        return new AttractedRealityVector(vec);
    }

    public AttractedRealityVector offset(RealityDirection dir, short value) {
        short[] vec = Arrays.copyOf(vector, vector.length);
        vec[dir.ordinal()] = (short)((vector[dir.ordinal()] + value) & 0xFF);
        return new AttractedRealityVector(vec);
    }

    public RealityVector exact() {
        double[] vec = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            vec[i] = vector[i];
        }
        return new RealityVector(vec);
    }

    public AttractedRealityVector clone() {
        return new AttractedRealityVector(vector.clone());
    }
    
    public String toString() {
        return RealityVectorConverter.toResourceString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AttractedRealityVector)) return false;
        return Arrays.equals(vector, ((AttractedRealityVector)obj).vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }

    public static AttractedRealityVector parse(String coord) {
        return RealityVectorConverter.fromResourceString(coord);
    }

    public static AttractedRealityVector random(int length) {
        short[] vector = new short[length];
        for (int i = 0; i < length; i++) {
            vector[i] = (short)(Math.random() * 255);
        }
        return new AttractedRealityVector(vector);
    }

    private final static class RealityVectorConverter {

        //    private static final String chars = "0123456789abcdefghijklmnopqrstuv"; // Simpler for testing
        //    private static final String chars = "bcdfghjlmnopqrstuvwxyz0123456789";
        private static final String chars = "ugf9ldb2rwmnt3860phy5vqxzc7ojs41"; // Shuffled for fun to obscure it more

        private static final int COORD_SIZE = 8;
        private static final int PACK_SIZE = 5;

        public static String toResourceString(AttractedRealityVector coord) {
            String s = chars.charAt(coord.vector.length / 32) + "" + chars.charAt(coord.vector.length % 32);
            for (int i = 0; i < coord.vector.length / (double)PACK_SIZE; i++) {
                long merged = 0;
                for (int j = 0; j < PACK_SIZE; j++) {
                    if (i * PACK_SIZE + j < coord.vector.length) merged += (long)(coord.vector[i * PACK_SIZE + j] & 0xFF) << (PACK_SIZE - 1 - j) * COORD_SIZE;
                }
                for (int j = 0; j < COORD_SIZE; j++) {
                    s += chars.charAt((int)((merged >> (COORD_SIZE - 1 - j) * PACK_SIZE) & 0x1F));
                }
            }
            return s;
        }

        public static AttractedRealityVector fromResourceString(String coord) {
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
            return new AttractedRealityVector(vector);
        }

    }

}
