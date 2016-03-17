/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.recipes;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicRecipe;
import io.github.rumangerst.crystalmagic.MagicTable;
import io.github.rumangerst.crystalmagic.crystalls.ReactiveGem;
import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.AnyItemStack;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.helpers.InventoryHelper;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author ruman
 */
public class ReactiveGemRecipe extends MagicRecipe
{

    public ReactiveGemRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Open)
            return false;
        
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        AnyItemStack ingredient_diamond = new AnyItemStack(new AnyItem(Material.DIAMOND), 1);
        AnyItemStack ingredient_emerlad = new AnyItemStack(new AnyItem(Material.EMERALD), 1);
        AnyItemStack ingredient_lapis = new AnyItemStack(new AnyItem(Material.INK_SACK, (byte)4), 1);
        AnyItemStack ingredient_quartz = new AnyItemStack(new AnyItem(Material.QUARTZ), 1);
        AnyItemStack ingredient_gold = new AnyItemStack(new AnyItem(Material.GOLD_INGOT), 1);
        
        ReactiveGem gem = null;
        
        if(InventoryHelper.is(api, items.getContents(), ingredient_diamond, ingredient_gold))
        {
            gem = (ReactiveGem)api.getCustomItem("magicreactivediamond");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_emerlad, ingredient_gold))
        {
            gem = (ReactiveGem)api.getCustomItem("magicreactiveemerald");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_lapis, ingredient_gold))
        {
            gem = (ReactiveGem)api.getCustomItem("magicreactivelapis");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_quartz, ingredient_gold))
        {
            gem = (ReactiveGem)api.getCustomItem("magicreactivequartz");
        }
        
        if(gem != null)
        {
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
