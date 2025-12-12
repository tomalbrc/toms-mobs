package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.NoDeathRotationLivingEntityHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Tuna extends AbstractFish implements AnimatedEntity {
    public static final Identifier ID = Util.id("tuna");
    public static final Model MODEL = Util.loadBbModel(ID);
    private final EntityHolder<Tuna> holder;

    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.7)
                .add(Attributes.MAX_HEALTH, 16.0);
    }

    @Override
    public EntityHolder<Tuna> getHolder() {
        return this.holder;
    }

    public Tuna(EntityType<? extends AbstractFish> type, Level level) {
        super(type, level);

        this.holder = new NoDeathRotationLivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 3;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateFishAnimation(this, this.holder);
            AnimationHelper.updateHurtVariant(this, this.holder);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SALMON_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SALMON_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SALMON_HURT;
    }

    @Override
    @NotNull
    protected SoundEvent getFlopSound() {
        return SoundEvents.SALMON_FLOP;
    }

    @Override
    @NotNull
    public ItemStack getBucketItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        return InteractionResult.PASS;
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        if (this.getAttribute(Attributes.SCALE) != null) {
            var scale = this.getAttribute(Attributes.SCALE);
            assert scale != null;

            var s = input.getFloatOr("CustomScale", 1);
            scale.setBaseValue(s);
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        if (this.getAttribute(Attributes.SCALE) != null) {
            var scale = this.getAttribute(Attributes.SCALE);
            assert scale != null;
            output.putDouble("CustomScale", scale.getBaseValue());
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        var scaleVal = this.getRandom().nextInt(1,4);
        var scale = this.getAttribute(Attributes.SCALE);
        if (scale != null) scale.setBaseValue(0.75f + scaleVal * 0.125f);

        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);
    }
}
