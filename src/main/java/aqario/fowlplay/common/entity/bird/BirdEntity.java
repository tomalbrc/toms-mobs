package aqario.fowlplay.common.entity.bird;

import aqario.fowlplay.common.entity.ai.control.BirdBodyRotationControl;
import aqario.fowlplay.common.entity.ai.control.BirdLookControl;
import aqario.fowlplay.common.entity.ai.control.BirdMoveControl;
import aqario.fowlplay.common.util.AnimationStateList;
import aqario.fowlplay.common.util.BirdUtils;
import aqario.fowlplay.core.FowlPlayMemoryTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class BirdEntity extends Animal {
    private static final String AMBIENT_KEY = "ambient";
    private static final String SLEEPING_KEY = "sleeping";
    public final AnimationState standingState = new AnimationState();
    public final AnimationState swimmingState = new AnimationState();
    public final AnimationStateList idleAnimStates = this.createIdleAnimations();
    protected int idleAnimationChance;
    protected int callChance;
    protected int songChance;
    private boolean sleeping = false;
    private boolean ambient;
    private int eatingTime;

    protected BirdEntity(EntityType<? extends @NotNull BirdEntity> entityType, Level world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
        this.moveControl = this.createMoveControl();
        this.lookControl = new BirdLookControl(this, 85);
        this.idleAnimationChance = this.random.nextInt(this.getIdleAnimationDelay()) - this.getIdleAnimationDelay();
        this.callChance = this.random.nextInt(this.getCallDelay()) - this.getCallDelay();
        this.songChance = this.random.nextInt(this.getSongDelay()) - this.getSongDelay();
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0f);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0f);
        this.setPathfindingMalus(PathType.COCOA, -1.0f);
        this.setPathfindingMalus(PathType.FENCE, -1.0f);
    }

    public static AttributeSupplier.Builder createBirdAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MAX_HEALTH, 6.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return this.getTargetFromBrain();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setYRot(level.getRandom().nextFloat() * 360.0F);
        this.setYBodyRot(this.getYRot());
        this.setYHeadRot(this.getYRot());
        if (this.shouldSpawnAsAmbient(spawnType)) {
            this.setAmbient(true);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    protected boolean shouldSpawnAsAmbient(EntitySpawnReason spawnType) {
        return this.shouldBeAmbient()
                && (spawnType == EntitySpawnReason.NATURAL
                || spawnType == EntitySpawnReason.CHUNK_GENERATION);
    }

    protected boolean shouldBeAmbient() {
        // TODO: non ambient?
        return true;
        //return this.getType().getCategory() == CustomMobCategory.AMBIENT_BIRDS.mobCategory;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        //builder.define(SLEEPING, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean(AMBIENT_KEY, this.ambient);
        nbt.putBoolean(SLEEPING_KEY, this.isSleeping());
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.setAmbient(nbt.getBooleanOr(AMBIENT_KEY, this.isAmbient()));
        this.setSleeping(nbt.getBooleanOr(SLEEPING_KEY, this.isSleeping()));
    }

    /**
     * ambient birds are able to despawn when far enough from the player, or when the chunks are unloaded
     */
    public boolean isAmbient() {
        return this.ambient;
    }

    protected void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return this.isAmbient() && !this.isPersistenceRequired() && !this.hasCustomName();
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 6;
    }

    @Override
    protected boolean canDispenserEquipIntoSlot(@NotNull EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND && this.canPickUpLoot();
    }

    @Override
    public void setBaby(boolean baby) {
        this.setAge(baby ? -72000 : 0);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canHoldItem(@NotNull ItemStack stack) {
        ItemStack heldStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        return this.getFood().test(stack) && !this.getFood().test(heldStack);
    }

    public boolean shouldDropBeakItem(ItemStack stack) {
        return !stack.isEmpty() && !this.getFood().test(stack);
    }

    private void dropWithoutDelay(ItemStack stack, Entity thrower) {
        ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack);
        if (thrower != null) {
            item.setThrower(thrower);
        }
        this.level().addFreshEntity(item);
    }

    @Override
    protected void pickUpItem(@NotNull ServerLevel serverLevel, ItemEntity item) {
        Entity thrower = item.getOwner();
        ItemStack stack = item.getItem();
        if (this.canHoldItem(stack)) {
            int i = stack.getCount();
            if (i > 1) {
                this.dropWithoutDelay(stack.split(i - 1), thrower);
            }
            this.spawnAtLocation(serverLevel, this.getItemBySlot(EquipmentSlot.MAINHAND));
            this.onItemPickup(item);
            this.setItemSlot(EquipmentSlot.MAINHAND, stack.split(1));
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.take(item, stack.getCount());
            item.discard();
            this.eatingTime = 0;
            Brain<?> brain = this.getBrain();
            BrainUtil.clearMemory(brain, FowlPlayMemoryTypes.SEES_FOOD.get());
        }
    }

    public boolean isBelowWaterline() {
        return this.isUnderWater() || this.getFluidHeight(FluidTags.WATER) > this.getBoundingBox().getYsize() * 0.35;
    }

    @Override
    public boolean isSleeping() {
        return sleeping;
    }

    private void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    private void goToSleep() {
        this.setSleeping(true);
    }

    private void wakeUp() {
        this.setSleeping(false);
    }

    private boolean canEat(ItemStack stack) {
        return this.getFood().test(stack) && !this.isSleeping();
    }

    public abstract Ingredient getFood();

    public boolean canHunt(LivingEntity target) {
        return false;
    }

    public boolean shouldAttack(LivingEntity target) {
        return false;
    }

    public boolean shouldAvoid(LivingEntity entity) {
        return false;
    }

    public int getFleeRange(LivingEntity target) {
        return BirdUtils.isNotFlightless(target) ? 32 : 16;
    }

    public boolean hasLowHealth() {
        return this.getHealth() <= this.getMaxHealth() / 2;
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        return super.canAttack(target) && (this.shouldAttack(target) || this.canHunt(target));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.isAlive()) {
            ++this.eatingTime;
            ItemStack stack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (this.canEat(stack)) {
                if ((this.eatingTime > 40 && this.random.nextFloat() < 0.05f) || this.eatingTime > 200) {
                    if (stack.getItem().components().has(DataComponents.FOOD)) {
                        // noinspection ConstantConditions
                        this.heal(stack.getItem().components().get(DataComponents.FOOD).nutrition());
                    } else {
                        stack.shrink(1);
                    }
                    ItemStack usedStack = stack.finishUsingItem(this.level(), this);
                    if (!usedStack.isEmpty()) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, usedStack);
                    }
                    this.playEatingSound();
                    this.level().broadcastEntityEvent(this, EntityEvent.FOX_EAT);
                    this.eatingTime = 0;
                    return;
                }
                if (this.eatingTime > 20 && this.random.nextFloat() < 0.05f) {
                    this.playEatingSound();
                    this.level().broadcastEntityEvent(this, EntityEvent.FOX_EAT);
                }
            } else if (this.shouldDropBeakItem(stack)) {
                if (this.random.nextFloat() < 0.1f) {
                    if (this.level() instanceof ServerLevel serverLevel)
                        this.spawnAtLocation(serverLevel, this.getItemBySlot(EquipmentSlot.MAINHAND));
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.FOX_EAT) {
            ItemStack food = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!food.isEmpty()) {
                for (int i = 0; i < 8; i++) {
                    Vec3 vec3d = new Vec3(((double) this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .xRot(-this.getXRot() * (float) (Math.PI / 180.0))
                            .yRot(-this.getYRot() * (float) (Math.PI / 180.0));
                    if (this.level() instanceof ServerLevel serverLevel) serverLevel.sendParticles(
                            new ItemParticleOption(ParticleTypes.ITEM, food),
                            this.getX() + this.getLookAngle().x / 2.0,
                            this.getY(),
                            this.getZ() + this.getLookAngle().z / 2.0,
                            1, 0.25,
                            vec3d.x,
                            vec3d.y + 0.05,
                            vec3d.z
                    );
                }
            }
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        ProfilerFiller profilerFiller = Profiler.get();
        profilerFiller.push("birdBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.callChance++) {
            this.resetCallDelay();
            if (this.canCall()) {
                this.playCallSound();
            }
        } else if (this.isAlive() && this.random.nextInt(1000) < this.songChance++) {
            this.resetSongDelay();
            if (this.canSing()) {
                this.playSongSound();
            }
        }

        profilerFiller.pop();
    }

    protected AnimationStateList createIdleAnimations() {
        return new AnimationStateList();
    }

    @Override
    public void tick() {
        if (this.level().isClientSide()) {
            this.updateAnimations();
        }
        super.tick();
        if (this.isAmbient() && !this.shouldBeAmbient()) {
            this.setAmbient(false);
        }
    }

    protected boolean isMoving() {
        return this.walkAnimation.isMoving();
    }

    protected void updateAnimations() {
        // on land
        if (!this.isInWater()) {
            if (this.random.nextInt(1000) < this.idleAnimationChance++ && !this.isMoving()) {
                this.resetIdleAnimationDelay();
                this.standingState.stop();
                this.idleAnimStates.stopAll();
                this.idleAnimStates.startRandom(this.tickCount);
            } else if (this.isMoving()) {
                this.idleAnimStates.stopAll();
            }
            if (!this.idleAnimStates.containsStarted()) {
                this.standingState.startIfStopped(this.tickCount);
            } else {
                this.standingState.stop();
            }
        } else {
            this.standingState.stop();
            this.idleAnimStates.stopAll();
        }
        // in water
        this.swimmingState.animateWhen(this.isInWater(), this.tickCount);
    }

    protected int getIdleAnimationDelay() {
        return 240;
    }

    protected void resetIdleAnimationDelay() {
        this.idleAnimationChance = -(this.getIdleAnimationDelay() + this.random.nextIntBetweenInclusive(-200, 200));
    }

    protected boolean canCall() {
        return BirdUtils.isDaytime(this);
    }

    protected boolean canSing() {
        return BirdUtils.isDaytime(this) && this.onGround() && !this.isBaby();
    }

    private void resetCallDelay() {
        this.callChance = -(this.getCallDelay() + this.random.nextIntBetweenInclusive(-150, 150));
    }

    private void resetSongDelay() {
        this.songChance = -(this.getSongDelay() + this.random.nextIntBetweenInclusive(-150, 150));
    }

    public final void playCallSound() {
        SoundEvent call = this.getCallSound();
        if (call != null) {
            this.playSound(call, this.getCallVolume(), this.getVoicePitch());
        }
    }

    public final void playSongSound() {
        SoundEvent song = this.getSongSound();
        if (song != null) {
            this.playSound(song, this.getSongVolume(), this.getVoicePitch());
        }
    }

    @Override
    protected void playHurtSound(@NotNull DamageSource damageSource) {
        this.resetCallDelay();
        this.resetSongDelay();
        SoundEvent hurt = this.getHurtSound(damageSource);
        if (hurt != null) {
            this.playSound(hurt, this.getCallVolume(), this.getVoicePitch());
        }
    }

    public int getCallDelay() {
        return 240;
    }

    public int getSongDelay() {
        return 720;
    }

    @Nullable
    protected SoundEvent getCallSound() {
        return null;
    }

    @Nullable
    protected SoundEvent getSongSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void playEatingSound() {
        this.playSound(SoundEvents.PARROT_EAT, 1.0F, 1.0F);
        //return FowlPlaySoundEvents.ENTITY_BIRD_EAT.get();
    }

    protected float getCallVolume() {
        return 1.0F;
    }

    protected float getSongVolume() {
        return 1.0F;
    }

    @Override
    public int getHeadRotSpeed() {
        return 100;
    }

    @Override
    public int getMaxHeadXRot() {
        return 100;
    }

    @Override
    public int getMaxHeadYRot() {
        return 135;
    }

    protected MoveControl createMoveControl() {
        return new BirdMoveControl(this);
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new BirdBodyRotationControl(this);
    }

    @Override
    protected @NotNull MovementEmission getMovementEmission() {
        return MovementEmission.SOUNDS;
    }

    @Override
    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.05F + 1.0F;
    }

    public <U> void isMemoryPresent(MemoryModuleType<@NotNull U> memoryType) {
        this.brain.hasMemoryValue(memoryType);
    }

    public <U> U getPresentMemory(MemoryModuleType<@NotNull U> memoryType) {
        return this.getMemory(memoryType).orElseThrow();
    }

    public <U> Optional<U> getMemory(MemoryModuleType<@NotNull U> memoryType) {
        return this.brain.getMemory(memoryType);
    }

    public <U> U getMemoryOrDefault(MemoryModuleType<@NotNull U> memory, Supplier<U> fallback) {
        return this.brain.getMemory(memory).orElseGet(fallback);
    }

    public <U> void setMemory(MemoryModuleType<@NotNull U> memoryType, U value) {
        this.brain.setMemory(memoryType, value);
    }

    public <U> void clearMemory(MemoryModuleType<@NotNull U> memoryType) {
        this.brain.eraseMemory(memoryType);
    }
}