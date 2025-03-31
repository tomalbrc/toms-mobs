package de.tomalbrc.toms_mobs.entity.goal.flying;

import de.tomalbrc.toms_mobs.entity.passive.Seagull;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyingMobCircleAroundAnchorGoal extends Goal {
    static final int MAX_FLYTIME = 20*20;
    static final int RETURN_TIME = 15*20;

    private float angle;
    private float distance;
    private float height;
    private float clockwise;

    private boolean active = false;

    private final Seagull flyingMob;

    protected boolean touchingTarget() {
        return this.flyingMob.getMoveTargetPoint().distanceToSqr(this.flyingMob.getX(), this.flyingMob.getY(), this.flyingMob.getZ()) < 4.0;
    }

    public FlyingMobCircleAroundAnchorGoal(Seagull mob) {
        super();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.flyingMob = mob;
    }

    @Override
    public boolean canUse() {
        return flyingMob.flytime() <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        var tooOld = flyingMob.flytime() > MAX_FLYTIME;
        return !(tooOld || (flyingMob.flytime() > RETURN_TIME && this.flyingMob.getAnchorPoint().getCenter().distanceTo(this.flyingMob.position()) <= 2.5));
    }

    @Override
    public void start() {
        this.active = true;
        this.distance = 5.0F + this.flyingMob.getRandom().nextFloat() * 10.0F;
        this.height = -0.5F + this.flyingMob.getRandom().nextFloat() * 2.0F;
        this.clockwise = this.flyingMob.getRandom().nextBoolean() ? 1.0F : -1.0F;
        //this.flyingMob.setDeltaMovement(new Vec3(0,0.1,0));
        this.flyingMob.setOnGroundWithMovement(false, new Vec3(0,0.1,0));
        this.selectNext();
    }

    @Override
    public void stop() {
        this.active = false;
        this.flyingMob.setMoveTargetPoint(flyingMob.getAnchorPoint().getBottomCenter());
    }

    @Override
    public void tick() {

        flyingMob.incFlytime();

        if (this.flyingMob.getRandom().nextInt(this.adjustedTickDelay(350)) == 0) {
            this.height = -4.0F + this.flyingMob.getRandom().nextFloat() * 8.0F;
        }

        if (this.flyingMob.getRandom().nextInt(this.adjustedTickDelay(250)) == 0) {
            ++this.distance;
            if (this.distance > 15.0F) {
                this.distance = 5.0F;
                this.clockwise = -this.clockwise;
            }
        }

        if (this.flyingMob.getRandom().nextInt(this.adjustedTickDelay(450)) == 0) {
            this.angle += (this.flyingMob.getRandom().nextFloat()-0.5f)*0.01f;
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
        if (flyingMob.flytime() >= MAX_FLYTIME) {
            this.flyingMob.setMoveTargetPoint(flyingMob.getAnchorPoint().getBottomCenter().subtract(0,20,0));
            return;
        }


        if (BlockPos.ZERO.equals(this.flyingMob.getAnchorPoint())) {
            this.flyingMob.setAnchorPoint(this.flyingMob.blockPosition());
        }

        this.angle += this.clockwise * 15.0F * 0.017453292F;
        var vec = this.flyingMob.position().vectorTo(this.flyingMob.getAnchorPoint().getCenter()).multiply(1.0, 0.1f, 1.f).add(flyingMob.position());
        this.flyingMob.setMoveTargetPoint(vec.add((this.distance * Mth.cos(this.angle)), (-0.0F + this.height), (this.distance * Mth.sin(this.angle))));
    }

    public boolean active() {
        return this.active;
    }
}
