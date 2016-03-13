/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.recipes;

import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.recipes.impl.CustomItemRecipeImpl;
import java.util.List;

/**
 *
 * @author ruman
 */
public interface CustomItemRecipe
{
    List<String> getCustomItemIds();
    CustomItemRecipeImpl getImplementation(CustomItemsAPI api);
}
