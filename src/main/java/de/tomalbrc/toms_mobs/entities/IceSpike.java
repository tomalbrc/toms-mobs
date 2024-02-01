package de.tomalbrc.toms_mobs.entities;

import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import de.tomalbrc.toms_mobs.util.Util;
import org.provim.nylon.api.AjEntity;
import org.provim.nylon.data.AjLoader;
import org.provim.nylon.holders.entity.EntityHolder;
import org.provim.nylon.holders.entity.simple.SimpleEntityHolder;
import org.provim.nylon.model.AjModel;

import java.util.List;

public class IceSpike extends Entity implements AjEntity, TraceableEntity {
    public static final ResourceLocation ID = Util.id("ice_spike");
    public static final AjModel MODEL = AjLoader.require(ID);
    private final EntityHolder<IceSpike> holder;

    @Nullable
    private LivingEntity owner;
    private boolean didAttack;

    @Override
    public EntityHolder<IceSpike> getHolder() {
        return this.holder;
    }

    public IceSpike(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);

        this.setInvisible(true);
        this.setNoGravity(true);

        this.holder = new SimpleEntityHolder<>(this, MODEL);
        this.holder.getAnimator().playAnimation("up", 10);

        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public void setYRot(float f) {
        super.setYRot(f);

        this.holder.getElements().forEach(element -> {
            if (element instanceof DisplayElement displayElement) {
                displayElement.setYaw(f);
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount == 2) {
            this.level().playSound(null, this, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, .6f, .75f);
        } else if (this.tickCount == 6) {
            this.level().playSound(null, this, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.8f, 0.9f);
        }

        if (this.tickCount > 8 && !this.didAttack) {
            this.spawnParticles();

            this.didAttack = true;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.8, 0.8, 0.8));

            for (LivingEntity livingEntity : list) {
                if (livingEntity != this.owner)
                    this.dealDamageTo(livingEntity);
            }
        }

        if (this.tickCount < 40 && this.tickCount % 2 == 0 && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, position().x, position().y, position().z, 10, 0.3, 1, 0.3, 0);
        }

        if (this.tickCount == 40) {
            this.holder.getAnimator().playAnimation("down", 12);
        } else if (this.tickCount > 45) {
            this.discard();
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setNoGravity(true);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    private void dealDamageTo(LivingEntity livingEntity) {
        if (livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != this.owner) {
            if (this.owner == null) {
                livingEntity.hurt(this.damageSources().magic(), 8.0F);
            } else if (!this.owner.isAlliedTo(livingEntity)) {
                livingEntity.hurt(this.damageSources().indirectMagic(this, this.owner), 8.0F);
            }

            livingEntity.setTicksFrozen(10 * 20);
            float scale = 4;
            livingEntity.setDeltaMovement(Math.cos(Math.toRadians(this.getYRot())) * scale, 1, Math.sin(Math.toRadians(this.getYRot())) * scale);
        }
    }

    private void spawnParticles() {
        int particleCount = 20;
        float radius = 2;

        if (this.level() instanceof ServerLevel serverLevel) {
            RandomSource random = this.random;

            for (int i = 0; i < particleCount; i++) {
                double offsetX = random.nextDouble() * radius - radius * 0.5;
                double offsetY = random.nextDouble() * radius - radius * 0.5;
                double offsetZ = random.nextDouble() * radius - radius * 0.5;

                double x = getX() + offsetX;
                double y = getY() + offsetY;
                double z = getZ() + offsetZ;

                serverLevel.sendParticles(ParticleTypes.CRIT, x, y + 1.f, z, 0, random.nextDouble(), random.nextDouble(), random.nextDouble(), 0);
            }
        }
    }

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    protected void defineSynchedData() {
    }
}