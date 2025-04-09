package com.github.tomo25neko.miniHardcore.events;

import com.github.tomo25neko.miniHardcore.FileManager;
import com.github.tomo25neko.miniHardcore.Main;
import com.github.tomo25neko.miniHardcore.commands.PlayerList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/*
コメントアウトしているコードはデバッグ出力用のコードです
 */

public class PlayerDeath implements Listener {

    private final int LOSS_CHANCE = 4; // 40%の確率で発動（0〜9の乱数で4以下）
    private final Random RANDOM = new Random();
    private final int baseChance = 30; // 基本削除確率 30%

    private final FileManager players;

    public PlayerDeath(FileManager players) {
        this.players = players;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String name = event.getPlayer().getName();

        // 死亡メッセージ変更
        if (players.contains(name)) {
            event.deathMessage(Component.text("おぉ勇者「")
                    .append(Component.text(name, Style.style(NamedTextColor.AQUA, TextDecoration.BOLD)))
                    .append(Component.text("」よ死んでしまうとは情けないｗｗ"))
                    .color(NamedTextColor.YELLOW));
        }

        int roll = RANDOM.nextInt(10); // 0〜9
//        Bukkit.broadcast(Component.text("アイテム損失判定: ロール = " + roll, NamedTextColor.GRAY));

        // 確率がLOSS_CHANCE以下ならアイテム削除処理へ
        if (roll <= LOSS_CHANCE) {
            if (removeDrops(event)) {
                Bukkit.broadcast(
                        Component.text("===[mini-hardcore]===",NamedTextColor.AQUA,TextDecoration.BOLD)
                        .append(Component.newline())
                        .append(Component.text(name + " さんのアイテムの一部が不幸にも消えてしまいました…", NamedTextColor.YELLOW))
                );
            }
        }
    }

    private boolean removeDrops(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops().stream()
                .filter(item -> item.getType() != Material.AIR && item.getAmount() > 0)
                .collect(Collectors.toList());

        if (drops.isEmpty()) {
//            Bukkit.broadcast(Component.text("削除対象のアイテムがありません。", NamedTextColor.GRAY));
            return false;
        }

        int removeCount = drops.size() / 3; // 空でないドロップの1/3
//        Bukkit.broadcast(Component.text("ドロップ数: " + drops.size() + " → 削除予定: " + removeCount, NamedTextColor.GRAY));


        while (removeCount > 0 && !drops.isEmpty()) {
            Collections.shuffle(drops);
//            Bukkit.broadcast(Component.text("ドロップアイテムをシャッフルしました。", NamedTextColor.GRAY));

            Iterator<ItemStack> iterator = drops.iterator();
            while (iterator.hasNext() && removeCount > 0) {
                ItemStack item = iterator.next();
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                // エンチャント数に応じて確率上昇
                int enchantCount = meta.getEnchants().size();
                int enchantBonus = enchantCount * 5;
                int totalChance = Math.min(100, baseChance + enchantBonus); // 最大100%

//                // デバッグ出力
//                Bukkit.broadcast(Component.text(
//                        "対象: " + item.getType() +
//                                " | エンチャント数: " + enchantCount +
//                                " | 確率: " + totalChance + "%",
//                        NamedTextColor.GRAY));

                // 削除判定
                if (RANDOM.nextInt(100) < totalChance) {
                    int removedAmount = Math.min(item.getAmount(), item.getMaxStackSize());
                    item.setAmount(item.getAmount() - removedAmount);
                    if (item.getAmount() <= 0) iterator.remove();

                    removeCount--;
//                    Bukkit.broadcast(Component.text(
//                            "削除: " + item.getType() + " を " + removedAmount + " 個（残: " + removeCount + "）",
//                            NamedTextColor.RED));
                }
            }
        }

//        Bukkit.broadcast(Component.text("最終的に削除されたスタック数: " + removedStacks, NamedTextColor.GRAY));
        return true;
    }
}