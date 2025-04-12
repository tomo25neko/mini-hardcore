package com.github.tomo25neko.miniHardcore.commands;

import com.github.tomo25neko.miniHardcore.file.LocationFileManager;
import com.github.tomo25neko.miniHardcore.file.LocationData;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class MemoryLocations implements BasicCommand {
    private final LocationFileManager locations;

    public MemoryLocations(LocationFileManager locations) {
        this.locations = locations;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (args.length < 1) {
            sender.sendMessage(Component.text("使用方法: /memorylocations <add | remove | list> [name]", NamedTextColor.RED));
            return;
        }

        String action = args[0].toLowerCase(Locale.ROOT);

        switch (action) {
            case "add": {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Component.text("このコマンドはプレイヤーのみ実行できます。", NamedTextColor.RED));
                    break;
                }
                if (args.length < 2) {
                    sender.sendMessage(Component.text("追加する地点名を指定してください。", NamedTextColor.RED)
                            .appendNewline()
                            .append(Component.text("/memorylocations add [name]", NamedTextColor.YELLOW))
                    );
                    break;
                }
                String nameToAdd = args[1];
                if (locations.containsLocation(nameToAdd)) {
                    sender.sendMessage(Component.text("その名前は既に存在します。", NamedTextColor.YELLOW));
                    break;
                }
                // 保存する座標とディメンションの取得
                int x = player.getLocation().getBlockX();
                int y = player.getLocation().getBlockY();
                int z = player.getLocation().getBlockZ();
                String world = player.getWorld().getName();

                // LocationData を作成して追加
                LocationData locationData = new LocationData(x, y, z, world);
                locations.addLocation(nameToAdd, locationData);
                sender.sendMessage(Component.text("地点["+ nameToAdd + "]を追加しました")
                        .appendNewline()
                        .append(Component.text(String.format("X:%d Y:%d Z:%d ディメンション:%s", x, y, z, world)))
                        .color(NamedTextColor.GREEN)
                );
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("削除する地点名を指定してください。", NamedTextColor.RED)
                            .appendNewline()
                            .append(Component.text("/memorylocations remove [name]", NamedTextColor.YELLOW))
                    );
                    break;
                }
                String nameToRemove = args[1];
                if (!locations.removeLocation(nameToRemove)) {
                    sender.sendMessage(Component.text("その地点は存在しません。", NamedTextColor.YELLOW));
                    break;
                }
                sender.sendMessage(Component.text("地点「" + nameToRemove + "」を削除しました。", NamedTextColor.GREEN));
                break;
            }
            case "list": {
                Map<String, LocationData> allLocations = locations.getLocationData();
                if (allLocations.isEmpty()) {
                    sender.sendMessage(Component.text("登録されたロケーションはありません。", NamedTextColor.YELLOW));
                    break;
                }
                String query = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "";

                List<Map.Entry<String, LocationData>> filtered = allLocations.entrySet().stream()
                        .filter(entry -> entry.getKey().toLowerCase(Locale.ROOT).contains(query))
                        .toList();

                if (filtered.isEmpty()) {
                    sender.sendMessage(Component.text("条件に一致するロケーションはありません。", NamedTextColor.YELLOW));
                    break;
                }

                sender.sendMessage(Component.text("=== ロケーション一覧 ===", NamedTextColor.AQUA));
                for (Map.Entry<String, LocationData> entry : filtered) {
                    String name = entry.getKey();
                    LocationData loc = entry.getValue();
                    sender.sendMessage(Component.text("- [" + name + "] ", Style.style(TextDecoration.BOLD, NamedTextColor.AQUA))
                            .append(Component.text(String.format("X:%d Y:%d Z:%d ディメンション:%s",
                                    loc.x, loc.y, loc.z, loc.dimention), NamedTextColor.WHITE)));
                }
                break;
            }
            default: {
                sender.sendMessage(Component.text("不明なサブコマンドです: " + action, NamedTextColor.RED)
                        .appendNewline()
                        .append(Component.text("使用方法: /memorylocations <add | remove | list> [name]", NamedTextColor.YELLOW)));
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String[] args) {
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
            if (sub.equals("remove")) {
                return locations.getLocationData().keySet().stream()
                        .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase()))
                        .toList();
            }

            if (sub.equals("list")) {
                return  locations.getLocationData().keySet().stream()
                        .filter(name -> name.toLowerCase(Locale.ROOT).contains(args[1]))
                        .toList();
            }
        }
        return BasicCommand.super.suggest(source, args);
    }
}
