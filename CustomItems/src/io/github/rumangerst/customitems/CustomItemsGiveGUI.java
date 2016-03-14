/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import io.github.rumangerst.customitems.nbt.NBTAPI;
import java.util.ArrayList;
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
    private ArrayList<Inventory> inventories = new ArrayList<>();
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
    
    private void _rebuildInventory()
    {
        inventories.clear();
        
        Inventory inv = Bukkit.createInventory(null, 54, "Custom Items API (" + (inventories.size() + 1) + ")");        
        int current_count = 0;
        
        for(CustomItem item : api.items.values())
        {
            if(current_count < 45)
            {            
                inv.addItem(item.make(1));
                ++current_count;
            }
            else
            {
                //Inventory full add navigation.
                _addNavigation(inv);
                inventories.add(inv);
                
                inv = Bukkit.createInventory(null, 54, "Custom Items API (" + (inventories.size() + 1) + ")");
                current_count = 0;
            }
        }
        
        if(current_count != 0)
        {
            _addNavigation(inv);
            inventories.add(inv);
        }
    }
    
    public void showTo(Player player, int index)
    {
        if(rebuild)
            _rebuildInventory();
        
        if(inventories.isEmpty())
            return;
        
        if(index >= 0)
            index = index % (inventories.size());
        else
            index = (inventories.size() + index);
        
        Inventory inv = inventories.get(index);
        player.closeInventory();
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
                showTo(player, 0);
            }
        }
        
        return false;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(event.isLeftClick() && inventories.contains(event.getClickedInventory()))
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
                //Navigation items
                int index = inventories.indexOf(inventory);
                
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
