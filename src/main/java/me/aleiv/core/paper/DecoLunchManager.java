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

    public static HashMap<String, DecoItem> decoItems = new HashMap<>();
    HashMap<String, GuiCode> guiCodes = new HashMap<>();
    HashMap<Catalog, Material> catalogMaterials = new HashMap<Catalog, Material>() {
        {
            put(Catalog.DECORATION, Material.PAPER);
            put(Catalog.MODERN, Material.CLAY_BALL);
            put(Catalog.RUSTIC, Material.BRICK);
            put(Catalog.SECURITY, Material.IRON_INGOT);
        }
    };

    public DecoLunchManager(Core instance) {
        this.instance = instance;

        initSpecialDecoItems();
        initAnnotations();

    }

    private void initSpecialDecoItems() {
        try {
            var tool = instance.getNoteBlockTool();

            // NAME | CUSTOM MODEL DATA | BLOCKID | MATERIAL | CATALOG | RARITY | DECOTAGS

            put("Iron DecoHammer", 1, "", Material.IRON_PICKAXE, Catalog.ADMIN, Rarity.COMMON,
                    List.of(DecoTag.CRAFT, DecoTag.ITEM));
            put("Diamond DecoHammer", 1, "", Material.DIAMOND_PICKAXE, Catalog.ADMIN, Rarity.UNCOMMON,
                    List.of(DecoTag.CRAFT, DecoTag.ITEM));
            put("Netherite DecoHammer", 1, "", Material.NETHERITE_PICKAXE, Catalog.ADMIN, Rarity.RARE,
                    List.of(DecoTag.CRAFT, DecoTag.ITEM));

            var blockID = tool.getBlockIDbyData("harp", 1, false);
            put("DecoLunch Table", 1, blockID, Material.NOTE_BLOCK, Catalog.ADMIN, Rarity.COMMON,
                    List.of(DecoTag.BLOCK, DecoTag.CRAFT));
            guiCodes.put(blockID, GuiCode.DECOLUNCH);

            // NAME | CUSTOM MODEL DATA | BLOCKID | CATALOG | RARITY | DECOTAGS

            initDecoItems();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't register special Deco Items, Json data is not present.");
        }

    }

    private void put(String name, int customModelData, String blockID, Material material, Catalog catalog,
            Rarity rarity, List<DecoTag> decoTags) {
        decoItems.put(name, new DecoItem(name, customModelData, blockID, material, catalog, rarity, decoTags));
    }

    private void put(String name, int customModelData, String blockID, Catalog catalog, Rarity rarity,
            List<DecoTag> decoTags) {
        decoItems.put(name,
                new DecoItem(name, customModelData, blockID, catalogMaterials.get(catalog), catalog, rarity, decoTags));
    }

    public void spawnDecoStand(Location loc, Player player, DecoItem decoItem) {
        var world = loc.getWorld();
        var location = new Location(world, loc.getBlockX() + 0.5D, loc.getBlockY(), loc.getBlockZ() + 0.5D);

        var stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);

        stand.setRotation(player.getLocation().getYaw()+180, 0);
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

    public boolean hasDecoItem(String blockID) {
        return !decoItems.values().stream().filter(decoItem -> decoItem.getBlockID() == blockID).toList().isEmpty();
    }

    public DecoItem getDecoItem(String name) {
        return decoItems.containsKey(name) ? decoItems.get(name) : null;
    }

    public DecoItem getDecoItemByBlockID(String blockID) {
        return decoItems.values().stream().filter(decoItem -> decoItem.getBlockID().equals(blockID)).findAny()
                .orElse(null);
    }

    public boolean isDecoItem(ItemStack item) {
        if (!item.hasItemMeta())
            return false;
        var meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            var name = meta.getDisplayName();
            return !decoItems.values().stream().filter(deco -> name.contains(deco.getName())).toList().isEmpty();
        }
        return false;
    }

    public DecoItem getDecoItem(ItemStack item) {
        var meta = item.getItemMeta();
        if (isDecoItem(item)) {
            var name = meta.getDisplayName();
            return decoItems.values().stream().filter(deco -> name.contains(deco.getName())).findAny().orElse(null);
        }
        return null;
    }

    public DecoItem getDecoItem(Location loc) {
        var world = loc.getWorld();
        var location = new Location(world, loc.getBlockX() + 0.5D, loc.getBlockY(), loc.getBlockZ() + 0.5D);
        return world.getNearbyEntities(location, 0.5, 0.5, 0.5, entity -> entity instanceof ArmorStand)
                .stream().map(stand -> (ArmorStand) stand).filter(stand -> isDecoStand(stand))
                .map(stand -> getDecoItem(stand)).findAny().orElse(null);

    }

    public ArmorStand getDecoStand(Location loc) {
        var world = loc.getWorld();
        var location = new Location(world, loc.getBlockX() + 0.5D, loc.getBlockY(), loc.getBlockZ() + 0.5D);
        return world.getNearbyEntities(location, 0.5, 0.5, 0.5, entity -> entity instanceof ArmorStand)
                .stream().map(stand -> (ArmorStand) stand).filter(stand -> isDecoStand(stand)).findAny().orElse(null);
    }

    public DecoItem getDecoItem(ArmorStand stand) {
        var equip = stand.getEquipment();
        var item = equip.getHelmet();
        return getDecoItem(item);
    }

    public boolean isDecoStand(ArmorStand stand) {
        var equip = stand.getEquipment();
        var helmet = equip.getHelmet();
        return helmet != null && isDecoItem(helmet);
    }

    public boolean isDecoHammer(ItemStack item) {
        var type = item.getType();
        var meta = item.getItemMeta();
        return (type == Material.IRON_PICKAXE || type == Material.DIAMOND_PICKAXE || type == Material.NETHERITE_PICKAXE)
                && meta.hasCustomModelData()
                && meta.getCustomModelData() == 1;
    }

    public void damageHammer(ItemStack hammer) {
        var meta = (Damageable) hammer.getItemMeta();
        meta.setDamage(meta.getDamage() + 5);
        var damage = meta.getDamage();

        switch (hammer.getType()) {
            case IRON_PICKAXE: {
                // 250
                if (damage >= 250) {
                    meta.setDamage(250);
                    return;
                }

            }
                break;

            case DIAMOND_PICKAXE: {
                // 1561
                if (damage >= 1561) {
                    meta.setDamage(1561);
                    return;
                }

            }
                break;

            case NETHERITE_PICKAXE: {
                // 2031
                if (damage >= 2031) {
                    meta.setDamage(2031);
                    return;
                }

            }
                break;

            default:
                break;
        }
        hammer.setItemMeta((ItemMeta) meta);
    }

    public RapidInv getGui(GuiCode guiCode, Location location) {
        switch (guiCode) {
            case DECOLUNCH -> {
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
            return decoItems.keySet().stream().toList();
        });
    }

    // 18-53

    public enum Catalog {
        ALL, ADMIN, RUSTIC, SECURITY, DECORATION, MODERN, SEASONAL
    }

    public enum DecoTag {
        SIT, ENTITY, BLOCK, ITEM, BARRIER, CRAFT, OFF_HAND, MAIN_HAND, HEAD, LIGHT, SMOOTH_YAW
    }

    public enum Rarity {
        ALL, COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    public enum GuiCode {
        DECOLUNCH
    }

    private void initDecoItems() {
        //RUSTIC

        put("Oak Chair", 1, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Spruce Chair", 2, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Birch Chair", 3, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Jungle Chair", 4, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Acacia Chair", 5, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Dark Oak Chair", 6, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));

        put("Oak Bench", 7, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Spruce Bench", 8, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Birch Bench", 9, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Jungle Bench", 10, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Acacia Bench", 11, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Dark Oak Bench", 12, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));

        /*
        NOT USED TWO BLOCKS TABLE
        put("Oak Console Table", 13, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Spruce Console Table", 14, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Birch Console Table", 15, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Jungle Console Table", 16, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Acacia Console Table", 17, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Dark Oak Console Table", 18, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY));*/

        put("Oak Table", 19, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW));
        put("Spruce Table", 20, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW));
        put("Birch Table", 21, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW));
        put("Jungle Table", 22, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW));
        put("Acacia Table", 23, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW));
        put("Dark Oak Table", 24, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW));

        put("Oak Mini Table", 25, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Spruce Mini Table", 26, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Birch Mini Table", 27, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Jungle Mini Table", 28, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Acacia Mini Table", 29, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Dark Oak Mini Table", 30, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));

        put("Oak Dresser", 31, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Spruce Dresser", 32, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Birch Dresser", 33, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Jungle Dresser", 34, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Acacia Dresser", 35, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));
        put("Dark Oak Dresser", 36, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER));

        put("Oak Armchair", 37, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Spruce Armchair", 38, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Birch Armchair", 39, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Jungle Armchair", 40, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Acacia Armchair", 41, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));
        put("Dark Oak Armchair", 42, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT));

        put("Oak Chandelier", 43, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT));
        put("Spruce Chandelier", 44, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT));
        put("Birch Chandelier", 45, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT));
        put("Jungle Chandelier", 46, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT));
        put("Acacia Chandelier", 47, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT));
        put("Dark Oak Chandelier", 48, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT));

        //DECORATION

        put("Wooden Logs", 1, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Stacked Books", 2, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Mini Lunch", 3, "", Catalog.DECORATION, Rarity.EPIC, List.of(DecoTag.ENTITY));
        put("Curtain Closed", 4, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY));
        put("Curtain Open", 5, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY));
        put("BieberIV Poster", 6, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY));
        put("Astronaut", 7, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY));
        put("Nimu Puff", 8, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY));
        put("Corkboard", 9, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY));
        put("Mictia Planet", 10, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY));
        put("Laptop", 11, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY));
        put("LunchBox", 12, "", Catalog.DECORATION, Rarity.LEGENDARY, List.of(DecoTag.ENTITY));
        put("Bingo Space Painting", 13, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("DEDSAFIO Painting", 14, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY));
        put("Rodolfo", 15, "", Catalog.DECORATION, Rarity.EPIC, List.of(DecoTag.ENTITY));

        //MODERN
        put("Blinds", 1, "", Catalog.MODERN, Rarity.UNCOMMON, List.of(DecoTag.ENTITY));
        put("Tall lamp", 2, "", Catalog.MODERN, Rarity.UNCOMMON, List.of(DecoTag.ENTITY));


    }

}
