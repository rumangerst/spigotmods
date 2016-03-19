/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.spells;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.crystalls.MagicCrystal;
import io.github.rumangerst.customitems.helpers.MathHelper;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Loads an instant magic crystal with focus
 * @author ruman
 */
public class MagicCrystalLoadSpell implements Spell
{
    CrystalMagicPlugin plugin;
    int load = 0;
    int max_load = 0;
    Player caster;
    ItemStack stack;
    MagicCrystal type;
    
    public MagicCrystalLoadSpell(CrystalMagicPlugin plugin, Player caster, ItemStack stack, MagicCrystal type)
    {
        this.plugin = plugin;
        this.caster = caster;
        this.stack = stack;
        this.type = type;
        
        max_load = type.getMaxFocus(stack);
    }
    
    @Override
    public void execute()
    {        
        caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f);
    }

    @Override
    public boolean load()
    {
        boolean full = type.getFocus(stack) == type.getMaxFocus(stack);

        if (full)
        {
            return true;
        }
        else
        {
            load = type.getFocus(stack) + manaCost();
            
            type.setFocus(stack, (int)load); // Push mana into the crystal
            caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 2.0f, MathHelper.lerp(0.2f, 0.8f, load / type.getMaxFocus(stack)));
            
            return false;
        }
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

    @Override
    public String getStatus()
    {
        return "Lade Stein auf ... " + (int)load + "/" + (int)max_load + " Fokus";
    }
    
}
