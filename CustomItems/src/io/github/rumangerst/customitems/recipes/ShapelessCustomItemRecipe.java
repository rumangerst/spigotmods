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
import io.github.rumangerst.customitems.recipes.impl.ShapelessCustomItemRecipeImpl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ruman
 */
public class ShapelessCustomItemRecipe implements CustomItemRecipe
{
    private AnyItem result;
    private int amount;
    private ArrayList<AnyItem> ingredients = new ArrayList<>();
    private ArrayList<Integer> ingredients_amount = new ArrayList<>();
    
    public ShapelessCustomItemRecipe(AnyItem result, int amount)
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
        
        for(AnyItem it : ingredients)
        {
            if(!it.isVanilla())
                ids.add(it.getCustomItemID());
        }
        
        return ids;
    }

    @Override
    public CustomItemRecipeImpl getImplementation(CustomItemsAPI api)
    {
        CustomItem[] customs = new CustomItem[ingredients.size()];
        
        for(int i = 0; i < ingredients.size(); ++i)
        {
            AnyItem ai = ingredients.get(i);
            
            if(!ai.isVanilla())
                customs[i] = api.getCustomItem(ai.getCustomItemID());
        }
        
        return new ShapelessCustomItemRecipeImpl(result.make(api, amount), customs);
    }
    
    public void addIngredient(AnyItem ingredient, int amount)
    {
        ingredients.add(ingredient);
        ingredients_amount.add(amount);
    }
}
