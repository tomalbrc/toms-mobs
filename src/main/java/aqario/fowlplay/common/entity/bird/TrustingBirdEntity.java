package aqario.fowlplay.common.entity.bird;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class TrustingBirdEntity extends FlyingBirdEntity {
    protected List<UUID> TRUSTED = new ArrayList<>();

    protected TrustingBirdEntity(EntityType<? extends @NotNull BirdEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public int getFleeRange(LivingEntity target) {
        return !this.getTrustedUuids().isEmpty() && target instanceof Player ? 8 : super.getFleeRange(target);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.store("trusted", UUIDUtil.LENIENT_CODEC.listOf(), this.getTrustedUuids());
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput nbt) {
        super.readAdditionalSaveData(nbt);

        nbt.read("trusted", UUIDUtil.LENIENT_CODEC.listOf()).ifPresent(x -> {
            x.forEach(this::addTrustedUuid);
        });
    }

    @Override
    protected void pickUpItem(@NotNull ServerLevel serverLevel, ItemEntity item) {
        super.pickUpItem(serverLevel, item);
        UUID thrower = item.getOwner() != null ? item.getOwner().getUUID() : null;
        if (thrower != null && !this.trustsUuid(thrower)) {
            if (this.random.nextInt(3) == 0) {
                this.addTrustedUuid(thrower);
                this.level().broadcastEntityEvent(this, EntityEvent.VILLAGER_HAPPY);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.VILLAGER_HAPPY) {
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 20;
            }
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return this.trusts(player) ? super.mobInteract(player, hand) : InteractionResult.PASS;
    }

    @Override
    protected boolean shouldBeAmbient() {
        return super.shouldBeAmbient() && this.getTrustedUuids().isEmpty();
    }

    public List<UUID> getTrustedUuids() {
        return this.TRUSTED;
    }

    public void addTrustedUuid(UUID uuid) {
        this.TRUSTED.add(uuid);
    }

    public void removeTrustedUuid(UUID uuid) {
        this.TRUSTED.remove(uuid);
    }

    public void stopTrusting(Player player) {
        this.removeTrustedUuid(player.getUUID());
    }

    public List<Player> getTrusted() {
        List<UUID> uuids = this.getTrustedUuids();
        List<Player> entities = new ArrayList<>();
        for (UUID uuid : uuids) {
            entities.add(this.level().getPlayerByUUID(uuid));
        }
        return entities;
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        return (!(target instanceof Player player) || !this.trusts(player)) && super.canAttack(target);
    }

    public boolean trusts(Player player) {
        return this.getTrusted().contains(player);
    }

    public boolean trustsUuid(UUID uuid) {
        return this.getTrustedUuids().contains(uuid);
    }
}