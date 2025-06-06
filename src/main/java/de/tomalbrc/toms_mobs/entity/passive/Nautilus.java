package de.tomalbrc.toms_mobs.entity.passive;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.util.AnimationHelper;
import de.tomalbrc.toms_mobs.util.NoDeathRotationLivingEntityHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Nautilus extends AbstractFish implements AnimatedEntity {
    public static final ResourceLocation ID = Util.id("nautilus");
    public static final Model MODEL = Util.loadBbModel(ID);
    private final EntityHolder<Nautilus> holder;

    @NotNull
    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.6)
                .add(Attributes.MAX_HEALTH, 16.0);
    }

    @Override
    public EntityHolder<Nautilus> getHolder() {
        return this.holder;
    }

    public Nautilus(EntityType<? extends AbstractFish> type, Level level) {
        super(type, level);

        this.holder = new NoDeathRotationLivingEntityHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 2 == 0) {
            AnimationHelper.updateFishAnimation(this, this.holder);
            AnimationHelper.updateHurtVariant(this, this.holder);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PUFFER_FISH_HURT;
    }

    @Override
    @NotNull
    protected SoundEvent getFlopSound() {
        return SoundEvents.PUFFER_FISH_FLOP;
    }

    @Override
    @NotNull
    public ItemStack getBucketItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        return InteractionResult.PASS;
    }
}
