package com.up.dt;

import com.up.dt.dimension.RealityManager;
import com.up.dt.network.CoordinatePacket;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.up.dt.dimension.RealityCoordinate;
import com.up.dt.entity.FancyPortal;
import com.up.dt.entity.PortalParticle;
import com.up.dt.item.CoordinatePaperItem;
import com.up.dt.item.DimensionStickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(DimensionTravelMod.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DimensionTravelMod.MODID)
public class DimensionTravelMod {
    
    public static final String MODID = "dimension_travel";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MODID);
    
    public static final DeferredItem<Item> DIMENSION_STICK = ITEMS.registerItem("dimension_stick", DimensionStickItem::new, new Item.Properties().setNoRepair().durability(16).fireResistant());
    public static final DeferredItem<Item> COORDINATE_PAPER = ITEMS.registerItem("coordinate_paper", CoordinatePaperItem::new);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register(MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> DIMENSION_STICK.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(DIMENSION_STICK.get());
                output.accept(COORDINATE_PAPER.get());
            }).build());
    
    public static final DeferredHolder<ParticleType<?>, ParticleType<DustParticleOptions>> PORTAL_PARTICLE = DimensionTravelMod.PARTICLE_TYPES.register("reality-portal", PortalParticle.dustTypeSupplier);
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RealityCoordinate>> REALITY_COORDINATE = DATA_COMPONENTS.registerComponentType("reality-coordinate", builder -> builder.persistent(RealityCoordinate.CODEC).networkSynchronized(RealityCoordinate.STREAM_CODEC));

    public DimensionTravelMod(IEventBus modEventBus, ModContainer modContainer) {
        ENTITY_TYPE.register("fancy-portal", () -> FancyPortal.ENTITY_TYPE);
        
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        ENTITY_TYPE.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    private static void commonSetup(final FMLCommonSetupEvent event) {
        // Common
    }
	
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(DimensionTravelMod.MODID).versioned("1");
        registrar.playToClient(CoordinatePacket.TYPE, CoordinatePacket.STREAM_CODEC, (p, c) -> RealityManager.addClientLevel(p.coordinate()));
    }
	
//    @SubscribeEvent
//    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
////        event.registerLayerDefinition(layerLocation, supplier);
//    }
	
    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(FancyPortal.ENTITY_TYPE, FancyPortal.Renderer::new);
    }
    
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(PORTAL_PARTICLE.get(), PortalParticle.Provider::new);
    }
}
