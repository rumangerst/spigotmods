/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import io.github.rumangerst.customitems.nbt.NBTAPI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class CustomItemsGiveGUI implements CommandExecutor, Listener
{
    private CustomItemsAPI api;
    private HashMap<Player, GiveInventory> player_inventories = new HashMap<>();
    
    public CustomItemsGiveGUI(CustomItemsAPI api)
    {
        this.api = api;
    }
    
    public void rebuildInventory()
    {
        player_inventories.clear();
    }
    
    private void updateInventory(Player player, String search)
    {
        GiveInventory inv = player_inventories.getOrDefault(player, null);
        
        if(inv == null || !inv.search_string.equals(search.toLowerCase()))
        {
            inv = new GiveInventory(search.toLowerCase());
            player_inventories.put(player, inv);
        }
    }
    
    private boolean isManagedInventory(Inventory inv)
    {
        for(GiveInventory i : player_inventories.values())
        {
            if(i.contains(inv))
                return true;
        }
        
        return false;
    }
    
    public void showTo(Player player, int index)
    {
        GiveInventory ginv = player_inventories.getOrDefault(player, null);
        
        if(ginv == null)
        {
            player.sendMessage("Nothing to show.");
            return;
        }
        
        ArrayList<Inventory> invs = ginv.inventories;
        
        if(invs.isEmpty())
        {
            player.sendMessage("Nothing to show.");
            return;
        }
        
        if(index >= 0)
            index = index % (invs.size());
        else
            index = (invs.size() + index);
        
        Inventory inv = invs.get(index);
        player.openInventory(inv);
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(cs instanceof Player)
        {
            Player player = (Player)cs;
            
            if(player.isOp())
            {
                String search;
                
                if(strings.length == 1)
                    search = strings[0];
                else
                    search = "";
                
                updateInventory(player, search);
                showTo(player, 0);
                
                return true;
            }
        }
        
        return false;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(event.isLeftClick() && isManagedInventory(event.getClickedInventory()))
        {
            Player player = (Player) event.getWhoClicked(); // The player that clicked the item
            ItemStack clicked = event.getCurrentItem(); // The item that was clicked
            Inventory inventory = event.getInventory(); // The inventory that was clicked in
            
            if(!player.isOp())
            {                
                player.closeInventory();
                event.setCancelled(true);
                return;
            }
            
            if(api.getCustomItem(clicked) != null)
            {
                player.getInventory().addItem(new ItemStack(clicked));
            }
            else
            {
                GiveInventory inv = player_inventories.getOrDefault(player, null);
                
                if(inv == null)
                    return;
                
                //Navigation items
                int index = inv.inventories.indexOf(inventory);
                
                if(NBTAPI.getString(clicked, "display/Name", "").equals("Next"))
                {
                    ++index;
                }
                else
                {
                    --index;
                }
                
                showTo(player, index);
            }
            
            event.setCancelled(true);
        }
    }
    
    private class GiveInventory
    {
        public ArrayList<Inventory> inventories = new ArrayList<>();
        public String search_string;
        
        public GiveInventory(String search)
        {
            this.search_string = search;

            Inventory inv = Bukkit.createInventory(null, 54, title(search, inventories.size() + 1));
            int current_count = 0;

            ArrayList<CustomItem> items = new ArrayList<>(api.items.values());
            items.sort(new Comparator<CustomItem>()
            {
                @Override
                public int compare(CustomItem o1, CustomItem o2)
                {
                    return o1.id.compareTo(o2.id);
                }

            });

            for (CustomItem item : items)
            {
                if (!item.getName().toLowerCase().contains(search) && !item.getId().toLowerCase().contains(search))
                {
                    continue;
                }

                if (current_count < 45)
                {
                    inv.addItem(item.make(1));
                    ++current_count;
                }
                else
                {
                    //Inventory full add navigation.
                    addNavigation(inv);
                    inventories.add(inv);

                    inv = Bukkit.createInventory(null, 54, title(search, inventories.size() + 1));
                    current_count = 0;
                }
            }

            if (current_count != 0)
            {
                addNavigation(inv);
                inventories.add(inv);
            }
        }
        
        public boolean contains(Inventory inv)
        {
            return inventories.contains(inv);
        }
        
        private String title(String search, int index)
        {
            if(search.isEmpty())
                return "Custom Items (Page " + index + ")";
            else
                return "Custom Items \"" + search + "\" (Page " + index + ")";  
        }
        
        private void addNavigation(Inventory inv)
        {
            ItemStack back = new ItemStack(Material.NETHER_STAR);
            ItemStack forward = new ItemStack(Material.NETHER_STAR);

            NBTAPI.setString(back, "display/Name", "Back");
            NBTAPI.setString(forward, "display/Name", "Next");

            inv.setItem(45, back);
            inv.setItem(53, forward);
        }
    }
    
}
