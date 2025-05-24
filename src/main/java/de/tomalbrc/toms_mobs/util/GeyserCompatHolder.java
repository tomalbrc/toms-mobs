package de.tomalbrc.toms_mobs.util;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;

public class GeyserCompatHolder<T extends LivingEntity & AnimatedEntity> extends LivingEntityHolder<T> {
    private final String geyserId;

    public GeyserCompatHolder(T parent, Model model, String geyserId) {
        super(parent, model);
        this.geyserId = geyserId;
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        if (GeyserCompat.isGeyserLoaded()) GeyserCompat.setCustomEntity(player.player, this.parent.getId(), "modelengine:" + geyserId);
        return super.startWatching(player);
    }
}
