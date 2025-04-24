package de.tomalbrc.toms_mobs.entity.hostile;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.entity.goal.CircularFangGoal;
import de.tomalbrc.toms_mobs.entity.goal.IceSpikeGoal;
import de.tomalbrc.toms_mobs.entity.goal.RapidfireGoal;
import de.tomalbrc.toms_mobs.entity.goal.ThrowPotionsUpwardGoal;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.GeyserCompatHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import xyz.nucleoid.packettweaker.PacketContext;

public class Showmaster extends SpellcasterIllager implements AnimatedEntity {
    public static final ResourceLocation ID = Util.id("showmaster");
    public static final Model MODEL = Util.loadModel(ID);
    private final EntityHolder<Showmaster> holder;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    public EntityHolder<Showmaster> getHolder() {
        return this.holder;
    }

    public Showmaster(EntityType<? extends SpellcasterIllager> entityType, Level level) {
        super(entityType, level);

        this.holder = new GeyserCompatHolder<>(this, MODEL);
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
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));

        this.goalSelector.addGoal(1, new ThrowPotionsUpwardGoal(this));
        this.goalSelector.addGoal(2, new RapidfireGoal(this));
        this.goalSelector.addGoal(3, new CircularFangGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.05, true));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, false));
    }

    @Override
    public void applyRaidBuffs(ServerLevel serverLevel, int i, boolean bl) {
        // TODO
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
