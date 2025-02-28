package de.tomalbrc.toms_mobs.entity.navigation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.jetbrains.annotations.NotNull;

public class SemiAmphibiousPathNavigation extends AmphibiousPathNavigation {
    public SemiAmphibiousPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    @NotNull
    protected PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new SemiAmphibiousNodeEvaluator(true);
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, i);
    }
}
