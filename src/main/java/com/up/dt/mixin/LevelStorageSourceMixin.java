package com.up.dt.mixin;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.up.dt.dimension.RealityCoordinate;
import com.up.dt.dimension.RealityManager;
import com.up.dt.dimension.RealityResourceUtil;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Although unsused, leaving for reference since it was an effective way to patch a registry on world load. Currently removed from .mixins.json
 */
@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {

//    @ModifyVariable(method = "getLevelDataAndDimensions", at = @At("INVOKE"), argsOnly = true, ordinal = 0)
//    private static RegistryAccess.Frozen getLevelDataAndDimensions(RegistryAccess.Frozen registry, Dynamic<?> p_dynamic, WorldDataConfiguration dataConfiguration, Registry<LevelStem> stemRegistry, RegistryAccess.Frozen _ignore) {
//        System.out.println("Starting registry fixing!");
//        List<RegistryAccess.RegistryEntry<?>> regs = new ArrayList<>(registry.registries().toList());
//        RegistryAccess.RegistryEntry<DimensionType> entry = (RegistryAccess.RegistryEntry<DimensionType>)regs.stream().filter(e -> e.key().equals(Registries.DIMENSION_TYPE)).findAny().orElse(null);
//        regs.set(regs.indexOf(entry), fixDimensionRegistry(entry));
//        RegistryAccess.ImmutableRegistryAccess replaced = new RegistryAccess.ImmutableRegistryAccess(regs.stream());
//        return replaced.freeze();
//    }
//
//    private static RegistryAccess.RegistryEntry<DimensionType> fixDimensionRegistry(RegistryAccess.RegistryEntry<DimensionType> dimRegistry) {
//        MappedRegistry<DimensionType> replaced = new MappedRegistry<>(dimRegistry.key(), dimRegistry.value().registryLifecycle());
//        for (Map.Entry<ResourceKey<DimensionType>, DimensionType> entry : dimRegistry.value().entrySet()) {
//            System.out.println("Propagating old type " + entry.getKey().toString());
//            replaced.register(entry.getKey(), entry.getValue(), dimRegistry.value().registrationInfo(entry.getKey()).orElse(null));
//        }
////        //TODO: Load coordinate somehow
////        List<RealityCoordinate> coords = List.of(RealityCoordinate.parse("uru90240uuwt15buuu"), RealityCoordinate.parse("uru90240uuwt15fuuu"), RealityCoordinate.parse("uru90240uuwt15luuu"), RealityCoordinate.parse("uru90240uuwt15ruuu"), RealityCoordinate.parse("uru90240uuwt15uuuu"));
////        for (RealityCoordinate coord : coords) {
////            System.out.println("Creating dynamic level stem for " + coord);
////            replaced.register(RealityResourceUtil.createLevelStemKeyFor(coord),
////                    RealityManager.createOverworldStem(registry, coord),
////                    new RegistrationInfo(Optional.empty(), Lifecycle.experimental()));
////        }
//        replaced.register(RealityManager.DIMENSION_TYPE_KEY, RealityManager.DIMENSION_TYPE, new RegistrationInfo(Optional.empty(), Lifecycle.stable()));
//        System.out.println("Finished types!");
//        return new RegistryAccess.RegistryEntry<>(Registries.DIMENSION_TYPE, replaced.freeze());
//    }

}
