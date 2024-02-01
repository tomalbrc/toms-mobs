package de.tomalbrc.toms_mobs.entities;

import com.mojang.math.Axis;
import de.tomalbrc.toms_mobs.entities.goals.IceSpikeGoal;
import de.tomalbrc.toms_mobs.entities.goals.SummonIceClusterGoal;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.provim.nylon.api.AjEntity;
import org.provim.nylon.data.AjLoader;
import org.provim.nylon.holders.entity.EntityHolder;
import org.provim.nylon.holders.entity.living.LivingEntityHolder;
import org.provim.nylon.holders.wrappers.Locator;
import org.provim.nylon.model.AjModel;

import java.util.ArrayList;

public class Iceologer extends SpellcasterIllager implements AjEntity {
    public static final ResourceLocation ID = Util.id("iceologer");
    public static final AjModel MODEL = AjLoader.require(ID);
    private final EntityHolder<Iceologer> holder;

    private final Locator.LocatorListener listener = (holder, pose) -> {
        Quaternionf bodyRot = Axis.YP.rotationDegrees(this.yBodyRot);

        Vector3f position = pose.translation()
                .mul(holder.getScale())
                .rotate(bodyRot.get(new Quaternionf()).conjugate())
                .add(this.position().toVector3f());

        Vector3f rot = new Vector3f(0, -1, 0)
                .rotate(bodyRot)
                .rotate(pose.readOnlyLeftRotation())
                .rotate(pose.readOnlyLeftRotation())
                .normalize();

        for (int i = 0; i < 5; i++) {
            Vector3f v = rot.mul(i).add(position);
            holder.sendPacket(new ClientboundLevelParticlesPacket(
                    ParticleTypes.ENTITY_EFFECT, false, v.x, v.y, v.z, 0.3f, 0.3f, 0.95f, 2.f, 0
            ));
        }
    };

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4);
    }

    @Override
    public EntityHolder<Iceologer> getHolder() {
        return this.holder;
    }

    public Iceologer(EntityType<? extends SpellcasterIllager> entityType, Level level) {
        super(entityType, level);

        this.holder = new LivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.55, 0.65));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));

        this.goalSelector.addGoal(2, new IceSpikeGoal(this));
        this.goalSelector.addGoal(2, new SummonIceClusterGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateHurtVariant(this, this.holder);

            if (!this.isCastingSpell()) {
                AnimationHelper.updateWalkAnimation(this, this.holder);
            } else {
                this.holder.getAnimator().pauseAnimation("walk");
                this.holder.getAnimator().pauseAnimation("idle");
            }
        }
    }


    public void animate(String animationName) {
        Locator leftArm = this.holder.getLocator("larm");
        Locator rightArm = this.holder.getLocator("rarm");

        leftArm.addListener(this.listener);
        rightArm.addListener(this.listener);

        this.holder.getAnimator().playAnimation(animationName, 10, () -> {
            leftArm.removeAllListeners();
            rightArm.removeAllListeners();
        });
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
