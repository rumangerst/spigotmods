/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.recipes;

import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.recipes.impl.CustomItemRecipeImpl;
import io.github.rumangerst.customitems.recipes.impl.ShapedCustomItemRecipeImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ruman
 */
public class ShapedCustomItemRecipe implements CustomItemRecipe
{
    private AnyItem result;
    private int amount;
    private HashMap<Character, AnyItem> ingredients = new HashMap<>();
    private String[] shape;
    
    public ShapedCustomItemRecipe(AnyItem result, int amount)
    {
        this.result = result;
        this.amount = amount;
    }

    @Override
    public List<String> getCustomItemIds()
    {
        ArrayList<String> ids = new ArrayList<>();
        
        if(!result.isVanilla())
            ids.add(result.getCustomItemID());
        
        for(AnyItem it : ingredients.values())
        {
            if(!it.isVanilla())
                ids.add(it.getCustomItemID());
        }
        
        return ids;
    }

    @Override
    public CustomItemRecipeImpl getImplementation(CustomItemsAPI api)
    {
        HashMap<Character, CustomItem> ci = new HashMap<>();
        
        for(char key : ingredients.keySet())
        {
            AnyItem it = ingredients.get(key);
            
            if(!it.isVanilla())
            {
                ci.put(key, api.getCustomItem(it.getCustomItemID()));
            }
        }
        
        return new ShapedCustomItemRecipeImpl(result.make(api, amount), ci);
    }
    
    public void setShape(String ... shape)
    {
        this.shape = shape;
    }
    
    public void setIngredient(char key, AnyItem ingredient)
    {
        ingredients.put(key, ingredient);
    }
    
}
