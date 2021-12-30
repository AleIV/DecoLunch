package me.aleiv.core.paper.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.DecoLunchManager.Catalog;
import me.aleiv.core.paper.DecoLunchManager.DecoTag;
import me.aleiv.core.paper.DecoLunchManager.Rarity;
import me.aleiv.core.paper.objects.DecoItem;

@CommandAlias("decolunch")
@CommandPermission("admin.perm")
public class DecoLunchCMD extends BaseCommand {

    private @NonNull Core instance;
    String errorColor = "<#b2383a>";
    String cmdColor = "<#38b290>";

    public DecoLunchCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("register")
    public void register(CommandSender sender, String name, int customModelData, int blockID, Material material, Catalog catalog, Rarity rarity, List<DecoTag> decoTags) {

        var manager = instance.getDecoLunchManager();
        var decoitems = manager.getDecoitems();

        if(decoitems.containsKey(name)){
            sender.sendMessage(errorColor + "DecoItem " + name + " is already registered.");

        }else{
            var decoitem = new DecoItem(name, customModelData, blockID, material, catalog, rarity, decoTags);

            var player = (Player) sender;
            player.getInventory().addItem(decoitem.getItemStack());
            
            decoitems.put(name, decoitem);
            instance.pushJson();

            sender.sendMessage(cmdColor + "DecoItem " + name + " registered.");

        }
        
        
    }

    @Subcommand("get")
    public void get(CommandSender sender, String name) {

        var manager = instance.getDecoLunchManager();
        var decoitems = manager.getDecoitems();

        if(!decoitems.containsKey(name)){
            sender.sendMessage(errorColor + "DecoItem " + name + " is not registered.");

        }else{
            var decoitem = decoitems.get(name);

            var player = (Player) sender;
            player.getInventory().addItem(decoitem.getItemStack());

            sender.sendMessage(cmdColor + "DecoItem " + name + " given.");

        }
        
        
    }

}
