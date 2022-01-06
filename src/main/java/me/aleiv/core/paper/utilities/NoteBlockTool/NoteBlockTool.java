package me.aleiv.core.paper.utilities.NoteBlockTool;

import java.util.HashMap;

import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.utilities.utils.JsonConfig;
import net.md_5.bungee.api.ChatColor;

@Data
public class NoteBlockTool implements Listener {

    Core instance;
    NoteBlockCMD noteBlockCMD;
    HashMap<String, NoteBlockID> noteblocks = new HashMap<>();

    public NoteBlockTool(Core instance) {
        this.instance = instance;
        Bukkit.getPluginManager().registerEvents(this, instance);

        noteBlockCMD = new NoteBlockCMD(instance);
        instance.getCommandManager().registerCommand(noteBlockCMD);

        pullJson();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent e) {
        var block = e.getBlock();
        var aboveBlock = block.getLocation().add(0, 1, 0).getBlock();
        if (aboveBlock.getType() == Material.NOTE_BLOCK) {
            updateAndCheck(block.getLocation());
            e.setCancelled(true);
        }
        if (block.getType() == Material.NOTE_BLOCK)
            e.setCancelled(true);

        if (block.getType().toString().toLowerCase().contains("sign"))
            return;
        block.getState().update(true, false);

    }

    public void updateAndCheck(Location loc) {
        Block b = loc.add(0, 1, 0).getBlock();
        if (b.getType() == Material.NOTE_BLOCK)
            b.getState().update(true, true);
        Location nextBlock = b.getLocation().add(0, 1, 0);
        if (nextBlock.getBlock().getType() == Material.NOTE_BLOCK)
            updateAndCheck(b.getLocation());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        var hand = e.getHand();
        var player = e.getPlayer();
        var equip = player.getEquipment();
        var item = equip.getItem(hand);

        var block = e.getClickedBlock();
        var action = e.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.NOTE_BLOCK) {

            if(player.isSneaking() && item.getType() != Material.AIR) return;
                
            e.setCancelled(true);

            var tool = instance.getNoteBlockTool();
            if (tool.isDefaultNoteBlock(block)) {
                // vanilla noteblock
            }
        }
    }

    @EventHandler
    public void onNote(NotePlayEvent e) {
        e.setCancelled(true);
    }

    public boolean isDefaultNoteBlock(Block block) {
        var noteBlock = getNoteBlockData(block);
        var thisID = getBlockID(noteBlock);
        var defaultID = getBlockIDbyData("harp", 0, false);
        return thisID == defaultID;
    }

    public BlockData getDefaultBlockData() {
        return getNoteBlockData("harp", 0, false);
    }

    public NoteBlock getNoteBlockData(String instrument, int note, boolean powered){
        return getNoteBlockData(getBlockIDbyData(instrument, note, powered));
    }

    public void setBlockData(Block block, NoteBlock noteBlock) {
        block.setBlockData(noteBlock);
    }

    public NoteBlock getNoteBlockData(Block block) {
        return (NoteBlock) block.getBlockData();
    }

    public NoteBlockID getNoteBlockID(String blockID) {
        return noteblocks.containsKey(blockID) ? noteblocks.get(blockID) : null;
    }

    public NoteBlock getNoteBlockData(String blockID) {
        var noteblockID = getNoteBlockID(blockID);

        if (noteblockID == null)
            return null;

        NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();

        var octave = noteblockID.getOctave();
        var tone = noteblockID.getTone();
        var sharped = noteblockID.isSharped();

        var n = new Note(octave, tone, sharped);

        noteBlock.setNote(n);
        noteBlock.setInstrument(noteblockID.getInstrument());
        noteBlock.setPowered(noteblockID.isPowered());

        return noteBlock;

    }

    public NoteBlockID getNoteBlockID(NoteBlock noteBlock) {
        var nt = noteBlock.getAsString();

        var val1 = nt.replace("minecraft:note_block[", "");
        var val2 = val1.replace("]", "");
        var vals = val2.split(",");

        var instrumentF3 = vals[0].split("=")[1];

        var noteF3 = vals[1].split("=")[1];

        var instrument = noteBlock.getInstrument();
        var note = noteBlock.getNote();
        var tone = note.getTone();
        var octave = note.getOctave();
        var sharped = note.isSharped();
        var powered = noteBlock.isPowered();

        return new NoteBlockID(instrumentF3, Integer.parseInt(noteF3), powered, instrument, tone, octave, sharped);

    }

    public String getBlockID(NoteBlock noteBlock) {
        var nt = getNoteBlockID(noteBlock);

        var format = new StringBuilder();
        format.append(nt.getInstrumentF3() + ";");
        format.append(nt.getNoteF3() + ";");
        format.append(nt.isPowered() + ";");
        format.append(nt.getInstrument().toString() + ";");
        format.append(nt.getTone().toString() + ";");
        format.append(nt.getOctave() + ";");
        format.append(nt.isSharped() + ";");

        return format.toString();

    }

    public String getBlockIDbyData(String instrument, int note, boolean powered) {
        return noteblocks.keySet().stream()
                .filter(noteblock -> noteblock.startsWith(instrument + ";" + note + ";" + powered)).findAny()
                .orElse("");
    }

    // @EventHandler
    public void addNoteBlocks(PlayerInteractEvent e) {
        var block = e.getClickedBlock();
        var player = e.getPlayer();

        if (block != null && block.getType() == Material.NOTE_BLOCK) {

            NoteBlock noteBlock = (NoteBlock) block.getBlockData();

            var blockID = getBlockID(noteBlock);

            var noteBlockID = getNoteBlockID(noteBlock);

            if (!noteblocks.containsKey(blockID)) {
                noteblocks.put(blockID, noteBlockID);
                player.sendMessage(ChatColor.YELLOW + "(" + noteblocks.size() + ") Added "
                        + noteBlockID.getInstrumentF3() + " " + noteBlockID.getNoteF3() + " " + noteBlock.isPowered());
                pushJson();

            }
        }
    }

    public void pushJson() {
        try {
            var gson = instance.getGson();
            var jsonConfig = new JsonConfig("noteblocks.json");
            var json = gson.toJson(noteblocks);
            var obj = gson.fromJson(json, JsonObject.class);
            jsonConfig.setJsonObject(obj);
            jsonConfig.save();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void pullJson() {
        try {
            var gson = instance.getGson();
            var jsonConfig = new JsonConfig("noteblocks.json");
            var list = jsonConfig.getJsonObject();
            var iter = list.entrySet().iterator();
            var map = noteblocks;

            while (iter.hasNext()) {
                var entry = iter.next();
                var key = entry.getKey();
                var value = entry.getValue();
                var obj = gson.fromJson(value, NoteBlockID.class);
                map.put(key, obj);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

}
