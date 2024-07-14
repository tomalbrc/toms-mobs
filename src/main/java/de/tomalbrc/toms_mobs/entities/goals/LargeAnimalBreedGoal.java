package de.tomalbrc.toms_mobs.entities.goals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class LargeAnimalBreedGoal extends BreedGoal {

    public LargeAnimalBreedGoal(Animal animal, double d) {
        super(animal, d);
    }

    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0*2) {
            this.breed();
        }
    }
}