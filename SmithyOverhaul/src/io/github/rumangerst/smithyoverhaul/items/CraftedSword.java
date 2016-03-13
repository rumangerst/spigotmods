/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.smithyoverhaul.items;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import static io.github.rumangerst.smithyoverhaul.TieredSmity.ratePercentage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class CraftedSword extends CustomItem
{
    
    public CraftedSword(String id, Material type, String name)
    {
        super(id, type, (byte)0, name);
    }
    
    @Override
    public void transform(ItemStack stack)
    {
        super.transform(stack);
        
       
    }
}
