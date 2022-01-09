package me.aleiv.core.paper.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.DecoLunchManager;
import me.aleiv.core.paper.DecoLunchManager.Catalog;
import me.aleiv.core.paper.DecoLunchManager.DecoTag;
import me.aleiv.core.paper.DecoLunchManager.Rarity;
import me.aleiv.core.paper.objects.DecoItem;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("decolunch")
@CommandPermission("admin.perm")
public class DecoLunchCMD extends BaseCommand {

    private @NonNull Core instance;
    String errorColor = "#b2383a";
    String cmdColor = "#38b290";

    public DecoLunchCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("register")
    public void register(CommandSender sender, String name, int customModelData, Material material, Catalog catalog,
            Rarity rarity, DecoTag decoTag) {

        var decoitems = DecoLunchManager.decoItems;

        if (decoitems.containsKey(name)) {
            sender.sendMessage(ChatColor.of(errorColor) + "DecoItem " + name + " is already registered.");

        } else {
            var decoitem = new DecoItem(name, customModelData, "", material, catalog, rarity, List.of(decoTag));

            var player = (Player) sender;
            player.getInventory().addItem(decoitem.getItemStack());

            decoitems.put(name, decoitem);
            instance.pushJson();

            sender.sendMessage(ChatColor.of(cmdColor) + "DecoItem " + name + " registered.");

        }

    }

    @Subcommand("get")
    @CommandCompletion("@decoitems")
    public void get(CommandSender sender, String name) {

        var decoitems = DecoLunchManager.decoItems;

        if (!decoitems.containsKey(name)) {
            sender.sendMessage(ChatColor.of(errorColor) + "DecoItem " + name + " is not registered.");

        } else {
            var decoitem = decoitems.get(name);

            var player = (Player) sender;
            player.getInventory().addItem(decoitem.getItemStack());

            sender.sendMessage(ChatColor.of(cmdColor) + "DecoItem " + name + " given.");

        }

    }

}
