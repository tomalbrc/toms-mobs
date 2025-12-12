package de.tomalbrc.toms_mobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tomalbrc.bil.json.SimpleCodecDeserializer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class ModConfig {
    private static final Path CONFIG_FILE_PATH = FabricLoader.getInstance().getConfigDir().resolve(TomsMobs.MODID + ".json");
    private static ModConfig instance;

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Identifier.class, new SimpleCodecDeserializer<>(Identifier.CODEC))
            .setPrettyPrinting()
            .create();

    // entries

    public List<Identifier> disabledMobs = new ObjectArrayList<>();
    public boolean noAdditionalRaidMobs = true;

    public static ModConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (!CONFIG_FILE_PATH.toFile().exists()) {
            instance = new ModConfig();
            try {
                if (CONFIG_FILE_PATH.toFile().createNewFile()) {
                    FileOutputStream stream = new FileOutputStream(CONFIG_FILE_PATH.toFile());
                    stream.write(gson.toJson(instance).getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try {
            ModConfig.instance = gson.fromJson(new FileReader(ModConfig.CONFIG_FILE_PATH.toFile()), ModConfig.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}