package com.rtxbb.lts_auth.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.rtxbb.lts_auth.LtsAuthMod;
import com.rtxbb.lts_auth.manager.AuthManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


public class LoginCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("login")
            .then(Commands.argument("password", StringArgumentType.string())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String password = StringArgumentType.getString(context, "password");

                    if (!AuthManager.getInstance().isRegistered(player.getUUID())) {
                        context.getSource().sendFailure(Component.literal(LtsAuthMod.messages().getColored("messages.not_registered")));
                        return 0;
                    }

                    if (AuthManager.getInstance().isAuthenticated(player.getUUID())) {
                        context.getSource().sendFailure(Component.literal(LtsAuthMod.messages().getColored("messages.already_logged_in")));
                        return 0;
                    }

                    if (AuthManager.getInstance().login(player, password)) {
                        return 1;
                    } else {
                        context.getSource().sendFailure(Component.literal(LtsAuthMod.messages().getColored("messages.invalid_password")));
                        return 0;
                    }
                })
            )
        );
    }
}
