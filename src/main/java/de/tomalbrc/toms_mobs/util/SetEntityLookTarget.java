package de.tomalbrc.toms_mobs.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.DynamicPositionTracker;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtil;

import java.util.List;

public class SetEntityLookTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).noMemory(MemoryModuleType.LOOK_TARGET);
    protected LivingEntity target = null;

    public SetEntityLookTarget() {
    }

    protected void start(E entity) {
        BrainUtil.setMemory(entity, MemoryModuleType.LOOK_TARGET, new DynamicPositionTracker(entity::position));
    }

    protected void stop(E entity) {
        this.target = null;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}