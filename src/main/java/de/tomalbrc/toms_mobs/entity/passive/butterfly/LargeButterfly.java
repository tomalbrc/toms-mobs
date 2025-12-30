package de.tomalbrc.toms_mobs.entity.passive.butterfly;

import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.util.SimpleMovementRotatingHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LargeButterfly extends AbstractButterfly {
    public static final Identifier ID = Util.id("large_butterfly");
    public static final Model MODEL = Util.loadModel(ID);

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractButterfly.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    public static boolean checkLargeButterflySpawnRules(EntityType<? extends @NotNull Mob> type, LevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return random.nextInt(1000) == 42 && level.canSeeSky(pos) && !level.getBlockState(pos.below()).getFluidState().is(FluidTags.WATER);
    }

    public LargeButterfly(EntityType<? extends @NotNull Animal> entityType, Level level) {
        super(entityType, level);
    }

    protected void setupModel() {
        this.holder = new SimpleMovementRotatingHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);

        this.holder.getAnimator().playAnimation("fly");
    }

    @Override
    public boolean canBeFollowed() {
        return true;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }
}