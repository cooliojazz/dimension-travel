package com.up.dt;

import com.up.dt.dimension.RealityManager;
import com.up.dt.network.JoinRealityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
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
    
}
