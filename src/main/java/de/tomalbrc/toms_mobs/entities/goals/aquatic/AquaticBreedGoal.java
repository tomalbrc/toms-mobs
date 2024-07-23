package de.tomalbrc.toms_mobs.entities.goals.aquatic;

import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;

public class AquaticBreedGoal extends BreedGoal {

    public AquaticBreedGoal(Animal animal, double d) {
        super(animal, d);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.animal.isInWater();
    }
}
