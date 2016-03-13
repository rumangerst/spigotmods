/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.recipes;

import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.recipes.impl.CustomItemFurnaceRecipeImpl;
import io.github.rumangerst.customitems.recipes.impl.CustomItemRecipeImpl;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class CustomItemFurnaceRecipe implements CustomItemRecipe
{
    private AnyItem result;
    private int amount;
    private AnyItem input;    
 
    public CustomItemFurnaceRecipe(AnyItem result, int amount, AnyItem input)
    {
        this.result = result;
        this.amount = amount;
        this.input = input;
    }

    @Override
    public List<String> getCustomItemIds()
    {
        ArrayList<String> ids = new ArrayList<>();
        
        if(!result.isVanilla())
            ids.add(result.getCustomItemID());
        
        if(!input.isVanilla())
            ids.add(input.getCustomItemID());
        
        return ids;
    }

    @Override
    public CustomItemRecipeImpl getImplementation(CustomItemsAPI api)
    {
        ItemStack i = input.make(api, 1);        
        return new CustomItemFurnaceRecipeImpl(result.make(api, amount), i.getType(), api.getCustomItem(i));
    }
    
}
