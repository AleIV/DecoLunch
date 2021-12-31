package me.aleiv.core.paper.listeners;

import java.util.Arrays;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import me.aleiv.core.paper.Core;

public class CanceledListener implements Listener{
    
    Core instance;

    public CanceledListener(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onRenameCustomModel(PrepareAnvilEvent e){
        var inv = e.getInventory();
        var item = inv.getItem(0);
        var manager = instance.getDecoLunchManager();
        if(item != null && manager.isDecoItem(item)){
            e.setResult(null);
        }
    }

    //@EventHandler
    public void onCraftCustomModel(PrepareItemCraftEvent e){
        var table = e.getInventory().getContents();
        var manager = instance.getDecoLunchManager();
        var contains = !Arrays.stream(table).filter(item -> item != null && manager.isDecoItem(item)).toList().isEmpty();
        if(contains){
            e.getInventory().setResult(null);
        }

    }
    
}
