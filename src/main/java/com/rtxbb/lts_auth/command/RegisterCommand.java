package com.rtxbb.lts_auth.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.rtxbb.lts_auth.manager.AuthManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RegisterCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("register")
            .then(Commands.argument("password", StringArgumentType.string())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String password = StringArgumentType.getString(context, "password");

                    if (AuthManager.getInstance().isRegistered(player.getUUID())) {
                        context.getSource().sendFailure(Component.literal("Ты уже зарегестрирован!"));
                        return 0;
                    }

                    if (password.length() < 4) {
                        context.getSource().sendFailure(Component.literal("Пароль должен содержать минимум 4 символа!"));
                        return 0;
                    }

                    AuthManager.getInstance().register(player, password);
                    return 1;
                })
            )
        );
    }
}
