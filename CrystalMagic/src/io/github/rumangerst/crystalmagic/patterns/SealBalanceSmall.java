/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.patterns;

import org.bukkit.Material;

/**
 *
 * @author ruman
 */
public class SealBalanceSmall extends BlockPattern
{
    
    public SealBalanceSmall()
    {
        super(false, "*******",
                    "***o***",
                    "**i*i**",
                    "*o***o*",
                    "**g*g**",
                    "***o***",
                    "*******");
        material_assoc.put('o', Material.OBSIDIAN);
        material_assoc.put('i', Material.IRON_BLOCK);
        material_assoc.put('g', Material.GOLD_BLOCK);
    }
    
}
