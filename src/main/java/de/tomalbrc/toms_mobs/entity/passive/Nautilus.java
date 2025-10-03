package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.effect.CustomMobEffects;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Nautilus extends TamableAnimal implements AnimatedEntity, OwnableEntity, PlayerRideable {
    public static final ResourceLocation ID = Util.id("nautilus");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Nautilus> holder;
    private BlockPos travelPos;
    private int boostTime;
    private int boostCooldown;
    private boolean boosting = false;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.TEMPT_RANGE, 8)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.08);
    }

    @Override
    public EntityHolder<Nautilus> getHolder() {
        return this.holder;
    }

    public Nautilus(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setTame(false, false);
        this.setPathfindingMalus(PathType.WATER, 0.0F);

        this.holder = new LivingEntityHolder<>(this, MODEL);

        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public float maxUpStep() {
        return 0.25f;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.wasEyeInWater || boosting) {
            this.moveRelative(0.1F, vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(vec3);
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        return new NautilusPathNavigation(this, level) {
            @Override
            protected void followThePath() {
                Vec3 vec3 = this.getTempMobPos();
                this.maxDistanceToWaypoint = this.mob.getBbWidth() / 2.0F;
                Vec3i vec3i = this.path.getNextNodePos();
                double d = Math.abs(this.mob.getX() - ((double)vec3i.getX() + (double)0.5F));
                double e = Math.abs(this.mob.getY() - (double)vec3i.getY());
                double f = Math.abs(this.mob.getZ() - ((double)vec3i.getZ() + (double)0.5F));
                boolean bl = d < (double)this.maxDistanceToWaypoint && f < (double)this.maxDistanceToWaypoint && e < (double)1.0F;
                if (bl || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vec3)) {
                    this.path.advance();
                }

                this.doStuckDetection(vec3);
            }


            private boolean shouldTargetNextNodeInDirection(Vec3 vec3) {
                if (path == null)
                    return false;

                if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
                    return false;
                } else {
                    Vec3 vec32 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
                    if (!vec3.closerThan(vec32, 2.0)) {
                        return false;
                    } else if (this.canMoveDirectly(vec3, this.path.getNextEntityPos(this.mob))) {
                        return true;
                    } else {
                        Vec3 vec33 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                        Vec3 vec34 = vec32.subtract(vec3);
                        Vec3 vec35 = vec33.subtract(vec3);
                        double d = vec34.lengthSqr();
                        double e = vec35.lengthSqr();
                        boolean bl = e < d;
                        boolean bl2 = d < 0.5;
                        if (!bl && !bl2) {
                            return false;
                        } else {
                            Vec3 vec36 = vec34.normalize();
                            Vec3 vec37 = vec35.normalize();
                            return vec37.dot(vec36) < 0.0;
                        }
                    }
                }
            }
        };
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, this::isFood, false));
        this.goalSelector.addGoal(3, new NautilusTravelGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f, 0.02f, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (boostTime > 0)
            boostTime--;

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateAquaticWalkAnimation(this, this.holder);
            AnimationHelper.updateHurtVariant(this, this.holder);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BUBBLE_POP;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.AXOLOTL_HURT;
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
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.PUFFERFISH);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        var bebe = MobRegistry.NAUTILUS.create(serverLevel, EntitySpawnReason.BREEDING);
        if (bebe != null) {
            bebe.setOwner(getOwner());
        }
        return bebe;
    }

    @Override
    public void baseTick() {
        int airSupply = this.getAirSupply();
        super.baseTick();

        if (!this.isNoAi()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                this.handleAirSupply(serverLevel, airSupply);
            }
        }

        if (level() instanceof ServerLevel serverLevel && this.isInWater()) {
            var vec = this.position().subtract(this.getForward().multiply(2,0,2).normalize()).add(0, getEyeHeight()/3, 0);
            if (boosting) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE, false, false, vec.x, vec.y, vec.z, 2, 0.0, 0.1, 0, 0.1);
            }

            if (this.tickCount % 20 == 0) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE, false, false, vec.x, vec.y, vec.z, 2, 0.0, 0.1, 0, 0.1);
            }
        }

        if (boostTime > 0 && boosting) {
            boostTime--;
        }
        else if (boosting) {
            setCooldown();
        }
        if (boostCooldown > 0) boostCooldown--;
    }

    protected void handleAirSupply(ServerLevel serverLevel, int previous) {
        if (this.isAlive() && !this.isInWaterOrRain()) {
            this.setAirSupply(previous - 1);
            if (this.shouldTakeDrowningDamage()) {
                this.setAirSupply(0);
                this.hurtServer(serverLevel, this.damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
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
    public void customServerAiStep(ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);

        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    @Override
    @NotNull
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        var item = player.getItemInHand(interactionHand);
        if (!this.isBaby() && this.isSaddled()) {
            if (!player.isSecondaryUseActive())
                this.doPlayerRide(player);
            else if (item.isEmpty() && !this.isVehicle()) {
                player.setItemInHand(interactionHand, this.getItemBySlot(EquipmentSlot.SADDLE));
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.SADDLE, ItemStack.EMPTY);
                this.holder.getVariantController().setDefaultVariant();
            }
            return InteractionResult.SUCCESS;
        } else if (!this.level().isClientSide() && !isTame() && isFood(item)) {
            item.consume(1, player);
            this.tryToTame(player);
            return InteractionResult.SUCCESS_SERVER;
        } else if (!this.isBaby() && !this.isSaddled() && this.isEquippableInSlot(item, EquipmentSlot.SADDLE)) {
            return item.interactLivingEntity(player, this, interactionHand);
        } else {
            return super.mobInteract(player, interactionHand);
        }
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide()) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
            this.travelPos = null;
        }
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);
            this.setTarget(null);
        }
    }

    @Override
    public void onEquipItem(EquipmentSlot equipmentSlot, ItemStack itemStack, ItemStack itemStack2) {
        super.onEquipItem(equipmentSlot, itemStack, itemStack2);

        if (equipmentSlot == EquipmentSlot.SADDLE && this.getItemBySlot(EquipmentSlot.SADDLE).getItem() == Items.SADDLE) {
            this.holder.getVariantController().setVariant("saddled");
        } else {
            this.holder.getVariantController().setDefaultVariant();
        }
    }

    @Override
    public boolean canUseSlot(EquipmentSlot equipmentSlot) {
        return this.isAlive() && !this.isBaby() && equipmentSlot == EquipmentSlot.SADDLE;
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yBodyRotO = this.yHeadRot = this.yHeadRotO = this.getYRot();
        if (player.tickCount % 10 == 0)
            player.addEffect(new MobEffectInstance(CustomMobEffects.NAUTILUS_BREATH, 100, 1, false, true));
    }

    private void setCooldown() {
        this.boostCooldown = 10*20;
        this.boosting = false;
        this.boostTime = 0;
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.35f;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof ServerPlayer player) {
            return player;
        } else if (!this.isNoAi() && passenger instanceof Mob mob) {
            return mob;
        }
        return null;
    }

    @Override
    protected double getEffectiveGravity() {
        if (isVehicle() && (boosting))
            return super.getEffectiveGravity()*0.75; // make jumps a bit cooler
        else
            return super.getEffectiveGravity();
    }

    @Override
    @NotNull
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        ServerPlayer p = (ServerPlayer) player;
        float f = p.getLastClientInput().forward() ? 1f : p.getLastClientInput().backward() ? -1f : 0f;
        float s = p.getLastClientInput().left() ? 1f : p.getLastClientInput().right() ? -1f : 0f;
        double f2 = f;
        if (f2 < 0) f2 *= 0.25;
        Vec3 v = p.getForward();

        if (p.getLastClientInput().jump()) {
            if (canBoost()) {
                boostTime = 10*20;
                boosting = true;
            }
        } else if (boostTime > 0) {
            boostTime = 0;
            setCooldown();
        }

        var scale = 0.21;
        return new Vec3(s * 0.1, v.y * f * scale, f2 * scale).scale(boosting ? 3.0f : 1f);
    }

    private boolean canBoost() {
        return boostCooldown <= 0 && boostTime <= 0 && !boosting;
    }


    @Override
    protected float nextStep() {
        return this.moveDist + 0.15F;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }
    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, EntitySpawnReason entitySpawnReason) {
        return true;
    }

    @Override
    public void setJumping(boolean bl) {

    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);

        var item = this.getItemBySlot(EquipmentSlot.SADDLE);
        if (!item.isEmpty() && item.getItem() == Items.SADDLE) {
            this.holder.getVariantController().setVariant("saddled");
        } else {
            this.holder.getVariantController().setDefaultVariant();
        }

        this.boostTime = valueInput.getIntOr("BoostTime", boostTime);
        this.boostCooldown = valueInput.getIntOr("BoostCooldown", boostCooldown);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("BoostTime", boostTime);
        valueOutput.putInt("BoostCooldown", boostCooldown);
    }

    public static boolean checkDeepWaterSpawnRules(EntityType<? extends LivingEntity> entityType, LevelAccessor levelAccessor, EntitySpawnReason entitySpawnReason, BlockPos blockPos, RandomSource randomSource) {
        int max = levelAccessor.getSeaLevel() - 7;
        int min = max - 40;
        return blockPos.getY() >= min && blockPos.getY() <= max && levelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER) && levelAccessor.getBlockState(blockPos.above()).is(Blocks.WATER);
    }

    public static boolean checkRareDeepWaterSpawnRules(EntityType<? extends LivingEntity> entityType, LevelAccessor levelAccessor, EntitySpawnReason entitySpawnReason, BlockPos blockPos, RandomSource randomSource) {
        int max = levelAccessor.getSeaLevel() - 7;
        int min = max - 40;
        return randomSource.nextInt(5) == 1 && blockPos.getY() >= min && blockPos.getY() <= max && levelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER) && levelAccessor.getBlockState(blockPos.above()).is(Blocks.WATER);
    }

    static class NautilusPathNavigation extends AmphibiousPathNavigation {
        NautilusPathNavigation(Nautilus nautilus, Level level) {
            super(nautilus, level);
        }

        @Override
        public boolean isDone() {
            return super.isDone();
        }

        public boolean isStableDestination(BlockPos blockPos) {
            return this.level.getBlockState(blockPos).is(Blocks.WATER) && this.level.getBlockState(blockPos.above()).is(Blocks.WATER);
        }

        @Override
        public boolean canNavigateGround() {
            return false;
        }
    }

    static class NautilusTravelGoal extends Goal {
        private final Nautilus nautilus;
        private final double speedModifier;
        private boolean stuck;

        NautilusTravelGoal(Nautilus nautilus, double d) {
            this.nautilus = nautilus;
            this.speedModifier = d;
        }

        public boolean canUse() {
            return this.nautilus.isInWater() && !nautilus.isVehicle();
        }

        public void start() {
            int i = 256;
            RandomSource randomSource = this.nautilus.random;
            int k = randomSource.nextInt(513) - i;
            int l = randomSource.nextInt(9) - 4;
            int m = randomSource.nextInt(513) - i;
            if ((double) l + this.nautilus.getY() > (double) (this.nautilus.level().getSeaLevel() - 1)) {
                l = 0;
            }

            this.nautilus.travelPos = BlockPos.containing((double) k + this.nautilus.getX(), (double) l + this.nautilus.getY(), (double) m + this.nautilus.getZ());
            this.stuck = false;
        }

        public void tick() {
            if (this.nautilus.travelPos == null) {
                this.stuck = true;
            } else {
                if (this.nautilus.getNavigation().isDone()) {
                    Vec3 vec3 = Vec3.atBottomCenterOf(this.nautilus.travelPos);
                    Vec3 vec32 = DefaultRandomPos.getPosTowards(this.nautilus, 16, 3, vec3, ((float) Math.PI / 10F));
                    if (vec32 == null) {
                        vec32 = DefaultRandomPos.getPosTowards(this.nautilus, 8, 7, vec3, ((float) Math.PI / 2F));
                    }

                    if (vec32 != null) {
                        int i = Mth.floor(vec32.x);
                        int j = Mth.floor(vec32.z);
                        int k = 34;
                        if (!this.nautilus.level().hasChunksAt(i - k, j - k, i + k, j + k)) {
                            vec32 = null;
                        }
                    }

                    if (vec32 == null) {
                        this.stuck = true;
                        return;
                    }

                    this.nautilus.getNavigation().moveTo(vec32.x, vec32.y, vec32.z, this.speedModifier);
                }

            }
        }

        public boolean canContinueToUse() {
            return !this.nautilus.getNavigation().isDone() && !this.stuck && !this.nautilus.isInLove();
        }

        public void stop() {
            this.nautilus.travelPos = null;
            super.stop();
        }
    }
}
