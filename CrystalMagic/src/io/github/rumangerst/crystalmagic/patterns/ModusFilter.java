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
public class ModusFilter extends BlockPattern
{
    public ModusFilter()
    {
        super(true,"*RRRRR*",
                   "R*****R",
                   "R**R**R",
                   "R*R*R*R",
                   "R**R**R",
                   "R*****R",
                   "*RRRRR*");        
        material_assoc.put('R', Material.REDSTONE_WIRE);
    }
}
