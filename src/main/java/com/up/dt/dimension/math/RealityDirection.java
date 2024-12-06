package com.up.dt.dimension.math;

/**
 *
 * @author Ricky
 */
public enum RealityDirection {
    MIN_Y, HEIGHT, OCEAN_LEVEL, H_SCALE, V_SCALE, STONE_TYPE, OCEAN_TYPE, BIOME_SCALE;
    
    public static final int size() {
        return values().length;
    }
}