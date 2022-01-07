package me.aleiv.core.paper.gui;

import org.bukkit.Location;
import org.bukkit.Material;

import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class DecoLunchTableGUI extends DecoGUIBase{

    public DecoLunchTableGUI(Location location) {
        super(9*6, "DecoLunch Table", location);
    }

    int page;
    
    public void getPreviousPage(){
        
    }

    public void getNextPage(){

    }


    public void updateLeftArrow(){
        var item = new ItemBuilder(Material.GOLDEN_HOE).meta(meta -> meta.setCustomModelData(1)).name(ChatColor.of("#fac638") + "Page " + page).build();
        this.setItem(45, item, handler ->{

        });
    }

    public void updateRightArrow(){

    }

    public void updateCatalog(){

    }

    public void updatePage(){

    }

    public void updateRarity(){

    }

}
