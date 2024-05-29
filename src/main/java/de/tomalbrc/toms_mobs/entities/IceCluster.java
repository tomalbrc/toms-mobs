package de.tomalbrc.toms_mobs.entities;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.simple.SimpleEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import de.tomalbrc.toms_mobs.util.Util;

import java.util.List;
import java.util.UUID;

public class IceCluster extends Entity implements AnimatedEntity, TraceableEntity {
    public static final ResourceLocation ID = Util.id("ice_cluster");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<IceCluster> holder;

    @Nullable
    private LivingEntity owner;
    private UUID ownerUUID;

    private LivingEntity target;
    private UUID targetUUID;

    private boolean falling = false;
    private int groundTicks = 0;

    @Override
    public EntityHolder<IceCluster> getHolder() {
        return this.holder;
    }

    public IceCluster(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);

        this.setInvisible(true);
        this.setNoGravity(true);

        this.holder = new SimpleEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

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

        if (this.getTarget() != null) {
            Vec3 towardsTarget = this.getTarget().position().subtract(this.position()).scale(0.08);

            double distance = towardsTarget.horizontalDistance();
            if ((position().y > this.getTarget().position().y && distance < 0.08) || falling) {
                if (!falling) {
                    falling = true;
                    //this.setDeltaMovement(0, 0, 0);
                }
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
                if (onGround() || groundTicks > 0) {
                    if (groundTicks == 0) {
                        this.hitEntities();
                    }

                    groundTicks++;

                    if (this.groundTicks % 2 == 0 && level() instanceof ServerLevel serverLevel) {
                        int num = 16;
                        for (int i = 0; i < num; i++) {
                            double step = (Math.PI * 2 / num);

                            Vec3 position = position();
                            Vec3 delta = new Vec3(Math.cos(step * i), 0.3, Math.sin(step * i)).scale(0.03);
                            serverLevel.sendParticles(ParticleTypes.CLOUD, position.x, position.y + 0.2, position.z, 0, delta.x, delta.y, delta.z, 2);
                        }
                    }

                    if (this.groundTicks > 5)
                        this.discard();
                }
            } else {
                this.setDeltaMovement(towardsTarget.subtract(0, towardsTarget.y, 0).add(0, 0.1 * (this.getTarget().position().y - position().y + 4), 0));
            }
        } else {
            this.discard();
        }

        if (falling && groundTicks == 0) {
            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, position().x, position().y, position().z, 10, 0.3, 1, 0.3, 0);
            }
        }

        if (this.tickCount > 20 * 20) {
            this.hitEntities();
            this.discard();
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void hitEntities() {
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1, 1, 1));

        for (LivingEntity livingEntity : list) {
            if (livingEntity != this.getOwner())
                this.dealDamageTo(livingEntity);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        if (compoundTag.hasUUID("Target")) {
            this.targetUUID = compoundTag.getUUID("Target");
        }

        this.setNoGravity(true);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        if (this.targetUUID != null) {
            compoundTag.putUUID("Target", this.targetUUID);
        }
    }


    private void dealDamageTo(LivingEntity livingEntity) {
        if (livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != this.getOwner()) {
            if (this.getOwner() == null) {
                livingEntity.hurt(this.damageSources().magic(), 14.0F);
            } else if (!this.getOwner().isAlliedTo(livingEntity)) {
                livingEntity.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 10.0F);
            }

            livingEntity.setTicksFrozen(15 * 20);
            spawnParticles(livingEntity.position());
        }
    }

    private void spawnParticles(Vec3 pos) {
        int particleCount = 20;
        float radius = 2;

        if (this.level() instanceof ServerLevel serverLevel) {
            RandomSource random = this.random;

            for (int i = 0; i < particleCount; i++) {
                double offsetX = random.nextDouble() * radius - radius * 0.5;
                double offsetY = random.nextDouble() * radius - radius * 0.5;
                double offsetZ = random.nextDouble() * radius - radius * 0.5;

                double x = pos.x + offsetX;
                double y = pos.y + offsetY;
                double z = pos.z + offsetZ;

                serverLevel.sendParticles(ParticleTypes.CRIT, x, y + 1.f, z, 0, random.nextDouble(), random.nextDouble(), random.nextDouble(), 0);
            }
        }
    }

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        if (livingEntity != null)
            this.ownerUUID = livingEntity.getUUID();
    }

    @Override
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }

        return this.owner;
    }

    public LivingEntity getTarget() {
        if (this.target == null && this.targetUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.targetUUID);
            if (entity instanceof LivingEntity) {
                this.target = (LivingEntity) entity;
            }
        }

        return this.target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
        if (target != null)
            this.targetUUID = target.getUUID();
    }
}