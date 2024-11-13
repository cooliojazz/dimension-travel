package com.up.dt.entity;

import com.up.dt.DimensionTravelMod;
import com.up.dt.dimension.DimensionManager;
import com.up.dt.dimension.RealityCoordinate;
import com.up.dt.dimension.RealityDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
@EventBusSubscriber(modid = DimensionTravelMod.MODID)
public class DimensionStickEvents {
    
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
        if (event.getItemStack().is(DimensionTravelMod.DIMENSION_STICK) && !event.getLevel().isClientSide) {
//        RealityCoordinate coord = new RealityCoordinate((short)200, (short)100, (short)200, (short)250, (short)50);
//        RealityCoordinate coord = new RealityCoordinate((short)1, (short)2, (short)4, (short)8, (short)16);
            ResourceKey<Level> key = DimensionManager.createLevel(RealityCoordinate.random(6));
//            ResourceKey<Level> key = DimensionManager.createLevel(DimensionManager.realities.get((ServerLevel)event.getLevel()).clone().offset(RealityDirection.H_SCALE, (short)(64 * (event.getHand() == InteractionHand.MAIN_HAND ? 1 : -1))));
            Portal in = makeAligned(event.getLevel(), event.getPos(), event.getFace(), key);

            Portal out = PortalAPI.createReversePortal(in);
//            Portal backIn = PortalAPI.createFlippedPortal(in);
//            Portal backOut = PortalAPI.createReversePortal(backIn);

            in.level().addFreshEntity(in);
            out.level().addFreshEntity(out);
//            backIn.level().addFreshEntity(backIn);
//            backOut.level().addFreshEntity(backOut);
        }
    }
    
    private static Portal makePersonal(Level l, Player p, ResourceKey<Level> dim) {
        Portal portal = Portal.ENTITY_TYPE.create(l);
//        portal.setPortalShape(new qouteall.imm_ptl.core.portal.shape.SpecialFlatPortalShape(null)); // Use to make round personal portals?
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
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 left = new Vec3(0, 1, 0).cross(Vec3.atLowerCornerOf(facing.getNormal()));
        Vec3 pos = bPos.getCenter().add(Vec3.atLowerCornerOf(facing.getNormal()).add(left).scale(0.5));
        Vec3 in = left.cross(up);
        portal.setOriginPos(pos.add(in.scale(0.005)));
        portal.setDestinationDimension(dim);
        portal.setDestination(pos.add(in.scale(-0.005)));
        portal.setOrientationAndSize(
                left, // axisW
                up, // axisH
                2, // width
                3 // height
            );
        return portal;
    }
    
}