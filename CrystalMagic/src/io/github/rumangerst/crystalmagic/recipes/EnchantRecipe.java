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
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.AnyItemStack;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class EnchantRecipe extends MagicRecipe
{

    public EnchantRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Balance && modus != MagicTable.Modus.Closed)
            return false;
        
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        ItemStack to_enchant = null;
        ItemStack element_stack = null;
        Element element = null;
        
        for(ItemStack stack : items.getContents())
        {
            if(stack != null)
            {
                //Material type = stack.getType();
                
                /*if(type == Material.IRON_AXE ||
                        type == Material.IRON_SPADE ||
                        type == Material.IRON_PICKAXE ||
                        type == Material.IRON_HOE ||
                        type == Material.IRON_SWORD ||
                        type == Material.IRON_HELMET ||
                        type == Material.IRON_CHESTPLATE ||
                        type == Material.IRON_LEGGINGS ||
                        type == Material.IRON_BOOTS ||
                        type == Material.LEATHER_HELMET ||
                        type == Material.LEATHER_CHESTPLATE ||
                        type == Material.p)
                {
                    if(pickaxe_stack != null)
                        return false;
                    pickaxe_stack = stack;
                }*/
                
                CustomItem item = api.getCustomItem(stack);
                
                if(item != null && item instanceof Element)
                {
                    if(element_stack != null)
                        return false;
                    
                    element_stack = stack;
                    element = (Element)item;
                }
                else
                {
                    if(to_enchant != null)
                        return false;
                    
                    to_enchant = stack;
                }
                
            }
        }
        
        if(element == null || to_enchant == null)
            return false;
        
        if(!element.canEnchant())
            return false;
        
        //Create the element
        items.clear();        
        level = Math.min(level, element.getLevel(element_stack));
        
        //enchant
        element.enchant(to_enchant, level);        
        items.addItem(to_enchant);
        
        return true;
    }
    
}
