/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import org.bukkit.Location;

/**
 *
 * @author ruman
 */
public class MagicWorld
{
    public static int NEXUS_POSITIONS = 500;
    
    public static double distanceToNexus(Location loc)
    {
        int x = (int)Math.abs(loc.getX());
        int z = (int)Math.abs(loc.getZ());
        
        x %= NEXUS_POSITIONS;
        z %= NEXUS_POSITIONS;
        
        return Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
    }
}
