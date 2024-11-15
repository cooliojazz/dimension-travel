package com.up.dt.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.up.dt.DimensionTravelMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.my_util.DQuaternion;

/**
 *
 * @author Ricky
 */
public class FancyPortal extends Portal {
    
    // Copy base portal type settings
    public static final EntityType<FancyPortal> ENTITY_TYPE = EntityType.Builder.of(FancyPortal::new, MobCategory.MISC).fireImmune().clientTrackingRange(96).updateInterval(20).setShouldReceiveVelocityUpdates(true).build("fancy-portal");
    
    public FancyPortal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (Math.random() < 0.03) {
            Vec3 dir = getAxisW().cross(getAxisH());
            
            Vec3 pos = new Vec3(getX(), getY(), getZ());
            pos = pos.add(getAxisW().scale(Math.random() - 0.5).scale(getWidth()));
            pos = pos.add(getAxisH().scale(Math.random() - 0.5).scale(getHeight()));
            
            Vec3 vel = dir.add(new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5)).normalize();
            vel = vel.scale(0.25 + Math.random() * 0.5).scale(0.1);
            
            Vector3f color = new Vector3f(0.75f + (float)Math.random() / 15, 0.7f + (float)Math.random() / 10, (float)Math.random() / 10);
            level().addParticle(new PortalParticle.Options(color, 0.25f + (float)Math.random() / 2), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
        }
        
//        ServerLevel sl = (ServerLevel)level();
//        sl.getLightEngine().
    }
    
    
    
    public static class Renderer extends EntityRenderer<FancyPortal> {
        
        private static final ResourceLocation FRONT = ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "block/fancy-portal");
        private static final ResourceLocation BACK = ResourceLocation.fromNamespaceAndPath(DimensionTravelMod.MODID, "block/fancy-portal-back");

        public Renderer(EntityRendererProvider.Context ctx) {
            super(ctx);
        }
        
        @Override
        public ResourceLocation getTextureLocation(FancyPortal entity) {
            return FRONT;
        }

        @Override
        public void render(FancyPortal portal, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            super.render(portal, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            renderPortalBack(portal, poseStack, bufferSource, packedLight, partialTick);
        }

        protected void renderPortalBack(FancyPortal portal, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
            poseStack.pushPose();
            poseStack.mulPose(DQuaternion.fromFacingVecs(portal.getAxisW(), portal.getAxisH()).toMatrix());
            poseStack.translate(0, 0, -0.002);
            renderPortalOutline(poseStack, bufferSource.getBuffer(RenderType.SOLID), (float)portal.getWidth() * 1.1f, (float)portal.getHeight() * 1.1f);
            poseStack.popPose();
        }
        
        private static void renderPortalOutline(PoseStack matrixStack, VertexConsumer builder, float width, float height) {
            // Maybe use these instad?
    //        BakedQuad quad = new BakedQuad(vertices, 0, Direction.DOWN, sprite, true);
            
            int b1 = LightTexture.FULL_BRIGHT >> 16 & 65535; // Ignore passed packedLight and create full I think?
            int b2 = LightTexture.FULL_BRIGHT & 65535;
            
            TextureAtlasSprite front = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(FRONT);
            TextureAtlasSprite back = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(BACK);
            Matrix4f matrix = matrixStack.last().pose();
            
            builder.addVertex(matrix, width / 2, -height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(front.getU0(), front.getV0()).setUv2(b1, b2).setNormal(0, 0, 1);
            builder.addVertex(matrix, width / 2, height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(front.getU0(), front.getV1()).setUv2(b1, b2).setNormal(0, 0, 1);
            builder.addVertex(matrix, -width / 2, height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(front.getU1(), front.getV1()).setUv2(b1, b2).setNormal(0, 0, 1);
            builder.addVertex(matrix, -width / 2, -height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(front.getU1(), front.getV0()).setUv2(b1, b2).setNormal(0, 0, 1);
            
            builder.addVertex(matrix, -width / 2, -height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(back.getU0(), back.getV0()).setUv2(b1, b2).setNormal(0, 0, 1);
            builder.addVertex(matrix, -width / 2, height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(back.getU0(), back.getV1()).setUv2(b1, b2).setNormal(0, 0, 1);
            builder.addVertex(matrix, width / 2, height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(back.getU1(), back.getV1()).setUv2(b1, b2).setNormal(0, 0, 1);
            builder.addVertex(matrix, width / 2, -height / 2, 0.0f).setColor(255, 255, 255, 255).setUv(back.getU1(), back.getV0()).setUv2(b1, b2).setNormal(0, 0, 1);
        }
        
    }
}
