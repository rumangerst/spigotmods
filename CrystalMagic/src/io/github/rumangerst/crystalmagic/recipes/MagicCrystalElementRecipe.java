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
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.ArrayList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class MagicCrystalElementRecipe extends MagicRecipe
{

    public MagicCrystalElementRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        if(seal != MagicTable.Seal.Order || modus != MagicTable.Modus.Filter)
            return false;
        
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        ItemStack crystal_stack = null;
        ItemStack quartz_stack = null;
        ItemStack element_stack = null;
        int element_level = 1;   
        String element_id = "";
        
        for(ItemStack content : items.getContents())
        {
            if(content != null)
            {
                CustomItem ci = api.getCustomItem(content);
                
                if(ci == null)
                    return false;
                
                if(ci.getId().equals("magicmagiccrystal"))
                {
                    if(crystal_stack != null)
                        return false;
                    
                    crystal_stack = content;
                    level = Math.min(level, ((MagicGem)ci).getLevel(content));
                }
                else if(ci.getId().equals("magicreactivequartz"))
                {
                    if(quartz_stack != null)
                        return false;
                    
                    quartz_stack = content;
                    level = Math.min(level, ((MagicGem)ci).getLevel(content));
                }
                else if(ci instanceof Element)
                {
                    if(element_stack != null)
                        return false;
                    
                    element_stack = content;
                    element_level = ((Element)ci).getLevel(content);
                    element_id = ci.getId();
                }
            }
        }
        
        if(crystal_stack == null || quartz_stack == null || element_stack == null)
            return false;
        
        items.clear();
        
        MagicCrystal crystal_type = (MagicCrystal)api.getCustomItem(crystal_stack);
        crystal_type.setLevel(crystal_stack, level);
        crystal_type.putElement(crystal_stack, element_id, element_level);
        
        items.addItem(crystal_stack);
        
        return true;
    }
    
}
