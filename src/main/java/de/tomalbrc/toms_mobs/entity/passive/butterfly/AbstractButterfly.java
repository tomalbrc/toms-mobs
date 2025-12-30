package de.tomalbrc.toms_mobs.entity.passive.butterfly;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.toms_mobs.entity.goal.flying.FlyingWanderGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.tslat.smartbrainlib.api.core.navigation.SmoothFlyingPathNavigation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public abstract class AbstractButterfly extends Animal implements AnimatedEntity, FlyingAnimal {
    protected EntityHolder<AbstractButterfly> holder;

    private int schoolSize;
    private AbstractButterfly leader;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.TEMPT_RANGE, 2.0)
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.1);
    }

    @Override
    public EntityHolder<AbstractButterfly> getHolder() {
        return this.holder;
    }

    public AbstractButterfly(EntityType<? extends @NotNull Animal> entityType, Level level) {
        super(entityType, level);

        this.moveControl = new FlyingMoveControl(this, 1, true);

        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.FENCE, -1.0F);

        setupModel();
    }

    protected abstract void setupModel();

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.WIND_CHARGE_BURST.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BREEZE_WIND_CHARGE_BURST.value();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0, this::isFood, false));

        this.goalSelector.addGoal(3, new FollowButterflyGoal(this));
        this.goalSelector.addGoal(4, new FlyingWanderGoal(this));

    }

    @Override
    protected void checkFallDamage(double d, boolean bl, @NotNull BlockState blockState, @NotNull BlockPos blockPos) {
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageMultiplier, @NotNull DamageSource damageSource) {
        return false;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(@NotNull Level level) {
        SmoothFlyingPathNavigation flyingPathNavigation = new SmoothFlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos blockPos) {
                return this.level.getBlockState(blockPos.below()).isAir();
            }
        };
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(false);
        return flyingPathNavigation;
    }

    @Override
    public float getWalkTargetValue(@NotNull BlockPos blockPos, LevelReader levelReader) {
        return levelReader.getBlockState(blockPos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    protected int getBaseExperienceReward(@NotNull ServerLevel serverLevel) {
        return 0;
    }
    
    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.BEE_FOOD);
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return !this.isLeashed() && !this.hasCustomName();
    }

    @Override
    public void setInLove(@Nullable Player player) {
        if (this.level() instanceof ServerLevel level) {
            for (int i = 0; i < 7; ++i) {
                double xOffset = this.random.nextGaussian() * 0.02;
                double yOffset = this.random.nextGaussian() * 0.02;
                double zOffset = this.random.nextGaussian() * 0.02;
                level.sendParticles(ParticleTypes.HEART, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, xOffset, yOffset, zOffset, 0);
            }
        }

        super.setInLove(player);
    }

    @Override
    public void customServerAiStep(@NotNull ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);

        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    public void addFollower() {
        ++schoolSize;
    }

    public void removeFollower() {
        --schoolSize;
    }

    public boolean canBeFollowed() {
        return false;
    }

    public boolean hasFollowers() {
        return schoolSize > 0;
    }

    public void addFollowers(Stream<? extends AbstractButterfly> stream) {
        stream.filter((abstractSchoolingFish) -> abstractSchoolingFish != this).forEach((abstractSchoolingFish) -> abstractSchoolingFish.startFollowing(this));
    }

    public AbstractButterfly startFollowing(AbstractButterfly largeButterfly) {
        this.leader = largeButterfly;
        largeButterfly.addFollower();
        return largeButterfly;
    }

    public void stopFollowing() {
        this.leader.removeFollower();
        this.leader = null;
    }

    public boolean inRangeOfLeader() {
        return this.distanceToSqr(this.leader) <= 121.;
    }

    public boolean isFollower() {
        return this.leader != null && this.leader.isAlive();
    }

    public void pathToLeader() {
        if (this.isFollower()) {
            this.getNavigation().moveTo(this.leader, (double)1.0F);
        }
    }

    @Override
    public float getAgeScale() {
        return 1f;
    }
}