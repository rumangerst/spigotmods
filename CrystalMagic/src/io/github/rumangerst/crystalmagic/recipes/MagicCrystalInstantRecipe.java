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
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicCrystalInstantRecipe extends MagicRecipe
{

    public MagicCrystalInstantRecipe(CrystalMagicPlugin plugin)
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
        
        if(contents.size() != 2)
            return false;
        
        ItemStack magic_crystal_stack = InventoryHelper.getOneStackOf(contents.get(new AnyItem("magicmagiccrystal")), 1);
        ItemStack collector_stack = InventoryHelper.getOneStackOf(contents.get(new AnyItem("magicmagiccollectorcrystal")), 1);
        
        if(magic_crystal_stack != null && collector_stack != null)
        {
            MagicCrystal crystal = (MagicCrystal)api.getCustomItem("magicmagiccrystal");
            
            if(crystal.isInstant(magic_crystal_stack) || crystal.getElements(magic_crystal_stack).isEmpty())
                return false;
            
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
            
            crystal.setLevel(magic_crystal_stack, level);
            crystal.setInstant(magic_crystal_stack, true);
            
            items.addItem(magic_crystal_stack);
            
            return true;
        }
        
        return false;
    }
    
}
