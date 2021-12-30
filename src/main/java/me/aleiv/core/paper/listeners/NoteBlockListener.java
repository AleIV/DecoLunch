package me.aleiv.core.paper.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aleiv.core.paper.Core;

public class NoteBlockListener implements Listener{
    
    Core instance;

    public NoteBlockListener(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        var block = e.getClickedBlock();
        
        if(block != null && block.getType() == Material.NOTE_BLOCK){
            e.setCancelled(true);
            
        }

    }

}
