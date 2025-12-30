package aqario.fowlplay.common.entity.ai.brain.sensor;

import aqario.fowlplay.common.entity.bird.BirdEntity;
import aqario.fowlplay.common.util.BirdUtils;
import aqario.fowlplay.core.FowlPlayMemoryTypes;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AttackedSensor<E extends BirdEntity> extends PredicateSensor<DamageSource, E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ImmutableList.of(
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.AVOID_TARGET,
            FowlPlayMemoryTypes.SEES_FOOD.get(),
            FowlPlayMemoryTypes.CANNOT_PICKUP_FOOD.get()
    );

    public AttackedSensor() {
        super((damageSource, entity) -> true);
        this.setScanRate(bird -> 10);
    }

    public static <T extends BirdEntity> void onAttacked(T bird, LivingEntity attacker) {
        Brain<?> brain = bird.getBrain();
        BrainUtil.clearMemory(brain, FowlPlayMemoryTypes.SEES_FOOD.get());
        if (attacker instanceof Player player) {
            BrainUtil.setForgettableMemory(brain, FowlPlayMemoryTypes.CANNOT_PICKUP_FOOD.get(), true, BirdUtils.CANNOT_PICKUP_FOOD_TICKS);
            // TODO: trust?
//            if (bird instanceof TrustingBirdEntity trustingBird && trustingBird.trusts(player)) {
//                trustingBird.stopTrusting(player);
//            }
        }
        if (attacker.getType() != bird.getType() && !bird.shouldAttack(attacker)) {
            BirdUtils.alertOthers(bird, attacker);
        }
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends @NotNull ExtendedSensor<?>> type() {
        return FowlPlaySensorTypes.ATTACKED.get();
    }

    @Override
    protected void doTick(ServerLevel world, E bird) {
        Brain<?> brain = bird.getBrain();
        DamageSource damageSource = bird.getLastDamageSource();
        if (damageSource == null) {
            BrainUtil.clearMemory(brain, MemoryModuleType.HURT_BY);
            BrainUtil.clearMemory(brain, MemoryModuleType.HURT_BY_ENTITY);
            return;
        }
        if (this.predicate().test(damageSource, bird)) {
            BrainUtil.setMemory(brain, MemoryModuleType.HURT_BY, damageSource);

            if (damageSource.getEntity() instanceof LivingEntity attacker && attacker.isAlive() && attacker.level() == bird.level()) {
                BrainUtil.setMemory(brain, MemoryModuleType.HURT_BY_ENTITY, attacker);
                onAttacked(bird, attacker);
            }
            return;
        }
        BrainUtil.withMemory(brain, MemoryModuleType.HURT_BY_ENTITY, attacker -> {
            if (!attacker.isAlive() || attacker.level() != bird.level()) {
                BrainUtil.clearMemory(brain, MemoryModuleType.HURT_BY_ENTITY);
            }
        });
    }
}