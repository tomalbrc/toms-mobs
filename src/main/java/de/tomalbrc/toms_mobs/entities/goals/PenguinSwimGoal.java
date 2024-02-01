package de.tomalbrc.toms_mobs.entities.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import de.tomalbrc.toms_mobs.entities.Penguin;

// todo, unused
public class PenguinSwimGoal extends Goal {
    private final Penguin penguin;
    private final double speedModifier;
    private boolean stuck;

    public PenguinSwimGoal(Penguin penguin, double d) {
        this.penguin = penguin;
        this.speedModifier = d;
    }

    public boolean canUse() {
        return this.penguin.isInWater();
    }

    @Override
    public void start() {
        this.stuck = false;
    }

    @Override
    public void tick() {
        if (this.penguin.getNavigation().isDone()) {

            RandomSource randomSource = this.penguin.getRandom();
            int k = randomSource.nextInt(1025) - 512;
            int l = randomSource.nextInt(9) - 4;
            int m = randomSource.nextInt(1025) - 512;

            BlockPos blockPos = BlockPos.containing((double) k + this.penguin.getX(), (double) l + this.penguin.getY(), (double) m + this.penguin.getZ());

            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
            Vec3 vec32 = DefaultRandomPos.getPosTowards(this.penguin, 16, 3, vec3, Mth.PI / 10);
            if (vec32 == null) {
                vec32 = DefaultRandomPos.getPosTowards(this.penguin, 8, 7, vec3, Mth.PI / 2);
            }

            if (vec32 != null) {
                int i = Mth.floor(vec32.x);
                int j = Mth.floor(vec32.z);
                if (!this.penguin.level().hasChunksAt(i - 34, j - 34, i + 34, j + 34)) {
                    vec32 = null;
                }
            }

            if (vec32 == null) {
                this.stuck = true;
                return;
            }

            this.penguin.getNavigation().moveTo(vec32.x, vec32.y, vec32.z, this.speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.penguin.getNavigation().isDone() && !this.stuck && !this.penguin.isInLove();
    }
}