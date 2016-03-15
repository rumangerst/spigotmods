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
public class ModusClosed extends BlockPattern
{
    public ModusClosed()
    {
        super(true,"RRRRRRR",
                   "R**R**R",
                   "R*****R",
                   "RR***RR",
                   "R*****R",
                   "R**R**R",
                   "RRRRRRR");        
        material_assoc.put('R', Material.REDSTONE_WIRE);
    }
}
