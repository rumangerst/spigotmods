/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.recipes.impl;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

/**
 *
 * @author ruman
 */
public class ShapelessCustomItemRecipeImpl extends ShapelessRecipe implements CustomItemRecipeImpl
{
    private CustomItem[] ingredients_custom;
    
    public ShapelessCustomItemRecipeImpl(ItemStack result, CustomItem ... ingredients_custom)
    {
        super(result);        
        this.ingredients_custom = ingredients_custom;
    }   

    @Override
    public boolean hasCorrectCustomItems(ItemStack[] matrix)
    {
        HashSet<Integer> marked_positions = new HashSet<>();
        int custom_ingredients = 0;
        
        /*
        For each item we search for a unmarked position that contains the item
        We always mark empty positions
        
        success if marked positions is same as matrix size
        */
        
        for(int i = 0; i < getIngredientList().size(); ++i)
        {
            ItemStack vs = getIngredientList().get(i);            
            int amount = vs.getAmount();
            
            CustomItem item = ingredients_custom[i];
            
            if(item == null)
            {
                continue;
            }
            else
            {
                ++custom_ingredients;
            }
            
            for(int j = 0; j < matrix.length; ++j)
            {
                if(marked_positions.contains(j))
                    continue;
                
                ItemStack stack = matrix[j];
                
                if(stack != null)
                {
                    if(stack.getAmount() == amount && item.isOf(stack))
                    {
                        marked_positions.add(j);
                        break;
                    }
                }
            }
        }
        
        return marked_positions.size() == custom_ingredients;
    }
}
