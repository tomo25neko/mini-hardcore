package com.github.tomo25neko.miniHardcore;


import com.github.tomo25neko.miniHardcore.commands.PlayerList;
import com.github.tomo25neko.miniHardcore.events.PlayerDeath;
import com.google.gson.Gson;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public final class Main extends JavaPlugin {

    //ファイル生成のための変数群
    private static final String DIRECTORY_NAME = "mini-hardcore";
    private static final String PLAYER_LIST_FILE_NAME = "players.json";
    private static final String LOCATION_FILE_NAME = "locations.json";
    private final File LIST_FILE = new File(getDataFolder(), PLAYER_LIST_FILE_NAME);
    private final File LOCATION_FILE = new File(DIRECTORY_NAME, LOCATION_FILE_NAME);

    private static final Gson gson = new Gson();
    private static Set<String> playerList = new HashSet<>();
    private static Set<String> locationList = new HashSet<>();

    private FileManager players;
    private FileManager locations;

    private PluginManager plManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        //フォルダ及びファイルの生成
        players = new FileManager(getDataFolder(), PLAYER_LIST_FILE_NAME);
        locations = new FileManager(getDataFolder(),LOCATION_FILE_NAME);

        plManager = getServer().getPluginManager();

        //イベント登録
        plManager.registerEvents(new PlayerDeath(players), this);

        //コマンド登録
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                 event -> event.registrar().register("msgplayerlist",new PlayerList(players))
        );

        getLogger().info("[mini-hardcore]:プラグインが起動しました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        players.save();
        locations.save();
        getLogger().info("[mini-hardcore]:プラグインが停止しました");
    }
}
