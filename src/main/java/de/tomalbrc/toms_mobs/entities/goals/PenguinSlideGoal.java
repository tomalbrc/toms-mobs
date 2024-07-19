package de.tomalbrc.toms_mobs.entities.goals;

import de.tomalbrc.toms_mobs.entities.passive.Penguin;
import de.tomalbrc.toms_mobs.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class PenguinSlideGoal extends RandomStrollGoal {
    public PenguinSlideGoal(PathfinderMob penguin, double speed) {
        super(penguin, speed);
    }

    @Override
    protected Vec3 getPosition() { return LandRandomPos.getPos(this.mob, 11, 0); }

    @Override
    public boolean canUse() {
        var canUse = super.canUse();
        var len = new Vec3(this.wantedX, this.wantedY, this.wantedZ).subtract(this.mob.position()).length();
        var farEnough = Math.abs(len) > 12;
        if (canUse && farEnough && !this.mob.isInWater() && !this.mob.isPathFinding() && this.mob.getVehicle() == null) {
            var bsBelow = this.mob.level().getBlockState(BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ).below());
            var bs = this.mob.level().getBlockState(BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ));
            return bs.is(BlockTags.SNOW) || (bs.isAir() && (bsBelow.is(BlockTags.SNOW) || bsBelow.is(BlockTags.ICE) || bsBelow.is(Blocks.WATER)));
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if(this.mob.isInWater()) return false;
        return super.canContinueToUse();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        if (this.mob instanceof Penguin penguin) {
            penguin.setSliding(true);
            penguin.playSound(SoundRegistry.PENGUIN_AMBIENT, 0.5F, 1);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (this.mob instanceof Penguin penguin) {
            penguin.setSliding(false);
        }
    }
}