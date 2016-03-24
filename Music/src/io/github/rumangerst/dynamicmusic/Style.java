/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/**
 * A collection of songs with conditions
 * @author ruman
 */
public class Style implements ConfigurationSerializable
{
    private String name;
    private List<String> songs = new ArrayList<>();
    private Expr conditions = null;
    
    private HashMap<Character, Condition> condition_map = new HashMap<>();
    
    public Style(Map<String, Object> serialized)
    {
        //DynamicMusicPlugin.LOGGER.info("" + serialized);
        //DynamicMusicPlugin.LOGGER.info("" + serialized.size());
        
        name = serialized.get("name").toString();
        
        String term = serialized.getOrDefault("condition-term", "").toString();
        Object condition_str = serialized.getOrDefault("conditions", null);
        
        if(condition_str != null)
        {
            List<Condition> conditions = (List<Condition>)condition_str;
            
            for(Condition cond : conditions)
            {
                this.condition_map.put(cond.getLabel(), cond);
            }
        }
        
        if(term.replace(" ", "").isEmpty())
            this.conditions = null;
        else
        {
            this.conditions = parseConditionTerm(term);
            
            //DynamicMusicPlugin.LOGGER.info(this.conditions.toString());
        }
        
        List<List<String>> buckets = new ArrayList<>((List<List<String>>)serialized.get("songs"));
        this.songs = buckets.get(0);
    }
    
    private Expr parseConditionTerm(String input)
    {
        input = input.replace(" ", "");        
        
        Expr expr = null;       
        
        for(int i = 0; i < input.length(); ++i)
        {
            String sub = input.substring(i);
            char chr = input.charAt(i);
            
            if(sub.startsWith("AND(") || sub.startsWith("OR(") || sub.startsWith("NOT("))
            {
                Expr e = null;
                
                if(expr == null)
                {
                    e = expr = new Expr();                    
                }
                else if(expr.L == null)
                    e = expr.L = new Expr();
                else
                    e = expr.R = new Expr();
                
                e.parent = expr;
                e.O = sub.startsWith("OR(") ? LogicalOperation.OR : LogicalOperation.valueOf(sub.substring(0, 3));
                expr = e; //Go 1 deeper
                
                //DynamicMusicPlugin.LOGGER.info("op: " + e.O + " i:" + i + " sub:" + sub);
            }            
            else if(chr == ',')
            {
                // Do nothing
            }          
            else if(chr == ')')
            {
                expr = expr.parent; //G 1 back
            }
            else if(i == 0 || input.charAt(i-1) == '(' || input.charAt(i-1) == ',')
            {
                Expr e = null;
                
                if(expr == null)
                {
                    e = expr = new Expr();                    
                }
                else if(expr.L == null)
                    e = expr.L = new Expr();
                else
                    e = expr.R = new Expr();
                
                e.parent = expr;
                //expr = e; //Go 1 deeper

                e.O = LogicalOperation.IS;               
                e.C = condition_map.get(chr); //Is a IS term
            }
        }
        
        return expr;

        /*if (input.length() == 1)
        {
            Expr e = new Expr();
            e.O = LogicalOperation.IS;
            e.C = condition_map.get(input.charAt(0));

            return e;
        }
        else
        {
            HashMap<Integer, Integer> bracket_positions = new HashMap<>();
            Stack<Integer> bracket_stack = new Stack<>();
            
            for (int i = 0; i < input.length(); ++i)
            {
                char c = input.charAt(i);
                
                if (c == '(')
                {
                    bracket_stack.push(i);
                }
                else if (c == ')')
                {
                    int start = bracket_stack.pop();                    
                    bracket_positions.put(start, i);
                }
            }            
            
            // input begins with OPERATION(x,y,...)
            int first_bracket = input.indexOf("(");
            String operation_det = input.substring(0, first_bracket);
            LogicalOperation operation = LogicalOperation.valueOf(operation_det);

            int current_depth = depth;

            Expr expr = new Expr();
            expr.O = operation;

            for (int i = 0; i < input.length(); ++i)
            {
                /*char c = input.charAt(i);

                if (c == '(')
                {
                    ++current_depth;
                }
                else if (c == ')')
                {
                    --current_depth;
                }
                else if (current_depth == depth && c != ',')
                {
                    //We are now at the first index of another expr
                    Expr subexpr = parseConditionTerm(input.substring(i), current_depth);

                    if (expr.L == null)
                    {
                        expr.L = subexpr;
                    }
                    else if (expr.R == null)
                    {
                        expr.R = subexpr;
                    }
                    else
                    {
                        throw new IllegalArgumentException("Too many parameters in term!");
                    }
                }*/
            /*}

            return expr;
        }*/
    }
    
    public List<Song> getSongInstances(DynamicMusicAPI api)
    {
        ArrayList<Song> songs = new ArrayList<>();
        
        for(String s : this.songs)
        {
            Song song = api.getSongFromId(s);
            
            if(song == null)
                song = api.getSongFromName(s);
            
            if(song != null)
                songs.add(song);
        }
        
        return songs;
    }
    
    public List<String> getSongs()
    {
        return songs;
    }
    
    public String getName()
    {
        return name;
    }
    
    public boolean applies(Player player)
    {
        if(conditions == null)
            return true;
        
        return conditions.applies(player);
    } 

    @Override
    public Map<String, Object> serialize()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static enum LogicalOperation
    {
        AND,
        OR,
        NOT,
        IS
    }
    
    public static class Expr
    {
        public Expr parent = null;
        public Expr L = null;
        public Expr R = null;
        public Condition C = null;
        public LogicalOperation O = LogicalOperation.IS;
        
        public boolean applies(Player player)
        {
            switch(O)
            {
                case AND:
                    return L.applies(player) && R.applies(player);
                case OR:
                    return L.applies(player) || R.applies(player);
                case IS:
                    return C.applies(player);
                case NOT:
                    return !C.applies(player);
            }
            
            return false;
        }
        
        @Override
        public String toString()
        {
            String d = O.name() +"(";
            
            if(C != null)            
                d += C.getLabel();
            else if(R == null)
                d += L.toString();
            else 
                d += L.toString() + ", " + R.toString();
            
            return d +")";
        }
    }
}
