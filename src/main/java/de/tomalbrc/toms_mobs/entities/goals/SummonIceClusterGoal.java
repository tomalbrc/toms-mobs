package de.tomalbrc.toms_mobs.entities.goals;

import de.tomalbrc.toms_mobs.entities.IceCluster;
import de.tomalbrc.toms_mobs.registries.MobRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import org.jetbrains.annotations.Nullable;
import de.tomalbrc.toms_mobs.entities.Iceologer;

public class SummonIceClusterGoal extends AnimatedGoal {
    @Nullable
    private LivingEntity target;

    private final Mob mob;

    private int count = 0;

    public SummonIceClusterGoal(Monster monster) {
        super(240, 40, 20);
        this.mob = monster;
    }

    public boolean canUse() {
        goalUseDelay--;
        LivingEntity livingEntity = this.mob.getTarget();
        return super.canUse() && livingEntity != null && livingEntity.isAlive() && livingEntity.distanceToSqr(this.mob) > 8.f;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() || count > 0;
    }

    @Override
    public void start() {
        super.start();
        this.mob.getNavigation().stop();
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
        if (!this.hasWarmupDelay() && count > 0) {
            this.mob.getNavigation().stop();
            count--;

            IceCluster cluster = new IceCluster(MobRegistry.ICE_SPIKE, this.mob.level());
            cluster.setOwner(this.mob);
            cluster.setTarget(this.target);
            cluster.setPos(this.mob.position().add(Math.cos(Math.toRadians(this.mob.getYRot())) * 5, 2, Math.sin(Math.toRadians(this.mob.getYRot())) * 5));
            this.mob.level().addFreshEntity(cluster);
        }
    }
}
