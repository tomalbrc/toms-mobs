package aqario.fowlplay.common.entity.ai.brain.sensor;

import de.tomalbrc.toms_mobs.util.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class FowlPlaySensorTypes {
    public static final Supplier<SensorType<@NotNull NearbyAdultsSensor<?>>> NEARBY_ADULTS = register("nearby_adults",
            NearbyAdultsSensor::new
    );
    public static final Supplier<SensorType<@NotNull AttackedSensor<?>>> ATTACKED = register("attacked",
            AttackedSensor::new
    );
    public static final Supplier<SensorType<@NotNull AvoidTargetSensor<?>>> AVOID_TARGETS = register("avoid_targets",
            AvoidTargetSensor::new
    );
    public static final Supplier<SensorType<@NotNull AttackTargetSensor<?>>> ATTACK_TARGETS = register("attack_targets",
            AttackTargetSensor::new
    );
//    public static final Supplier<SensorType<PigeonSpecificSensor>> PIGEON_SPECIFIC_SENSOR = register("pigeon_specific_sensor",
//        PigeonSpecificSensor::new
//    );

    private static <U extends Sensor<?>> Supplier<SensorType<@NotNull U>> register(String id, Supplier<U> factory) {
        return registerSensorType(id, () -> new SensorType<>(factory));
    }

    public static <T extends Sensor<?>> Supplier<SensorType<@NotNull T>> registerSensorType(String id, Supplier<SensorType<@NotNull T>> sensorType) {
        SensorType<@NotNull T> registry = Registry.register(BuiltInRegistries.SENSOR_TYPE, Util.id(id), sensorType.get());
        return () -> registry;
    }

    public static void init() {
    }
}