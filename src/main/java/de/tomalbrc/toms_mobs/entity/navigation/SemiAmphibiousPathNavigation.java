package de.tomalbrc.toms_mobs.entity.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.*;
import org.jetbrains.annotations.NotNull;

public class SemiAmphibiousPathNavigation extends AmphibiousPathNavigation {
    public SemiAmphibiousPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    @NotNull
    protected PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new AmphibiousNodeEvaluator(true) {

            public void prepare(PathNavigationRegion pathNavigationRegion, Mob mob) {
                super.prepare(pathNavigationRegion, mob);
                mob.setPathfindingMalus(PathType.WATER, 0.0F);
                mob.setPathfindingMalus(PathType.WALKABLE, 0.0F);
                mob.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
            }

            public @NotNull Node getStart() {
                return !this.mob.isInWater() ? super.getStart() : this.getStartNode(new BlockPos(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY), Mth.floor(this.mob.getBoundingBox().minZ)));
            }
        };
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, i);
    }
}
