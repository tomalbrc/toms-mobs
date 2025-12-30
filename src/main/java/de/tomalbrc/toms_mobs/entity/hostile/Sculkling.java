package de.tomalbrc.toms_mobs.entity.hostile;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.AnimatedMeleeAttackGoal;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import org.jetbrains.annotations.NotNull;

public class Sculkling extends Monster implements AnimatedEntity, AnimatedMeleeAttackGoal.IMeleeAttackAnimatable {
    public static final Identifier ID = Util.id("sculkling");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Sculkling> holder;
    private int stolenXP = 0;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4);
    }

    public static boolean checkSculklingSpawnRules(EntityType<? extends @NotNull Monster> type, ServerLevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return pos.getY() < 10 && !level.canSeeSky(pos) && checkMonsterSpawnRules(type, level, spawnReason, pos, random);
    }

    @Override
    public EntityHolder<Sculkling> getHolder() {
        return this.holder;
    }

    public Sculkling(EntityType<? extends @NotNull Sculkling> type, Level level) {
        super(type, level);

        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.9, 0.9, x -> this.stolenXP > 0));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 2.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new AnimatedMeleeAttackGoal(this, 0.55, true));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel serverLevel, DamageSource damageSource) {
        return damageSource.is(DamageTypeTags.IS_FIRE) && damageSource.is(DamageTypeTags.IS_DROWNING);
    }

    @Override
    public void meleeAttackAnimation() {
        this.holder.getAnimator().playAnimation("melee", 10);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_CATALYST_BLOOM;
    }

    @Override
    @NotNull
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.SCULK_CLICKING;
    }

    @Override
    @NotNull
    protected SoundEvent getDeathSound() {
        return SoundEvents.SCULK_SHRIEKER_SHRIEK;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateWalkAnimation(this, this.holder);
            AnimationHelper.updateHurtColor(this, this.holder);
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull ServerLevel serverLevel, @NotNull Entity entity) {
        boolean result = super.doHurtTarget(serverLevel, entity);

        if (result && entity instanceof ServerPlayer player) {
            if (player.totalExperience >= 4) {
                player.setExperiencePoints(player.totalExperience-4);
                stolenXP += 4;
            }
        }

        return result;
    }

    @Override
    public int getBaseExperienceReward(@NotNull ServerLevel serverLevel) {
        return (int) ((stolenXP * 1.25) + 2);
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);

        this.stolenXP = input.getIntOr("XP", 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.putInt("XP", this.stolenXP);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundNavigation(this, level);
    }
}