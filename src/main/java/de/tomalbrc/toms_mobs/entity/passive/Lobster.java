package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.LargeAnimalBreedGoal;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class Lobster extends Animal implements AnimatedEntity {
    public static final ResourceLocation ID = Util.id("lobster");
    public static final Model MODEL = Util.loadBbModel(ID);
    private final EntityHolder<Lobster> holder;

    boolean partyMode = false;
    BlockPos jukebox;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.MAX_HEALTH, 8.0);
    }

    @Override
    public EntityHolder<Lobster> getHolder() {
        return this.holder;
    }

    public Lobster(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public void setRecordPlayingNearby(BlockPos blockPos, boolean play) {
        this.jukebox = blockPos;
        this.partyMode = play;
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.5) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
            this.partyMode = false;
            this.jukebox = null;
        }

        super.customServerAiStep(serverLevel);

        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new LargeAnimalBreedGoal(this, 0.5));
        this.goalSelector.addGoal(4, new TemptGoal(this, 0.5, this::isFood, false));
        this.goalSelector.addGoal(5, new PanicGoal(this, 0.6));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.5) {
            @Override
            public boolean canUse() {
                return !partyMode && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !partyMode && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            if (partyMode && !isPanicking()) {
                this.holder.getAnimator().pauseAnimation("walk");
                this.holder.getAnimator().pauseAnimation("idle");
                this.holder.getAnimator().playAnimation("party");
            } else {
                AnimationHelper.updateWalkAnimation(this, this.holder);
                AnimationHelper.updateHurtVariant(this, this.holder);
            }
        }
    }

    @Override
    public int getNoActionTime() {
        return partyMode ? 200 : super.getNoActionTime();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.TROPICAL_FISH_HURT;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return Ingredient.of(Items.SEAGRASS, Items.SALMON, Items.TROPICAL_FISH, Items.COD).test(itemStack);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return MobRegistry.LOBSTER.create(serverLevel, EntitySpawnReason.BREEDING);
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
    public void setAge(int age) {
        super.setAge(age);
        if (age < 0) {
            this.holder.setScale(0.5f);
        } else {
            this.holder.setScale(1.f);
        }
    }
}
