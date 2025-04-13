package com.github.tomo25neko.miniHardcore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class PlayerLocation implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        if (args.length < 1) {
            sender.sendMessage(Component.text("使用方法: /playerlocation <player>", NamedTextColor.RED));
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(Component.text("プレイヤーが見つかりません: " + targetName, NamedTextColor.RED));
            return;
        }

        Location loc = target.getLocation();
        String worldName = loc.getWorld() != null ? loc.getWorld().getName() : "不明";

        sender.sendMessage(
                Component.text("プレイヤー: ", NamedTextColor.AQUA)
                        .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" の現在地:"))
                        .appendNewline()
                        .append(Component.text(String.format("X:%.1f Y:%.1f Z:%.1f ワールド: %s",
                                loc.getX(), loc.getY(), loc.getZ(), worldName), NamedTextColor.GREEN))
        );
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {

        if(args.length == 0) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName).collect(Collectors.toList());
        }

        if (args.length == 1) {
            String partial = args[0].toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(partial))
                    .collect(Collectors.toList());
        }
        return BasicCommand.super.suggest(commandSourceStack, args);
    }
}
