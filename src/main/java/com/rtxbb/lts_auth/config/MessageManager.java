package com.rtxbb.lts_auth.config;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {
    private final Path filePath;
    private Map<String, String> messages = new HashMap<>();

    public MessageManager(Path filePath) {
        this.filePath = filePath;
    }

    public void load() {
        createDefaultFileIfMissing();

        try {
            messages = parseYamlLikeFile(Files.readAllLines(filePath));
        } catch (IOException e) {
            messages = new HashMap<>();
        }
    }

    public String get(String key) {
        return messages.getOrDefault(key, key);
    }

    public String getColored(String key) {
        return get(key).replace("&", "§");
    }

    private void createDefaultFileIfMissing() {
        if (Files.exists(filePath)) {
            return;
        }

        try {
            Files.createDirectories(filePath.getParent());
            try (Writer writer = Files.newBufferedWriter(filePath)) {
                writer.write(defaultMessages());
            }
        } catch (IOException ignored) {
        }
    }

    private Map<String, String> parseYamlLikeFile(List<String> lines) {
        Map<String, String> result = new HashMap<>();
        String currentSection = "";

        for (String rawLine : lines) {
            String line = rawLine.replace("\t", "    ");
            String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            if (!line.startsWith(" ") && trimmed.endsWith(":")) {
                currentSection = trimmed.substring(0, trimmed.length() - 1).trim();
                continue;
            }

            if (line.startsWith("  ")) {
                int colonIndex = trimmed.indexOf(':');
                if (colonIndex <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, colonIndex).trim();
                String value = trimmed.substring(colonIndex + 1).trim();

                if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }

                value = value
                    .replace("\\\"", "\"")
                    .replace("\\'", "'")
                    .replace("\\n", "\n");

                String fullKey = currentSection.isEmpty() ? key : currentSection + "." + key;
                result.put(fullKey, value);
            }
        }

        return result;
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(messages);
    }

    private String defaultMessages() {
        return """
messages:
  mod_initialized: "&aLTS Auth Mod initialized!"
  register_success: "&aRegistration successful. You are now logged in."
  login_success: "&aLogin successful."
  login_prompt: "&6Please log in using: /login <password>"
  register_prompt: "&6Please register using: /register <password>"
  login_reminder: "&cPlease log in using: /login <password>"
  register_reminder: "&cPlease register using: /register <password>"
  timeout_kick: "Authentication time has expired!"
  already_registered: "&cYou are already registered!"
  password_too_short: "&cPassword must contain at least 4 characters!"
  not_registered: "&cYou are not registered! Use /register <password>"
  already_logged_in: "&cYou are already logged in!"
  invalid_password: "&cInvalid password!"
  storage_load_failed: "Failed to load player data"
  storage_save_failed: "Failed to save player data"
""";
    }
}