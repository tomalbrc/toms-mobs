package de.tomalbrc.toms_mobs.entities;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedHolder;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.simple.SimpleEntityHolder;
import de.tomalbrc.bil.core.model.Model;
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

import java.util.List;

public class IceSpikeSmall extends Entity implements AnimatedEntity, TraceableEntity {
    public static final ResourceLocation ID = Util.id("ice_spike_small");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<IceSpikeSmall> holder;

    @Nullable
    private LivingEntity owner;
    private boolean didAttack;

    @Override
    public EntityHolder<IceSpikeSmall> getHolder() {
        return this.holder;
    }

    public IceSpikeSmall(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);

        this.setInvisible(true);
        this.setNoGravity(true);

        this.holder = new SimpleEntityHolder<>(this, MODEL);
        this.holder.getAnimator().playAnimation("up", 10, () -> {
            this.level().playSound(null, this, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, .6f, .75f);
            this.holder.getAnimator().playAnimation("down", 10, this::discard);
        });

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

        if (this.tickCount > 8 && !this.didAttack) {
            this.spawnParticles();

            this.didAttack = true;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.8, 0.25, 0.8));

            for (LivingEntity livingEntity : list) {
                if (livingEntity != this.owner)
                    this.dealDamageTo(livingEntity);
            }
        }

        if (this.tickCount < 24 && this.tickCount % 3 == 0 && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, position().x, position().y, position().z, 10, 0.25, 0.25, 0.25, 0);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    private void dealDamageTo(LivingEntity livingEntity) {
        if (livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != this.owner) {
            if (this.owner == null) {
                livingEntity.hurt(this.damageSources().magic(), 4.0F);
            } else if (!this.owner.isAlliedTo(livingEntity)) {
                livingEntity.hurt(this.damageSources().indirectMagic(this, this.owner), 6.0F);
            }

            float scale = 2;
            livingEntity.setDeltaMovement(Math.cos(Math.toRadians(this.getYRot() + 90)) * scale, 0.75, Math.sin(Math.toRadians(this.getYRot() + 90)) * scale);
        }
    }

    private void spawnParticles() {
        // spawn particles
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