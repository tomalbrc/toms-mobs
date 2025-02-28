package de.tomalbrc.toms_mobs.entity.goal.aquatic;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

// todo, unused
public class PathfinderMobSwimGoal extends Goal {
    private final PathfinderMob animal;
    private final double speedModifier;
    private boolean stuck;

    public PathfinderMobSwimGoal(PathfinderMob animal, double d) {
        this.animal = animal;
        this.speedModifier = d;
    }

    public boolean canUse() {
        return this.animal.isInWater();
    }

    @Override
    public void start() {
        this.stuck = false;
    }

    @Override
    public void tick() {
        if (this.animal.getNavigation().isDone()) {

            RandomSource randomSource = this.animal.getRandom();
            int k = randomSource.nextInt(1025) - 512;
            int l = randomSource.nextInt(9) - 4;
            int m = randomSource.nextInt(1025) - 512;

            BlockPos blockPos = BlockPos.containing((double) k + this.animal.getX(), (double) l + this.animal.getY(), (double) m + this.animal.getZ());

            Vec3 pos = Vec3.atBottomCenterOf(blockPos);
            Vec3 posTowards = DefaultRandomPos.getPosTowards(this.animal, 16, 3, pos, Mth.PI / 10);
            if (posTowards == null) {
                posTowards = DefaultRandomPos.getPosTowards(this.animal, 8, 7, pos, Mth.PI / 2);
            }

            if (posTowards != null) {
                int x = Mth.floor(posTowards.x);
                int z = Mth.floor(posTowards.z);
                if (!this.animal.level().hasChunksAt(x - 34, z - 34, x + 34, z + 34)) {
                    posTowards = null;
                }
            }

            if (posTowards == null) {
                this.stuck = true;
                return;
            }

            this.animal.getNavigation().moveTo(posTowards.x, posTowards.y, posTowards.z, this.speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.animal.getNavigation().isDone() && !this.stuck;
    }
}