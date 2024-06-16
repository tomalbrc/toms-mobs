package de.tomalbrc.toms_mobs.entities.goals;

import de.tomalbrc.toms_mobs.entities.Showmaster;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RapidfireGoal extends AnimatedGoal {
    private static final int MAX_GOAL_USE_DELAY = 100;

    private final Mob mob;

    private int count = 0;


    public RapidfireGoal(Monster monster) {
        super(170, 10, 70);
        this.mob = monster;
    }

    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null && livingEntity.isAlive() && super.canUse() && this.mob instanceof Showmaster showmaster && showmaster.canCast();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() || count > 0;
    }

    @Override
    public void start() {
        super.start();
        count = 60;
    }

    public void stop() {
        super.stop();
        goalUseDelay = MAX_GOAL_USE_DELAY;
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
    protected void customTick() {
        if (!this.hasWarmupDelay() && count > 0 && this.mob.getTarget() != null) {
            count--;

            if (count % 2 == 0) {
                Arrow arrow = new Arrow(this.mob.level(), this.mob, new ItemStack(Items.ARROW), null);
                double d = this.mob.getTarget().getX() - this.mob.getX();
                double e = this.mob.getTarget().getY(1.f / 3.f) - arrow.getY();
                double g = this.mob.getTarget().getZ() - this.mob.getZ();
                double h = Math.sqrt(d * d + g * g);
                arrow.shoot(d, e + h * 0.2, g, 1.6F, (float) (14 - this.mob.level().getDifficulty().getId() * 4));
                this.mob.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 0.4F / (this.mob.getRandom().nextFloat() * 0.1F + 0.8F));
                this.mob.level().addFreshEntity(arrow);
            }
        }
    }
}
