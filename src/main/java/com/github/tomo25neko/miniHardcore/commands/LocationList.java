package com.github.tomo25neko.miniHardcore.commands;

import com.github.tomo25neko.miniHardcore.FileManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Stream;

public class LocationList implements BasicCommand {
    private final FileManager locations;

    public LocationList(FileManager locations) {
        this.locations = locations;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        if (args.length < 1) {
            sender.sendMessage(Component.text("使用方法: /locationlist <add|remove|list> [name]", NamedTextColor.RED));
            return;
        }

        String action = args[0].toLowerCase(Locale.ROOT);

        switch (action) {
            case "add" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("追加するロケーション名を指定してください。", NamedTextColor.RED));
                    return;
                }

                String nameToAdd = args[1];
                if (locations.contains(nameToAdd)) {
                    sender.sendMessage(Component.text("その名前は既に存在します。", NamedTextColor.YELLOW));
                    return;
                }

                locations.add(nameToAdd); // 仮：実装してあると仮定
                sender.sendMessage(Component.text("ロケーション「" + nameToAdd + "」を追加しました。", NamedTextColor.GREEN));
            }

            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("削除するロケーション名を指定してください。", NamedTextColor.RED));
                    return;
                }

                String nameToRemove = args[1];
                if (!locations.contains(nameToRemove)) {
                    sender.sendMessage(Component.text("そのロケーションは存在しません。", NamedTextColor.YELLOW));
                    return;
                }

                locations.remove(nameToRemove); // 仮：実装してあると仮定
                sender.sendMessage(Component.text("ロケーション「" + nameToRemove + "」を削除しました。", NamedTextColor.GREEN));
            }

            case "list" -> {
                List<String> all = locations.getData().stream().toList(); // 仮：全ロケーション名のリストを返す
                if (all.isEmpty()) {
                    sender.sendMessage(Component.text("登録されたロケーションはありません。", NamedTextColor.GRAY));
                    return;
                }

                String query = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "";
                List<String> filtered = all.stream()
                        .filter(loc -> loc.toLowerCase(Locale.ROOT).contains(query))
                        .toList();

                if (filtered.isEmpty()) {
                    sender.sendMessage(Component.text("条件に一致するロケーションはありません。", NamedTextColor.GRAY));
                    return;
                }

                sender.sendMessage(Component.text("=== ロケーション一覧 ===", NamedTextColor.AQUA));
                for (String loc : filtered) {
                    sender.sendMessage(Component.text("- " + loc, NamedTextColor.WHITE));
                }
            }

            default -> {
                sender.sendMessage(Component.text("不明なサブコマンドです: " + action, NamedTextColor.RED));
            }
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 0) {
            return List.of("add", "remove", "list");
        }

        if (args.length == 1) {
            return Stream.of("add", "remove", "list")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();

            if (sub.equals("remove") || sub.equals("list")) {
                return locations.getData().stream()
                        .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase()))
                        .toList();
            }

            // "add" の場合は補完なし
        }

        return BasicCommand.super.suggest(source, args);
    }
}
