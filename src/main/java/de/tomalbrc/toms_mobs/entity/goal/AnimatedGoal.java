package de.tomalbrc.toms_mobs.entity.goal;

import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AnimatedGoal extends Goal {
    protected final int GOAL_USE_DELAY;
    protected final int WARMUP_TIME;

    protected final int COOLDOWN_TIME;

    private boolean isRunning = false;

    protected int goalUseDelay;
    protected int warmupDelay = 0;
    protected int cooldownDelay = 0;

    protected boolean onCooldown = false;

    protected int useCount = 0;

    public AnimatedGoal(int goalUseDelay, int warmupDelay, int cooldownDelay) {
        GOAL_USE_DELAY = goalUseDelay;
        WARMUP_TIME = warmupDelay;
        COOLDOWN_TIME = cooldownDelay;

        this.goalUseDelay = GOAL_USE_DELAY;

        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        goalUseDelay--;
        return goalUseDelay <= 0 && !isRunning;
    }

    public boolean canContinueToUse() {
        return cooldownDelay > 0 && warmupDelay >= 0;
    }

    @Override
    public void start() {
        isRunning = true;
        cooldownDelay = COOLDOWN_TIME;
        warmupDelay = WARMUP_TIME;
    }

    public void stop() {
        isRunning = false;
        goalUseDelay = GOAL_USE_DELAY;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean isInterruptable() {
        return false;
    }

    public void tick() {
        if (isRunning) {
            this.customTick();

            if (warmupDelay <= 0) {
                if (cooldownDelay >= 0)
                    cooldownDelay--;
                else {
                    warmupDelay = WARMUP_TIME;
                    cooldownDelay = COOLDOWN_TIME;
                }
            } else {
                warmupDelay--;
            }
        }
    }

    public boolean hasWarmupDelay() {
        return this.warmupDelay > 0;
    }

    public boolean hasCooldownDelay() {
        return this.cooldownDelay > 0 || !onCooldown;
    }

    abstract protected void customTick();
}
