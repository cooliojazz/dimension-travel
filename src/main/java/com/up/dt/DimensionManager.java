package com.up.dt;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.OptionalLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import qouteall.q_misc_util.dimension.DimensionIntId;

/**
 *
 * @author Ricky
 */
@EventBusSubscriber(modid = DimensionTravel.MODID)
public class DimensionManager {
    
    private static final DimensionType DIMENSION_TYPE = 
            new DimensionType(OptionalLong.empty(), true, false, false, true,
                    1.0, true, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD,
                    BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F,
                    new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)); // Copied from vanilla overworld
    private static final ArrayList<ResourceKey<Level>> dimensionKeys = new ArrayList<>();
    
    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        Registry<DimensionType> dimRegistry = server.registries.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE);
        if (!event.getLevel().isClientSide()) {
            if (event.getLevel().dimensionType() == dimRegistry.get(BuiltinDimensionTypes.OVERWORLD)) {
                getSave(server);
                for (ResourceKey<Level> key : dimensionKeys) {
//                    setupLevel(server, key);
                    // TODO: Loading saves broken until dimensinokeys stores coordinates or they have a method to parse strings back into coords
                }
            }
        } else {
            if (event.getLevel().dimensionType() == dimRegistry.get(BuiltinDimensionTypes.OVERWORLD)) {
                getSave(server);
                for (ResourceKey<Level> key : dimensionKeys) { // Needs to move to some sort of network packet for real multiplayer
                    Minecraft.getInstance().player.connection.levels().add(key);
                }
            }
        }
    }

    public static ResourceKey<Level> createLevel(boolean client) {
        RealityCoordinate coord = new RealityCoordinate(2);
        ResourceKey<Level> key = getKeyFor(coord);
        if (client) {
            Minecraft.getInstance().player.connection.levels().add(key);
            return null;
        } else {
            MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
            if (!server.levels.containsKey(key)) {
                dimensionKeys.add(key);
                setupLevel(server, coord);
                getSave(server).setDirty();
                DimensionIntId.onServerDimensionChanged(server);
            }
//            return server.levels.get(key);
            return key;
        }
    }
    
    private static void setupLevel(MinecraftServer server, RealityCoordinate coordinates) {
        ResourceKey<Level> dimensionKey = getKeyFor(coordinates);
        Registry<LevelStem> stemRegistry = server.registries.compositeAccess().registryOrThrow(Registries.LEVEL_STEM);
//        Registry<DimensionType> dimRegistry = server.registries.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE);
        ServerLevelData serverleveldata = server.getWorldData().overworldData();
        WorldOptions worldoptions = server.getWorldData().worldGenOptions();

        ServerLevel level = new ServerLevel(
                server, server.executor, server.storageSource,
                new DerivedLevelData(server.getWorldData(), serverleveldata), dimensionKey, createOverworldStem(server, stemRegistry, coordinates),
                server.progressListenerFactory.create(1), server.getWorldData().isDebugWorld(), BiomeManager.obfuscateSeed(worldoptions.seed()),
                ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(serverleveldata)),
                false, null
            );
//            worldborder.addListener(new BorderChangeListener.DelegateBorderChangeListener(level.getWorldBorder()));
        server.levels.put(dimensionKey, level);
        server.markWorldsDirty();

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.LevelEvent.Load(server.getLevel(dimensionKey)));
    }
    
    private static ResourceKey<Level> getKeyFor(RealityCoordinate coordinates) {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DimensionTravel.MODID, "gen_dim_" + coordinates));
    }
    
    private static LevelStem createOverworldStem(MinecraftServer server, Registry<LevelStem> stemRegistry, RealityCoordinate coord) {
//        return new LevelStem(Holder.direct(DIMENSION_TYPE), stemRegistry.get(LevelStem.OVERWORLD).generator());
        NoiseGeneratorSettings settings = overworld(server, false, false, coord);
        return new LevelStem(Holder.direct(DIMENSION_TYPE), new NoiseBasedChunkGenerator(stemRegistry.get(LevelStem.OVERWORLD).generator().getBiomeSource(), Holder.direct(settings)));
    }
    
    private RealityCoordinate home = new RealityCoordinate((short)0, (short)224);
    
    public static NoiseGeneratorSettings overworld(MinecraftServer server, boolean large, boolean amplified, RealityCoordinate coord) {
        int min = (coord.get(0) / 16 - 4) * 16;
        return new NoiseGeneratorSettings(
//            NoiseSettings.create(min, min + coord.get(1) / 8 * 16, 1, 2),
            NoiseSettings.create(-64, 384, 1, 2), // Well, it was workking with just one static coord, but now its not working with even none?
            Blocks.STONE.defaultBlockState(),
            Blocks.WATER.defaultBlockState(),
            NoiseRouterData.overworld(server.registries.compositeAccess().lookupOrThrow(Registries.DENSITY_FUNCTION), server.registries.compositeAccess().lookupOrThrow(Registries.NOISE), amplified, large),
            SurfaceRuleData.overworld(),
            new OverworldBiomeBuilder().spawnTarget(),
            63,
            false,
            true,
            true,
            false
        );
    }
    
    public static SavedData getSave(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(new SavedData.Factory<>(DimensionManager::create, DimensionManager::load), "alternateDimensions");
    }
    
    public static SavedData create() {
      return new SavedData() {
          @Override
          public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
              tag.putString("ids", dimensionKeys.stream().map(k -> k.location().toString()).collect(Collectors.joining(";")));
              return tag;
          }
      };
    }

    public static SavedData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        SavedData data = create();
        String ids = tag.getString("ids");
        dimensionKeys.clear();
        if (ids != null) Stream.of(ids.split(";")).forEach(s -> dimensionKeys.add(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(s))));
        return data;
    }

}
