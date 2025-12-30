package de.tomalbrc.toms_mobs.entity.passive;

import aqario.fowlplay.common.entity.ai.navigation.AmphibiousNavigation;
import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.control.SemiAquaticMoveControl;
import de.tomalbrc.toms_mobs.entity.goal.PenguinSlideGoal;
import de.tomalbrc.toms_mobs.entity.goal.aquatic.*;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.registry.SoundRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Penguin extends Animal implements AnimatedEntity, RangedAttackMob {
    public static final Identifier ID = Util.id("penguin");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Penguin> holder;
    private boolean sliding;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.TEMPT_RANGE, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public EntityHolder<Penguin> getHolder() {
        return this.holder;
    }

    public Penguin(EntityType<? extends @NotNull Penguin> type, Level level) {
        super(type, level);

        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.DOOR_IRON_CLOSED, -1.0F);
        this.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, -1.0F);
        this.setPathfindingMalus(PathType.DOOR_OPEN, -1.0F);

        this.moveControl = new SemiAquaticMoveControl(this);
        this.jumpControl = new JumpControl(this);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public void setAge(int age) {
        super.setAge(age);
        if (age < 0) {
            this.holder.setScale(0.5f);
        } else {
            this.holder.setScale(1.f);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AquaticRangedAttackGoal(this, 0.75, 20, 10.0F));
        this.goalSelector.addGoal(1, new AquaticPanicGoal(this, 0.9));
        this.goalSelector.addGoal(2, new AquaticBreedGoal(this, 0.7));
        this.goalSelector.addGoal(3, new TemptGoal(this, 0.65, Ingredient.of(Items.SALMON, Items.COD), false));
        this.goalSelector.addGoal(4, new AquaticFollowParentGoal(this, 0.65));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 0.7, 0.85));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PolarBear.class, 16.0F, 0.7, 0.85));
        this.goalSelector.addGoal(5, new AquaticWaterAvoidingRandomStrollGoal(this, 0.59));
        this.goalSelector.addGoal(5, new PathfinderMobSwimGoal(this, 3));
        this.goalSelector.addGoal(8, new AquaticRandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(12, new PenguinSlideGoal(this, 0.9));


        //this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Skeleton.class, Penguin.class));
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
            if (this.sliding) {
                this.holder.getAnimator().pauseAnimation("walk");
                this.holder.getAnimator().pauseAnimation("idle");
                this.holder.getAnimator().playAnimation("slide");
            } else {
                this.holder.getAnimator().stopAnimation("slide");
                AnimationHelper.updateAquaticWalkAnimation(this, this.holder);
            }
            AnimationHelper.updateHurtColor(this, this.holder);
        }
    }

    @Override
    public void customServerAiStep(@NotNull ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);

        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    @Override
    public Penguin getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return MobRegistry.PENGUIN.create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    public void setInLove(@Nullable Player player) {
        if (this.level() instanceof ServerLevel level) {
            for (int i = 0; i < 7; ++i) {
                double xOffset = this.random.nextGaussian() * 0.02;
                double yOffset = this.random.nextGaussian() * 0.02;
                double zOffset = this.random.nextGaussian() * 0.02;
                level.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, xOffset, yOffset, zOffset, 0);
            }
        }

        super.setInLove(player);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.SALMON) || itemStack.is(Items.COD);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 220;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.PENGUIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundRegistry.PENGUIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.PENGUIN_DEATH;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        this.playSound(SoundEvents.FROG_STEP, 0.15F, 1.0F);
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(@NotNull Level level) {
        return new AmphibiousNavigation(this, level);
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        double dx = livingEntity.getX() - this.getX();
        double y = livingEntity.getEyeY() - 1.1;
        double dz = livingEntity.getZ() - this.getZ();
        double val = Math.sqrt(dx * dx + dz * dz) * 0.2;

        ItemStack itemStack = new ItemStack(Items.SNOWBALL);
        Projectile.spawnProjectile(new Snowball(level(), this, itemStack), (ServerLevel) level(), itemStack, (snowball) -> snowball.shoot(dx, y + val - snowball.getY(), dz, 1.6F, 12.0F));

        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
    }

    public void setSliding(boolean b) {
        this.sliding = b;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
}