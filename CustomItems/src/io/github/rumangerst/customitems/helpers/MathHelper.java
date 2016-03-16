/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.helpers;

/**
 *
 * @author ruman
 */
public class MathHelper
{
    private MathHelper()
    {
        
    }
    
    public static int clamp(int min, int max, int value)
    {
        return Math.min(max, Math.max(min, value));
    }
    
    public static float clamp(float min, float max, float value)
    {
        return Math.min(max, Math.max(min, value));
    }
    
    public static double clamp(double min, double max, double value)
    {
        return Math.min(max, Math.max(min, value));
    }
    
    public static long clamp(long min, long max, long value)
    {
        return Math.min(max, Math.max(min, value));
    }
    
    public static int lerp(int min, int max, double value)
    {
        return (int)(min + value * (max - min));
    }
    
    public static float lerp(float min, float max, double value)
    {
        return (float)(min + value * (max - min));
    }
    
    public static double lerp(double min, double max, double value)
    {
        return (min + value * (max - min));
    }
}
