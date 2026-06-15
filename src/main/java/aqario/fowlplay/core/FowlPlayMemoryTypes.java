package aqario.fowlplay.core;

import aqario.fowlplay.common.entity.ai.brain.TeleportTarget;
import aqario.fowlplay.common.util.RememberedPositions;
import com.mojang.serialization.Codec;
import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FowlPlayMemoryTypes {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES =
            DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, TomsMobs.MODID);

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull List<? extends AgeableMob>>> NEAREST_VISIBLE_ADULTS = register("nearest_visible_adults");

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull Unit>> SEES_FOOD = register("sees_food", Unit.CODEC);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull Boolean>> CANNOT_PICKUP_FOOD = register("cannot_pickup_food", Codec.BOOL);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull Unit>> IS_AVOIDING = register("is_avoiding", Unit.CODEC);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull TeleportTarget>> TELEPORT_TARGET = register("teleport_target");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull UUID>> RECIPIENT = register("recipient", UUIDUtil.CODEC);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull RememberedPositions>> REMEMBERED_POSITIONS = register("remembered_positions", RememberedPositions.CODEC);

    private static <U> DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull U>> register(String id, Codec<U> codec) {
        return MEMORY_MODULE_TYPES.register(id, () -> new MemoryModuleType<>(Optional.of(codec)));
    }

    private static <U> DeferredHolder<MemoryModuleType<?>, MemoryModuleType<@NotNull U>> register(String id) {
        return MEMORY_MODULE_TYPES.register(id, () -> new MemoryModuleType<>(Optional.empty()));
    }
}