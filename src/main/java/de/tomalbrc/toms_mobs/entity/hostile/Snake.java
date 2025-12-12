package de.tomalbrc.toms_mobs.entity.hostile;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.LargeAnimalBreedGoal;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Snake extends Animal implements AnimatedEntity {
    public static final Identifier ID = Util.id("snake");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Snake> holder;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.TEMPT_RANGE, 8.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public EntityHolder<Snake> getHolder() {
        return this.holder;
    }

    public Snake(EntityType<? extends Snake> type, Level level) {
        super(type, level);

        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 2.0F));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.59));
        this.goalSelector.addGoal(3, new LargeAnimalBreedGoal(this, 0.5));
        this.goalSelector.addGoal(4, new TemptGoal(this, 0.5, Ingredient.of(BuiltInRegistries.ITEM.get(ItemTags.MEAT).orElseThrow()).or(Ingredient.of(BuiltInRegistries.ITEM.get(ItemTags.EGGS).orElseThrow())), false));
        this.goalSelector.addGoal(5, new PanicGoal(this, 0.6));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 0.6));

        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Snake.class));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Rabbit.class, true));
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
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        boolean result = super.doHurtTarget(serverLevel, entity);

        if (result) {
            if (entity instanceof LivingEntity livingEntity && this.random.nextInt(5) == 1) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, level().getDifficulty().getId() * 2 * 20, 0));
            }
        }

        return result;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.MEAT) || itemStack.is(ItemTags.EGGS);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return MobRegistry.SNAKE.create(serverLevel, EntitySpawnReason.BREEDING);
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
}