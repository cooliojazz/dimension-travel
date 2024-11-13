package com.up.dt.dimension;

import com.up.dt.network.CoordinatePacket;
import com.google.common.collect.ImmutableList;
import com.up.dt.DimensionTravelMod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.OptionalLong;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.SurfaceRuleData;
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
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import qouteall.q_misc_util.dimension.DimensionIntId;

/**
 *
 * @author Ricky
 */
@EventBusSubscriber(modid = DimensionTravelMod.MODID)
public class DimensionManager {
    
    private static final DimensionType DIMENSION_TYPE = new DimensionType(
            OptionalLong.empty(), true, false, false, true, 1.0, true,
            false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD,
            BuiltinDimensionTypes.OVERWORLD_EFFECTS, 0.0F,
            new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)); // Copied from vanilla overworld
    
    private static final ArrayList<ResourceKey<Level>> dimensionKeys = new ArrayList<>();
    public static final HashMap<ServerLevel, RealityCoordinate> realities = new HashMap<>();
    
    private static final RealityCoordinate HOME_COORDINATE = new RealityCoordinate((short)0, (short)224, (short)127, (short)64, (short)0, (short)75); //Convert to .withs
    
    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        Registry<DimensionType> dimRegistry = server.registries.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE);
        if (!event.getLevel().isClientSide()) {
            if (event.getLevel().dimensionType() == dimRegistry.get(BuiltinDimensionTypes.OVERWORLD)) {
                realities.put((ServerLevel)event.getLevel(), HOME_COORDINATE);
                DimensionsData save = DimensionsData.getSave(server);
                dimensionKeys.clear();
                for (String id : save.getIds()) {
                    RealityCoordinate coord = RealityCoordinate.parse(id);
                    setupLevel(server, coord, getKeyFor(coord));
                }
            }
        } else {
            if (event.getLevel().dimensionType() == dimRegistry.get(BuiltinDimensionTypes.OVERWORLD)) {
                DimensionsData save = DimensionsData.getSave(server);
                dimensionKeys.clear();
                for (String id : save.getIds()) {
                    dimensionKeys.add(getKeyFor(RealityCoordinate.parse(id)));
                }
                for (ResourceKey<Level> key : dimensionKeys) { // Needs to move to some sort of network packet for real multiplayer
                    Minecraft.getInstance().player.connection.levels().add(key);
                }
            }
        }
    }

    public static ResourceKey<Level> createLevel(RealityCoordinate coord) {
        ResourceKey<Level> key = getKeyFor(coord);
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        if (!server.levels.containsKey(key)) {
            setupLevel(server, coord, key);
            DimensionsData save = DimensionsData.getSave(server);
            save.setIds(dimensionKeys.stream().map(k -> k.location().getPath().toString().replaceAll("alter_(.+)", "$1")).toArray(String[]::new));
            
            PacketDistributor.sendToAllPlayers(new CoordinatePacket(coord));
            DimensionIntId.onServerDimensionChanged(server);

        }
        return key;
    }
    
    public static void addClientLevel(RealityCoordinate coord) {
        Minecraft.getInstance().player.connection.levels().add(getKeyFor(coord));
    }
    
    private static void setupLevel(MinecraftServer server, RealityCoordinate coordinate, ResourceKey<Level> dimensionKey) {
        Registry<LevelStem> stemRegistry = server.registries.compositeAccess().registryOrThrow(Registries.LEVEL_STEM);
        ServerLevelData serverleveldata = server.getWorldData().overworldData();
        WorldOptions worldoptions = server.getWorldData().worldGenOptions();

        ServerLevel level = new ServerLevel(
                server, server.executor, server.storageSource,
                new DerivedLevelData(server.getWorldData(), serverleveldata), dimensionKey, createOverworldStem(server, stemRegistry, coordinate),
                server.progressListenerFactory.create(1), server.getWorldData().isDebugWorld(), BiomeManager.obfuscateSeed(worldoptions.seed()),
                ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(serverleveldata)),
                false, null
            );
//        worldborder.addListener(new BorderChangeListener.DelegateBorderChangeListener(level.getWorldBorder()));
        server.levels.put(dimensionKey, level);
        server.markWorldsDirty();

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.LevelEvent.Load(server.getLevel(dimensionKey)));
        
        dimensionKeys.add(dimensionKey);
        realities.put(level, coordinate);
    }
    
    private static ResourceKey<Level> getKeyFor(RealityCoordinate coordinate) {
        return ResourceKey.create(Registries.DIMENSION, getLocationFor(coordinate));
    }
    
    private static ResourceLocation getLocationFor(RealityCoordinate coordinates) {
        return ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "alter_" + coordinates);
    }
    
    private static LevelStem createOverworldStem(MinecraftServer server, Registry<LevelStem> stemRegistry, RealityCoordinate coord) {
        NoiseGeneratorSettings settings = overworld(server, false, false, coord);
        return new LevelStem(Holder.direct(DIMENSION_TYPE), new NoiseBasedChunkGenerator(stemRegistry.get(LevelStem.OVERWORLD).generator().getBiomeSource(), Holder.direct(settings)));
    }
    
    private static final BlockState[] stones = new BlockState[] {Blocks.SMOOTH_STONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.ANDESITE.defaultBlockState(), Blocks.GRAVEL.defaultBlockState(), Blocks.CLAY.defaultBlockState()};
    
    public static NoiseGeneratorSettings overworld(MinecraftServer server, boolean large, boolean amplified, RealityCoordinate coord) {
        int min = (coord.get(RealityDirection.MIN_Y.ordinal()) / 32 - 4) * 16;
        return new NoiseGeneratorSettings(
            NoiseSettings.create(min, (coord.get(RealityDirection.HEIGHT.ordinal()) / 8 + 1) * 16, 1 + (int)Math.round(RealityDirection.H_SCALE.ordinal() / 255d * 4), 1 + (int)Math.round(RealityDirection.V_SCALE.ordinal() / 255d * 4)),
            stones[(int)Math.floor(coord.get(RealityDirection.STONE_TYPE.ordinal()) / 255d * stones.length)],
            Blocks.WATER.defaultBlockState(),
            NoiseRouterData.overworld(server.registries.compositeAccess().lookupOrThrow(Registries.DENSITY_FUNCTION), server.registries.compositeAccess().lookupOrThrow(Registries.NOISE), amplified, large),
            SurfaceRuleData.overworld(),
            new OverworldBiomeBuilder().spawnTarget(),
            coord.get(RealityDirection.OCEAN_LEVEL.ordinal()) / 2,
            false,
            true,
            true,
            false
        );
    }

}
