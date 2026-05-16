package aqario.fowlplay.common.entity.ai.brain;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An extension of SmartBrainSchedule that optionally takes a supplier instead of the activity object
 */
public class ExtendedSchedule<BO extends LivingEntity & SmartBrainOwner<BO>, N extends Number & Comparable<N>> extends SmartBrainSchedule<BO, N> {

    protected ExtendedSchedule(Type<BO, N> type) {
        super(type);
    }
}