package com.up.dt;

import com.up.dt.dimension.DimensionManager;
import com.up.dt.network.CoordinatePacket;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final DeferredItem<Item> DIMENSION_STICK = ITEMS.registerSimpleItem("dimension_stick");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID)) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> DIMENSION_STICK.get().getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(DIMENSION_STICK.get())).build());

    public DimensionTravelMod(IEventBus modEventBus, ModContainer modContainer) {
//        modEventBus.addListener(this::commonSetup);
        
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
//        BakedQuad quad = new BakedQuad(vertices, 0, Direction.DOWN, sprite, true);
//        MobRenderer m = new MobRenderer(context, model, 0) {
//            @Override
//            public ResourceLocation getTextureLocation(Entity entity) {
//                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//            }
//
//            @Override
//            public void render(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//                buffer.getBuffer(RenderType.SOLID).putBulkData(pose, quad, entityYaw, entityYaw, entityYaw, entityYaw, packedLight, packedLight);
//            }
//            
//            
//        }
    }

    @SubscribeEvent
    private static void commonSetup(final FMLCommonSetupEvent event) {
        // Common
    }
	
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(DimensionTravelMod.MODID).versioned("1");
        registrar.playToClient(CoordinatePacket.TYPE, CoordinatePacket.STREAM_CODEC, (p, c) -> DimensionManager.addClientLevel(p.coordinate()));
    }
}
