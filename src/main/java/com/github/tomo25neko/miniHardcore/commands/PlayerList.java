package com.github.tomo25neko.miniHardcore.commands;


import com.github.tomo25neko.miniHardcore.FileManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;



public class PlayerList implements BasicCommand {
    private final FileManager players;

    public  PlayerList(FileManager players) {
        this.players = players;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("mini-hardcore.use")) {
            sender.sendMessage(Component.text("権限がありません。"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("使用方法: /msgplayerlist <add | remove> <player>"));
            return;
        }

        String action = args[0];
        String playerName = args[1];

        switch (action.toLowerCase()) {
            case "add":
                if (!players.add(playerName)) {
                    sender.sendMessage(Component.text(playerName + " はすでに登録されています。"));
                } else {
                    sender.sendMessage(Component.text(playerName + " を追加しました。"));
                }
                break;
            case "remove":
                if (!players.remove(playerName)) {
                    sender.sendMessage(Component.text(playerName + " は登録されていません。"));
                } else {
                    sender.sendMessage(Component.text(playerName + " を削除しました。"));
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
            return List.of("add", "remove");
        }
        //第一引数は追加か削除かの選択
        if(args[0].length() == 1) {
            return Stream.of("add","remove")
                    .filter(string -> string.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        //第二引数はプレイヤー名
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName) // プレイヤー名を取得
                    .filter(name -> name.startsWith(args[1])) // 部分一致検索
                    .toList();
        }
        return BasicCommand.super.suggest(commandSourceStack, args);
    }


}
