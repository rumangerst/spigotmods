/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.recipes.impl;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.CustomItemsPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 *
 * @author ruman
 */
public class ShapedCustomItemRecipeImpl extends ShapedRecipe implements CustomItemRecipeImpl
{
    private HashMap<Character, CustomItem> customitemingredients;
    
    public ShapedCustomItemRecipeImpl(ItemStack result, HashMap<Character, CustomItem> customingredients)
    {
        super(result);
        
        this.customitemingredients = customingredients;
    }

    @Override
    public boolean hasCorrectCustomItems(ItemStack[] matrix)
    {
        ArrayList<ItemStack[]> pruned = new ArrayList<>();
        
        CustomItemsPlugin.LOGGER.info(matrix.length + "");
        
        return false;
    }
}
