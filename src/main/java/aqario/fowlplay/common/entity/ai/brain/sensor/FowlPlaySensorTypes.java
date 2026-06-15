package aqario.fowlplay.common.entity.ai.brain.sensor;

import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class FowlPlaySensorTypes {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, TomsMobs.MODID);

    public static final DeferredHolder<SensorType<?>, SensorType<@NotNull NearbyAdultsSensor<?>>> NEARBY_ADULTS = register("nearby_adults", NearbyAdultsSensor::new);
    public static final DeferredHolder<SensorType<?>, SensorType<@NotNull AttackedSensor<?>>> ATTACKED = register("attacked", AttackedSensor::new);
    public static final DeferredHolder<SensorType<?>, SensorType<@NotNull AvoidTargetSensor<?>>> AVOID_TARGETS = register("avoid_targets", AvoidTargetSensor::new);
    public static final DeferredHolder<SensorType<?>, SensorType<@NotNull AttackTargetSensor<?>>> ATTACK_TARGETS = register("attack_targets", AttackTargetSensor::new);

    private static <U extends Sensor<?>> DeferredHolder<SensorType<?>, SensorType<@NotNull U>> register(String id, Supplier<U> factory) {
        return SENSOR_TYPES.register(id, () -> new SensorType<>(factory));
    }
}