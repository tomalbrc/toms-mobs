package de.tomalbrc.toms_mobs.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.base.ExtendedBehaviour;
import net.tslat.smartbrainlib.library.object.DynamicPositionTracker;
import net.tslat.smartbrainlib.library.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class SetEntityLookTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).noMemory(MemoryModuleType.LOOK_TARGET);
    protected LivingEntity target = null;

    public SetEntityLookTarget() {
    }

    protected void start(@NonNull E entity) {
        BrainUtil.setMemory(entity, MemoryModuleType.LOOK_TARGET, new DynamicPositionTracker(entity::position));
    }

    protected void stop(@NonNull E entity) {
        this.target = null;
    }

    @Override
    public @NonNull Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}