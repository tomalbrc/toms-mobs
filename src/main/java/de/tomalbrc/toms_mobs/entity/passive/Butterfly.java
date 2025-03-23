package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.FlyingWanderGoal;
import de.tomalbrc.toms_mobs.util.MovementRotatingHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Butterfly extends Animal implements AnimatedEntity, FlyingAnimal {
    public static final ResourceLocation ID = Util.id("butterfly");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Butterfly> holder;

    private int color;
    private String variant;

    private final String[] variants = new String[]{
            "default",
            "1",
            "2"
    };

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.MOVEMENT_SPEED, 0.1);
    }

    public static boolean checkButterflySpawnRules(EntityType<? extends Mob> type, LevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return level.canSeeSky(pos);
    }

    @Override
    public EntityHolder<Butterfly> getHolder() {
        return this.holder;
    }

    public Butterfly(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

        this.moveControl = new FlyingMoveControl(this, 1, true);

        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.FENCE, -1.0F);

        this.holder = new MovementRotatingHolder(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);

        this.setColor(Color.hslToRgb(this.getRandom().nextFloat(), 0.99f, 0.65f));
        this.setVariant(this.variants[this.random.nextInt(this.variants.length)]);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WIND_CHARGE_BURST.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BREEZE_WIND_CHARGE_BURST.value();
    }

    private void setColor(int color) {
        if (this.color == color) return;

        this.color = color;
        this.holder.setColor(color);
    }

    private void setVariant(String variant) {
        if (this.variant != null && this.variant.equals(variant)) return;

        this.variant = variant;
        this.holder.getVariantController().setVariant(variant);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FlyingWanderGoal(this));
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
    }

    @Override
    @NotNull
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level) {
            public boolean isStableDestination(BlockPos blockPos) {
                return this.level.getBlockState(blockPos.below()).isAir();
            }
        };
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(false);
        return flyingPathNavigation;
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        return levelReader.getBlockState(blockPos).isAir() ? 10.0F : 0.0F;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    protected int getBaseExperienceReward(ServerLevel serverLevel) {
        return 0;
    }
    
    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.BEE_FOOD);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Color")) this.setColor(tag.getInt("Color").orElseThrow());
        if (tag.contains("Variant")) {
            String v = tag.getString("Variant").orElseThrow();
            if (this.containsVariant(v)) this.setVariant(v);
        }
    }

    private boolean containsVariant(String target) {
        for (String variant : this.variants) {
            if (variant.equals(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("Color", this.color);
        tag.putString("Variant", this.variant);
    }

    static class Color {
        public static int hslToRgb(float h, float s, float l){
            float r, g, b;

            if (s == 0f) {
                r = g = b = l; // achromatic
            } else {
                float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
                float p = 2 * l - q;
                r = hueToRgb(p, q, h + 1f/3f);
                g = hueToRgb(p, q, h);
                b = hueToRgb(p, q, h - 1f/3f);
            }
            return to255(r) << 16 | to255(g) << 8 | to255(b);
        }
        public static int to255(float v) { return (int)Math.min(255,256*v); }
        /** Helper method that converts hue to rgb */
        public static float hueToRgb(float p, float q, float t) {
            if (t < 0f)
                t += 1f;
            if (t > 1f)
                t -= 1f;
            if (t < 1f/6f)
                return p + (q - p) * 6f * t;
            if (t < 1f/2f)
                return q;
            if (t < 2f/3f)
                return p + (q - p) * (2f/3f - t) * 6f;
            return p;
        }
    }

}