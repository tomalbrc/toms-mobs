package de.tomalbrc.toms_mobs.entities.goals;

import de.tomalbrc.toms_mobs.entities.hostile.Showmaster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CircularFangGoal extends AnimatedGoal {

    private static final int CIRCLE_SEGMENTS = 36;

    @Nullable
    private LivingEntity target;

    private final Mob mob;

    public CircularFangGoal(Monster monster) {
        super(200, 35, 60);
        this.mob = monster;
    }

    @Override
    public boolean canUse() {
        goalUseDelay--;
        LivingEntity livingEntity = this.mob.getTarget();
        return super.canUse() && livingEntity != null && livingEntity.isAlive() && livingEntity.distanceToSqr(this.mob) < 10.f && this.mob instanceof Showmaster showmaster && showmaster.canCast();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() || useCount > 0;
    }

    @Override
    public void start() {
        super.start();

        useCount = CIRCLE_SEGMENTS;
        this.mob.getNavigation().stop();

        if (useCount == CIRCLE_SEGMENTS && this.mob instanceof Showmaster showmaster) {
            showmaster.spellAnimation();
        }

        target = this.mob.getTarget();
    }

    @Override
    public void stop() {
        super.stop();

        target = null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();
        super.tick();
    }

    @Override
    protected void customTick() {
        if (!this.hasWarmupDelay() && useCount > 0) {
            this.mob.getNavigation().stop();
            useCount--;

            this.mob.getLookControl().setLookAt(this.mob.position().add(0, 20, 0));
            performSpellCasting((float) Math.toRadians(useCount * 10));
        }

    }

    protected void performSpellCasting(float angle) {
        if (target == null)
            return;

        double d = Math.min(target.getY(), this.mob.getY());
        double e = Math.max(target.getY(), this.mob.getY()) + 1.0;
        float f = angle + (float) Mth.atan2(target.getZ() - this.mob.getZ(), target.getX() - this.mob.getX());

        for (int i = 0; i < 10; ++i) {
            double h = i + 1.5;
            this.createSpellEntity(this.mob.getX() + (double) Mth.cos(f) * h, this.mob.getZ() + (double) Mth.sin(f) * h, d, e);
        }
    }

    private void createSpellEntity(double d, double e, double f, double g) {
        BlockPos blockPos = BlockPos.containing(d, g, e);
        boolean canSpawn = false;
        double j = 0.0;

        do {
            BlockPos blockPos2 = blockPos.below();
            BlockState blockState = this.mob.level().getBlockState(blockPos2);
            if (blockState.isFaceSturdy(this.mob.level(), blockPos2, Direction.UP)) {
                if (!this.mob.level().isEmptyBlock(blockPos)) {
                    BlockState blockState2 = this.mob.level().getBlockState(blockPos);
                    VoxelShape voxelShape = blockState2.getCollisionShape(this.mob.level(), blockPos);
                    if (!voxelShape.isEmpty()) {
                        j = voxelShape.max(Direction.Axis.Y);
                    }
                }

                canSpawn = true;
                break;
            }

            blockPos = blockPos.below();
        } while (blockPos.getY() >= Mth.floor(f) - 1);

        if (canSpawn) {
            this.mob.level().addFreshEntity(new EvokerFangs(this.mob.level(), d, (double) blockPos.getY() + j, e, (float) 90, 1, this.mob));
        }
    }
}
