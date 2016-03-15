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
public class ModusOpen extends BlockPattern
{
    public ModusOpen()
    {
        super(true,"***R***",
                   "***R***",
                   "**RRR**",
                   "RRR*RRR",
                   "**RRR**",
                   "***R***",
                   "***R***");        
        material_assoc.put('R', Material.REDSTONE_WIRE);
    }
}
