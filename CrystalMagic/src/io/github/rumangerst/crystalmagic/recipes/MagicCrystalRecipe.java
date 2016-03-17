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
import io.github.rumangerst.customitems.AnyItemStack;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.helpers.InventoryHelper;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicCrystalRecipe extends MagicRecipe
{

    public MagicCrystalRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Order || modus != MagicTable.Modus.Closed)
            return false;
        
         CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        AnyItemStack ingredient_diamond = new AnyItemStack(new AnyItem("magicmagicdiamond"), 1);
        AnyItemStack ingredient_emerlad = new AnyItemStack(new AnyItem("magicmagicemerald"), 1);
        AnyItemStack ingredient_lapis = new AnyItemStack(new AnyItem("magicmagiclapis"), 1); 
        AnyItemStack ingredient_quartz = new AnyItemStack(new AnyItem("magicmagicquartz"), 1); 
        
        if(InventoryHelper.is(api, items.getContents(), ingredient_diamond, ingredient_emerlad, ingredient_lapis, ingredient_quartz))
        {
            // Find minimum level of target item
            for(ItemStack content : items.getContents())
            {
                if(content != null)
                {
                    CustomItem ci = api.getCustomItem(content);
                    
                    if(ci instanceof MagicGem)
                    {
                        MagicGem gem = (MagicGem)ci;
                        level = Math.min(gem.getLevel(content), level);
                    }
                }
            }
            
            //Clear and push output
            items.clear();
            
            MagicCrystal type = (MagicCrystal)api.getCustomItem("magicmagiccrystal");
            items.addItem(type.make(1, level));
            
            return true;
        }
        
        return false;
    }
    
}
