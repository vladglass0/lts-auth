package com.rtxbb.lts_auth.manager;

import com.rtxbb.lts_auth.data.PlayerData;
import com.rtxbb.lts_auth.data.PlayerStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthManager {
    private static AuthManager instance;
    private final PlayerStorage storage;
    private final Set<UUID> authenticatedPlayers = new HashSet<>();

    private AuthManager() {
        this.storage = new PlayerStorage();
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public boolean isRegistered(UUID uuid) {
        return storage.playerExists(uuid);
    }

    public boolean isAuthenticated(UUID uuid) {
        return authenticatedPlayers.contains(uuid);
    }

    public void register(ServerPlayer player, String password) {
        PlayerData data = new PlayerData(player.getUUID(), player.getGameProfile().getName(), password);
        storage.addPlayer(data);
        authenticatedPlayers.add(player.getUUID());
        player.sendSystemMessage(Component.literal("§aУспешная регистрация и вход!"));
    }

    public boolean login(ServerPlayer player, String password) {
        PlayerData data = storage.getPlayer(player.getUUID());
        if (data != null && data.getPassword().equals(password)) {
            authenticatedPlayers.add(player.getUUID());
            player.sendSystemMessage(Component.literal("§aУспешный вход!"));
            return true;
        }
        return false;
    }

    public void logout(UUID uuid) {
        authenticatedPlayers.remove(uuid);
    }
}
