/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.recipes;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicRecipe;
import io.github.rumangerst.crystalmagic.MagicTable;
import io.github.rumangerst.crystalmagic.crystalls.MagicCrystal;
import io.github.rumangerst.crystalmagic.crystalls.MagicGem;
import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.helpers.InventoryHelper;
import java.util.HashMap;
import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicCrystalCollectorRecipe extends MagicRecipe
{

    public MagicCrystalCollectorRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Balance || modus != MagicTable.Modus.Closed)
            return false;
        
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        HashMap<AnyItem, List<ItemStack>> contents = InventoryHelper.extractAnyItemStacks(api, items.getContents());
        
        if(contents.size() != 3)
            return false;
        
        ItemStack magic_crystal_stack = InventoryHelper.getOneStackOf(contents.get(new AnyItem("magicmagiccrystal")), 1);
        ItemStack emerald_stack = InventoryHelper.getOneStackOf(contents.get(new AnyItem("magicreactiveemerald")), 1);
        ItemStack element_stack = InventoryHelper.getOneStackOf(contents.get(new AnyItem("magicmagicelement")), 1);
        
        if(magic_crystal_stack != null && emerald_stack != null && element_stack != null)
        {
            MagicGem crystal = (MagicGem)api.getCustomItem("magicmagiccollectorcrystal");
                        
            // Find minimum level of target item
            for(ItemStack content : items.getContents())
            {
                if(content != null)
                {
                    CustomItem ci = api.getCustomItem(content);
                    
                    if(ci instanceof MagicGem)
                    {
                        MagicGem g = (MagicGem)ci;
                        level = Math.min(g.getLevel(content), level);
                    }
                }
            }
            
            items.clear();
            
            ItemStack collector_crystal_stack = crystal.make(1);            
            crystal.setLevel(collector_crystal_stack, level);            
            items.addItem(collector_crystal_stack);
            
            return true;
        }
        
        return false;
    }
    
}
