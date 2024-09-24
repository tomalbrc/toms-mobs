package de.tomalbrc.toms_mobs.entities.goals;

import de.tomalbrc.toms_mobs.entities.hostile.IceSpike;
import de.tomalbrc.toms_mobs.entities.hostile.IceSpikeSmall;
import de.tomalbrc.toms_mobs.entities.hostile.Iceologer;
import de.tomalbrc.toms_mobs.registries.MobRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class IceSpikeGoal extends AnimatedGoal {
    @Nullable
    private LivingEntity target;

    private final Mob mob;

    private int count = 0;

    public IceSpikeGoal(Monster monster) {
        super(100, 35, 20);
        this.mob = monster;
    }

    public boolean canUse() {
        goalUseDelay--;
        LivingEntity livingEntity = this.mob.getTarget();
        return super.canUse() && livingEntity != null && livingEntity.isAlive() && livingEntity.distanceToSqr(this.mob) < 11.f;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() || count > 0;
    }

    @Override
    public void start() {
        super.start();
        this.mob.getNavigation().stop();
        target = this.mob.getTarget();

        if (this.mob instanceof Iceologer iceologer) {
            iceologer.animate("summon_spikes");
        }

        count = 1;
    }

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
        if (!this.hasWarmupDelay() && count > 0) {
            this.mob.getNavigation().stop();
            count--;

            performSpellCasting();
        }
    }

    protected void performSpellCasting() {
        if (target == null)
            return;

        double minY = Math.min(target.getY(), this.mob.getY());
        double maxY = Math.max(target.getY(), this.mob.getY()) + 1.0;
        float angle = (float) Mth.atan2(target.getZ() - this.mob.getZ(), target.getX() - this.mob.getX());

        int max = 6;
        for (int i = 0; i < max; ++i) {
            double h = i * 1.3 + 1.5;
            this.createSpellEntity(this.mob.getX() + (double) Mth.cos(angle) * h, this.mob.getZ() + (double) Mth.sin(angle) * h, minY, maxY, i == max - 1, angle);
        }
    }

    private void createSpellEntity(double x, double z, double minY, double maxY, boolean big, float angle) {
        BlockPos blockPos = BlockPos.containing(x, maxY, z);
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
        } while (blockPos.getY() >= Mth.floor(minY) - 1);

        if (canSpawn || big) {
            Entity iceSpike;
            if (big) {
                iceSpike = new IceSpike(MobRegistry.ICE_SPIKE, this.mob.level());
                ((IceSpike) iceSpike).setOwner(this.mob);
            } else {
                iceSpike = new IceSpikeSmall(MobRegistry.ICE_SPIKE_SMALL, this.mob.level());
                ((IceSpikeSmall) iceSpike).setOwner(this.mob);
            }
            iceSpike.setPos(x, j + blockPos.getY(), z);
            iceSpike.setYRot((float) Math.toDegrees(angle) - 90 + this.mob.getRandom().nextInt(20) - 10);
            this.mob.level().addFreshEntity(iceSpike);
        }
    }
}
