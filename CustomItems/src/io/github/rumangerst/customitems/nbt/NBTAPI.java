/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems.nbt;

import java.util.List;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;
import net.minecraft.server.v1_9_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author ruman
 */
public class NBTAPI
{

    private static void setString(NBTTagCompound nbt, String key, String value)
    {
        navigateToContainer(nbt, key, true).setString(finalKeyString(key), value);
    }

    private static void setFloat(NBTTagCompound nbt, String key, float value)
    {
        navigateToContainer(nbt, key, true).setFloat(finalKeyString(key), value);
    }

    private static void setDouble(NBTTagCompound nbt, String key, double value)
    {
        navigateToContainer(nbt, key, true).setDouble(finalKeyString(key), value);
    }

    private static void setBool(NBTTagCompound nbt, String key, boolean value)
    {
        navigateToContainer(nbt, key, true).setBoolean(finalKeyString(key), value);
    }

    private static void setInt(NBTTagCompound nbt, String key, int value)
    {
        navigateToContainer(nbt, key, true).setInt(finalKeyString(key), value);
    }

    private static void setIntArray(NBTTagCompound nbt, String key, int... value)
    {
        navigateToContainer(nbt, key, true).setIntArray(finalKeyString(key), value);
    }

    private static void setLong(NBTTagCompound nbt, String key, long value)
    {
        navigateToContainer(nbt, key, true).setLong(finalKeyString(key), value);
    }

    private static void setStringList(NBTTagCompound nbt, String key, String... value)
    {
        NBTTagList list = new NBTTagList();

        for (String t : value)
        {
            list.add(new NBTTagString(t));
        }

        navigateToContainer(nbt, key, true).set(finalKeyString(key), list);
    }

    private static String finalKeyString(String key)
    {
        String[] cell = key.split("/");
        return cell[cell.length - 1];
    }

    private static String subPathString(String[] cell)
    {
        String newkey = "";

        for (int i = 1; i < cell.length; ++i)
        {
            newkey += cell[i];

            if (i != cell.length - 1)
            {
                newkey += "/";
            }
        }

        return newkey;
    }

    /**
     * Returns the containing NBTTagCompound for given key
     *
     * @param root
     * @param key
     * @param create Create the path
     * @return
     */
    private static NBTTagCompound navigateToContainer(NBTTagCompound root, String key, boolean create)
    {
        if (!key.contains("/"))
        {
            return root;
        }
        else
        {
            String[] cell = key.split("/");
            String subtag = cell[0];

            if (!root.hasKey(subtag))
            {
                if (create)
                {
                    root.set(subtag, new NBTTagCompound());
                }
                else
                {
                    return null;
                }
            }

            NBTTagCompound child = root.getCompound(subtag);
            return navigateToContainer(child, subPathString(cell), create);
        }
    }

    // Item getters and setters
    public static boolean hasNBT(org.bukkit.inventory.ItemStack cbitemstack)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        return stack.hasTag();
    }

    public static void setString(org.bukkit.inventory.ItemStack cbitemstack, String key, String value)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        NBTTagCompound nbt = getOrCreateNBT(stack);

        setString(nbt, key, value);
        saveNBT(stack, cbitemstack);
    }

    public static void setFloat(org.bukkit.inventory.ItemStack cbitemstack, String key, float value)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        NBTTagCompound nbt = getOrCreateNBT(stack);

        setFloat(nbt, key, value);
        saveNBT(stack, cbitemstack);
    }

    public static void setDouble(org.bukkit.inventory.ItemStack cbitemstack, String key, double value)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        NBTTagCompound nbt = getOrCreateNBT(stack);

        setDouble(nbt, key, value);
        saveNBT(stack, cbitemstack);
    }

    public static void setInt(org.bukkit.inventory.ItemStack cbitemstack, String key, int value)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        NBTTagCompound nbt = getOrCreateNBT(stack);

        setInt(nbt, key, value);
        saveNBT(stack, cbitemstack);
    }

    public static void setLong(org.bukkit.inventory.ItemStack cbitemstack, String key, long value)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        NBTTagCompound nbt = getOrCreateNBT(stack);

        setLong(nbt, key, value);
        saveNBT(stack, cbitemstack);
    }

    public static void setIntArray(org.bukkit.inventory.ItemStack cbitemstack, String key, int... value)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        NBTTagCompound nbt = getOrCreateNBT(stack);

        setIntArray(nbt, key, value);
        saveNBT(stack, cbitemstack);
    }

    public static void setStringList(org.bukkit.inventory.ItemStack cbitemstack, String key, List<String> value)
    {
        setStringList(cbitemstack, key, value.toArray(new String[0]));
    }

    public static void setStringList(org.bukkit.inventory.ItemStack cbitemstack, String key, String... value)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);
        NBTTagCompound nbt = getOrCreateNBT(stack);

        setStringList(nbt, key, value);
        saveNBT(stack, cbitemstack);
    }

    private static void saveNBT(ItemStack stack, org.bukkit.inventory.ItemStack cbstack)
    {
        CraftItemStack nbtcopy = CraftItemStack.asCraftMirror(stack);
        cbstack.setItemMeta(nbtcopy.getItemMeta());
    }

    private static NBTTagCompound getOrCreateNBT(ItemStack stack)
    {
        NBTTagCompound nbt;

        if (stack.hasTag())
        {
            nbt = stack.getTag();
        }
        else
        {
            nbt = new NBTTagCompound();
            stack.setTag(nbt);
        }

        return nbt;
    }

    public static boolean remove(org.bukkit.inventory.ItemStack cbitemstack, String key)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                container.remove(containerkey);

                return true;
            }
        }

        return false;
    }

    public static boolean has(org.bukkit.inventory.ItemStack cbitemstack, String key)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return true;
            }
        }

        return false;
    }

    public static String getString(org.bukkit.inventory.ItemStack cbitemstack, String key, String defaultvalue)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getString(containerkey);
            }
        }

        return defaultvalue;
    }

    public static float getFloat(org.bukkit.inventory.ItemStack cbitemstack, String key, float defaultvalue)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getFloat(containerkey);
            }
        }

        return defaultvalue;
    }

    public static double getDouble(org.bukkit.inventory.ItemStack cbitemstack, String key, double defaultvalue)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getDouble(containerkey);
            }
        }

        return defaultvalue;
    }

    public static boolean getBool(org.bukkit.inventory.ItemStack cbitemstack, String key, boolean defaultvalue)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getBoolean(containerkey);
            }
        }

        return defaultvalue;
    }

    public static int getInt(org.bukkit.inventory.ItemStack cbitemstack, String key, int defaultvalue)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getInt(containerkey);
            }
        }

        return defaultvalue;
    }

    public static int[] getIntArray(org.bukkit.inventory.ItemStack cbitemstack, String key, int... defaultvalue)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getIntArray(containerkey);
            }
        }

        return defaultvalue;
    }

    public static long getLong(org.bukkit.inventory.ItemStack cbitemstack, String key, long defaultvalue)
    {
        ItemStack stack = CraftItemStack.asNMSCopy(cbitemstack);

        if (stack != null && stack.hasTag())
        {
            NBTTagCompound container = navigateToContainer(stack.getTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getLong(containerkey);
            }
        }

        return defaultvalue;
    }
        
    /*// Entity getters and setters
    public static boolean hasNBT(org.bukkit.entity.Entity cbentity)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();
        
        return entity.getNBTTag() != null;
    }
    
    private static boolean hasNBT(Entity entity)
    {
        return entity.getNBTTag() != null;
    }

    private static NBTTagCompound getOrCreateNBT(Entity entity)
    {
        if(!hasNBT(entity))
        {
            NBTTagCompound nbt = new NBTTagCompound();
            entity.e(nbt); //Should be set?
            
            return nbt;
        }
        
        return entity.getNBTTag();
    }
    
    public static void setString(org.bukkit.entity.Entity cbentity, String key, String value)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();        
        NBTTagCompound nbt = getOrCreateNBT(entity);

        setString(nbt, key, value);
    }

    public static void setFloat(org.bukkit.entity.Entity cbentity, String key, float value)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();        
        NBTTagCompound nbt = getOrCreateNBT(entity);

        setFloat(nbt, key, value);
    }

    public static void setDouble(org.bukkit.entity.Entity cbentity, String key, double value)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();        
        NBTTagCompound nbt = getOrCreateNBT(entity);

        setDouble(nbt, key, value);
    }

    public static void setInt(org.bukkit.entity.Entity cbentity, String key, int value)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();        
        NBTTagCompound nbt = getOrCreateNBT(entity);

        setInt(nbt, key, value);
    }

    public static void setLong(org.bukkit.entity.Entity cbentity, String key, long value)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();        
        NBTTagCompound nbt = getOrCreateNBT(entity);

        setLong(nbt, key, value);
    }

    public static void setIntArray(org.bukkit.entity.Entity cbentity, String key, int... value)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();        
        NBTTagCompound nbt = getOrCreateNBT(entity);

        setIntArray(nbt, key, value);
    }

    public static void setStringList(org.bukkit.entity.Entity cbentity, String key, List<String> value)
    {
        setStringList(cbentity, key, value.toArray(new String[0]));
    }

    public static void setStringList(org.bukkit.entity.Entity cbentity, String key, String... value)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();        
        NBTTagCompound nbt = getOrCreateNBT(entity);

        setStringList(nbt, key, value);
    }

    public static boolean remove(org.bukkit.entity.Entity cbentity, String key)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                container.remove(containerkey);

                return true;
            }
        }

        return false;
    }

    public static boolean has(org.bukkit.entity.Entity cbentity, String key)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return true;
            }
        }

        return false;
    }

    public static String getString(org.bukkit.entity.Entity cbentity, String key, String defaultvalue)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getString(containerkey);
            }
        }

        return defaultvalue;
    }

    public static float getFloat(org.bukkit.entity.Entity cbentity, String key, float defaultvalue)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getFloat(containerkey);
            }
        }

        return defaultvalue;
    }

    public static double getDouble(org.bukkit.entity.Entity cbentity, String key, double defaultvalue)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getDouble(containerkey);
            }
        }

        return defaultvalue;
    }

    public static boolean getBool(org.bukkit.entity.Entity cbentity, String key, boolean defaultvalue)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getBoolean(containerkey);
            }
        }

        return defaultvalue;
    }

    public static int getInt(org.bukkit.entity.Entity cbentity, String key, int defaultvalue)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getInt(containerkey);
            }
        }

        return defaultvalue;
    }

    public static int[] getIntArray(org.bukkit.entity.Entity cbentity, String key, int... defaultvalue)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getIntArray(containerkey);
            }
        }

        return defaultvalue;
    }

    public static long getLong(org.bukkit.entity.Entity cbentity, String key, long defaultvalue)
    {
        CraftEntity crentity = (CraftEntity)cbentity;
        Entity entity = crentity.getHandle();      

        if (hasNBT(entity))
        {
            NBTTagCompound container = navigateToContainer(entity.getNBTTag(), key, false);
            String containerkey = finalKeyString(key);

            if (container != null && container.hasKey(containerkey))
            {
                return container.getLong(containerkey);
            }
        }

        return defaultvalue;
    }*/
    
    //Metadata wrappers
    public static MetadataValue getMetadata(org.bukkit.metadata.Metadatable entity, String key)
    {
        key = key.replace("/", "_");
        
        if(entity.hasMetadata(key))
        {
            return entity.getMetadata(key).get(0);
        }
        
        return null;
    }
    
    public static void setMetadata(org.bukkit.metadata.Metadatable entity, String key, Object value, Plugin plugin)
    {
        key = key.replace("/", "_");
        
        MetadataValue existing = getMetadata(entity, key);
        
        if(existing != null)
            existing.invalidate();
        
        entity.setMetadata(key, new FixedMetadataValue(plugin, value));
    }
    
    public static String getMetadataString(org.bukkit.metadata.Metadatable entity, String key, String defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asString();
    }
    
    public static double getMetadataDouble(org.bukkit.metadata.Metadatable entity, String key, double defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asDouble();
    }
    
    public static float getMetadataFloat(org.bukkit.metadata.Metadatable entity, String key, float defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asFloat();
    }
    
    public static int getMetadataInt(org.bukkit.metadata.Metadatable entity, String key, int defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asInt();
    }
    
    public static long getMetadataLong(org.bukkit.metadata.Metadatable entity, String key, long defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asLong();
    }
    
    public static boolean getMetadataBool(org.bukkit.metadata.Metadatable entity, String key, boolean defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asBoolean();
    }
    
    public static byte getMetadataByte(org.bukkit.metadata.Metadatable entity, String key, byte defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asByte();
    }
    
    public static short getMetadataShort(org.bukkit.metadata.Metadatable entity, String key, short defaultvalue)
    {
        MetadataValue v = getMetadata(entity, key);
        
        if(v == null)
            return defaultvalue;
        else
            return v.asShort();
    }
}
