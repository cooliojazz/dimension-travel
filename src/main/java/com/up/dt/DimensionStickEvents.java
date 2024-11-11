package com.up.dt;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.portal.Portal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import qouteall.imm_ptl.core.api.PortalAPI;

/**
 *
 * @author Ricky
 */
@EventBusSubscriber(modid = DimensionTravel.MODID)
public class DimensionStickEvents {
    
//    public static final ResourceKey<LevelStem> DIMENSION_STEM = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath(DimensionTravel.MODID, "gen_dim"));
//    private static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(DimensionTravel.MODID, "gen_dim"));
//    private static final ResourceKey<DimensionType> DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(DimensionTravel.MODID, "gen_dim"));
//    DimensionType type = new DimensionType(
//                OptionalLong.empty(),
//                true,
//                false,
//                false,
//                true,
//                1.0,
//                true,
//                false,
//                -64,
//                384,
//                384,
//                BlockTags.INFINIBURN_OVERWORLD,
//                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
//                0.0F,
//                new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0));
    
//    @SubscribeEvent
//    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
//        if (event.getItemStack().is(DimensionTravel.DIMENSION_STICK)) {
//            ResourceKey<Level> key = DimensionManager.createLevel(event.getLevel().isClientSide);
//            if (!event.getLevel().isClientSide) {
//                Portal in = makePersonal(event.getLevel(), event.getEntity(), key);
//
//                Portal out = PortalAPI.createReversePortal(in);
//                Portal backIn = PortalAPI.createFlippedPortal(in);
//                Portal backOut = PortalAPI.createReversePortal(backIn);
//
//                in.level().addFreshEntity(in);
//                out.level().addFreshEntity(out);
//                backIn.level().addFreshEntity(backIn);
//                backOut.level().addFreshEntity(backOut);
//            }
//        }
//    }
    
    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(DimensionTravel.DIMENSION_STICK)) {
            ResourceKey<Level> key = DimensionManager.createLevel(event.getLevel().isClientSide);
            if (!event.getLevel().isClientSide) {
                Portal in = makeAligned(event.getLevel(), event.getPos(), event.getFace(), key);

                Portal out = PortalAPI.createReversePortal(in);
                Portal backIn = PortalAPI.createFlippedPortal(in);
                Portal backOut = PortalAPI.createReversePortal(backIn);

                in.level().addFreshEntity(in);
                out.level().addFreshEntity(out);
                backIn.level().addFreshEntity(backIn);
                backOut.level().addFreshEntity(backOut);
            }
        }
    }
    
    private static Portal makePersonal(Level l, Player p, ResourceKey<Level> dim) {
        Portal portal = Portal.ENTITY_TYPE.create(l);
        Vec3 pos = p.getEyePosition().add(p.getViewVector(0).scale(2));
        portal.setOriginPos(pos);
        portal.setDestinationDimension(dim);
        portal.setDestination(pos);
        Vec3 left = p.getUpVector(0).cross(p.getViewVector(0));
        portal.setOrientationAndSize(
                left.reverse(), // axisW
                p.getViewVector(0).cross(left), // axisH
                1, // width
                2 // height
            );
        return portal;
    }
    
    private static Portal makeAligned(Level l, BlockPos bPos, Direction facing, ResourceKey<Level> dim) {
        Portal portal = Portal.ENTITY_TYPE.create(l);
        Vec3 left = new Vec3(0, 1, 0).cross(Vec3.atLowerCornerOf(facing.getNormal()));
        Vec3 pos = bPos.getCenter().add(Vec3.atLowerCornerOf(facing.getNormal()).add(left).scale(0.5));
        portal.setOriginPos(pos);
        portal.setDestinationDimension(dim);
        portal.setDestination(pos);
        portal.setOrientationAndSize(
                left, // axisW
                new Vec3(0, 1, 0), // axisH
                2, // width
                3 // height
            );
        return portal;
    }
    
}