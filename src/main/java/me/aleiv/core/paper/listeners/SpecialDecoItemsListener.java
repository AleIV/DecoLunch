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

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        var block = e.getClickedBlock();
        var action = e.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.NOTE_BLOCK) {

            var tool = instance.getNoteBlockTool();
            var noteBlock = tool.getNoteBlockData(block);
            var guiCodes = instance.getDecoLunchManager().getGuiCodes();
            var blockID = tool.getBlockID(noteBlock);

            var player = e.getPlayer();
            if(guiCodes.containsKey(blockID) && !player.isSneaking()){

                var manager = instance.getDecoLunchManager();
                var guiCode = guiCodes.get(blockID);
                var location = block.getRelative(e.getBlockFace()).getLocation();
                var gui = manager.getGui(guiCode, location);
                gui.open(player);

            }

        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        var block = e.getBlock();
        var item = e.getItemInHand();
        var manager = instance.getDecoLunchManager();
        
        if(block.getType() == Material.NOTE_BLOCK && item != null){
            var tool = instance.getNoteBlockTool();

            if(manager.isDecoItem(item)){
                var decoItem = manager.getDecoItem(item);
                var noteBlock = tool.getNoteBlockData(decoItem.getBlockID());
                if(noteBlock != null){
                    block.setBlockData(noteBlock);
                }
            }else{
                block.setBlockData(tool.getDefaultBlockData());
            }

        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        var block = e.getBlock();
        var manager = instance.getDecoLunchManager();
        var tool = instance.getNoteBlockTool();
        
        if(block.getType() == Material.NOTE_BLOCK){
            if(tool.isDefaultNoteBlock(block)) return;
            e.setDropItems(false);
            
            var noteBlock = tool.getNoteBlockData(block);
            var blockID = tool.getBlockID(noteBlock);
            var decoItem = manager.getDecoItemByBlockID(blockID);
            
            if(decoItem != null){
                var loc = block.getLocation();
                block.getWorld().dropItemNaturally(loc, decoItem.getItemStack());
            }

        }
    }

}
