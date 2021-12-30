package me.aleiv.core.paper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.bukkit.Material;

import lombok.Data;
import me.aleiv.core.paper.objects.DecoItem;
import me.aleiv.core.paper.specialDecoItems.DecoLunch;

@Data
public class DecoLunchManager {
    
    Core instance;

    HashMap<String, DecoItem> decoitems = new HashMap<>();

    public DecoLunchManager(Core instance){
        this.instance = instance;

        instance.pullJson();
        initSpecialDecoItems();

        initAnnotations();

    }

    private void initSpecialDecoItems(){
        decoitems.put("DecoLunch", new DecoLunch("DecoLunch", 0, 0, Material.AZURE_BLUET, Catalog.ADMIN, Rarity.COMMON, List.of(DecoTag.GUI)));
    }

    private void initAnnotations(){
        var manager = instance.getCommandManager();

        manager.getCommandCompletions().registerAsyncCompletion("bool", c -> {
            return ImmutableList.of("true", "false");
        });

        manager.getCommandCompletions().registerAsyncCompletion("catalog", c -> {
            return Arrays.stream(Catalog.values()).map(val -> val.toString()).toList();
        });

        manager.getCommandCompletions().registerAsyncCompletion("decotag", c -> {
            return Arrays.stream(DecoTag.values()).map(val -> val.toString()).toList();
        });

        manager.getCommandCompletions().registerAsyncCompletion("rarity", c -> {
            return Arrays.stream(Rarity.values()).map(val -> val.toString()).toList();
        });
    }
    //18-53

    public enum Catalog{
        ADMIN, RUSTIC, SECURITY
    }

    public enum DecoTag{
        SIT, GUI, GRAVITY, PUT
    }

    public enum DecoGUI{
        DECOLUNCH, NONE
    }

    public enum Rarity{
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }



}
