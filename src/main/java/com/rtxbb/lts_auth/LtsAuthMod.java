package com.rtxbb.lts_auth;

import com.mojang.logging.LogUtils;
import com.rtxbb.lts_auth.command.LoginCommand;
import com.rtxbb.lts_auth.command.RegisterCommand;
import com.rtxbb.lts_auth.config.MessageManager;
import com.rtxbb.lts_auth.manager.AuthManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Mod(LtsAuthMod.MODID)
public class LtsAuthMod {
    public static final String MODID = "lts_auth";
        private static final Logger LOGGER = LogUtils.getLogger();
    private static final int KICK_TIMEOUT_TICKS = 60 * 20; // 60 seconds
    private static final MessageManager MESSAGE_MANAGER = new MessageManager(FMLPaths.CONFIGDIR.get().resolve("lts_auth").resolve("messages.yml"));
    private final Map<UUID, Integer> joinTicks = new HashMap<>();


        public LtsAuthMod() {
        MESSAGE_MANAGER.load();
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info(messages().get("messages.mod_initialized"));
    }

    public static MessageManager messages() {
        return MESSAGE_MANAGER;
    }


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RegisterCommand.register(event.getDispatcher());
        LoginCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AuthManager.getInstance().logout(player.getUUID());
            joinTicks.put(player.getUUID(), 0);
            
                        if (AuthManager.getInstance().isRegistered(player.getUUID())) {
                player.sendSystemMessage(Component.literal(messages().getColored("messages.login_prompt")));
            } else {
                player.sendSystemMessage(Component.literal(messages().getColored("messages.register_prompt")));
            }

        }
    }

    @SubscribeEvent
    public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        AuthManager.getInstance().logout(event.getEntity().getUUID());
        joinTicks.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (UUID uuid : new HashMap<>(joinTicks).keySet()) {
                if (!AuthManager.getInstance().isAuthenticated(uuid)) {
                    int ticks = joinTicks.get(uuid) + 1;
                    joinTicks.put(uuid, ticks);

                    ServerPlayer player = event.getServer().getPlayerList().getPlayer(uuid);
                    if (player != null) {
                        if (ticks % 100 == 0) { // Every 5 seconds
                                                        if (AuthManager.getInstance().isRegistered(uuid)) {
                                player.sendSystemMessage(Component.literal(messages().getColored("messages.login_reminder")), true);
                            } else {
                                player.sendSystemMessage(Component.literal(messages().getColored("messages.register_reminder")), true);
                            }

                        }

                                                if (ticks >= KICK_TIMEOUT_TICKS) {
                            player.connection.disconnect(Component.literal(messages().getColored("messages.timeout_kick")));
                        }

                    }
                } else {
                    joinTicks.remove(uuid);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerMove(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.player instanceof ServerPlayer player) {
            if (!AuthManager.getInstance().isAuthenticated(player.getUUID())) {
                player.teleportTo(player.getX(), player.getY(), player.getZ());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!AuthManager.getInstance().isAuthenticated(player.getUUID())) {
                event.setCanceled(true);
            }
        }
    }
}
