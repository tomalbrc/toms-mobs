package de.tomalbrc.toms_mobs.entity.passive.butterfly;

import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.toms_mobs.registry.MobRegistry;
import de.tomalbrc.toms_mobs.util.SimpleMovementRotatingHolder;
import de.tomalbrc.toms_mobs.util.Util;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class Butterfly extends AbstractButterfly {
    public static final ResourceLocation ID = Util.id("butterfly");
    public static final Model MODEL = Util.loadModel(ID);

    private int color;
    private String variant;

    private static final String[] variants = new String[]{
            "default",
            "1",
            "2"
    };

    public static boolean checkButterflySpawnRules(EntityType<? extends Mob> type, LevelAccessor level, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return level.canSeeSky(pos) && !level.getBlockState(pos.below()).getFluidState().is(FluidTags.WATER);
    }

    public Butterfly(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    protected void setupModel() {
        this.holder = new SimpleMovementRotatingHolder<>(this, MODEL);
        EntityAttachment.ofTicking(this.holder, this);

        this.setColor(Color.hslToRgb(this.getRandom().nextFloat(), 0.99f, 0.65f));
        this.setVariant(variants[this.random.nextInt(variants.length)]);
    }

    private void setColor(int color) {
        if (this.color == color) return;

        this.color = color;
        this.holder.setColor(color);
    }

    private void setVariant(String variant) {
        if (this.variant != null && this.variant.equals(variant)) return;

        this.variant = variant;
        this.holder.getVariantController().setVariant(variant);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        var special = level().random.nextInt(1000) == 42;
        if (special) {
            return MobRegistry.EMPEROR_BUTTERFLY.create(serverLevel, EntitySpawnReason.BREEDING);
        }

        return MobRegistry.BUTTERFLY.create(serverLevel, EntitySpawnReason.BREEDING);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        input.getInt("Color").ifPresent(this::setColor);
        input.getString("Variant").ifPresent(variant -> {
            if (this.containsVariant(variant))
                this.setVariant(variant);
        });
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.putInt("Color", this.color);
        output.putString("Variant", this.variant);
    }

    private boolean containsVariant(String target) {
        for (String variant : variants) {
            if (variant.equals(target)) {
                return true;
            }
        }
        return false;
    }

    static class Color {
        public static int hslToRgb(float h, float s, float l){
            float r, g, b;

            if (s == 0f) {
                r = g = b = l; // achromatic
            } else {
                float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
                float p = 2 * l - q;
                r = hueToRgb(p, q, h + 1f/3f);
                g = hueToRgb(p, q, h);
                b = hueToRgb(p, q, h - 1f/3f);
            }
            return to255(r) << 16 | to255(g) << 8 | to255(b);
        }
        public static int to255(float v) { return (int)Math.min(255,256*v); }
        /** Helper method that converts hue to rgb */
        public static float hueToRgb(float p, float q, float t) {
            if (t < 0f)
                t += 1f;
            if (t > 1f)
                t -= 1f;
            if (t < 1f/6f)
                return p + (q - p) * 6f * t;
            if (t < 1f/2f)
                return q;
            if (t < 2f/3f)
                return p + (q - p) * (2f/3f - t) * 6f;
            return p;
        }
    }


}