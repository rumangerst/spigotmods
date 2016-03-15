/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.crystalls;

import io.github.rumangerst.customitems.CustomItem;
import org.bukkit.Material;

/**
 *
 * @author ruman
 */
public class MagicCrystal extends CustomItem
{
    
    public MagicCrystal(String id, String name)
    {
        super(id, Material.NETHER_STAR, name);
    }
    
}
