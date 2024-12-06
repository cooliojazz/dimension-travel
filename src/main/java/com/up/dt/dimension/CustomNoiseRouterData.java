/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.up.dt.dimension;

import java.util.stream.Stream;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import static net.minecraft.world.level.levelgen.NoiseRouterData.CONTINENTS;
import static net.minecraft.world.level.levelgen.NoiseRouterData.CONTINENTS_LARGE;
import static net.minecraft.world.level.levelgen.NoiseRouterData.DEPTH;
import static net.minecraft.world.level.levelgen.NoiseRouterData.EROSION;
import static net.minecraft.world.level.levelgen.NoiseRouterData.EROSION_LARGE;
import static net.minecraft.world.level.levelgen.NoiseRouterData.FACTOR;
import static net.minecraft.world.level.levelgen.NoiseRouterData.RIDGES;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 *
 * @author Ricky
 */
public class CustomNoiseRouterData extends NoiseRouterData {

    /**
     * @param scale Varies between 0 and 64
     */
    protected static DensityFunction slideOverworld(int scale, DensityFunction densityFunction) {
        scale = 64 - scale;
        return slide(densityFunction, -64, 384, 16 + scale, scale, -0.078125, 0, 24, 0.1171875 + scale / 226.29834254143646408839779005525);
    }

//    private static DensityFunction slideOverworld(boolean amplified, DensityFunction densityFunction) {
//        return slide(densityFunction, -64, 384, amplified ? 16 : 80, amplified ? 0 : 64, (double)-0.078125F, 0, 24, amplified ? 0.4 : (double)0.1171875F);
//    }
    
    public static NoiseRouter overworld(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParameters, boolean large, int amplified, double biomeScale) {
        
        DensityFunction aquiferBarrier = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction aquiferFluidFloodedness = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction aquiferFluidSpread = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction aquiferLava = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.AQUIFER_LAVA));
        
        DensityFunction shiftX = getFunction(densityFunctions, SHIFT_X);
        DensityFunction shiftZ = getFunction(densityFunctions, SHIFT_Z);
        
        DensityFunction temperature = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, biomeScale, noiseParameters.getOrThrow(large ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
        DensityFunction vegetation = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, biomeScale, noiseParameters.getOrThrow(large ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
        
        DensityFunction factor = getFunction(densityFunctions, large ? FACTOR_LARGE : (amplified > 0 ? FACTOR_AMPLIFIED : FACTOR));
        DensityFunction depth = getFunction(densityFunctions, large ? DEPTH_LARGE : (amplified > 0 ? DEPTH_AMPLIFIED : DEPTH));
        DensityFunction densityfunction10 = noiseGradientDensity(DensityFunctions.cache2d(factor), depth);
        DensityFunction cheese = getFunction(densityFunctions, large ? SLOPED_CHEESE_LARGE : (amplified > 0 ? SLOPED_CHEESE_AMPLIFIED : SLOPED_CHEESE));
        DensityFunction spaghetti = DensityFunctions.min(cheese, DensityFunctions.mul(DensityFunctions.constant(5.0), getFunction(densityFunctions, ENTRANCES)));
        DensityFunction overworld = DensityFunctions.rangeChoice(cheese, -1000000.0, 1.5625, spaghetti, underground(densityFunctions, noiseParameters, cheese));
//        DensityFunction finalDensity = DensityFunctions.min(postProcess(slideOverworld(amplified, overworld)), getFunction(densityFunctions, NOODLE));
        DensityFunction finalDensity = DensityFunctions.min(postProcess(slideOverworld(amplified, overworld)), getFunction(densityFunctions, NOODLE));
        DensityFunction y = getFunction(densityFunctions, Y);
        int minY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(t -> t.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int maxY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(t -> t.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction veinToggle = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), minY, maxY, 0);
//        float f = 4.0F;
        DensityFunction oreVeinA = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), minY, maxY, 0).abs();
        DensityFunction oreVeinB = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), minY, maxY, 0).abs();
        DensityFunction veinRigged = DensityFunctions.add(DensityFunctions.constant(-0.08F), DensityFunctions.max(oreVeinA, oreVeinB));
        DensityFunction veinGap = DensityFunctions.noise(noiseParameters.getOrThrow(Noises.ORE_GAP));
        
        return new NoiseRouter(
                aquiferBarrier, aquiferFluidFloodedness, aquiferFluidSpread, aquiferLava,
                temperature, vegetation,
                getFunction(densityFunctions, large ? CONTINENTS_LARGE : CONTINENTS),
                getFunction(densityFunctions, large ? EROSION_LARGE : EROSION),
                depth, getFunction(densityFunctions, RIDGES),
                slideOverworld(amplified, DensityFunctions.add(densityfunction10, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0)),
                finalDensity,
                veinToggle, veinRigged, veinGap
            );
    }
}
