package me.aleiv.core.paper.listeners;

import org.bukkit.event.Listener;

import me.aleiv.core.paper.Core;

public class GlobalListener implements Listener{
    
    Core instance;

    public GlobalListener(Core instance){
        this.instance = instance;
    }


}
