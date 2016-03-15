/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import org.bukkit.inventory.Inventory;

/**
 *
 * @author ruman
 */
public abstract class MagicRecipe
{
    public static final int MODUS_OPEN = 0;
    public static final int MODUS_FILTER = 1;
    public static final int MODUS_CLOSED = 2;
    
    public static final int SEAL_ORDER = 0;
    public static final int SEAL_BALANCE = 1;
    
    public MagicRecipe(int seal, int modus)
    {
        
    }
    
    public abstract boolean execute(Inventory items);
}
