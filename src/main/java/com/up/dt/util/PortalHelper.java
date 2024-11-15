package com.up.dt.util;

import com.up.dt.entity.FancyPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 *
 * @author Ricky
 */
public class PortalHelper {
    
    public static FancyPortal makePersonal(Level l, Player p, ResourceKey<Level> dim) {
        FancyPortal portal = FancyPortal.ENTITY_TYPE.create(l);
//        portal.setPortalShape(new qouteall.imm_ptl.core.portal.shape.SpecialFlatPortalShape(null)); // Use to make round personal portals?
        Vec3 pos = p.getEyePosition().add(p.getViewVector(0).scale(3));
        portal.setOriginPos(pos);
        portal.setDestinationDimension(dim);
        portal.setDestination(pos);
        Vec3 left = p.getUpVector(0).cross(p.getViewVector(0));
        portal.setOrientationAndSize(left.reverse(), p.getViewVector(0).cross(left), 1, 2);
        return portal;
    }
    
    public static FancyPortal makeAligned(Level l, BlockPos bPos, Direction facing, ResourceKey<Level> dim) {
        FancyPortal portal = FancyPortal.ENTITY_TYPE.create(l);
        Vec3 forward = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 left = new Vec3(0, 1, 0).cross(forward);
        Vec3 pos = bPos.getCenter().add(forward.add(left).scale(0.5).add(up));
        Vec3 in = left.cross(up);
        portal.setOriginPos(pos.add(in.scale(0.003)));
        portal.setDestinationDimension(dim);
        portal.setDestination(pos.add(in.scale(-0.003)));
        portal.setOrientationAndSize(left, up, 2, 3);
        return portal;
    }
}
