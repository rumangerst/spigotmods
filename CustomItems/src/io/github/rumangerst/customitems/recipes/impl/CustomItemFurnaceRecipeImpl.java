/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.recipes.impl;

import io.github.rumangerst.customitems.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class CustomItemFurnaceRecipeImpl extends FurnaceRecipe implements CustomItemRecipeImpl
{
    private CustomItem input_custom;
    
    public CustomItemFurnaceRecipeImpl(ItemStack result, Material input, CustomItem input_custom)
    {
        super(result, input);        
        this.input_custom = input_custom;
    }

    @Override
    public boolean hasCorrectCustomItems(ItemStack[] matrix)
    {
        ItemStack stack = matrix[0];  
        
        if(stack != null)
        {
            if(input_custom != null)
                return input_custom.isOf(stack);
            else
                return true;
        }
        
        return false;
    }
}
