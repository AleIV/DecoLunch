package me.aleiv.core.paper.objects;

import java.util.HashMap;

import org.bukkit.Material;

import lombok.Data;

@Data
public class DecoPrize {
    HashMap<Material, Integer> prize;
    
    public DecoPrize(HashMap<Material, Integer> prize){
        this.prize = prize;
    }

}
