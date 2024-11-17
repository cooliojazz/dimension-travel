package com.up.dt.dimension;

import com.up.dt.network.NewRealityPacket;
import com.google.common.collect.ImmutableList;
import com.up.dt.DimensionTravelMod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.OptionalLong;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
public class RealityManager {

    public static final ResourceKey<DimensionType> DIMENSION_TYPE_KEY = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "alter-overworld"));
    
    private static final ArrayList<ResourceKey<Level>> dimensionKeys = new ArrayList<>();
    public static final HashMap<Level, RealityCoordinate> realities = new HashMap<>();
    private static RealityCoordinate current = null;
    
    private static final RealityCoordinate HOME_COORDINATE = new RealityCoordinate((short)0, (short)224, (short)127, (short)64, (short)0, (short)75, (short)63, (short)63); //Convert to .withs
    
    @OnlyIn(Dist.CLIENT)
    public static RealityCoordinate currentReality() {
        return current;
    }
    
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
                    setupLevel(server, coord, RealityResourceUtil.createLevelKeyFor(coord));
                }
            }
        } else {
            if (event.getLevel().dimensionType() == dimRegistry.get(BuiltinDimensionTypes.OVERWORLD)) {
                DimensionsData save = DimensionsData.getSave(server);
                dimensionKeys.clear();
                for (String id : save.getIds()) {
                    dimensionKeys.add(RealityResourceUtil.createLevelKeyFor(RealityCoordinate.parse(id)));
                }
                for (ResourceKey<Level> key : dimensionKeys) { // Needs to move to some sort of network packet for real multiplayer
                    Minecraft.getInstance().player.connection.levels().add(key);
                }
            }
        }
    }

    public static ResourceKey<Level> createLevel(RealityCoordinate coord) {
        ResourceKey<Level> key = RealityResourceUtil.createLevelKeyFor(coord);
        MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
        if (!server.levels.containsKey(key)) {
            setupLevel(server, coord, key);
            DimensionsData save = DimensionsData.getSave(server);
            save.setIds(dimensionKeys.stream().map(k -> k.location().getPath().toString().replaceAll("alter_(.+)", "$1")).toArray(String[]::new));
            
            PacketDistributor.sendToAllPlayers(new NewRealityPacket(coord));
            DimensionIntId.onServerDimensionChanged(server);

        }
        return key;
    }
    
    public static void setCurrentReality(RealityCoordinate coord) {
        current = coord;
    }
    
    public static void addClientLevel(RealityCoordinate coord) {
        Minecraft.getInstance().player.connection.levels().add(RealityResourceUtil.createLevelKeyFor(coord));
    }
    
    private static void setupLevel(MinecraftServer server, RealityCoordinate coordinate, ResourceKey<Level> dimensionKey) {
        ServerLevelData serverleveldata = server.getWorldData().overworldData();
        WorldOptions worldoptions = server.getWorldData().worldGenOptions();

        ServerLevel level = new ServerLevel(
                server, server.executor, server.storageSource,
                new DerivedLevelData(server.getWorldData(), serverleveldata), dimensionKey, createOverworldStem(server.registries.compositeAccess(), coordinate),
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
        
//        DimensionIntId.onServerDimensionChanged(server);
    }
    
    public static LevelStem createOverworldStem(RegistryAccess.Frozen registries, RealityCoordinate coord) {
        NoiseGeneratorSettings settings = overworld(registries, false, false, coord);
        NoiseBasedChunkGenerator gen = new NoiseBasedChunkGenerator(
                MultiNoiseBiomeSource.createFromPreset(registries.registryOrThrow(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST).getHolderOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)),
                Holder.direct(settings));
        return new LevelStem(registries.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(DIMENSION_TYPE_KEY), gen);
    }
    
    private static final BlockState[] stones = new BlockState[] {Blocks.SMOOTH_STONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.ANDESITE.defaultBlockState(), Blocks.GRAVEL.defaultBlockState(), Blocks.CLAY.defaultBlockState()};
    private static final BlockState[] liquids = new BlockState[] {Blocks.WATER.defaultBlockState(), Blocks.LAVA.defaultBlockState()};
    
    public static NoiseGeneratorSettings overworld(RegistryAccess.Frozen registries, boolean large, boolean amplified, RealityCoordinate coord) {
        int min = (coord.get(RealityDirection.MIN_Y) / 32 - 4) * 16;
        return new NoiseGeneratorSettings(
            NoiseSettings.create(min, (coord.get(RealityDirection.HEIGHT) / 8 + 1) * 16, 1 + (int)Math.round(coord.get(RealityDirection.H_SCALE) / 255d * 4), 1 + (int)Math.round(coord.get(RealityDirection.V_SCALE) / 255d * 4)),
            stones[(int)Math.floor(coord.get(RealityDirection.STONE_TYPE) / 255d * stones.length)],
            liquids[(int)Math.floor(coord.get(RealityDirection.OCEAN_TYPE) / 255d * liquids.length)],
            CustomNoiseRouterData.overworld(registries.lookupOrThrow(Registries.DENSITY_FUNCTION), registries.lookupOrThrow(Registries.NOISE), amplified, large ? 64 : 0, coord.get(RealityDirection.BIOME_SCALE) / 255.0),
            SurfaceRuleData.overworld(),
            new OverworldBiomeBuilder().spawnTarget(),
            coord.get(RealityDirection.OCEAN_LEVEL) / 2,
            false, true, true, false);
    }

}
