package aqario.fowlplay.common.entity.ai.brain.sensor;

import aqario.fowlplay.core.FowlPlayMemoryTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.base.NearestVisibleEntityFilteredSensor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class NearbyAdultsSensor<E extends AgeableMob> extends NearestVisibleEntityFilteredSensor<E, List<? extends AgeableMob>> {
    @Override
    public SensorType<? extends @NotNull ExtendedSensor<?>> type() {
        return FowlPlaySensorTypes.NEARBY_ADULTS.get();
    }

    @Override
    protected MemoryModuleType<@NotNull List<? extends AgeableMob>> getMemory() {
        return FowlPlayMemoryTypes.NEAREST_VISIBLE_ADULTS.get();
    }

    @Override
    protected BiPredicate<E, LivingEntity> predicate() {
        return (target, self) -> target.getType() == self.getType() && !self.isBaby();
    }

    @Override
    protected List<? extends AgeableMob> findMatches(E self, NearestVisibleLivingEntities matcher) {
        List<AgeableMob> nearbyVisibleAdults = new ArrayList<>();
        matcher.find(target -> this.predicate().test(self, target))
                .forEach(target -> nearbyVisibleAdults.add((AgeableMob) target));
        return nearbyVisibleAdults;
    }
}