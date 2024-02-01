package de.tomalbrc.toms_mobs.entities;

import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import de.tomalbrc.toms_mobs.entities.goals.CircularFangGoal;
import de.tomalbrc.toms_mobs.entities.goals.IceSpikeGoal;
import de.tomalbrc.toms_mobs.entities.goals.RapidfireGoal;
import de.tomalbrc.toms_mobs.entities.goals.ThrowPotionsUpwardGoal;
import de.tomalbrc.resin.api.AjEntity;
import de.tomalbrc.resin.data.AjLoader;
import de.tomalbrc.resin.holders.entity.EntityHolder;
import de.tomalbrc.resin.holders.entity.living.LivingEntityHolder;
import de.tomalbrc.resin.model.AjModel;

import java.util.ArrayList;

public class Showmaster extends SpellcasterIllager implements AjEntity {
    public static final ResourceLocation ID = Util.id("showmaster");
    public static final AjModel MODEL = AjLoader.require(Util.id("stenel"));
    private final EntityHolder<Showmaster> holder;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 140.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public EntityHolder<Showmaster> getHolder() {
        return this.holder;
    }

    public Showmaster(EntityType<? extends SpellcasterIllager> entityType, Level level) {
        super(entityType, level);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));

        this.goalSelector.addGoal(1, new ThrowPotionsUpwardGoal(this));
        this.goalSelector.addGoal(2, new RapidfireGoal(this));
        this.goalSelector.addGoal(3, new CircularFangGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 0.75, true));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateHurtVariant(this, this.holder);

            if (!this.isCastingSpell() && this.canCast()) {
                AnimationHelper.updateWalkAnimation(this, this.holder);
            } else {
                this.holder.getAnimator().pauseAnimation("walk");
                this.holder.getAnimator().pauseAnimation("idle");
            }
        }
    }

    @Override
    @NotNull
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.ILLUSIONER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(int i, boolean bl) {
    }

    @Override
    @NotNull
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    public void spellAnimation() {
        this.holder.getAnimator().playAnimation("spell", 10);
    }

    public void throwAnimation() {
        this.holder.getAnimator().playAnimation("throw", 10);
    }

    public boolean canCast() {
        for (WrappedGoal wrappedGoal : this.goalSelector.getAvailableGoals()) {
            if (wrappedGoal.isRunning()) {
                Goal goal = wrappedGoal.getGoal();
                if (goal instanceof ThrowPotionsUpwardGoal || goal instanceof CircularFangGoal || goal instanceof RapidfireGoal || goal instanceof IceSpikeGoal) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    @NotNull
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    @NotNull
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }
}
