package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.LargeAnimalBreedGoal;
import de.tomalbrc.toms_mobs.entity.navigation.LessSpinnyGroundPathNavigation;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.GeyserCompat;
import de.tomalbrc.toms_mobs.util.LivingEntityHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Elephant extends Animal implements AnimatedEntity, PlayerRideable {
    public static final ResourceLocation ID = Util.id("elephant");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Elephant> holder;

    private static Ingredient tempting() {
        return Ingredient.of(Items.SUGAR, Items.SUGAR_CANE, Items.BAMBOO);
    }

    private int attackCooldown = -1;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.TEMPT_RANGE, 8)
                .add(Attributes.MAX_HEALTH, 28.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    public EntityHolder<Elephant> getHolder() {
        return this.holder;
    }

    public Elephant(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);

        GeyserCompat.setCustomEntity(level.players(), this.getId(), "modelengine:elephant");

        this.holder = new LivingEntityHolder<>(this, MODEL, ID.getPath());
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
    public boolean isFood(ItemStack itemStack) {
        return tempting().test(itemStack);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(3, new LargeAnimalBreedGoal(this, 0.5));
        this.goalSelector.addGoal(4, new PanicGoal(this, 0.7));
        this.goalSelector.addGoal(5, new TemptGoal(this, 0.55, tempting(), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 0.7));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateWalkAnimation(this, this.holder);
            AnimationHelper.updateHurtVariant(this, this.holder);
        }
    }

    @Override
    public boolean causeFallDamage(double f, float g, DamageSource damageSource) {
        int i = this.calculateFallDamage(f, g);
        if (i <= 0) {
            return false;
        } else {
            this.hurt(damageSource, (float)i);
            if (this.isVehicle()) {
                for(Entity entity : this.getIndirectPassengers()) {
                    entity.hurt(damageSource, (float)i);
                }
            }

            this.playBlockFallSound();
            return true;
        }
    }

    @Override
    public int getAmbientSoundInterval() {
        return 250;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNIFFER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SNIFFER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNIFFER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.SNIFFER_STEP, 0.15F, 1.0F);
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
    public Elephant getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return MobRegistry.ELEPHANT.create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    @NotNull
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (!this.isBaby() && interactionHand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
            this.doPlayerRide(player);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, interactionHand);
        }
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
            this.goalSelector.enableControlFlag(Goal.Flag.LOOK);
        }
    }

    @Override
    public void stopRiding() {
        super.stopRiding();
        this.goalSelector.enableControlFlag(Goal.Flag.LOOK);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.4f;
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
    @NotNull
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        if (attackCooldown != -1)
            return Vec3.ZERO;

        ServerPlayer p = (ServerPlayer) player;
        float x = p.getLastClientInput().left() ? 1 : p.getLastClientInput().right() ? -1 : 0;
        float z = p.getLastClientInput().forward() ? 1 : p.getLastClientInput().backward() ? -1 : 0;
        if (z <= 0.0F) {
            z *= 0.25F;
        }

        return new Vec3(x, 0.0, z);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        if (attackCooldown >= 0) attackCooldown--;

        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yBodyRotO = this.yHeadRot = this.yHeadRotO = this.getYRot();

        if (attackCooldown == -1 && player instanceof ServerPlayer serverPlayer && serverPlayer.getLastClientInput().jump()) {
            attackCooldown += 30;
            this.holder.getAnimator().playAnimation("attack");
        } else if (attackCooldown == 20) {

            var entities = level().getEntities(player, this.getBoundingBox().move(this.getForward().normalize().scale(1.5f)));
            var factor = (1.0f/6.0f);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    if (entity == this || entity == player) {
                        continue;
                    }

                    float applyKnockbackResistance = 0;
                    if (entity instanceof LivingEntity) {
                        entity.hurt(player.damageSources().mobAttack(this), Math.abs((factor * 5.0f + 1.0f) * 2.0f));
                        applyKnockbackResistance = (float) ((LivingEntity) entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                    }
                    var v = this.getForward().multiply(1.0, 0.0, 1.0).add(0, 1, 0);
                    entity.setDeltaMovement(entity.getDeltaMovement().add(v.scale(1.0-applyKnockbackResistance)));
                }
            }
        }
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(Level level) {
        return new LessSpinnyGroundPathNavigation(this, level);
    }
}
