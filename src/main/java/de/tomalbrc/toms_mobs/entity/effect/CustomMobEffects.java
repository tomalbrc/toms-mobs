package de.tomalbrc.toms_mobs.entity.effect;

import de.tomalbrc.toms_mobs.TomsMobs;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CustomMobEffects {
    public static final Holder<MobEffect> NAUTILUS_BREATH = register("nautilus_breath", new NautilusBreathEffect(MobEffectCategory.BENEFICIAL, 8954814).addAttributeModifier(Attributes.SUBMERGED_MINING_SPEED, ResourceLocation.fromNamespaceAndPath("toms_mobs", "nautilus_breath"), 1.0, AttributeModifier.Operation.ADD_VALUE));

    public static Holder<MobEffect> register(String string, MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.withDefaultNamespace(TomsMobs.MODID), mobEffect);
    }

    public static void init() {
    }
}

