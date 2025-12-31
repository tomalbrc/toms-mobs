package de.tomalbrc.toms_mobs.entity.passive;

import aqario.fowlplay.common.entity.ai.brain.BirdBrain;
import aqario.fowlplay.common.entity.ai.brain.behaviour.*;
import aqario.fowlplay.common.entity.ai.brain.sensor.*;
import aqario.fowlplay.common.entity.ai.control.BirdFloatMoveControl;
import aqario.fowlplay.common.entity.ai.navigation.AmphibiousNavigation;
import aqario.fowlplay.common.entity.bird.BirdEntity;
import aqario.fowlplay.common.entity.bird.FlyingBirdEntity;
import aqario.fowlplay.common.util.BirdUtils;
import aqario.fowlplay.common.util.CylindricalRadius;
import aqario.fowlplay.core.FowlPlaySchedules;
import com.mojang.datafixers.util.Pair;
import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.CustomTags;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.registry.SoundRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.MovementRotatingHolder;
import de.tomalbrc.toms_mobs.util.SetEntityLookTarget;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowParent;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.InWaterSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Seagull extends FlyingBirdEntity implements AnimatedEntity, BirdBrain<Seagull> {
    public static final Identifier ID = Util.id("seagull");
    public static final Model MODEL = Util.loadBbModel(ID);
    private final MovementRotatingHolder<Seagull> holder;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.17)
                .add(Attributes.FLYING_SPEED, 0.15)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 1)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5f);
    }

    public Seagull(EntityType<? extends @NotNull BirdEntity> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0f);
        this.setPathfindingMalus(PathType.WATER, 0.0f);

        this.holder = new MovementRotatingHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public EntityHolder<Seagull> getHolder() {
        return this.holder;
    }

    @Override
    public boolean isFood(@NotNull ItemStack itemStack) {
        return itemStack.is(ConventionalItemTags.FOODS);
    }

    @Override
    public Ingredient getFood() {
        return Ingredient.of(BuiltInRegistries.ITEM.get(ConventionalItemTags.FOODS).orElseThrow());
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
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateBirdAnimation(this, this.holder);
            AnimationHelper.updateHurtColor(this, this.holder);
        }
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
    public Seagull getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return MobRegistry.SEAGULL.create(serverLevel, EntitySpawnReason.BREEDING);
    }


    @Override
    protected void customServerAiStep(@NotNull ServerLevel serverLevel) {
        Brain<?> brain = this.getBrain();
        Activity activity = brain.getActiveNonCoreActivity().orElse(null);
        this.tickBrain(this);
        if (activity == Activity.FIGHT && brain.getActiveNonCoreActivity().orElse(null) != Activity.FIGHT) {
            brain.setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
        }

        super.customServerAiStep(serverLevel);

        this.holder.roll = getRoll(0.5f);

        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    @Override
    protected MoveControl createMoveControl() {
        return new BirdFloatMoveControl(this);
    }

    @Override
    public int getMaxPitchChange() {
        return 18;
    }

    @Override
    public int getMaxYawChange() {
        return 18;
    }

    @Override
    public Pair<Integer, Integer> getFlyHeightRange() {
        return Pair.of(24, 32);
    }

    @Override
    protected PathNavigation getLandNavigation() {
        return new AmphibiousNavigation(this, this.level())
                .setSurfaceOnly();
    }

    @Override
    protected boolean canSwim() {
        return true;
    }

    @Override
    public boolean canHunt(LivingEntity target) {
        return target.getType().is(CustomTags.SEAGULL_HUNT_TARGET);
    }

    @Override
    public boolean shouldAvoid(LivingEntity entity) {
        return entity.getType() == EntityType.PLAYER && entity.isInvisible();
    }

    @Override
    public void updateAnimations() {
        this.standingState.animateWhen(!this.isFlying() && !this.isInWater(), this.tickCount);
        this.glidingState.animateWhen(this.isFlying(), this.tickCount);
        this.swimmingState.animateWhen(!this.isFlying() && this.isInWater(), this.tickCount);
    }

    @Override
    public float getFlapVolume() {
        return 0.8f;
    }

    @Override
    public float getFlapPitch() {
        return 0.6f;
    }

    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.5f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    @Nullable
    @Override
    protected SoundEvent getCallSound() {
        return SoundRegistry.SEAGULL_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getSongSound() {
        return SoundRegistry.SEAGULL_AMBIENT;
    }

    @Override
    protected float getCallVolume() {
        return 0.9f;
    }

    @Override
    protected float getSongVolume() {
        return 0.7f;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundRegistry.SEAGULL_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.SEAGULL_DEATH;
    }

    @Override
    public CylindricalRadius getWalkRange() {
        return new CylindricalRadius(24, 8);
    }

    @Override
    protected @NotNull Brain.Provider<@NotNull Seagull> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends Seagull>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new NearbyPlayersSensor<>(),
                new NearbyFoodSensor<>(),
                new NearbyAdultsSensor<>(),
                new InWaterSensor<>(),
                new AttackedSensor<>(),
                new AvoidTargetSensor<>(),
                new AttackTargetSensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends Seagull> getCoreTasks() {
        return BirdBrain.coreActivity(
                FlightBehaviours.stopFalling(),
                new SetAttackTarget<>(),
                new LookAtTarget<>().runForBetween(45, 90),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends Seagull> getAvoidTasks() {
        return BirdBrain.avoidActivity(
                CustomBehaviours.setAvoidEntityWalkTarget()
        );
    }

    @Override
    public BrainActivityGroup<? extends Seagull> getFightTasks() {
        return BirdBrain.fightActivity(
                new InvalidateAttackTarget<>(),
                FlightBehaviours.startFlying(),
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<>(0),
                CustomBehaviours.forgetUnderwaterAttackTarget()
        );
    }

    @Override
    public BrainActivityGroup<? extends Seagull> getForageTasks() {
        return BirdBrain.forageActivity(
                new OneRandomBehaviour<>(
                        Pair.of(
                                CompositeBehaviours.trySetNonAirWalkTarget(),
                                1
                        ),
                        Pair.of(
                                CustomBehaviours.idleIfNotFlying()
                                        .runForBetween(100, 300),
                                2
                        )
                ),
                new SetRandomFlightTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends Seagull> getIdleTasks() {
        return BirdBrain.idleActivity(
                new BreedWithPartner<>(),
                new FollowParent<>(),
                new LookAtTarget<>(),
                new SetEntityLookTarget<>().startCondition(x -> BirdUtils.isPlayerHoldingFood(this, x)),
                new SetRandomLookTarget<>().lookChance(ConstantFloat.of(0.02f)),
                new OneRandomBehaviour<>(
                        CompositeBehaviours.trySetNonAirWalkTarget(),
                        CustomBehaviours.idleIfNotFlying()
                                .runForBetween(100, 300)
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends Seagull> getPickupFoodTasks() {
        return BirdBrain.pickupFoodActivity(
                CompositeBehaviours.tryPickUpFood()
        );
    }

    @Override
    public BrainActivityGroup<? extends Seagull> getRestTasks() {
        return BirdBrain.restActivity(
                CompositeBehaviours.trySetWaterRestTarget(),
                CustomBehaviours.idleIfInWater()
        );
    }

    @Override
    public BrainActivityGroup<Seagull> getSoarTasks() {
        return BirdBrain.soarActivity(
                new SetRandomFlightTarget<>()
        );
    }

    @Nullable
    @Override
    public SmartBrainSchedule getSchedule() {
        return FowlPlaySchedules.SEABIRD;
    }
}
