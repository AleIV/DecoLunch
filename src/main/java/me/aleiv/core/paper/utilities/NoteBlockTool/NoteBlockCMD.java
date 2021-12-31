package me.aleiv.core.paper.utilities.NoteBlockTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import lombok.NonNull;
import me.aleiv.core.paper.Core;

@CommandAlias("noteblock")
@CommandPermission("noteblock.cmd")
public class NoteBlockCMD extends BaseCommand {

    private @NonNull Core instance;

    public NoteBlockCMD(Core instance) {
        this.instance = instance;

    }

}
