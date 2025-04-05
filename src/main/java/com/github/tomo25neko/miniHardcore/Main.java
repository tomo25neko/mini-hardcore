package com.github.tomo25neko.miniHardcore;


import com.github.tomo25neko.miniHardcore.commands.PlayerList;
import com.github.tomo25neko.miniHardcore.events.PlayerDeath;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public final class Main extends JavaPlugin {

    //ファイル生成のための変数群
    private static final String DIRECTORY_NAME = "mini-hardcore";
    private static final String FILE_NAME = "players.json";
    private final File FILE = new File(getDataFolder(), FILE_NAME);

    private static final Gson gson = new Gson();
    private static Set<String> playerList = new HashSet<>();

    private PluginManager plManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        //フォルダ及びファイルの生成
        createFile();

        plManager = getServer().getPluginManager();

        //イベント登録
        plManager.registerEvents(new PlayerDeath(), this);

        //コマンド登録
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                 event -> event.registrar().register("msgplayerlist",new PlayerList())
        );

        getLogger().info("[mini-hardcore]:プラグインが起動しました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        savePlayersToFile();
        getLogger().info("[mini-hardcore]:プラグインが停止しました");
    }

    private void createFile() {
        try {
            File directory = new File(getDataFolder().getParentFile(),DIRECTORY_NAME);
            if (!directory.exists()) {
                directory.mkdir();//フォルダ生成
            }

            if (!FILE.exists()) {
                try (FileWriter writer = new FileWriter(FILE, StandardCharsets.UTF_8)) {
                    gson.toJson(playerList, writer); // 空の JSON を作成
                }
                return;//新規作成時はここで終了
            }

            // JSONファイルを読み込み
            try (FileReader reader = new FileReader(FILE, StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Set<String>>() {}.getType();
                playerList = gson.fromJson(reader, type);//読み込み
                if (playerList == null) {
                    playerList = new HashSet<>();//nullなら空を入れる
                }
            }

        } catch (Exception e) {
            getLogger().severe("Failed to load players.json: " + e.getMessage());
        }

    }

    //playerListに渡されたプレイヤーネームが含まれるならtrue
    public static boolean isPlayerContainList(String playerName) {
        return playerList.contains(playerName);
    }

    public static boolean addPlayerToList(String playerName) {
        return playerList.add(playerName);
    }

    public static boolean removePlayerFromList(String playerName) {
        return playerList.remove(playerName);
    }

    /**
     * プレイヤーリストをJSONに保存
     */
    private void savePlayersToFile() {
        try (FileWriter writer = new FileWriter(FILE, StandardCharsets.UTF_8)) {
            gson.toJson(playerList, writer);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save players.json: " + e.getMessage());
        }
    }
}
