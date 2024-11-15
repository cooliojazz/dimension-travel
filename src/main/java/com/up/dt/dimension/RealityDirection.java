package com.up.dt.dimension;

/**
 *
 * @author Ricky
 */
public enum RealityDirection {
    MIN_Y, HEIGHT, OCEAN_LEVEL, H_SCALE, V_SCALE, STONE_TYPE;
    
    public static final int size() {
        return values().length;
    }
}
