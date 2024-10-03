package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.toms_mobs.registries.MobRegistry;
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
    void spawnGroup(BlockPos blockPos, CallbackInfo ci){
        Raid raid = Raid.class.cast(this);
        // last wave
        if (raid.getGroupsSpawned() >= raid.getNumGroups(raid.getLevel().getDifficulty())) {
            Holder<Biome> biome = raid.getLevel().getBiome(blockPos);
            Raider mob;
            if (biome.is(BiomeTags.SPAWNS_SNOW_FOXES) && biome.is(BiomeTags.IS_MOUNTAIN)) {
                mob = MobRegistry.SHOWMASTER.create(raid.getLevel(), EntitySpawnReason.MOB_SUMMONED);
            } else {
                mob = MobRegistry.ICEOLOGER.create(raid.getLevel(), EntitySpawnReason.MOB_SUMMONED);
            }

            tomsmobs$spawn(raid.getGroupsSpawned(), mob, blockPos);
        }
    }

    @Unique
    public void tomsmobs$spawn(int wave, Raider raider, @Nullable BlockPos blockPos) {
        Raid raid = Raid.class.cast(this);
        boolean didAdd = raid.addWaveMob(wave, raider, true);
        if (didAdd) {
            raider.setCurrentRaid(raid);
            raider.setWave(wave);
            raider.setCanJoinRaid(true);
            raider.setTicksOutsideRaid(0);
            if (blockPos != null) {
                raider.setPos((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
                raider.finalizeSpawn((ServerLevel) raid.getLevel(), raid.getLevel().getCurrentDifficultyAt(blockPos), EntitySpawnReason.EVENT, null);
                raider.applyRaidBuffs((ServerLevel) raid.getLevel(), wave, false);
                raider.setOnGround(true);
                raid.getLevel().addFreshEntity(raider);
            }
        }
    }
}