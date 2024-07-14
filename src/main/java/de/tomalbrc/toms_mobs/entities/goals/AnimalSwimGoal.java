package de.tomalbrc.toms_mobs.entities.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;

// todo, unused
public class AnimalSwimGoal extends Goal {
    private final Animal animal;
    private final double speedModifier;
    private boolean stuck;

    public AnimalSwimGoal(Animal animal, double d) {
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

            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
            Vec3 vec32 = DefaultRandomPos.getPosTowards(this.animal, 16, 3, vec3, Mth.PI / 10);
            if (vec32 == null) {
                vec32 = DefaultRandomPos.getPosTowards(this.animal, 8, 7, vec3, Mth.PI / 2);
            }

            if (vec32 != null) {
                int i = Mth.floor(vec32.x);
                int j = Mth.floor(vec32.z);
                if (!this.animal.level().hasChunksAt(i - 34, j - 34, i + 34, j + 34)) {
                    vec32 = null;
                }
            }

            if (vec32 == null) {
                this.stuck = true;
                return;
            }

            this.animal.getNavigation().moveTo(vec32.x, vec32.y, vec32.z, this.speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.animal.getNavigation().isDone() && !this.stuck && !this.animal.isInLove();
    }
}