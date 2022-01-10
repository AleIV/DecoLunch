package me.aleiv.core.paper;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;
import me.aleiv.core.paper.commands.DecoLunchCMD;
import me.aleiv.core.paper.listeners.CanceledListener;
import me.aleiv.core.paper.listeners.DecoItemsListener;
import me.aleiv.core.paper.listeners.GlobalListener;
import me.aleiv.core.paper.listeners.SpecialDecoItemsListener;
import me.aleiv.core.paper.objects.DecoItem;
import me.aleiv.core.paper.utilities.NoteBlockTool.NoteBlockTool;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import me.aleiv.core.paper.utilities.utils.JsonConfig;
import me.aleiv.core.paper.utilities.utils.NegativeSpaces;
import me.aleiv.core.paper.utilities.utils.ResourcePackManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.libs.rapidinv.RapidInvManager;

@SpigotPlugin
public class Core extends JavaPlugin {

    private static @Getter Core instance;
    private @Getter PaperCommandManager commandManager;
    private @Getter static MiniMessage miniMessage = MiniMessage.get();
    private @Getter Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @Getter DecoLunchManager decoLunchManager;
    private @Getter NoteBlockTool noteBlockTool;
    private @Getter ResourcePackManager resourcePackManager;
    private @Getter ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        RapidInvManager.register(this);
        BukkitTCT.registerPlugin(this);
        NegativeSpaces.registerCodes();

        //COMMANDS

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new DecoLunchCMD(this));

        this.noteBlockTool = new NoteBlockTool(this);

        this.decoLunchManager = new DecoLunchManager(this);

        instance.pullJson();

        //LISTENERS

        Bukkit.getPluginManager().registerEvents(new DecoItemsListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpecialDecoItemsListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CanceledListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GlobalListener(this), this);

        resourcePackManager = new ResourcePackManager(this);
        resourcePackManager.setResoucePackURL("https://download.mc-packs.net/pack/f768106114052c8bcf7e1c2303d0aa24de7afe8c.zip");
        resourcePackManager.setResourcePackHash("f768106114052c8bcf7e1c2303d0aa24de7afe8c");

    }

    @Override
    public void onDisable() {

    }

    public Component parse(String str){
        return MiniMessage.get().parse(str);
    }

    public void pushJson(){
        var list = DecoLunchManager.decoItems;

        try {
            var jsonConfig = new JsonConfig("decoitems.json");
            var json = gson.toJson(list);
            var obj = gson.fromJson(json, JsonObject.class);
            jsonConfig.setJsonObject(obj);
            jsonConfig.save();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void pullJson(){
        try {
            var jsonConfig = new JsonConfig("decoitems.json");
            var list = jsonConfig.getJsonObject();
            var iter = list.entrySet().iterator();
            var map = DecoLunchManager.decoItems;

            while (iter.hasNext()) {
                var entry = iter.next();
                var name = entry.getKey();
                var value = entry.getValue();
                var obj = gson.fromJson(value, DecoItem.class);
                map.put(name, obj);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    

}