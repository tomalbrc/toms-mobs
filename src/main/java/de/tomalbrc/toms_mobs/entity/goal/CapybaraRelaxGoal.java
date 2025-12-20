package de.tomalbrc.toms_mobs.entity.goal;

import de.tomalbrc.toms_mobs.entity.passive.Capybara;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CapybaraRelaxGoal extends Goal {
    private static final int WAIT_TIME_BEFORE_SLEEP = reducedTickDelay(200);
    private int countdown;

    private int useTime;

    private final Capybara entity;

    public CapybaraRelaxGoal(Capybara entity) {
        super();
        this.countdown = entity.getRandom().nextInt(WAIT_TIME_BEFORE_SLEEP);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Flag.LOOK));
        this.entity = entity;
    }

    public boolean canUse() {
        if (this.entity.getRandom().nextInt(10) == 1 && this.entity.getNavigation().isDone()) {
            return this.canSleep() && !this.entity.isRelaxing();
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        this.useTime--;
        return this.useTime > 0 && this.canSleep() && this.entity.xxa == 0.0F && this.entity.yya == 0.0F && this.entity.zza == 0.0F;
    }

    private boolean canSleep() {
        if (this.countdown > 0) {
            --this.countdown;
            return false;
        } else {
            return true;
        }
    }

    public void stop() {
        this.countdown = this.entity.getRandom().nextInt(WAIT_TIME_BEFORE_SLEEP);
        this.entity.setRelaxing(false);
    }

    public void start() {
        this.useTime = 180*20;
        this.entity.setJumping(false);
        this.entity.setRelaxing(true);
        this.entity.getNavigation().stop();
        this.entity.getMoveControl().setWantedPosition(this.entity.getX(), this.entity.getY(), this.entity.getZ(), 0.0);
    }
}
