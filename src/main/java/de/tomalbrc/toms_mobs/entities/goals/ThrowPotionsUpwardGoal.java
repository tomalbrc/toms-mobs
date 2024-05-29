package de.tomalbrc.toms_mobs.entities.goals;

import de.tomalbrc.toms_mobs.entities.Showmaster;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Nullable;

public class ThrowPotionsUpwardGoal extends AnimatedGoal {
    @Nullable
    private LivingEntity target;

    private final Mob mob;

    private int count = 0;

    public ThrowPotionsUpwardGoal(Monster monster) {
        super(200, 30, 70);
        this.mob = monster;
    }

    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        return this.mob instanceof Showmaster showmaster && showmaster.canCast() && super.canUse() && livingEntity != null && livingEntity.isAlive() && livingEntity.distanceToSqr(this.mob) < 10.f;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() || count > 0;
    }

    @Override
    public void start() {
        super.start();

        count = 50;
        warmupDelay = WARMUP_TIME;
        this.mob.getNavigation().stop();

        if (this.mob instanceof Showmaster showmaster) {
            showmaster.throwAnimation();
        }

        target = this.mob.getTarget();
    }

    public void stop() {
        super.stop();
        target = null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();
        super.tick();
    }

    @Override
    public void customTick() {
        if (!this.hasWarmupDelay() && count > 0 && target != null) {
            count--;

            if (count % 2 == 0) {
                ThrownPotion potion = new ThrownPotion(this.mob.level(), this.mob);
                ItemStack itemStack = new ItemStack(Items.SPLASH_POTION);
                itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.SLOWNESS));
                potion.setItem(itemStack);

                potion.shoot(0, 0.7, 0, 0.7F, 20.0F);
                this.mob.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.4F / (this.mob.getRandom().nextFloat() * 0.4F + 0.8F));
                this.mob.level().addFreshEntity(potion);

                this.mob.getNavigation().stop();
                this.mob.getLookControl().setLookAt(this.mob.position().add(0, 20, 0));
            }
        }

    }
}