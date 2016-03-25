/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.recipes;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicRecipe;
import io.github.rumangerst.crystalmagic.MagicTable;
import io.github.rumangerst.crystalmagic.crystalls.MagicGem;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.AnyItem;
import io.github.rumangerst.customitems.AnyItemStack;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.helpers.InventoryHelper;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class ElementRecipe extends MagicRecipe
{

    public ElementRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        
        // Find minimum level of target item
        for (ItemStack content : items.getContents())
        {
            if (content != null)
            {
                CustomItem ci = api.getCustomItem(content);

                if (ci instanceof MagicGem)
                {
                    MagicGem g = (MagicGem) ci;
                    level = Math.min(g.getLevel(content), level);
                }
                else if (ci instanceof Element)
                {
                    Element g = (Element) ci;
                    level = Math.min(g.getLevel(content), level);
                }
            }
        }
        
        Element result = null;
        
        AnyItemStack ingredient_fire = new AnyItemStack(new AnyItem("magicfireelement"), 1);
        AnyItemStack ingredient_water = new AnyItemStack(new AnyItem("magicwaterelement"), 1);
        AnyItemStack ingredient_life = new AnyItemStack(new AnyItem("magiclifeelement"), 1);
        AnyItemStack ingredient_magic = new AnyItemStack(new AnyItem("magicmagicelement"), 1);
        AnyItemStack ingredient_inversion = new AnyItemStack(new AnyItem("magicinversionelement"), 1);
        AnyItemStack ingredient_shield = new AnyItemStack(new AnyItem("magicshieldelement"), 1);
        AnyItemStack ingredient_levitation = new AnyItemStack(new AnyItem("magiclevitationelement"), 1);
        AnyItemStack ingredient_explosion = new AnyItemStack(new AnyItem("magicexplosionelement"), 1);
        AnyItemStack ingredient_diamond = new AnyItemStack(new AnyItem(Material.DIAMOND), 1);
        AnyItemStack ingredient_fish = new AnyItemStack(new AnyItem(Material.RAW_FISH), 1);
        AnyItemStack ingredient_tnt = new AnyItemStack(new AnyItem(Material.TNT), 1);
        AnyItemStack ingredient_nmshield = new AnyItemStack(new AnyItem(Material.SHIELD), 1);
        AnyItemStack ingredient_reactive_emerald = new AnyItemStack(new AnyItem("magicreactiveemerald"), 1);
        AnyItemStack ingredient_reactive_lapis = new AnyItemStack(new AnyItem("magicreactivelapis"), 1);
        AnyItemStack ingredient_feather = new AnyItemStack(new AnyItem(Material.FEATHER), 1);
        
        AnyItemStack ingredient_orchid = new AnyItemStack(new AnyItem(Material.RED_ROSE, (byte)1), 1);
        AnyItemStack ingredient_allium = new AnyItemStack(new AnyItem(Material.RED_ROSE, (byte)2), 1);
        AnyItemStack ingredient_daisy = new AnyItemStack(new AnyItem(Material.RED_ROSE, (byte)8), 1);
        AnyItemStack ingredient_sunflower = new AnyItemStack(new AnyItem(Material.DOUBLE_PLANT, (byte)0), 1);
        AnyItemStack ingredient_milk = new AnyItemStack(new AnyItem(Material.MILK_BUCKET, (byte)0), 1);
        
        if(InventoryHelper.is(api, items.getContents(), ingredient_shield, ingredient_life, ingredient_inversion))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicthornselement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_shield, ingredient_fire, ingredient_inversion))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicfireprotectionelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_shield, ingredient_levitation, ingredient_magic))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicfeatherfallelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_shield, ingredient_explosion, ingredient_inversion))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicexplosionprotectionelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_shield, ingredient_magic, ingredient_diamond, ingredient_diamond))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicprojectileprotectionelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_shield, ingredient_life, ingredient_fish))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicunderwaterbreathingelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_fire, ingredient_water, ingredient_levitation, ingredient_magic))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicboltelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_fire, ingredient_magic, ingredient_tnt))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Closed)
                return false;
            
            result = (Element)api.getCustomItem("magicexplosionelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_magic, ingredient_reactive_emerald, ingredient_nmshield))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Filter)
                return false;
            
            result = (Element)api.getCustomItem("magicshieldelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_magic, ingredient_feather, ingredient_reactive_lapis))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Filter)
                return false;
            
            result = (Element)api.getCustomItem("magiclevitationelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_life, ingredient_allium, ingredient_orchid))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Filter)
                return false;
            
            result = (Element)api.getCustomItem("magichealelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_life, ingredient_daisy, ingredient_milk, ingredient_sunflower))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Filter)
                return false;
            
            result = (Element)api.getCustomItem("magicdetoxelement");
        }
        else if(InventoryHelper.is(api, items.getContents(), ingredient_magic, ingredient_water, ingredient_shield, ingredient_inversion, ingredient_reactive_emerald, ingredient_reactive_lapis))
        {
            if(seal != MagicTable.Seal.Order && modus != MagicTable.Modus.Open)
                return false;
            
            result = (Element)api.getCustomItem("magicdisarmelement");
        }
      
        
        if(result != null)
        {
            items.clear();
            items.addItem(result.make(1, level));
        }
        
        return false;
    }
    
}
