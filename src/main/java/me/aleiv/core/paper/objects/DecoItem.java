package me.aleiv.core.paper.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import me.aleiv.core.paper.DecoLunchManager.Catalog;
import me.aleiv.core.paper.DecoLunchManager.DecoTag;
import me.aleiv.core.paper.DecoLunchManager.Rarity;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

@Data
public class DecoItem {

    public static HashMap<Rarity, String> colorRarity = new HashMap<Rarity, String>() {
        {
            put(Rarity.COMMON, "#6d777e");
            put(Rarity.UNCOMMON, "#81bc2f");
            put(Rarity.RARE, "#2fbc9d");
            put(Rarity.EPIC, "#bc2f3b");
            put(Rarity.LEGENDARY, "#eba400");
            put(Rarity.ALL, "#9585db");
        }
    };

    public static HashMap<Catalog, String> colorCatalog = new HashMap<Catalog, String>() {
        {
            put(Catalog.ALL, "#9585db");
            put(Catalog.ADMIN, "#aa0e0e");
            put(Catalog.DECORATION, "#f3936d");
            put(Catalog.MODERN, "#a1f7ec");
            put(Catalog.SEASONAL, "#f0dc47");
            put(Catalog.RUSTIC, "#a7300c");
            put(Catalog.SECURITY, "#7d9be8");

        }
    };

    String name;
    int customModelData;
    String blockID;
    Material material;
    Catalog catalog;
    Rarity rarity;
    List<DecoTag> decoTags = new ArrayList<>();
    String prize;

    public DecoItem(String name, int customModelData, String blockID, Material material, Catalog catalog, Rarity rarity,
            List<DecoTag> decoTags, String prize) {
        this.name = name;
        this.customModelData = customModelData;
        this.blockID = blockID;
        this.material = material;
        this.catalog = catalog;
        this.rarity = rarity;
        this.decoTags = decoTags;
        this.prize = prize;
    }

    public DecoPrize getPrize() {
        try {
            var split = prize.split(";");
            HashMap<Material, Integer> map = new HashMap<>();
            for (String string : split) {
                var mp = string.split("-");
                var material = Material.valueOf(mp[0]);
                var value = Integer.parseInt(mp[1]);
                map.put(material, value);
            }

            return new DecoPrize(map);

        } catch (Exception e) {

        }

        return null;

    }

    public ItemStack getItemStack() {
        List<String> lines = new ArrayList<>();
        var decoPrize = getPrize();
        if(decoPrize != null){
            lines.add(ChatColor.of("#bb636c") + "Price: ");
            for (var entry : decoPrize.getPrize().entrySet()) {
    
                lines.add(ChatColor.of("#dcb841") + "- " + formatName(entry.getKey().toString()) + ": " + entry.getValue());
            }
        }

        return new ItemBuilder(material).meta(meta -> meta.setCustomModelData(customModelData))
                .name(ChatColor.of(colorRarity.get(rarity)) + name).addLore(lines).flags(ItemFlag.HIDE_ATTRIBUTES).build();
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

}
