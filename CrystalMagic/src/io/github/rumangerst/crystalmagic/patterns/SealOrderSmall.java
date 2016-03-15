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
public class SealOrderSmall extends BlockPattern
{
    
    public SealOrderSmall()
    {
        super(true, "*******",
                    "*i***i*",
                    "***o***",
                    "**o*o**",
                    "***o***",
                    "*i***i*",
                    "*******");
        material_assoc.put('o', Material.OBSIDIAN);
        material_assoc.put('i', Material.IRON_BLOCK);
    }
    
}
