package com.github.tomo25neko.miniHardcore;


import com.github.tomo25neko.miniHardcore.commands.PlayerList;
import com.github.tomo25neko.miniHardcore.commands.PlayerLocation;
import com.github.tomo25neko.miniHardcore.events.PlayerDeath;
import com.github.tomo25neko.miniHardcore.file.LocationFileManager;
import com.github.tomo25neko.miniHardcore.file.PlayerFileManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    //ファイル生成のための変数群
    private static final String PLAYER_LIST_FILE_NAME = "players.json";
    private static final String LOCATION_FILE_NAME = "locations.json";


    private PlayerFileManager players;
    private LocationFileManager locations;

    private PluginManager plManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        //フォルダ及びファイルの生成
        players = new PlayerFileManager(getDataFolder(), PLAYER_LIST_FILE_NAME);
        locations = new LocationFileManager(getDataFolder(), LOCATION_FILE_NAME);

        plManager = getServer().getPluginManager();

        //イベント登録
        plManager.registerEvents(new PlayerDeath(players), this);

        //コマンド登録
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                 event -> event.registrar().register("playerlist",new PlayerList(players))
        );
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register("playerlocation",new PlayerLocation())
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
