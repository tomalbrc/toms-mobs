package de.tomalbrc.toms_mobs.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyingMobCircleAroundAnchorGoal extends Goal {
    private float angle;
    private float distance;
    private float height;
    private float clockwise;

    private final FlyCirclingMob flyingMob;


    protected boolean touchingTarget() {
        return this.flyingMob.getMoveTargetPoint().distanceToSqr(this.flyingMob.getX(), this.flyingMob.getY(), this.flyingMob.getZ()) < 4.0;
    }

    public FlyingMobCircleAroundAnchorGoal(FlyCirclingMob mob) {
        super();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.flyingMob = mob;
    }

    public boolean canUse() {
        return true;
    }

    public void start() {
        this.distance = 5.0F + this.flyingMob.getRandom().nextFloat() * 10.0F;
        this.height = -4.0F + this.flyingMob.getRandom().nextFloat() * 9.0F;
        this.clockwise = this.flyingMob.getRandom().nextBoolean() ? 1.0F : -1.0F;
        this.selectNext();
    }

    public void tick() {
        if (this.flyingMob.getRandom().nextInt(this.adjustedTickDelay(350)) == 0) {
            this.height = -4.0F + this.flyingMob.getRandom().nextFloat() * 9.0F;
        }

        if (this.flyingMob.getRandom().nextInt(this.adjustedTickDelay(250)) == 0) {
            ++this.distance;
            if (this.distance > 15.0F) {
                this.distance = 5.0F;
                this.clockwise = -this.clockwise;
            }
        }

        if (this.flyingMob.getRandom().nextInt(this.adjustedTickDelay(450)) == 0) {
            this.angle = this.flyingMob.getRandom().nextFloat() * 2.0F * Mth.PI;
            this.selectNext();
        }

        if (this.touchingTarget()) {
            this.selectNext();
        }

        if (this.flyingMob.getMoveTargetPoint().y < this.flyingMob.getY() && !this.flyingMob.level().isEmptyBlock(this.flyingMob.blockPosition().below(1))) {
            this.height = Math.max(1.0F, this.height);
            this.selectNext();
        }

        if (this.flyingMob.getMoveTargetPoint().y > this.flyingMob.getY() && !this.flyingMob.level().isEmptyBlock(this.flyingMob.blockPosition().above(1))) {
            this.height = Math.min(-1.0F, this.height);
            this.selectNext();
        }
    }

    private void selectNext() {
        if (BlockPos.ZERO.equals(this.flyingMob.getAnchorPoint())) {
            this.flyingMob.setAnchorPoint(this.flyingMob.blockPosition());
        }

        this.angle += this.clockwise * 15.0F * 0.017453292F;
        this.flyingMob.setMoveTargetPoint(Vec3.atLowerCornerOf(this.flyingMob.getAnchorPoint()).add((this.distance * Mth.cos(this.angle)), (-4.0F + this.height), (this.distance * Mth.sin(this.angle))));
    }

    public interface FlyCirclingMob {
        Vec3 getMoveTargetPoint();
        BlockPos getAnchorPoint();

        void setMoveTargetPoint(Vec3 point);
        void setAnchorPoint(BlockPos blockPos);

        double getX();
        double getY();
        double getZ();

        RandomSource getRandom();

        Level level();

        BlockPos blockPosition();
    }
}
