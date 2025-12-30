package aqario.fowlplay.common.entity.bird;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class TameableBirdEntity extends TrustingBirdEntity implements OwnableEntity {
    protected byte TAMEABLE_FLAGS = 0;
    @Nullable protected EntityReference<@NotNull Player> OWNER = null;
    private boolean sitting;

    protected TameableBirdEntity(EntityType<? extends @NotNull BirdEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        if (OWNER != null) {
            OWNER.store(nbt, "owner");
        }

        nbt.putBoolean("sitting", this.sitting);
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput nbt) {
        super.readAdditionalSaveData(nbt);

        nbt.read("owner", UUIDUtil.LENIENT_CODEC).ifPresent(uuid -> {
            this.setOwnerUuid(uuid);
            this.setTamed(true);
        });

        this.sitting = nbt.getBooleanOr("sitting", this.sitting);
        this.setInSittingPose(this.sitting);
    }

    protected void showEmoteParticle(boolean positive) {
        ParticleOptions particleEffect = ParticleTypes.HEART;
        if (!positive) {
            particleEffect = ParticleTypes.SMOKE;
        }

        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleEffect, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.TAMING_SUCCEEDED) {
            this.showEmoteParticle(true);
        } else if (status == EntityEvent.TAMING_FAILED) {
            this.showEmoteParticle(false);
        } else {
            super.handleEntityEvent(status);
        }
    }

    public boolean isTamed() {
        return (TAMEABLE_FLAGS & 4) != 0;
    }

    public void setTamed(boolean tamed) {
        byte b = TAMEABLE_FLAGS;
        if (tamed) {
            TAMEABLE_FLAGS = (byte) (b | 4);
        } else {
            TAMEABLE_FLAGS = (byte) (b & -5);
        }

        this.onTamedChanged();
    }

    protected void onTamedChanged() {
    }

    public boolean isInSittingPose() {
        return (TAMEABLE_FLAGS & 1) != 0;
    }

    public void setInSittingPose(boolean inSittingPose) {
        byte b = TAMEABLE_FLAGS;
        if (inSittingPose) {
            TAMEABLE_FLAGS = (byte) (b | 1);
        } else {
            TAMEABLE_FLAGS = (byte) (b & -2);
        }
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        if (!this.level().isClientSide() && !this.isInvulnerableTo(level, source)) {
            this.setSitting(false);
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwnerReference() != null) {
            if (!this.trustsUuid(this.getOwnerReference().getUUID())) {
                this.addTrustedUuid(this.getOwnerReference().getUUID());
            }
            if (!this.isPersistenceRequired()) {
                this.setPersistenceRequired();
            }
        }
        if (this.isFlying()) {
            this.setSitting(false);
        }
        if (!this.level().isClientSide()) {
            if (this.isSitting()) {
                this.getNavigation().stop();
                this.setInSittingPose(true);
            } else {
                this.setInSittingPose(false);
            }
        }
    }

    @Nullable
    @Override
    public EntityReference getOwnerReference() {
        return OWNER;
    }

    public void setOwnerUuid(UUID uuid) {
        OWNER = EntityReference.of(uuid);
    }

    public void setOwner(Player player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUUID());
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer) player, this);
        }
    }

    @Nullable
    public Player getOwner() {
        return EntityReference.getPlayer(OWNER, level());
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        return !this.isOwner(target) && super.canAttack(target);
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    public PlayerTeam getTeam() {
        if (this.isTamed()) {
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity != null) {
                return livingEntity.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean considersEntityAsAlly(@NotNull Entity other) {
        if (this.isTamed()) {
            LivingEntity livingEntity = this.getOwner();
            if (other == livingEntity) {
                return true;
            }

            if (livingEntity != null) {
                return livingEntity.isAlliedTo(other);
            }
        }

        return super.isAlliedTo(other);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (!this.level().isClientSide() && ((ServerLevel)this.level()).getGameRules().get(GameRules.SHOW_DEATH_MESSAGES) && this.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(this.getCombatTracker().getDeathMessage());
        }

        super.die(source);
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }
}