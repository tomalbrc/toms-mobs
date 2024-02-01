package de.tomalbrc.toms_mobs.entities;

import de.tomalbrc.toms_mobs.registries.MobRegistry;
import de.tomalbrc.toms_mobs.registries.SoundRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import de.tomalbrc.resin.api.AjEntity;
import de.tomalbrc.resin.data.AjLoader;
import de.tomalbrc.resin.holders.entity.EntityHolder;
import de.tomalbrc.resin.holders.entity.living.LivingEntityHolder;
import de.tomalbrc.resin.model.AjModel;

public class Penguin extends Animal implements AjEntity, RangedAttackMob {
    public static final ResourceLocation ID = Util.id("penguin");
    public static final AjModel MODEL = AjLoader.require(ID);
    private final EntityHolder<Penguin> holder;

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public EntityHolder<Penguin> getHolder() {
        return this.holder;
    }

    public Penguin(EntityType<? extends Penguin> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);

        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 0.75, 20, 10.0F));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.9));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.7));
        this.goalSelector.addGoal(3, new TemptGoal(this, 0.65, Ingredient.of(Items.SALMON, Items.COD), false));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 0.7, 0.85));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PolarBear.class, 16.0F, 0.7, 0.85));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.59));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Skeleton.class, Penguin.class));
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
    public void customServerAiStep() {
        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    @Override
    public Penguin getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return MobRegistry.PENGUIN.create(serverLevel);
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
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegistry.PENGUIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.PENGUIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.FROG_STEP, 0.15F, 1.0F);
    }

    @Override
    @NotNull
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level);
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        // via SnowGolem.performRangedAttack:
        Snowball snowball = new Snowball(this.level(), this);
        double d = livingEntity.getEyeY() - 1.1;
        double e = livingEntity.getX() - this.getX();
        double g = d - snowball.getY();
        double h = livingEntity.getZ() - this.getZ();
        double i = Math.sqrt(e * e + h * h) * 0.2;
        snowball.shoot(e, g + i, h, 1.6F, 12.0F);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(snowball);
    }
}