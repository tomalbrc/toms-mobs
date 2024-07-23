package de.tomalbrc.toms_mobs.entities.goals.aquatic;

import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.animal.Animal;

public class AquaticFollowParentGoal extends FollowParentGoal {
    private final Animal animal;

    public AquaticFollowParentGoal(Animal animal, double speedModifier) {
        super(animal, speedModifier);
        this.animal = animal;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.animal.isInWater();
    }
}
