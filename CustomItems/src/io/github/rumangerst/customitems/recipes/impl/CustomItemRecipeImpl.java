/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.recipes.impl;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public interface CustomItemRecipeImpl
{
    /**
     * Checks if the matrix contains the correct items for crafting
     * @param matrix the crafting matrix
     * @return 
     */
    boolean hasCorrectCustomItems(ItemStack[] matrix);   
}
