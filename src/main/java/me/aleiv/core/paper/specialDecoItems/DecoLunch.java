package me.aleiv.core.paper.specialDecoItems;

import java.util.List;

import org.bukkit.Material;

import me.aleiv.core.paper.DecoLunchManager.Catalog;
import me.aleiv.core.paper.DecoLunchManager.DecoGUI;
import me.aleiv.core.paper.DecoLunchManager.DecoTag;
import me.aleiv.core.paper.DecoLunchManager.Rarity;
import me.aleiv.core.paper.gui.DecoLunchTableGUI;
import me.aleiv.core.paper.objects.DecoItem;

public class DecoLunch extends DecoItem{

    DecoLunchTableGUI decoLunchTableGUI;
    DecoGUI decoGUI;

    public DecoLunch(String name, int customModelData, int blockID, Material material, Catalog catalog, Rarity rarity,
            List<DecoTag> decoTags) {
        super(name, customModelData, blockID, material, catalog, rarity, decoTags);
        
        this.decoLunchTableGUI = new DecoLunchTableGUI();
    }



    
}
