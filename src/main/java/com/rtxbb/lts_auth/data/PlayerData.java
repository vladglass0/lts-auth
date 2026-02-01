package com.rtxbb.lts_auth.data;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final String username;
    private String password;
    private final long registeredAt;

    public PlayerData(UUID uuid, String username, String password) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.registeredAt = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }
}
