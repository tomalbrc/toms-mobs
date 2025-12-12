package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.CapybaraRelaxGoal;
import de.tomalbrc.toms_mobs.entity.goal.aquatic.*;
import de.tomalbrc.toms_mobs.entity.move.SemiAquaticMoveControl;
import de.tomalbrc.toms_mobs.entity.navigation.SemiAmphibiousPathNavigation;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.Objects;

public class Capybara extends Animal implements AnimatedEntity {
    public static final Identifier ID = Util.id("capybara");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Capybara> holder;

    private static Ingredient tempting() {
        return Ingredient.of(Items.APPLE, Items.MELON, Items.PUMPKIN, Items.SUGAR_CANE);
    }

    private ItemStack apple = ItemStack.EMPTY;
    private boolean relaxing;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.TEMPT_RANGE, 8)
                .add(Attributes.MAX_HEALTH, 16.0);
    }

    @Override
    public EntityHolder<Capybara> getHolder() {
        return this.holder;
    }

    public Capybara(EntityType<? extends Animal> type, Level level) {
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
    @NotNull
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (this.isBaby()) {
            return super.mobInteract(player, interactionHand);
        }

        var item = player.getItemInHand(interactionHand);
        if (item.isEmpty() && !this.apple.isEmpty()) {
            if (!this.apple.has(DataComponents.LORE) || Objects.requireNonNull(this.apple.get(DataComponents.LORE)).lines().isEmpty()) {
                this.apple.set(DataComponents.LORE, new ItemLore(ImmutableList.of(Component.literal("Blessed by a Capybara").withColor(0xFFD700))));
            }

            player.setItemInHand(interactionHand, this.apple);
            this.apple = ItemStack.EMPTY;

            this.holder.getVariantController().setDefaultVariant();

            return InteractionResult.SUCCESS;
        } else if (player.getMainHandItem().is(Items.APPLE) && this.apple.isEmpty() && player.isShiftKeyDown()) {
            this.apple = item.copyWithCount(1);
            item.shrink(1);
            this.holder.getVariantController().setVariant("apple");

            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, interactionHand);
        }
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        if (levelReader.getFluidState(blockPos).is(FluidTags.WATER)) {
            return 1;
        } else {
            return levelReader.getPathfindingCostFromLightLevels(blockPos);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return tempting().test(itemStack);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AquaticPanicGoal(this, 0.35));
        this.goalSelector.addGoal(1, new AquaticBreedGoal(this, 0.3));
        this.goalSelector.addGoal(2, new TemptGoal(this, 0.3, tempting(), false));
        this.goalSelector.addGoal(2, new AquaticFollowParentGoal(this, 0.25));
        this.goalSelector.addGoal(3, new PathfinderMobSwimGoal(this, 2.5));
        this.goalSelector.addGoal(4, new CapybaraRelaxGoal(this));

        this.goalSelector.addGoal(5, new AquaticRandomStrollGoal(this, 0.25));

        this.goalSelector.addGoal(6, new AnimalGoToWaterGoal(this, 0.25));
        this.goalSelector.addGoal(7, new AquaticRandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateCapybaraWalkAnimation(this, this.holder);
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
    public void setAge(int age) {
        super.setAge(age);
        if (age < 0) {
            this.holder.setScale(0.5f);
        } else {
            this.holder.setScale(1.f);
        }
    }

    @Override
    public Capybara getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return MobRegistry.CAPYBARA.create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(Level level) {
        return new SemiAmphibiousPathNavigation(this, level);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        input.read("Apple", ItemStack.CODEC).ifPresent(itemStack -> {
            this.apple = itemStack;
        });

        if (!this.apple.isEmpty()) {
            this.holder.getVariantController().setVariant("apple");
        } else {
            this.holder.getVariantController().setDefaultVariant();
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (!this.apple.isEmpty())
            output.store("Apple", ItemStack.CODEC, this.apple);
    }

    public void setRelaxing(boolean b) {
        if (this.relaxing == b)
            return;

        this.relaxing = b;
        if (this.relaxing) {
            this.holder.getAnimator().playAnimation("relax");
        } else {
            this.holder.getAnimator().stopAnimation("relax");
        }
    }

    public boolean isRelaxing() {
        return this.relaxing;
    }
}
