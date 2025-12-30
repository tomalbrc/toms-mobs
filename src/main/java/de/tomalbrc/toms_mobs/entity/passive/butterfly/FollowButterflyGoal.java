package de.tomalbrc.toms_mobs.entity.passive.butterfly;

import com.mojang.datafixers.DataFixUtils;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;

public class FollowButterflyGoal extends Goal {
    private static final int INTERVAL_TICKS = 200;
    private final AbstractButterfly mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public FollowButterflyGoal(AbstractButterfly butterfly) {
        this.mob = butterfly;
        this.nextStartTick = this.nextStartTick(butterfly);
    }

    protected int nextStartTick(AbstractButterfly butterfly) {
        return reducedTickDelay(INTERVAL_TICKS + butterfly.getRandom().nextInt(INTERVAL_TICKS) % 20);
    }

    public boolean canUse() {
        if (this.mob.hasFollowers()) {
            return false;
        } else if (this.mob.isFollower()) {
            return true;
        } else if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            List<? extends AbstractButterfly> list = this.mob.level().getEntitiesOfClass(AbstractButterfly.class, this.mob.getBoundingBox().inflate(8.0F, 8.0F, 8.0F));
            AbstractButterfly butterfly = DataFixUtils.orElse(list.stream().filter(AbstractButterfly::canBeFollowed).findAny(), null);
            if (butterfly != null)
                butterfly.addFollowers(list.stream().filter((b) -> !b.isFollower()));

            return this.mob.isFollower();
        }
    }

    public boolean canContinueToUse() {
        return this.mob.inRangeOfLeader();
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.mob.stopFollowing();
    }

    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.mob.pathToLeader();
        }
    }
}
