package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.control.SemiAquaticMoveControl;
import de.tomalbrc.toms_mobs.entity.goal.aquatic.AquaticPanicGoal;
import de.tomalbrc.toms_mobs.entity.goal.aquatic.PathfinderMobSwimGoal;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.tslat.smartbrainlib.api.core.navigation.SmoothWaterBoundPathNavigation;
import org.jetbrains.annotations.NotNull;

public class Mantaray extends AbstractFish implements AnimatedEntity {
    public static final Identifier ID = Util.id("mantaray");
    public static final Model MODEL = Util.loadBbModel(ID);
    private final EntityHolder<Mantaray> holder;

    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 1.0)
                .add(Attributes.MAX_HEALTH, 16.0);
    }

    public static boolean checkRareDeepWaterSpawnRules(EntityType<? extends @NotNull LivingEntity> entityType, LevelAccessor levelAccessor, EntitySpawnReason entitySpawnReason, BlockPos blockPos, RandomSource randomSource) {
        int max = levelAccessor.getSeaLevel() - 7;
        int min = max - 40;
        return randomSource.nextInt(5) == 1 && blockPos.getY() >= min && blockPos.getY() <= max && levelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER) && levelAccessor.getBlockState(blockPos.above()).is(Blocks.WATER);
    }

    @Override
    public EntityHolder<Mantaray> getHolder() {
        return this.holder;
    }

    public Mantaray(EntityType<? extends @NotNull AbstractFish> type, Level level) {
        super(type, level);

        this.setPathfindingMalus(PathType.WATER, 0.F);

        this.moveControl = new SemiAquaticMoveControl(this);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticPanicGoal(this, 0.6));
        this.goalSelector.addGoal(1, new PathfinderMobSwimGoal(this, 1));
    }

    @Override
    public float getWalkTargetValue(@NotNull BlockPos blockPos, LevelReader levelReader) {
        if (levelReader.getFluidState(blockPos).is(FluidTags.WATER)) {
            return 1;
        } else {
            return levelReader.getPathfindingCostFromLightLevels(blockPos);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateAquaticWalkAnimation(this, this.holder);
            AnimationHelper.updateHurtColor(this, this.holder);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GUARDIAN_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.GUARDIAN_HURT;
    }

    @Override
    @NotNull
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    @Override
    @NotNull
    public ItemStack getBucketItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    @NotNull
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothWaterBoundPathNavigation(this, level);
    }
}
