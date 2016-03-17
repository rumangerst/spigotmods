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
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class CollectorGemRecipe extends MagicRecipe
{

    public CollectorGemRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Order || modus != MagicTable.Modus.Filter)
            return false;
        
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        AnyItemStack ingredient_diamond = new AnyItemStack(new AnyItem(Material.DIAMOND), 1);
        AnyItemStack ingredient_emerlad = new AnyItemStack(new AnyItem(Material.EMERALD), 1);
        AnyItemStack ingredient_lapis = new AnyItemStack(new AnyItem(Material.INK_SACK, (byte)4), 1);
        AnyItemStack ingredient_quartz = new AnyItemStack(new AnyItem(Material.QUARTZ), 1);
        AnyItemStack ingredient_glass = new AnyItemStack(new AnyItem(Material.GLASS), 1);
        AnyItemStack ingredient_iron = new AnyItemStack(new AnyItem(Material.IRON_INGOT), 1);
        
        ReactiveGem gem = null;
        
        if(InventoryHelper.is(api, items.getContents(), ingredient_diamond, ingredient_glass, ingredient_iron))
        {
            gem = (ReactiveGem)api.getCustomItem("magiccollectordiamond");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_emerlad, ingredient_glass, ingredient_iron))
        {
            gem = (ReactiveGem)api.getCustomItem("magiccollectoremerald");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_lapis, ingredient_glass, ingredient_iron))
        {
            gem = (ReactiveGem)api.getCustomItem("magiccollectorlapis");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_quartz, ingredient_glass, ingredient_iron))
        {
            gem = (ReactiveGem)api.getCustomItem("magiccollectorquartz");
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
            
            return true;
        }
        else
        {
            return false;
        }
    }
}
