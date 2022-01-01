package me.aleiv.core.paper.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
            var noteBlock = tool.getNoteBlockData(decoItem.getBlockID());
            
            if(noteBlock != null){
                block.setBlockData(noteBlock);
            }

        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        var block = e.getBlock();
        var manager = instance.getDecoLunchManager();
        var tool = instance.getNoteBlockTool();
        
        if(block.getType() == Material.NOTE_BLOCK && !tool.isDefaultNoteBlock(block)){
            e.setDropItems(false);
            
            var noteBlock = tool.getNoteBlockData(block);
            var blockID = tool.getBlockID(noteBlock);
            var decoitems = manager.getDecoItems(blockID);
            
            if(!decoitems.isEmpty()){
                var loc = block.getLocation();
                var decoItem = decoitems.get(0);
                block.getWorld().dropItemNaturally(loc, decoItem.getItemStack());
            }
            

        }
    }

}
