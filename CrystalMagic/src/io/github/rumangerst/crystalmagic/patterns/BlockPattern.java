/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.patterns;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author ruman
 */
public class BlockPattern
{
    private boolean symmetric;
    private String[] pattern_0;
    private String[] pattern_90;
    private String[] pattern_180;
    private String[] pattern_270;
    private int size;
    protected HashMap<Character, Material> material_assoc = new HashMap<>();
    
    public BlockPattern(boolean symmetric, String ... pattern)
    {
        this.symmetric = symmetric;
        this.size = pattern.length;
        
        if(size % 2 == 0 || size <= 0)
            throw new IllegalArgumentException("Pattern size must be odd and not zero!");
        
        for(String p : pattern)
        {
            if(p.length() != size)
                throw new IllegalArgumentException("Pattern must be square!");
        }
        
        this.pattern_0 = pattern;
        
        if(!symmetric)
        {
            this.pattern_90 = rotate_pattern(pattern);
            this.pattern_180 = rotate_pattern(pattern_90);
            this.pattern_270 = rotate_pattern(pattern_180);
        }
        
        material_assoc.put('*', Material.APPLE); //Set as impossible item
    }
    
    private String[] rotate_pattern(String[] pattern)
    {
        char[][] data = new char[size][size];
        
        for(int i = 0; i < size; ++i)
        {
            for(int j = 0; j < size; ++j)
            {
                data[size - j - 1][i] = pattern[i].charAt(j);
                
            }
        }
        
        String[] dst  = new String[size];        
        
        for(int i = 0; i < size; ++i)
        {
            dst[i] = new String(data[i]);
            
            CrystalMagicPlugin.LOGGER.info(dst[i]);
        }
        
        return dst;
    }
    
    private boolean test(String[] pattern, Location center)
    {
        int extent = size / 2; //floor
        
        for(int i = 0; i < size; ++i)
        {
            for (int j = 0; j < size; ++j)
            {
                Location loc = new Location(center.getWorld(), center.getBlockX() - extent + i, center.getBlockY(), center.getBlockZ() - extent + j);                
                Material mat = loc.getBlock().getType();
                
                Material expected = material_assoc.get(pattern[i].charAt(j));
                
                if(expected == Material.APPLE) //With apple we expect anything but something in our material list
                {
                    if(material_assoc.containsValue(mat))
                        return false;
                }
                else if(mat != expected)
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
     private boolean remove(String[] pattern, Location center)
    {
        int extent = size / 2; //floor
        
        for(int i = 0; i < size; ++i)
        {
            for (int j = 0; j < size; ++j)
            {
                Location loc = new Location(center.getWorld(), center.getBlockX() - extent + i, center.getBlockY(), center.getBlockZ() - extent + j);                
                Material mat = loc.getBlock().getType();
                
                Material expected = material_assoc.get(pattern[i].charAt(j));
                
                if(mat == expected) 
                {
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }
        
        return true;
    }
    
    public boolean remove(Location center)
    {
        if(test(pattern_0, center))
        {
            remove(pattern_0, center);
            return true;
        }
        
        if(!symmetric)
        {
            if(test(pattern_90, center))
            {
                remove(pattern_90, center);
                return true;
            }
            if(test(pattern_180, center))
            {
                remove(pattern_180, center);
                return true;
            }
            if(test(pattern_270, center))
            {
                remove(pattern_270, center);
                return true;
            }
        }
        
        return false;
    }
    
    public boolean test(Location center)
    {
        if (symmetric)
        {
            return test(pattern_0, center);
        }
        else
        {
            return test(pattern_0, center) || test(pattern_90, center) || test(pattern_180, center) || test(pattern_270, center);
        }
    }
}
