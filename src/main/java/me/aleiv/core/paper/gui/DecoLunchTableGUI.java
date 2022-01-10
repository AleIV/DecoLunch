package me.aleiv.core.paper.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.DecoLunchManager;
import me.aleiv.core.paper.DecoLunchManager.Catalog;
import me.aleiv.core.paper.DecoLunchManager.Rarity;
import me.aleiv.core.paper.objects.DecoItem;
import me.aleiv.core.paper.objects.DecoPrize;
import me.aleiv.core.paper.utilities.utils.NegativeSpaces;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class DecoLunchTableGUI extends DecoGUIBase {

    public DecoLunchTableGUI(Location location) {
        super(9 * 6, ChatColor.WHITE + NegativeSpaces.get(-8) + Character.toString('\u3400'), location);

        refreshPages();
        update();

    }

    Rarity rarity = Rarity.ALL;
    Catalog catalog = Catalog.ALL;
    int rarityN = 0;
    int catalogN = 0;
    int page = 1;

    HashMap<Integer, List<ItemStack>> pages = new HashMap<>();

    public void update() {
        updateCatalog();
        updateLeftArrow();
        updateRightArrow();
        updateRarity();
        updatePage();
        refreshPage();
    }

    public boolean containsDecoPrize(DecoPrize decoPrize, PlayerInventory inv){
        var contains = true;
        try {
            for (var item : decoPrize.getPrize().entrySet()) {
                var i = new ItemBuilder(item.getKey()).amount(item.getValue()).build();
                if(!inv.containsAtLeast(i, item.getValue())){
                    contains = false;
                }
            }   
        } catch (Exception e) {
            return false;
        }
        return contains;
    }

    public void removeDecoPrize(DecoPrize decoPrize, PlayerInventory inv){
        for (var item : decoPrize.getPrize().entrySet()) {
            var i = new ItemBuilder(item.getKey()).amount(item.getValue()).build();
            inv.removeItem(i);
        }
    }

    public void refreshPage() {
        if (pages.isEmpty()) {
            for (int i = 0; i < 45; i++) {
                this.removeItem(i);
            }
        } else {
            var manager = Core.getInstance().getDecoLunchManager();
            
            var p = pages.get(page);
            var size = p.size();
            for (int i = 0; i < 45; i++) {
                if (i < size) {
                    var item = p.get(i);
                    var decoItem = manager.getDecoItem(item);
                    var decoPrize = decoItem.getPrize();
                    this.setItem(i, item, handler -> {
                        var player = (Player) handler.getWhoClicked();
                        var inv = player.getInventory();
                        
                        if(containsDecoPrize(decoPrize, inv)){
                            removeDecoPrize(decoPrize, inv);
                            inv.addItem(item);


                        }else{
                            player.sendMessage(ChatColor.of("#d3274f") + "You don't have the needed materials for this.");
                        }
                    });
                } else {
                    this.removeItem(i);
                }

            }
        }

    }

    public void refreshPages() {
        pages.clear();
        // 0-44
        var decoItems = DecoLunchManager.decoItems;
        var itemList = decoItems.values().stream().filter(decoItem -> decoItem.getCatalog() != Catalog.ADMIN)
                .collect(Collectors.toList());

        if (catalog != Catalog.ALL) {
            itemList = itemList.stream().filter(decoItem -> decoItem.getCatalog() == catalog)
                    .collect(Collectors.toList());
        }
        if (rarity != Rarity.ALL) {
            itemList = itemList.stream().filter(decoItem -> decoItem.getRarity() == rarity)
                    .collect(Collectors.toList());

        }

        var pageN = 1;
        while (!itemList.isEmpty()) {
            List<ItemStack> list = new ArrayList<>();
            var c = 0;
            while (c < 45 && !itemList.isEmpty()) {
                var i = itemList.get(0);
                list.add(i.getItemStack());
                itemList.remove(i);
                c++;
            }

            pages.put(pageN, list);
            pageN++;
        }

    }

    public void switchCatalog() {
        var catalogList = Arrays.stream(Catalog.values()).collect(Collectors.toList());
        catalogList.remove(Catalog.ADMIN);

        catalogN++;
        if (catalogN >= catalogList.size())
            catalogN = 0;
        var c = catalogList.get(catalogN);
        catalog = c;

        page = 1;
        refreshPages();
        update();

    }

    public void switchRarity() {
        var rarityList = Arrays.stream(Rarity.values()).collect(Collectors.toList());

        rarityN++;
        if (rarityN >= rarityList.size())
            rarityN = 0;
        var r = rarityList.get(rarityN);
        rarity = r;

        page = 1;
        
        refreshPages();
        update();
    }

    public void updateLeftArrow() {
        var item = getItemBuilder(1).name(ChatColor.of("#fac638") + "Page " + getPreviousPage()).build();
        this.setItem(45, item, handler -> {
            if (page - 1 > 0) {
                page--;
                
                update();
            }
        });
    }

    public void updateRightArrow() {
        var item = getItemBuilder(2).name(ChatColor.of("#fac638") + "Page " + getNextPage()).build();
        this.setItem(53, item, handler -> {
            if (page < pages.size()) {
                page++;
                
                update();
            }
        });
    }

    public void updateCatalog() {
        var color = DecoItem.colorCatalog.get(catalog);
        var item = getItemBuilder(catalogModel.get(catalog))
                .name(ChatColor.of(color) + "Catalog: " + formatName(catalog.toString())).build();
        this.setItem(48, item, handler -> {
            switchCatalog();
        });
    }

    public void updatePage() {
        var item = getItemBuilder(20).name(ChatColor.of("#fac638") + "Page " + page).amount(page).build();
        this.setItem(49, item, handler -> {
            // TODO: gui pages
        });
    }

    public void updateRarity() {
        var color = DecoItem.colorRarity.get(rarity);
        var item = getItemBuilder(rarityModel.get(rarity))
                .name(ChatColor.of(color) + "Rarity: " + formatName(rarity.toString())).build();
        this.setItem(50, item, handler -> {
            switchRarity();
        });
    }

    public ItemBuilder getItemBuilder(int customModelData) {
        return new ItemBuilder(Material.GOLDEN_HOE).flags(ItemFlag.HIDE_ATTRIBUTES)
                .meta(meta -> meta.setCustomModelData(customModelData));
    }

    public int getPreviousPage() {
        return page - 1;
    }

    public int getNextPage() {
        return page + 1;
    }

    public String formatName(String string) {
        var str = string.toLowerCase();
        var array = str.toCharArray();

        var upper = true;
        var count = 0;
        for (char c : array) {
            if (upper) {
                array[count] = Character.toUpperCase(c);
                upper = false;
            } else if (c == '_') {
                upper = true;
            }
            count++;
        }

        var newString = String.valueOf(array);
        return newString.replace("_", " ");
    }

    public static HashMap<Catalog, Integer> catalogModel = new HashMap<Catalog, Integer>() {
        {
            put(Catalog.RUSTIC, 10);
            put(Catalog.SECURITY, 11);
            put(Catalog.MODERN, 12);
            put(Catalog.SEASONAL, 13);
            put(Catalog.DECORATION, 14);
            put(Catalog.ALL, 15);
        }
    };

    public static HashMap<Rarity, Integer> rarityModel = new HashMap<Rarity, Integer>() {
        {
            put(Rarity.COMMON, 5);
            put(Rarity.UNCOMMON, 6);
            put(Rarity.RARE, 7);
            put(Rarity.EPIC, 8);
            put(Rarity.LEGENDARY, 9);
            put(Rarity.ALL, 21);

        }
    };

}
