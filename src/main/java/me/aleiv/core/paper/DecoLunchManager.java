package me.aleiv.core.paper;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import me.aleiv.core.paper.gui.DecoLunchTableGUI;
import me.aleiv.core.paper.objects.DecoItem;
import us.jcedeno.libs.rapidinv.RapidInv;

@Data
public class DecoLunchManager {
    
    Core instance;

    HashMap<String, DecoItem> decoitems = new HashMap<>();
    HashMap<String, RapidInv> guiCodes = new HashMap<>();

    public DecoLunchManager(Core instance){
        this.instance = instance;

        initSpecialDecoItems();

    }

    public boolean isDecoItem(ItemStack item){
        var meta = item.getItemMeta();
        if(meta.hasDisplayName()){
            var name = meta.getDisplayName();
            return !decoitems.values().stream().filter(deco -> name.contains(deco.getName())).toList().isEmpty();
        }
        return false;
    }

    public DecoItem getDecoItem(ItemStack item){
        var meta = item.getItemMeta();
        if(isDecoItem(item)){
            var name = meta.getDisplayName();
            return decoitems.values().stream().filter(deco -> name.contains(deco.getName())).findAny().orElse(null);
        }
        return null;
    }

    private void initSpecialDecoItems(){
        var tool = instance.getNoteBlockTool();

        var blockID = tool.getBlockIDbyData("harp", 0, false);
        decoitems.put("DecoLunch", new DecoItem("DecoLunch", 0, blockID, Material.NOTE_BLOCK, Catalog.ADMIN, Rarity.COMMON, List.of()));
        guiCodes.put(blockID, new DecoLunchTableGUI());

    }

    //18-53

    public enum Catalog{
        ADMIN, RUSTIC, SECURITY
    }

    public enum DecoTag{
        SIT
    }

    public enum Rarity{
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }



}
