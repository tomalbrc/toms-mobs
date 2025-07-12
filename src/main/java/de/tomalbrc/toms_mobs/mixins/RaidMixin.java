package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.toms_mobs.ModConfig;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public class RaidMixin {
    @Inject(method = "spawnGroup", at = @At("TAIL"))
    void spawnGroup(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci) {
        if (ModConfig.getInstance().noAdditionalRaidMobs) {
            return;
        }
        Raid raid = Raid.class.cast(this);
        // last wave
        if (raid.getGroupsSpawned() >= raid.getNumGroups(serverLevel.getDifficulty())) {
            Holder<Biome> biome = serverLevel.getBiome(blockPos);
            Raider mob;
            if (biome.is(BiomeTags.SPAWNS_SNOW_FOXES) && biome.is(BiomeTags.IS_MOUNTAIN)) {
                mob = MobRegistry.ICEOLOGER.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
            } else {
                mob = MobRegistry.SHOWMASTER.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
            }

            tomsmobs$spawn(serverLevel, raid.getGroupsSpawned(), mob, blockPos);
        }
    }

    @Unique
    public void tomsmobs$spawn(ServerLevel level, int wave, Raider raider, @Nullable BlockPos blockPos) {
        Raid raid = Raid.class.cast(this);
        boolean didAdd = raid.addWaveMob(level, wave, raider, true);
        if (didAdd) {
            raider.setCurrentRaid(raid);
            raider.setWave(wave);
            raider.setCanJoinRaid(true);
            raider.setTicksOutsideRaid(0);
            if (blockPos != null) {
                raider.setPos((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
                raider.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPos), EntitySpawnReason.EVENT, null);
                raider.applyRaidBuffs(level, wave, false);
                raider.setOnGround(true);
                level.addFreshEntity(raider);
            }
        }
    }
}