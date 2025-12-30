package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Possum extends Animal implements AnimatedEntity {
    public static final Identifier ID = Util.id("possum");
    public static final Model MODEL = Util.loadBbModel(ID);
    private static final int MAX_PET_DELAY = 6*20;
    private final EntityHolder<Possum> holder;

    private int petDelay = 0;

    private static Ingredient tempting() {
        return Ingredient.of(BuiltInRegistries.ITEM.get(ConventionalItemTags.FOODS).orElseThrow());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.TEMPT_RANGE, 8)
                .add(Attributes.MAX_HEALTH, 14.0);
    }

    @Override
    public EntityHolder<Possum> getHolder() {
        return this.holder;
    }

    public Possum(EntityType<? extends @NotNull Animal> type, Level level) {
        super(type, level);

        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
        this.setPathfindingMalus(PathType.DOOR_IRON_CLOSED, -1.0F);
        this.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, -1.0F);
        this.setPathfindingMalus(PathType.DOOR_OPEN, -1.0F);
        this.setPathfindingMalus(PathType.WALKABLE, 0.0F);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    void maybePet(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        if (this.onGround() && this.petDelay <= 0 && player.getItemInHand(interactionHand).isEmpty()) {
            this.getNavigation().stop();

            this.holder.getAnimator().playAnimation("howl");

            this.petDelay = MAX_PET_DELAY;
        }
    }

    void petParticle() {
        this.getNavigation().stop();

        if (this.level() instanceof ServerLevel level) {
            double xOffset = this.random.nextGaussian() * 0.25;
            double yOffset = this.random.nextGaussian() * 0.25;
            double zOffset = this.random.nextGaussian() * 0.25;
            level.sendParticles(ParticleTypes.HEART, this.getRandomX(1), this.getRandomY() + .25, this.getRandomZ(1), 0, xOffset, yOffset, zOffset, 0);
        }
    }

    @Override
    @NotNull
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        maybePet(player, interactionHand);
        return super.mobInteract(player, interactionHand);
    }

    @Override
    public boolean isFood(@NotNull ItemStack itemStack) {
        return tempting().test(itemStack);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.1));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.1));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1, tempting(), false));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 8.f));
    }

    @Override
    public void tick() {
        super.tick();

        if (petDelay > 0) {
            petDelay--;
            if (petDelay == MAX_PET_DELAY-10) {
                petParticle();
            }
        }

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateWalkAnimation(this, this.holder);
            AnimationHelper.updateHurtColor(this, this.holder);
        }
    }

    @Override
    public void setInLove(@Nullable Player player) {
        if (this.level() instanceof ServerLevel level) {
            for (int i = 0; i < 7; ++i) {
                double xOffset = this.random.nextGaussian() * 0.01;
                double yOffset = this.random.nextGaussian() * 0.01;
                double zOffset = this.random.nextGaussian() * 0.01;
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
    public Possum getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return MobRegistry.POSSUM.create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);

        this.petDelay = input.getIntOr("PetDelay", 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.putInt("PetDelay", this.petDelay);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundNavigation(this, level);
    }
}
