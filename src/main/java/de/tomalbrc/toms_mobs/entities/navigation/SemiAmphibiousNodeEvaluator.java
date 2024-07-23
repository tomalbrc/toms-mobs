package de.tomalbrc.toms_mobs.entities.navigation;

import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;

public class SemiAmphibiousNodeEvaluator extends AmphibiousNodeEvaluator {
    public SemiAmphibiousNodeEvaluator(boolean bl) {
        super(bl);
    }

    @Override
    public int getNeighbors(Node[] nodes, Node node) {
        int i = super.getNeighbors(nodes, node);

        for(int k = 0; k < i; ++k) {
            Node currentNode = nodes[k];
            if (currentNode.type == PathType.WATER && currentNode.y < this.mob.level().getSeaLevel() - 4) {
                currentNode.costMalus += 3;
            }
        }

        return i;
    }
}
