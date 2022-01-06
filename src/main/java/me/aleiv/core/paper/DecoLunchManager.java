package me.aleiv.core.paper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Data;
import me.aleiv.core.paper.gui.DecoLunchTableGUI;
import me.aleiv.core.paper.objects.DecoItem;
import us.jcedeno.libs.rapidinv.RapidInv;

@Data
public class DecoLunchManager {
    
    Core instance;

    HashMap<String, DecoItem> decoItems = new HashMap<>();
    HashMap<String, GuiCode> guiCodes = new HashMap<>();

    public DecoLunchManager(Core instance){
        this.instance = instance;

        initSpecialDecoItems();
        initAnnotations();

    }

    private void initSpecialDecoItems(){
        try {
            var tool = instance.getNoteBlockTool();

            //NAME | CUSTOM MODEL DATA | BLOCKID | MATERIAL | CATALOG | RARITY | DECOTAGS

            put("test", 38, "", Material.BRICK, Catalog.ADMIN, Rarity.COMMON, List.of(DecoTag.SIT, DecoTag.ENTITY));
            put("Iron DecoHammer", 1, "", Material.IRON_PICKAXE, Catalog.ADMIN, Rarity.COMMON, List.of(DecoTag.CRAFT, DecoTag.ITEM));
            put("Diamond DecoHammer", 1, "", Material.DIAMOND_PICKAXE, Catalog.ADMIN, Rarity.UNCOMMON, List.of(DecoTag.CRAFT, DecoTag.ITEM));
            put("Netherite DecoHammer", 1, "", Material.NETHERITE_PICKAXE, Catalog.ADMIN, Rarity.RARE, List.of(DecoTag.CRAFT, DecoTag.ITEM));

            var blockID = tool.getBlockIDbyData("harp", 1, false);
            put("DecoLunch Table", 1, blockID, Material.NOTE_BLOCK, Catalog.ADMIN, Rarity.COMMON, List.of(DecoTag.BLOCK, DecoTag.CRAFT));
            guiCodes.put(blockID, GuiCode.DECOLUNCH);

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Couldn't register special Deco Items, Json data is not present.");
        }

    }

    private void put(String name, int customModelData, String blockID, Material material, Catalog catalog, Rarity rarity, List<DecoTag> decoTags){
        decoItems.put(name, new DecoItem(name, customModelData, blockID, material, catalog, rarity, decoTags));
    }

    public void spawnDecoStand(Location loc, Player player, DecoItem decoItem) {
        var world = loc.getWorld();
        var x = (int) loc.getX();
        var y = (int) loc.getY();
        var z = (int) loc.getZ();
        var sx = Math.abs(x)+0.5;
        var sz = Math.abs(x)+0.5;

        var location = new Location(world, x < 0 ? -sx : sx, y, z < 0 ? -sz : sz);
        var stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);

        stand.setRotation(player.getLocation().getYaw(), 0);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setSmall(true);
        for (var equipmentSlot : EquipmentSlot.values()) {
            for (var lock : LockType.values()) {
                stand.addEquipmentLock(equipmentSlot, lock);
            }
        }

        var equip = stand.getEquipment();
        equip.setHelmet(decoItem.getItemStack());

    }

    public boolean hasDecoItem(String blockID){
        return !decoItems.values().stream().filter(decoItem -> decoItem.getBlockID() == blockID).toList().isEmpty();
    }

    public DecoItem getDecoItem(String name){
        return decoItems.containsKey(name) ? decoItems.get(name) : null;
    }

    public DecoItem getDecoItemByBlockID(String blockID){
        return decoItems.values().stream().filter(decoItem -> decoItem.getBlockID().equals(blockID)).findAny().orElse(null);
    }

    public boolean isDecoItem(ItemStack item){
        if(!item.hasItemMeta()) return false;
        var meta = item.getItemMeta();
        if(meta.hasDisplayName()){
            var name = meta.getDisplayName();
            return !decoItems.values().stream().filter(deco -> name.contains(deco.getName())).toList().isEmpty();
        }
        return false;
    }

    public DecoItem getDecoItem(ItemStack item){
        var meta = item.getItemMeta();
        if(isDecoItem(item)){
            var name = meta.getDisplayName();
            return decoItems.values().stream().filter(deco -> name.contains(deco.getName())).findAny().orElse(null);
        }
        return null;
    }

    public DecoItem getDecoItem(Location loc) {
        var world = loc.getWorld();
        var x = (int) loc.getX();
        var y = (int) loc.getY();
        var z = (int) loc.getZ();
        var sx = Math.abs(x)+0.5;
        var sz = Math.abs(x)+0.5;

        var location = new Location(world, x < 0 ? -sx : sx, y, z < 0 ? -sz : sz);
        return world.getNearbyEntities(location, 0.5, 0.5, 0.5, entity -> entity instanceof ArmorStand)
            .stream().map(stand -> (ArmorStand) stand).filter(stand -> isDecoStand(stand)).map(stand -> getDecoItem(stand)).findAny().orElse(null);
        
    }

    public ArmorStand getDecoStand(Location loc){
        var world = loc.getWorld();
        var x = (int) loc.getX();
        var y = (int) loc.getY();
        var z = (int) loc.getZ();
        var sx = Math.abs(x)+0.5;
        var sz = Math.abs(x)+0.5;

        var location = new Location(world, x < 0 ? -sx : sx, y, z < 0 ? -sz : sz);
        return world.getNearbyEntities(location, 0.5, 0.5, 0.5, entity -> entity instanceof ArmorStand)
            .stream().map(stand -> (ArmorStand) stand).filter(stand -> isDecoStand(stand)).findAny().orElse(null);
    }

    public DecoItem getDecoItem(ArmorStand stand){
        var equip = stand.getEquipment();
        var item = equip.getHelmet();
        return getDecoItem(item);
    }

    public boolean isDecoStand(ArmorStand stand){
        var equip = stand.getEquipment();
        var helmet = equip.getHelmet();
        return helmet != null && isDecoItem(helmet);
    }

    public boolean isDecoHammer(ItemStack item){
        var type = item.getType();
        var meta = item.getItemMeta();
        return (type == Material.IRON_PICKAXE || type == Material.DIAMOND_PICKAXE || type == Material.NETHERITE_PICKAXE)
            && meta.hasCustomModelData() 
            && meta.getCustomModelData() == 1;
    }

    public void damageHammer(ItemStack hammer){
        var meta = (Damageable) hammer.getItemMeta();
        meta.setDamage(meta.getDamage()+5);
        var damage = meta.getDamage();


        switch (hammer.getType()) {
            case IRON_PICKAXE:{
                //250
                if(damage >= 250){
                    meta.setDamage(250);
                    return;
                }

            }break;

            case DIAMOND_PICKAXE:{
                //1561
                if(damage >= 1561){
                    meta.setDamage(1561);
                    return;
                }

            }break;

            case NETHERITE_PICKAXE:{
                //2031
                if(damage >= 2031){
                    meta.setDamage(2031);
                    return;
                }

            }break;
        
            default:
                break;
        }
        hammer.setItemMeta((ItemMeta) meta);
    }

    public RapidInv getGui(GuiCode guiCode, Location location){
        switch (guiCode) {
            case DECOLUNCH ->{
                return new DecoLunchTableGUI(location);
            }
        }
        return null;
    }

    

    private void initAnnotations() {
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

        manager.getCommandCompletions().registerAsyncCompletion("decoitems", c -> {
            return instance.getDecoLunchManager().getDecoItems().keySet().stream().toList();
        });
    }



    //18-53

    public enum Catalog{
        ADMIN, RUSTIC, SECURITY, DECORATION, MODERN
    }

    public enum DecoTag{
        SIT, ENTITY, BLOCK, ITEM, BARRIER, CRAFT, OFF_HAND, MAIN_HAND, HEAD, LIGHT
    }

    public enum Rarity{
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    public enum GuiCode{
        DECOLUNCH
    }



}
