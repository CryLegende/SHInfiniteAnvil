package org.Cry_Legende.sHInfiniteAnvil.listeners;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import org.Cry_Legende.sHInfiniteAnvil.SHInfiniteAnvil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.AnvilInventory;

public class AnvilListener implements Listener {

    private final SHInfiniteAnvil plugin;

    public AnvilListener(SHInfiniteAnvil plugin) {
        this.plugin = plugin;
    }

    // L'enclume vanilla s'use quand le joueur prend le résultat. On annule l'usure avant qu'elle
    // soit appliquée : l'enclume ne change plus d'état et ne se casse plus, y compris si elle
    // était sur le point de se briser.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilDamaged(AnvilDamagedEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        Location anvilLocation = anvilInventory.getLocation();

        // Si l'enclume n'a pas de location (inventaire virtuel), on ignore
        if (anvilLocation == null || anvilLocation.getWorld() == null) {
            return;
        }

        // Vérifie si le monde de l'enclume est whitelisté
        if (!plugin.isWorldWhitelisted(anvilLocation.getWorld().getName())) {
            return;
        }

        event.setCancelled(true);
    }
}
