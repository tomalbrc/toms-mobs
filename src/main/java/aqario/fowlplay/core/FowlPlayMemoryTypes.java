package aqario.fowlplay.core;

import aqario.fowlplay.common.entity.ai.brain.TeleportTarget;
import aqario.fowlplay.common.util.RememberedPositions;
import com.mojang.serialization.Codec;
import de.tomalbrc.toms_mobs.util.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public final class FowlPlayMemoryTypes {
    public static final Supplier<MemoryModuleType<@NotNull List<? extends AgeableMob>>> NEAREST_VISIBLE_ADULTS = register("nearest_visible_adults");
    public static final Supplier<MemoryModuleType<@NotNull Unit>> SEES_FOOD = register("sees_food", Unit.CODEC);
    public static final Supplier<MemoryModuleType<@NotNull Boolean>> CANNOT_PICKUP_FOOD = register("cannot_pickup_food", Codec.BOOL);
    public static final Supplier<MemoryModuleType<@NotNull Unit>> IS_AVOIDING = register("is_avoiding", Unit.CODEC);
    public static final Supplier<MemoryModuleType<@NotNull TeleportTarget>> TELEPORT_TARGET = register("teleport_target");
    public static final Supplier<MemoryModuleType<@NotNull UUID>> RECIPIENT = register("recipient", UUIDUtil.CODEC);
    public static final Supplier<MemoryModuleType<@NotNull RememberedPositions>> REMEMBERED_POSITIONS = register("remembered_positions", RememberedPositions.CODEC);

    private static <U> Supplier<MemoryModuleType<@NotNull U>> register(String id, Codec<U> codec) {
        return registerMemoryModuleType(id, () -> new MemoryModuleType<>(Optional.of(codec)));
    }

    private static <U> Supplier<MemoryModuleType<@NotNull U>> register(String id) {
        return registerMemoryModuleType(id, () -> new MemoryModuleType<>(Optional.empty()));
    }

    public static <T> Supplier<MemoryModuleType<@NotNull T>> registerMemoryModuleType(String id, Supplier<MemoryModuleType<@NotNull T>> memoryModuleType) {
        MemoryModuleType<@NotNull T> registry = Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, Util.id(id), memoryModuleType.get());
        return () -> registry;
    }

    public static void init() {
    }
}