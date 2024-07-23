package de.tomalbrc.toms_mobs.entities.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entities.goals.LargeAnimalBreedGoal;
import de.tomalbrc.toms_mobs.entities.navigation.LessSpinnyGroundPathNavigation;
import de.tomalbrc.toms_mobs.registries.MobRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Elephant extends Animal implements AnimatedEntity, PlayerRideable {
    public static final ResourceLocation ID = Util.id("elephant");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Elephant> holder;

    private static final Ingredient tempting = Ingredient.of(Items.SUGAR, Items.SUGAR_CANE, Items.BAMBOO);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
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
    public boolean isFood(ItemStack itemStack) {
        return this.tempting.test(itemStack);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(3, new TemptGoal(this, 0.5, this.tempting, false));
        this.goalSelector.addGoal(4, new LargeAnimalBreedGoal(this, 0.5));
        this.goalSelector.addGoal(4, new PanicGoal(this, 0.6));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 0.6));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
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
    public void customServerAiStep() {
        super.customServerAiStep();

        if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1), this.getRandomY() + 0.5, this.getRandomZ(1), 0, 0.0, 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
        }
    }

    @Override
    public Elephant getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return MobRegistry.ELEPHANT.create(serverLevel);
    }

    @Override
    @NotNull
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (!this.isBaby() && interactionHand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
            this.doPlayerRide(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, interactionHand);
        }
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) / 2.f;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        LivingEntity passenger = this.getControllingPassenger();
        return passenger instanceof ServerPlayer || this.isEffectiveAi();
    }

    @Nullable
    @Override
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
        float x = player.xxa * 0.5F;
        float z = player.zza;
        if (z <= 0.0F) {
            z *= 0.25F;
        }

        return new Vec3(x, 0.0, z);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(Level level) {
        return new LessSpinnyGroundPathNavigation(this, level);
    }
}
