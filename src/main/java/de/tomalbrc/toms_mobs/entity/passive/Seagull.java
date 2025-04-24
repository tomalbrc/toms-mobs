package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.flying.FlyingMobCircleAroundAnchorGoal;
import de.tomalbrc.toms_mobs.entity.move.FlyingMobCircleMoveControl;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.MovementRotatingHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class Seagull extends Animal implements AnimatedEntity {
    public static final ResourceLocation ID = Util.id("seagull");
    public static final Model MODEL = Util.loadBbModel(ID);

    private final EntityHolder<Seagull> holder;

    private FlyingMobCircleAroundAnchorGoal circleAroundAnchorGoal;

    ///  TODO: save/load values
    private int flytime = -1;
    private BlockPos anchor;
    private Vec3 moveTarget;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.FLYING_SPEED, 1)
                .add(Attributes.MAX_HEALTH, 16.0);
    }

    public Seagull(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMobCircleMoveControl(this);
        this.lookControl = new LookControl(this);

        this.holder = new MovementRotatingHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        if (FloodgateApi.getInstance().isFloodgatePlayer(context.getPlayer().getUUID())) {
            return EntityType.PIG;
        }

        return AnimatedEntity.super.getPolymerEntityType(context);
    }

    @Override
    public EntityHolder<Seagull> getHolder() {
        return this.holder;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    @Override
    public void travel(Vec3 vec3) {
        if (!canFlyCurrently()) {
            super.travel(vec3);
            return;
        }

        if (!this.hasControllingPassenger()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());

                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5F));
            } else {
                float f = 0.91F;
                if (this.onGround()) {
                    f = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.91F;
                }

                float g = 0.16277137F / (f * f * f);
                f = 0.91F;
                if (this.onGround()) {
                    f = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.91F;
                }

                this.moveRelative(this.onGround() ? 0.1F * g : 0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(f));
            }
        }

    }

    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.FOX_FOOD) || itemStack.is(ItemTags.WOLF_FOOD);
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
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new PanicGoal(this, 0.45) {
            @Override
            public boolean canUse() {
                return !canFlyCurrently() && super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new BreedGoal(this, 0.35) {
            @Override
            public boolean canUse() {
                return !canFlyCurrently() && super.canUse();
            }
        });

        this.goalSelector.addGoal(4, new TemptGoal(this, 0.4, itemStack -> itemStack.is(ItemTags.FOX_FOOD) || itemStack.is(ItemTags.WOLF_FOOD), false) {
            @Override
            public boolean canUse() {
                return !canFlyCurrently() && super.canUse();
            }
        });

        this.circleAroundAnchorGoal = new FlyingMobCircleAroundAnchorGoal(this);
        this.goalSelector.addGoal(5, this.circleAroundAnchorGoal);
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.3) {
            @Override
            public boolean canUse() {
                return !canFlyCurrently() && super.canUse();
            }
        });

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return !canFlyCurrently() && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }


    @Override
    public void tick() {
        super.tick();

        if (!this.circleAroundAnchorGoal.active() && this.level().getGameTime() % 2 == 0) {
            this.decFlytime();
        }

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateBirdAnimation(this, this.holder);
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
    public Seagull getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return MobRegistry.SEAGULL.create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(Level level) {

        GroundPathNavigation flyingPathNavigation = new GroundPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos blockPos) {
                if (canFlyCurrently())
                    return this.level.getBlockState(blockPos.below()).isAir();
                return this.level.getBlockState(blockPos).entityCanStandOn(this.level, blockPos, this.mob);
            }
        };
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(false);
        return flyingPathNavigation;
    }

    public BlockPos getAnchorPoint() {
        if (anchor == null) anchor = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition()).above(20);
        return anchor;
    }

    public void setAnchorPoint(BlockPos anchor) {
        this.anchor = anchor;
    }

    public int flytime() {
        return this.flytime;
    }

    public int incFlytime() {
        return ++this.flytime;
    }

    public int decFlytime() {
        return --this.flytime;
    }

    public boolean canFlyCurrently() {
        return circleAroundAnchorGoal.canContinueToUse();
    }

    public Vec3 getMoveTargetPoint() {
        if (moveTarget == null) moveTarget = this.position().add(0,2,0);
        return moveTarget;
    }

    public void setMoveTargetPoint(Vec3 pos) {
        moveTarget = pos;
    }

    @Override
    public @NotNull SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        this.anchor = serverLevelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.blockPosition()).above(20);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);
    }
}
