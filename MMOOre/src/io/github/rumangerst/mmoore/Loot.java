/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.mmoore;

import java.util.Random;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author ruman
 */
public class Loot
{
    public static Random RANDOM = new Random();
    
    public ItemStack[] loots;
    public double probability;
    
    public Loot(double probability, ItemStack ... loots)
    {
        this.loots = loots;
        this.probability = probability;
    }
    
    public ItemStack[] dice()
    {
        if(RANDOM.nextDouble() < probability)
            return loots;
        return null;
    }
}
