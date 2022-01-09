package me.aleiv.core.paper.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
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

    // TODO: prizes materials to craft

    public DecoItem(String name, int customModelData, String blockID, Material material, Catalog catalog, Rarity rarity,
            List<DecoTag> decoTags) {
        this.name = name;
        this.customModelData = customModelData;
        this.blockID = blockID;
        this.material = material;
        this.catalog = catalog;
        this.rarity = rarity;
        this.decoTags = decoTags;
    }

    public ItemStack getItemStack() {
        return new ItemBuilder(material).meta(meta -> meta.setCustomModelData(customModelData))
                .name(ChatColor.of(colorRarity.get(rarity)) + name).build();
    }

}
