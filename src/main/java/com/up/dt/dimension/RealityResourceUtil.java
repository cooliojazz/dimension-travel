package com.up.dt.dimension;

import com.up.dt.DimensionTravelMod;
import com.up.dt.dimension.math.AttractedRealityVector;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

public class RealityResourceUtil {

    public static <T> ResourceKey<T> createKeyForCoordinate(ResourceKey<? extends Registry<T>> registryKey, AttractedRealityVector coord) {
        return ResourceKey.create(registryKey, ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "alter_" + coord));
    }

    public static ResourceKey<Level> createLevelKeyFor(AttractedRealityVector coord) {
        return createKeyForCoordinate(Registries.DIMENSION, coord);
    }

    public static ResourceKey<LevelStem> createLevelStemKeyFor(AttractedRealityVector coord) {
        return createKeyForCoordinate(Registries.LEVEL_STEM, coord);
    }
}
