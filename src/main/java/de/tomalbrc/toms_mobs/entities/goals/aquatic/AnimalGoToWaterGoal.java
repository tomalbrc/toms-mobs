package de.tomalbrc.toms_mobs.entities.goals.aquatic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;

public class AnimalGoToWaterGoal extends MoveToBlockGoal {
    private static final int GIVE_UP_TICKS = 1200;
    private final Animal animal;

    public AnimalGoToWaterGoal(Animal animal, double d) {
        super(animal, animal.isBaby() ? 2.0 : d, 24);
        this.animal = animal;
        this.verticalSearchStart = -1;
    }

    public boolean canContinueToUse() {
        return !this.animal.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.animal.level(), this.blockPos);
    }

    public boolean canUse() {
        if (this.animal.isBaby() && !this.animal.isInWater()) {
            return super.canUse();
        } else {
            return !this.animal.isInWater() ? super.canUse() : false;
        }
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 160 == 0;
    }

    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos).is(Blocks.WATER);
    }
}