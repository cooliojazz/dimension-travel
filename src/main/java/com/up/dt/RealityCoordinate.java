package com.up.dt;

/**
 *
 * @author Ricky
 */
public class RealityCoordinate {
    
    private final short[] vector;

    public RealityCoordinate(short... vector) {
        this.vector = vector;
    }

    public RealityCoordinate(int length) {
        vector = new short[length];
        for (int i = 0; i < length; i++) {
            vector[i] = (short)(Math.random() * 255);
        }
    }
    
    private static final String chars = "defghijlmnopqrstuvwxyz0123456789";
    
    public String toString() {
        String s = "";
        for (int i = 0; i < vector.length / 3d; i++) {
            int merged = 0;
            for (int j = 0; j < 3; j++) {
                if (i * 3 + j < vector.length) merged += (vector[i * 3 + j] & 0xFF) << (2 - j) * 8;
            }
            // Wasting a bit becausse 6 fits better than 5 but there's not 64 availabe chars in a resourse id
            s += chars.charAt((merged & 0x7C0000) >> 18);
            s += chars.charAt((merged & 0x01F000) >> 12);
            s += chars.charAt((merged & 0x0007C0) >> 6);
            s += chars.charAt(merged & 0x00001F);
        }
        return s;
    }
    
    public short get(int i) {
        return vector[i];
    }
    
}
