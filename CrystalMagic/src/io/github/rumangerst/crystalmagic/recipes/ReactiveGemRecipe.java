/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.recipes;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicRecipe;
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
    public boolean execute(Inventory items, int seal, int modus, int level)
    {
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        AnyItemStack ingredient_diamond = new AnyItemStack(new AnyItem(Material.DIAMOND), 1);
        AnyItemStack ingredient_emerlad = new AnyItemStack(new AnyItem(Material.EMERALD), 1);
        AnyItemStack ingredient_lapis = new AnyItemStack(new AnyItem(Material.INK_SACK, (byte)4), 1);
        AnyItemStack ingredient_gold = new AnyItemStack(new AnyItem(Material.GOLD_INGOT), 1);
        
        if(InventoryHelper.is(api, items.getContents(), ingredient_diamond, ingredient_gold))
        {
            items.clear();
            items.addItem(api.getCustomItem("magicreactivediamond").make(1));
            
            return true;
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_emerlad, ingredient_gold))
        {
            items.clear();
            items.addItem(api.getCustomItem("magicreactiveemerald").make(1));
            
            return true;
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_lapis, ingredient_gold))
        {
            items.clear();
            items.addItem(api.getCustomItem("magicreactivelapis").make(1));
            
            return true;
        }
        
        return false;
    }
    
}
