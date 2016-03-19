/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import io.github.rumangerst.crystalmagic.crystalls.MagicCrystal;
import io.github.rumangerst.crystalmagic.crystalls.MagicGem;
import io.github.rumangerst.crystalmagic.crystalls.ReactiveGem;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
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
public class CrystalMagicGiveGUI implements CommandExecutor, Listener
{
    private CustomItemsAPI api;
    private HashMap<Player, GiveInventory> player_inventories = new HashMap<>();
    
    public CrystalMagicGiveGUI(CustomItemsAPI api)
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
            this.search_string = search.toLowerCase();

            Inventory inv = Bukkit.createInventory(null, 54, title(search, inventories.size() + 1));
            int current_count = 0;

            ArrayList<ItemStack> items = new ArrayList<>();
            
            // Create all cheat items
            for(int level = 1; level <= 4; ++level)
            {
                for(CustomItem ci : api.getCustomItems())
                {
                    if(ci instanceof MagicCrystal)
                    {
                        MagicCrystal crystal = (MagicCrystal)ci;                      
                      
                        
                        //Add for all elements of this level
                        // Add projectile or non projectile
                        
                        for(int mod = 0; mod <= 4; ++mod)
                        {
                            boolean instant = (mod & 1) == 1;
                            boolean projectile = (mod & 2) == 2;                            
                        
                            for(CustomItem ce : api.getCustomItems())
                            {
                                if(ce instanceof Element)
                                {
                                    ItemStack es = crystal.make(1, level);
                                    crystal.putElement(es, ce.getId(), level);
                                    crystal.setInstant(es, instant);
                                    crystal.setProjectile(es, projectile);

                                    if(NBTAPI.getString(es, "display/Name", "").toLowerCase().contains(search))
                                        items.add(es);
                                }
                            }
                        
                        }
                    }
                    else if(ci instanceof MagicGem)
                    {
                        ItemStack stack = ((MagicGem)ci).make(1, level);
                        
                        if(NBTAPI.getString(stack, "display/Name", "").toLowerCase().contains(search))
                            items.add(stack);
                    }
                    else if(ci instanceof Element)
                    {
                        ItemStack stack = ((Element)ci).make(1, level);
                        
                        if(NBTAPI.getString(stack, "display/Name", "").toLowerCase().contains(search))
                            items.add(stack);
                    }
                }
            }            
            
            items.sort(new Comparator<ItemStack>()
            {
                @Override
                public int compare(ItemStack o1, ItemStack o2)
                {
                    CustomItem c1 = api.getCustomItem(o1);
                    CustomItem c2 = api.getCustomItem(o2);
                    
                    if(c1 != null && c2 != null)
                        return c1.getId().compareTo(c2.getId());
                    return 0;                    
                }

            });

            for (ItemStack item : items)
            {

                if (current_count < 45)
                {
                    inv.addItem(item);
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
                return "Crystal Magic (Page " + index + ")";
            else
                return "Crystal Magic \"" + search + "\" (Page " + index + ")";  
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
