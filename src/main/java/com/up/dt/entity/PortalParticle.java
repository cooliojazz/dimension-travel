package com.up.dt.entity;

import com.mojang.serialization.MapCodec;
import com.up.dt.DimensionTravelMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.joml.Vector3f;
import java.util.function.Supplier;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;


/**
 *
 * @author Ricky
 */
public class PortalParticle extends DustParticle {
    
    public PortalParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, DustParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        friction = 0.97f;
        speedUpWhenYMotionIsBlocked = false;
        gravity = -0.01f;
        lifetime = 25;
    }
    
    public static class Options extends DustParticleOptions {
        
        public Options(Vector3f color, float scale) {
            super(color, scale);
        }

        @Override
        public ParticleType<DustParticleOptions> getType() {
            return DimensionTravelMod.PORTAL_PARTICLE.get();
        }
        
    }
    
    public static class Provider implements ParticleProvider<DustParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(DustParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, this.sprites);
        }
    }
    
    public static final Supplier<ParticleType<DustParticleOptions>> dustTypeSupplier = () -> new ParticleType<DustParticleOptions>(false) {
            @Override
            public MapCodec<DustParticleOptions> codec() {
                return DustParticleOptions.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, DustParticleOptions> streamCodec() {
                return DustParticleOptions.STREAM_CODEC;
            }
        };
}
