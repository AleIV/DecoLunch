package me.aleiv.core.paper.listeners;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.DecoLunchManager.DecoTag;

public class DecoItemsListener implements Listener {

    Core instance;

    public DecoItemsListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlace(PlayerInteractAtEntityEvent e) {
        var entity = e.getRightClicked();
        if (entity instanceof ArmorStand stand) {
            var player = e.getPlayer();
            var equip = stand.getEquipment();
            var helmet = equip.getHelmet();
            var hand = e.getHand();
            var equipment = player.getEquipment();
            var item = equipment.getItem(hand);
            var manager = instance.getDecoLunchManager();

            if (helmet != null && manager.isDecoItem(helmet)) {
                var decoItem = manager.getDecoItem(helmet);
                var decoTags = decoItem.getDecoTags();

                if (item != null && manager.isDecoHammer(item)) {
                    // remove deco item directly

                    manager.damageHammer(item);
                    var loc = stand.getLocation();
                    stand.remove();
                    loc.getWorld().dropItemNaturally(loc, decoItem.getItemStack());

                } else if (decoTags.contains(DecoTag.SIT) && stand.getPassengers().isEmpty()) {

                    stand.addPassenger(player);
                }
            }

        }
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        var action = e.getAction();
        var hand = e.getHand();
        var player = e.getPlayer();
        var equipment = player.getEquipment();
        var item = equipment.getItem(hand);
        var block = e.getClickedBlock();

        if (item == null || block == null)
            return;

        var manager = instance.getDecoLunchManager();
        var loc = block.getLocation();

        if (action == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.BARRIER) {

            if (manager.isDecoHammer(item)) {
                var decoItemStand = manager.getDecoStand(loc);

                if (decoItemStand != null) {
                    // remove decoitem from block
                    var decoItem = manager.getDecoItem(decoItemStand);
                    block.setType(Material.AIR);
                    decoItemStand.remove();
                    manager.damageHammer(item);
                    loc.getWorld().dropItemNaturally(loc, decoItem.getItemStack());

                } else {
                    block.setType(Material.AIR);
                    // remove barrier that doesnt have deco stand
                }
                return;
            }
            var decoStand = manager.getDecoStand(loc);

            if(decoStand != null && manager.isDecoStand(decoStand)) {
                
                var decoItem = manager.getDecoItem(decoStand);
                var decoTags = decoItem.getDecoTags();
                
                if (decoTags.contains(DecoTag.SIT) && decoStand.getPassengers().isEmpty()) {
                    decoStand.addPassenger(player);
                }
            }

        } else if (action == Action.RIGHT_CLICK_BLOCK && manager.isDecoItem(item)) {
            // put deco item to in loc
            block = block.getRelative(e.getBlockFace());
            loc = block.getLocation();
            var decoItem = manager.getDecoItem(item);
            var tags = decoItem.getDecoTags();
            if (tags.contains(DecoTag.ENTITY)) {

                manager.spawnDecoStand(loc, player, decoItem);
                if(item.getAmount() == 1){
                    equipment.setItem(hand, null);
                }else{
                    item.setAmount(item.getAmount()-1);
                    equipment.setItem(hand, item);
                }
                
                if (tags.contains(DecoTag.BARRIER)) {
                    loc.getBlock().setType(Material.BARRIER);
                }
            }
        }
    }

}
