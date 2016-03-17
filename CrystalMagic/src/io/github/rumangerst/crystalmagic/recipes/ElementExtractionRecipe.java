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
public class ElementExtractionRecipe extends MagicRecipe
{

    public ElementExtractionRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Balance && modus != MagicTable.Modus.Filter)
            return false;
        
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        ItemStack pickaxe_stack = null;
        ItemStack element_container_stack = null;
        
        MagicGem element_container_type = null;
        
        for(ItemStack stack : items.getContents())
        {
            if(stack != null)
            {
                if(stack.getType() == Material.IRON_PICKAXE)
                {
                    if(pickaxe_stack != null)
                        return false;
                    pickaxe_stack = stack;
                }
                else
                {
                    CustomItem ci = api.getCustomItem(stack);
                    
                    if(ci != null)
                    {
                        if(element_container_stack != null)
                            return false;
                        if(!(ci instanceof MagicGem))
                            return false;
                        
                        MagicGem gem = (MagicGem)ci;
                        
                        element_container_stack = stack;
                        element_container_type = gem;
                    }
                }
            }
        }
        
        if(pickaxe_stack == null || element_container_stack == null || element_container_type == null)
            return false;
        
        HashMap<String, Integer> elements = element_container_type.getElements(element_container_stack);
        
        if(elements.isEmpty())
            return false;
        
        String element_id = elements.keySet().iterator().next();
        int element_level = Math.min(level, elements.get(element_id));
        
        //Create the element
        items.clear();
        
        items.addItem(((Element)(api.getCustomItem(element_id))).make(1, element_level));
        
        return true;
    }
    
}
