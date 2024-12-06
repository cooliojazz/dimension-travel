package com.up.dt;

import com.up.dt.dimension.RealityManager;
import com.up.dt.entity.FancyPortal;
import com.up.dt.network.JoinRealityPacket;
import com.up.dt.util.PortalHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 *
 * @author Ricky
 */
@EventBusSubscriber
public class GameEvents {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer) PacketDistributor.sendToPlayer((ServerPlayer)event.getEntity(), new JoinRealityPacket(RealityManager.realities.get(event.getLevel())));
    }

    @SubscribeEvent
    public static void onPortalClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof FancyPortal p && event.getEntity().getInventory().getSelected().is(DimensionTravelMod.DIMENSION_STICK)) {
            p.remove(Entity.RemovalReason.KILLED);
        }
    }
    
}
