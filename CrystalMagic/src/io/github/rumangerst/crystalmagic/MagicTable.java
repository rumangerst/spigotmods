/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import io.github.rumangerst.crystalmagic.patterns.ModusClosed;
import io.github.rumangerst.crystalmagic.patterns.ModusFilter;
import io.github.rumangerst.crystalmagic.patterns.ModusOpen;
import io.github.rumangerst.crystalmagic.patterns.SealBalanceSmall;
import io.github.rumangerst.crystalmagic.patterns.SealOrderSmall;
import io.github.rumangerst.crystalmagic.recipes.CollectorGemRecipe;
import io.github.rumangerst.crystalmagic.recipes.MagicCrystalRecipe;
import io.github.rumangerst.crystalmagic.recipes.ReactiveGemRecipe;
import io.github.rumangerst.crystalmagic.spells.MagicTableSpell;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicTable implements Listener
{
    CrystalMagicPlugin plugin;
    
    private ModusClosed modus_closed;
    private ModusOpen modus_open;
    private ModusFilter modus_filter;
    
    private SealOrderSmall seal_order_small;
    private SealBalanceSmall seal_balance_small;
    
    private HashMap<Location, Inventory> inventories = new HashMap<>();
    private HashMap<Location, TableType> tables = new HashMap<>();
    
    private ArrayList<MagicRecipe> recipes = new ArrayList<>();
    
    public static String INVENTORY_NAME_BALANCE_SMALL = "Magie: Gleichgewicht (Klein)";
    public static String INVENTORY_NAME_ORDER_SMALL = "Magie: Ordnung (Klein)";
    public static String INVENTORY_NAME_BALANCE_BIG = "Magie: Gleichgewicht";
    public static String INVENTORY_NAME_ORDER_BIG = "Magie: Ordnung";
    
    public enum TableType
    {
        None,
        BalanceSmall,
        BalanceBig,
        OrderSmall,
        OrderBig
    }
    
    public enum Seal
    {
        None,
        Balance,
        Order
    }
    
    public enum Modus
    {
        None,
        Open,
        Filter,
        Closed
    }
    
    public MagicTable(CrystalMagicPlugin plugin)
    {
        this.plugin = plugin;
        
        modus_closed = new ModusClosed();
        modus_filter = new ModusFilter();
        modus_open = new ModusOpen();
        
        seal_order_small = new SealOrderSmall();
        seal_balance_small = new SealBalanceSmall();
        
        initializeRecipes();
    }
    
    private void initializeRecipes()
    {
        recipes.add(new ReactiveGemRecipe(plugin));
        recipes.add(new CollectorGemRecipe(plugin));
        recipes.add(new MagicCrystalRecipe(plugin));
    }
    
    @EventHandler
    public void onBreakTable(BlockBreakEvent event)
    {
        if(!event.isCancelled() && event.getBlock().getType() == Material.WORKBENCH)
        {
            if(tables.containsKey(event.getBlock().getLocation()))
            {
                breakTable(event.getBlock().getLocation());
            }
        }
    }
    
    @EventHandler
    public void hookIntoWorkbench(PlayerInteractEvent event)
    {
        if(!event.isCancelled() && event.getClickedBlock().getType() == Material.WORKBENCH)
        {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                if(tryOpenTableInventory(event.getPlayer(), event.getClickedBlock().getLocation()))
                {
                    event.setCancelled(true);
                }
            }
            else if(event.getAction() == Action.LEFT_CLICK_BLOCK && plugin.spellhandler.isUsingSpell(event.getPlayer()))
            {
                plugin.spellhandler.executeSpellNow(event.getPlayer());
            }
            else if(event.getAction() == Action.LEFT_CLICK_BLOCK && !plugin.spellhandler.isUsingSpell(event.getPlayer()))
            {
                Location loc = event.getClickedBlock().getLocation();
                TableType type = getTableTypeAt(loc);
                Modus mode = getTableModusAt(loc);
                
                if(type != TableType.None && mode != Modus.None)
                {
                    plugin.spellhandler.spell(event.getPlayer(), new MagicTableSpell(plugin, loc, type, mode));
                    event.setCancelled(true);
                }
            }
        }
    }
    
    /**
     * Drops all inventories of magic tables to the ground
     */
    public void disable()
    {
        for(Location loc : inventories.keySet())
        {
            Inventory inv = inventories.get(loc);
            
            for(ItemStack stack : inv)
            {
                if(stack != null)
                    loc.getWorld().dropItem(loc, stack);
            }
        }
        
        inventories.clear();
    }
    
    public void executeRecipe(Location center, int level)
    {
        TableType type = getTableTypeAt(center);
        Modus mode = getTableModusAt(center);
        
        //destroy the pattern
        switch (mode)
        {
            case Open:
                modus_open.remove(center);
                break;
            case Filter:
                modus_filter.remove(center);
                break;
            case Closed:
                modus_closed.remove(center);
                break;
            default:
                break;
        }
        
        for(MagicRecipe recipe : recipes)
        {
            if(recipe.execute(inventories.get(center), getSealFrom(type), mode, level))
                return;
        }
        
        breakTable(center);
        center.getWorld().createExplosion(center, 4.0f);
    }
    
    public void breakTable(Location center)
    {
        // Drop if invalid
        for(ItemStack stack : inventories.get(center))
        {
            if(stack != null)
                center.getWorld().dropItem(center, stack);
        }

        inventories.remove(center);
        tables.remove(center);
    }
    
    public Modus getTableModusAt(Location center)
    {
        if(modus_open.test(center))
            return Modus.Open;
        else if(modus_filter.test(center))
            return Modus.Filter;
        else if(modus_closed.test(center))
            return Modus.Closed;
        else
            return Modus.None;
    }
    
    public Seal getSealFrom(TableType type)
    {
        switch(type)
        {
            case BalanceBig:
                return Seal.Balance;
            case BalanceSmall:
                return Seal.Balance;
            case OrderBig:
                return Seal.Order;
            case OrderSmall:
                return Seal.Order;
            default:
                return Seal.None;
        }
    }
    
    public TableType getTableTypeAt(Location center)
    {
        Location bottom = new Location(center.getWorld(), center.getBlockX(),center.getBlockY() - 1, center.getBlockZ());
        
        TableType existing = tables.getOrDefault(center, TableType.None);
        TableType updated = existing;
        String updated_title = "";
        
        if(seal_order_small.test(bottom))
        {
            updated = TableType.OrderSmall;
            updated_title = INVENTORY_NAME_ORDER_SMALL;
        }
        else if(seal_balance_small.test(bottom))
        {
            updated = TableType.BalanceSmall;
            updated_title = INVENTORY_NAME_BALANCE_SMALL;
        }
        else
        {
            updated = TableType.None;
        }
        
        if(updated != existing)
        {
            if(existing != TableType.None)
            {
                breakTable(center);
            }
            if(updated != TableType.None)
            {
                Inventory inv = Bukkit.createInventory(null, 9, updated_title);
                inventories.put(center, inv);
                tables.put(center, updated);
            }
        }
        
        return updated;
    }
    
    private boolean tryOpenTableInventory(Player player, Location center)
    {
        if(getTableTypeAt(center) != TableType.None)
        {
            player.getWorld().playSound(center, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 1, 0.2f);
            player.openInventory(inventories.get(center));
            
            return true;
        }
        
        return false;        
    }
}
