package me.aleiv.core.paper.utilities.NoteBlockTool;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;

@CommandAlias("noteblock")
@CommandPermission("noteblock.cmd")
public class NoteBlockCMD extends BaseCommand {

    private @NonNull Core instance;

    public NoteBlockCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("check")
    public void check(Player sender) {
        var tool = instance.getNoteBlockTool();

        var block = sender.getTargetBlockExact(6);
        if(block != null){
            var noteBlock = tool.getNoteBlockData(block);
            var blockID = tool.getBlockID(noteBlock);
            
            sender.sendMessage(blockID);
        }

    }

}
