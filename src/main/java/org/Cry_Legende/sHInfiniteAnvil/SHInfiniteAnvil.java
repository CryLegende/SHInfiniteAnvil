package org.Cry_Legende.sHInfiniteAnvil;

import org.Cry_Legende.sHInfiniteAnvil.listeners.AnvilListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SHInfiniteAnvil extends JavaPlugin {

    private static SHInfiniteAnvil instance;
    private Set<String> whitelistedWorlds;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // Crée config.yml s'il n'existe pas
        loadConfigValues();

        Bukkit.getPluginManager().registerEvents(new AnvilListener(this), this);

        getLogger().info("SHInfiniteAnvil v" + getDescription().getVersion() + " has been enabled!");
        getLogger().info("Whitelisted worlds for infinite anvils: " + String.join(", ", whitelistedWorlds));
    }

    @Override
    public void onDisable() {
        getLogger().info("SHInfiniteAnvil has been disabled!");
        instance = null;
    }

    public void loadConfigValues() {
        reloadConfig(); // S'assure de lire la dernière version
        // getStringList ne renvoie jamais null : on teste la liste vide
        List<String> worlds = getConfig().getStringList("whitelisted-worlds");
        whitelistedWorlds = new HashSet<>(worlds);
        if (whitelistedWorlds.isEmpty()) {
            getLogger().warning("'whitelisted-worlds' list not found in config.yml or is empty. No worlds will have infinite anvils.");
        }
    }

    public boolean isWorldWhitelisted(String worldName) {
        return whitelistedWorlds.contains(worldName);
    }

    public static SHInfiniteAnvil getInstance() {
        return instance;
    }
}