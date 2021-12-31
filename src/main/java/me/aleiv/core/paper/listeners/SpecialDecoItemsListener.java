package me.aleiv.core.paper.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aleiv.core.paper.Core;

public class SpecialDecoItemsListener implements Listener{
    
    Core instance;

    public SpecialDecoItemsListener(Core instance){
        this.instance = instance;
    }

    //TODO: hammers

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        var block = e.getClickedBlock();
        var action = e.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.NOTE_BLOCK) {
            
            var tool = instance.getNoteBlockTool();
            var noteBlock = tool.getNoteBlockData(block);
            var guiCodes = instance.getDecoLunchManager().getGuiCodes();
            var blockID = tool.getBlockID(noteBlock);

            if(guiCodes.containsKey(blockID)){
                var gui = guiCodes.get(blockID);
                var player = e.getPlayer();
                gui.open(player);

                //TODO: SOUNDS ON OPEN GUI SWITCH
            }

        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        var block = e.getBlock();
        var item = e.getItemInHand();
        var manager = instance.getDecoLunchManager();
        
        if(block.getType() == Material.NOTE_BLOCK && item != null && manager.isDecoItem(item)){
            var tool = instance.getNoteBlockTool();
            var decoItem = manager.getDecoItem(item);
            var noteBlock = tool.getNoteBlock(decoItem.getBlockID());
            var player = e.getPlayer();
            player.sendMessage(decoItem.getBlockID());
            if(noteBlock != null){
                block.setBlockData(noteBlock);
            }

        }
    }

}
