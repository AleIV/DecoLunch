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

    public static HashMap<Rarity, String> colorItem = new HashMap<Rarity, String>() {
        {
            put(Rarity.COMMON, "#6d777e");
            put(Rarity.UNCOMMON, "#81bc2f");
            put(Rarity.RARE, "#2fbc9d");
            put(Rarity.EPIC, "#bc2f3b");
            put(Rarity.LEGENDARY, "#eba400");
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
                .name(ChatColor.of(colorItem.get(rarity)) + name).build();
    }

}
