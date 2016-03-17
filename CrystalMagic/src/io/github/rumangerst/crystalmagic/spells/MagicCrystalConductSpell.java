/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.spells;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.crystalls.MagicCrystal;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.helpers.MathHelper;
import java.util.HashMap;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Loads an instant magic crystal with focus
 * @author ruman
 */
public class MagicCrystalConductSpell implements Spell
{
    CrystalMagicPlugin plugin;
    double load = 0;
    double max_load = 0;
    Player caster;
    ItemStack stack;
    MagicCrystal type;
    
    public MagicCrystalConductSpell(CrystalMagicPlugin plugin, Player caster, ItemStack stack, MagicCrystal type)
    {
        this.plugin = plugin;
        this.caster = caster;
        this.stack = stack;
        this.type = type;
        
        
        //Determine max load
        CustomItemsAPI api = CustomItemsAPI.api(plugin);
        HashMap<String, Integer> elements = type.getElements(stack);
        
        for(String elementid : elements.keySet())
        {
            Element element = (Element)api.getCustomItem(elementid);
            max_load += element.getManaCost(elements.get(elementid));
        }
    }
    
    @Override
    public void execute()
    {        
        if(load >= 1)
        {
            type.executeSpell(caster, stack, (int)load);
        }
    }

    @Override
    public boolean load()
    {
        load += manaCost();
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 2.0f, MathHelper.lerp(0.2f, 0.8f, load / max_load));
        
        return load >= max_load;
    }

    @Override
    public int manaCost()
    {
        switch(type.getLevel(stack))
        {
            case 1:
                return 4;
            case 2:
                return 8;
            case 3:
                return 16;
            case 4:
                return 32;
            default:
                return 1000;
        }
    }
    
}
