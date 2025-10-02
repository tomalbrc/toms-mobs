package de.tomalbrc.toms_mobs.entity.effect;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class NautilusBreathEffect extends MobEffect implements PolymerStatusEffect {
    protected NautilusBreathEffect(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public @Nullable ItemStack getPolymerIcon(MobEffect effect, ServerPlayer player) {
        return Items.NAUTILUS_SHELL.getDefaultInstance();
    }

    @Override
    public @Nullable MobEffect getPolymerReplacement(MobEffect potion, PacketContext context) {
        return MobEffects.CONDUIT_POWER.value();
    }
}
