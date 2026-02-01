package com.rtxbb.lts_auth.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path STORAGE_DIR = FMLPaths.CONFIGDIR.get().resolve("lts_auth");
    private static final Path STORAGE_FILE = STORAGE_DIR.resolve("players.json");

    private Map<UUID, PlayerData> players = new HashMap<>();

    public PlayerStorage() {
        load();
    }

    public void load() {
        if (!Files.exists(STORAGE_FILE)) {
            players = new HashMap<>();
            return;
        }

        try (Reader reader = Files.newBufferedReader(STORAGE_FILE)) {
            Type type = new TypeToken<Map<UUID, PlayerData>>() {}.getType();
            players = GSON.fromJson(reader, type);
            if (players == null) {
                players = new HashMap<>();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load player data", e);
            players = new HashMap<>();
        }
    }

    public void save() {
        try {
            if (!Files.exists(STORAGE_DIR)) {
                Files.createDirectories(STORAGE_DIR);
            }

            try (Writer writer = Files.newBufferedWriter(STORAGE_FILE)) {
                GSON.toJson(players, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save player data", e);
        }
    }

    public PlayerData getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public void addPlayer(PlayerData player) {
        players.put(player.getUuid(), player);
        save();
    }

    public boolean playerExists(UUID uuid) {
        return players.containsKey(uuid);
    }
}
