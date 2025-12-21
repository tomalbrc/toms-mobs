package de.tomalbrc.toms_mobs.entity.goal;

import de.tomalbrc.toms_mobs.entity.hostile.IceCluster;
import de.tomalbrc.toms_mobs.entity.hostile.Iceologer;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class SummonIceClusterGoal extends AnimatedGoal {
    @Nullable
    private LivingEntity target;

    private final Mob mob;

    private int count = 0;

    public SummonIceClusterGoal(Monster monster) {
        super(240, 40, 20);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Flag.LOOK));
        this.mob = monster;
    }

    public boolean canUse() {
        goalUseDelay--;
        LivingEntity livingEntity = this.mob.getTarget();
        return !this.mob.getNavigation().isInProgress() && super.canUse() && livingEntity != null && livingEntity.isAlive() && livingEntity.distanceTo(this.mob) > 8.f;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() || count > 0;
    }

    @Override
    public void start() {
        super.start();
        this.target = this.mob.getTarget();

        if (this.mob instanceof Iceologer iceologer) {
            iceologer.animate("summon_floating");
        }

        count = 1;
    }

    public void stop() {
        super.stop();

        target = null;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void customTick() {
        if (!this.hasWarmupDelay() && count > 0) {
            count--;

            IceCluster cluster = new IceCluster(MobRegistry.ICE_SPIKE, this.mob.level());
            cluster.setOwner(this.mob);
            cluster.setTarget(this.target);
            cluster.setPos(this.mob.position().add(Math.cos(Math.toRadians(this.mob.getYRot())) * 5, 2, Math.sin(Math.toRadians(this.mob.getYRot())) * 5));
            this.mob.level().addFreshEntity(cluster);
        }
    }
}
