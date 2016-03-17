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
    private HashMap<String, ArrayList<Inventory>> search_inventories = new HashMap<>();
    private ArrayList<Inventory> managed_inventories = new ArrayList<>();
    private HashMap<Player, String> player_search = new HashMap<>();
    private boolean rebuild = true;
    
    public CustomItemsGiveGUI(CustomItemsAPI api)
    {
        this.api = api;
    }
    
    public void rebuildInventory()
    {
        rebuild = true;
    }
    
    private void _addNavigation(Inventory inv)
    {
        ItemStack back = new ItemStack(Material.NETHER_STAR);
        ItemStack forward = new ItemStack(Material.NETHER_STAR);
        
        NBTAPI.setString(back, "display/Name", "Back");
        NBTAPI.setString(forward, "display/Name", "Next");
        
        inv.setItem(45, back);
        inv.setItem(53, forward);
    }
    
    private String _title(String search, int index)
    {
        if(search.isEmpty())
            return "Custom Items (Page " + index + ")";
        else
            return "Custom Items \"" + search + "\" (Page " + index + ")";  
    }
    
    private void _rebuildInventory(String search)
    {
        search = search.toLowerCase();
        
        if(search_inventories.containsKey(search))
        {
            managed_inventories.removeAll(search_inventories.get(search));            
            search_inventories.get(search).clear();
        }
        else
        {
            search_inventories.put(search, new ArrayList<>());
        }
        
        ArrayList<Inventory> inventories = search_inventories.get(search);
        
        Inventory inv = Bukkit.createInventory(null, 54, _title(search, inventories.size() + 1));        
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
        
        for(CustomItem item : items)
        {
            if(!item.getName().toLowerCase().contains(search) && !item.getId().toLowerCase().contains(search))
                continue;
            
            if(current_count < 45)
            {            
                inv.addItem(item.make(1));
                ++current_count;
            }
            else
            {
                //Inventory full add navigation.
                _addNavigation(inv);
                search_inventories.get(search).add(inv);
                managed_inventories.add(inv);
                
                inv = Bukkit.createInventory(null, 54, _title(search, inventories.size() + 1));
                current_count = 0;
            }
        }
        
        if(current_count != 0)
        {
            _addNavigation(inv);
            inventories.add(inv);
            managed_inventories.add(inv);
        }
    }
    
    public void showTo(Player player, int index)
    {
        String search = player_search.getOrDefault(player, "");
        
        if(rebuild)
            _rebuildInventory(search);
        
        if(search_inventories.isEmpty())
            return;
        
        ArrayList<Inventory> invs = search_inventories.get(search);
        
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
                if(strings.length == 1)
                {
                    player_search.put(player, strings[0]);
                }
                else
                {
                    player_search.remove(player);
                }
                
                showTo(player, 0);
                
                return true;
            }
        }
        
        return false;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(event.isLeftClick() && managed_inventories.contains(event.getClickedInventory()))
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
                String search = player_search.getOrDefault(player, "");
                
                //Navigation items
                int index = search_inventories.get(search).indexOf(inventory);
                
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
    
}
