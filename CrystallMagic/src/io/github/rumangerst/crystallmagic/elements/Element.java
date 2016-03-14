/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystallmagic.elements;

import io.github.rumangerst.customitems.CustomItem;
import org.bukkit.Material;

/**
 *
 * @author ruman
 */
public class Element extends CustomItem
{
    private String[] names;
    
    public Element(String id, String ... name)
    {
        super(id, Material.NETHER_STAR, (byte)0, name[0]);
        names = name;
    }
    
}
