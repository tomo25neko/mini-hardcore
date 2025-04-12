package com.github.tomo25neko.miniHardcore.commands;


import com.github.tomo25neko.miniHardcore.file.PlayerFileManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;



public class PlayerList implements BasicCommand {
    private final PlayerFileManager players;

    public  PlayerList(PlayerFileManager players) {
        this.players = players;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("mini-hardcore.use")) {
            sender.sendMessage(Component.text("権限がありません。",NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("使用方法: /playerlist <add | remove> <player>", NamedTextColor.YELLOW));
            return;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];

        switch (action) {
            case "add":
                if (!players.addPlayer(playerName)) {
                    sender.sendMessage(Component.text(playerName + " はすでに登録されています。",NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text(playerName + " を追加しました。",NamedTextColor.GREEN));
                }
                break;
            case "remove":
                if (!players.removePlayer(playerName)) {
                    sender.sendMessage(Component.text(playerName + " は登録されていません。",NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text(playerName + " を削除しました。",NamedTextColor.GREEN));
                }
                break;
            default:
                sender.sendMessage(Component.text(("無効なコマンドです。使用できるコマンドは 'add' または 'remove' です。")));
                break;
        }


    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {


        if (args.length == 0) {
            return List.of("add","remove");
        }

        //第一引数は追加か削除かの選択
        if(args.length == 1) {
            return Stream.of("add","remove")
                    .filter(string -> string.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        //第二引数はプレイヤー名
        if (args.length == 2) {
            if (args[0].equals("add")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName) // プレイヤー名を取得
                        .filter(name -> name.toLowerCase().startsWith(args[1])) // 部分一致検索
                        .toList();
            }

            if(args[0].equals("remove")){
                return players.getPlayerData().stream()
                        .filter(name -> name.toLowerCase().startsWith(args[1])) // 部分一致検索
                        .toList();
            }
        }
        return BasicCommand.super.suggest(commandSourceStack, args);
    }


}
