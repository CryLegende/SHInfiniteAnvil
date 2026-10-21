package org.Cry_Legende.sHInfiniteAnvil.listeners;

import org.Cry_Legende.sHInfiniteAnvil.SHInfiniteAnvil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AnvilListener implements Listener {

    private final SHInfiniteAnvil plugin;

    public AnvilListener(SHInfiniteAnvil plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilUse(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }

        AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
        Location anvilLocation = anvilInventory.getLocation();

        // Si l'enclume n'a pas de location (peut arriver avec des plugins d'inventaire virtuel), on ignore
        if (anvilLocation == null || anvilLocation.getWorld() == null) {
            return;
        }

        // Vérifie si le monde de l'enclume est whitelisté
        if (!plugin.isWorldWhitelisted(anvilLocation.getWorld().getName())) {
            return;
        }

        // On s'intéresse uniquement au clic sur le slot de résultat
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        // L'item dans le slot de résultat
        ItemStack resultItem = event.getCurrentItem();
        if (resultItem == null || resultItem.getType() == Material.AIR) {
            return; // Pas d'item à prendre
        }

        // À ce stade, le joueur s'apprête à prendre l'item réparé/renommé
        // L'enclume vanilla va potentiellement se dégrader APRÈS cet événement.
        // On sauvegarde l'état actuel de l'enclume et on le restaure 1 tick plus tard.

        Block anvilBlock = anvilLocation.getBlock();
        // Vérifie que c'est bien une enclume (au cas où elle aurait été cassée entre-temps)
        if (!isAnvil(anvilBlock.getType())) {
            return;
        }

        final BlockData originalBlockData = anvilBlock.getBlockData().clone(); // Sauvegarde l'état EXACT

        new BukkitRunnable() {
            @Override
            public void run() {
                // On vérifie à nouveau que le bloc est toujours une enclume et au même endroit
                Block currentBlock = anvilLocation.getBlock();
                if (isAnvil(currentBlock.getType())) {
                    // Si l'état a changé (par exemple, de ANVIL à CHIPPED_ANVIL), on le restaure.
                    // Si l'état n'a pas changé, cette opération ne fait rien de visible.
                    currentBlock.setBlockData(originalBlockData, false); // false pour ne pas causer de physics update si pas nécessaire
                }
            }
        }.runTaskLater(plugin, 1L); // Exécuter 1 tick après que l'item ait été pris
    }

    private boolean isAnvil(Material material) {
        return material == Material.ANVIL || material == Material.CHIPPED_ANVIL || material == Material.DAMAGED_ANVIL;
    }
}