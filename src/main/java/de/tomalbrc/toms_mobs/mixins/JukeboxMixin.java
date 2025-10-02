package de.tomalbrc.toms_mobs.mixins;

import de.tomalbrc.bil.api.AnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(JukeboxSongPlayer.class)
public class JukeboxMixin {
    @Shadow @Final private BlockPos blockPos;

    @Inject(method = "play", at = @At("HEAD"))
    private void tm$partyModeStart(LevelAccessor levelAccessor, Holder<JukeboxSong> holder, CallbackInfo ci) {
        List<LivingEntity> list = levelAccessor.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos).inflate(3.0));
        for (LivingEntity livingEntity : list) {
            if (livingEntity instanceof AnimatedEntity) livingEntity.setRecordPlayingNearby(blockPos, true);
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void tm$partyModeStop(LevelAccessor levelAccessor, BlockState blockState, CallbackInfo ci) {
        List<LivingEntity> list = levelAccessor.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos).inflate(3.0));
        for (LivingEntity livingEntity : list) {
            if (livingEntity instanceof AnimatedEntity) livingEntity.setRecordPlayingNearby(blockPos, false);
        }
    }
}
