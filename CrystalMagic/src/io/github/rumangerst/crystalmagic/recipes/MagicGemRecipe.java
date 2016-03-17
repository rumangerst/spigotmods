/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.recipes;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicRecipe;
import io.github.rumangerst.crystalmagic.MagicTable;
import io.github.rumangerst.crystalmagic.crystalls.MagicGem;
import io.github.rumangerst.crystalmagic.crystalls.ReactiveGem;
import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.AnyItemStack;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.helpers.InventoryHelper;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicGemRecipe extends MagicRecipe
{

    public MagicGemRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Order || modus != MagicTable.Modus.Open)
        {
            return false;
        }
        
        CustomItemsAPI api = CustomItemsAPI.api(plugin);        
        HashMap<AnyItem, List<ItemStack>> contents = InventoryHelper.extractAnyItemStacks(api, items.getContents());
        
        AnyItemStack ingredient_diamond = new AnyItemStack(new AnyItem("magicreactivediamond"), 1);
        AnyItemStack ingredient_emerlad = new AnyItemStack(new AnyItem("magicreactiveemerald"), 1);
        AnyItemStack ingredient_lapis = new AnyItemStack(new AnyItem("magicreactivelapis"), 1);
        AnyItemStack ingredient_quartz = new AnyItemStack(new AnyItem("magicreactivequartz"),1);
        AnyItemStack ingredient_magic_crystal = new AnyItemStack(new AnyItem("magicmagiccollectorcrystal"), 1);
        
        MagicGem gem = null;
        
        if(InventoryHelper.is(api, items.getContents(), ingredient_diamond, ingredient_magic_crystal))
        {
            gem = (MagicGem)api.getCustomItem("magicmagicdiamond");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_emerlad, ingredient_magic_crystal))
        {
            gem = (MagicGem)api.getCustomItem("magicmagicemerald");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_lapis, ingredient_magic_crystal))
        {
            gem = (MagicGem)api.getCustomItem("magicmagiclapis");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_quartz, ingredient_magic_crystal))
        {
            gem = (MagicGem)api.getCustomItem("magicmagicquartz");
        }
        
        if(gem != null)
        {
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
            
            items.addItem(gem.make(1, level));            
            items.addItem(contents.get(new AnyItem("magicmagiccollectorcrystal")).get(0));
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
