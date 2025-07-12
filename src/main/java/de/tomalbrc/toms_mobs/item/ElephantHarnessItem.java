package de.tomalbrc.toms_mobs.item;

import de.tomalbrc.toms_mobs.registry.MobRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.Equippable;

public class ElephantHarnessItem extends TexturedPolymerItem {
    private final String variant;

    public ElephantHarnessItem(Properties settings, ResourceLocation modelPath, String variant) {
        super(settings.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.SADDLE).setAllowedEntities(MobRegistry.ELEPHANT).setCanBeSheared(true).setDispensable(true).setEquipOnInteract(true).build()), modelPath);
        this.variant = variant;
    }

    public String getVariant() {
        return this.variant;
    }
}
