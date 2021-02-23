package nl.parrotlync.discovsuite.spigot.manager;

import nl.parrotlync.discovsuite.spigot.DiscovSuite;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {
    private final HashMap<UUID, ItemStack[]> inventories = new HashMap<>();

    public void giveBuildInventory(Player player) {
        inventories.put(player.getUniqueId(), player.getInventory().getContents());
        Inventory buildInventory = Bukkit.createInventory(player, InventoryType.PLAYER);
        HashMap<Integer, ItemStack> buildItems = getBuildItems();
        for (Integer key : buildItems.keySet()) {
            buildInventory.setItem(key, buildItems.get(key));
        }
        player.getInventory().setContents(buildInventory.getContents());
    }

    public void returnInventory(Player player) {
        if (inventories.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(inventories.get(player.getUniqueId()));
            inventories.remove(player.getUniqueId());
        }
    }

    public boolean hasInventory(Player player) {
        return inventories.containsKey(player.getUniqueId());
    }

    private HashMap<Integer, ItemStack> getBuildItems() {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        ConfigurationSection section = DiscovSuite.getInstance().getConfig().getConfigurationSection("build-inventory");
        for (String key : section.getKeys(false)) {
            Material material = Material.getMaterial(section.getString(key));
            if (material != null) {
                items.put(Integer.valueOf(key), new ItemStack(material, 1));
            }
        }
        return items;
    }
}
