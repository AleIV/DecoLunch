package me.aleiv.core.paper.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.DecoLunchManager.DecoTag;

public class DecoItemsListener implements Listener{
    
    Core instance;

    public DecoItemsListener(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onPlace(PlayerInteractAtEntityEvent e){
        var entity = e.getRightClicked();
        if(entity instanceof ArmorStand stand){
            var equip = stand.getEquipment();
            var helmet = equip.getHelmet();
            var manager = instance.getDecoLunchManager();
            if(helmet != null && manager.isDecoItem(helmet)){
                var decoItem = manager.getDecoItem(helmet);
                var decoTags = decoItem.getDecoTags();
                if(decoTags.contains(DecoTag.SIT) && stand.getPassengers().isEmpty()){
                    var player = e.getPlayer();
                    stand.addPassenger(player);
                }
            }
            
        }
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent e){
        var action = e.getAction();
        if(action == Action.RIGHT_CLICK_BLOCK){

        }
    }

    

}
