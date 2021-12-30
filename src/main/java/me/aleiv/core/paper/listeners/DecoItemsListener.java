package me.aleiv.core.paper.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.aleiv.core.paper.Core;

public class DecoItemsListener implements Listener{
    
    Core instance;

    public DecoItemsListener(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void decoTags(PlayerInteractAtEntityEvent e){

    }

    

}
