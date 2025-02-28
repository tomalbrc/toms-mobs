package de.tomalbrc.toms_mobs.entity.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import java.util.EnumSet;

public class AnimatedMeleeAttackGoal extends AnimatedGoal {
    public interface IMeleeAttackAnimatable {
        void meleeAttackAnimation();
    }

    PathfinderMob mob;
    MeleeAttackGoal meleeAttackGoal;

    boolean attack = false;

    public AnimatedMeleeAttackGoal(PathfinderMob pathfinderMob, double d, boolean bl) {
        this(pathfinderMob, d, bl, 10, 10);
    }

    public AnimatedMeleeAttackGoal(PathfinderMob pathfinderMob, double d, boolean bl, int warmupdelay, int cooldowndelay) {
        super(0, warmupdelay, cooldowndelay);

        this.setFlags(EnumSet.of(Flag.MOVE));

        this.meleeAttackGoal = new MeleeAttackGoal(pathfinderMob, d, bl) {
            @Override
            protected boolean canPerformAttack(LivingEntity livingEntity) {
                return false;
            }
        };
        this.mob = pathfinderMob;
    }

    @Override
    public boolean canUse() {
        boolean canUse1 = this.meleeAttackGoal.canUse();
        boolean canUse2 = super.canUse();
        return canUse1 && canUse2;
    }

    @Override
    public boolean canContinueToUse() {
        return this.meleeAttackGoal.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        played = false;
        attack = false;
        this.meleeAttackGoal.start();
    }

    @Override
    public void stop() {
        super.stop();
        meleeAttackGoal.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return meleeAttackGoal.requiresUpdateEveryTick();
    }

    @Override
    public boolean isInterruptable() {
        return super.isInterruptable() || meleeAttackGoal.isInterruptable();
    }

    @Override
    public void tick() {
        super.tick();
        this.meleeAttackGoal.tick();
    }

    private boolean played = false;
    @Override
    protected void customTick() {
        if (!played && canAttack() && hasWarmupDelay() && this.mob instanceof IMeleeAttackAnimatable meleeAttackAnimatable) {
            meleeAttackAnimatable.meleeAttackAnimation();
            attack = false;
            played = true;
        }

        if (this.warmupDelay == 1 && this.cooldownDelay == COOLDOWN_TIME && !attack) {
            attack = true;
            played = false;
            this.performAttack(getServerLevel(this.mob.level()), this.mob.getTarget());
        }

    }

    protected void performAttack(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (this.canAttack()) {
            this.mob.doHurtTarget(serverLevel, livingEntity);
        }
        attack = false;
    }

    protected boolean canAttack() {
        LivingEntity target = this.mob.getTarget();
        return this.mob.isWithinMeleeAttackRange(target) && this.mob.getSensing().hasLineOfSight(target);
    }
}
