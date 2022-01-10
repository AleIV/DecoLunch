package me.aleiv.core.paper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Data;
import me.aleiv.core.paper.gui.DecoLunchTableGUI;
import me.aleiv.core.paper.objects.CustomRecipe;
import me.aleiv.core.paper.objects.DecoItem;
import us.jcedeno.libs.rapidinv.RapidInv;

@Data
public class DecoLunchManager{

    Core instance;

    public static HashMap<String, DecoItem> decoItems = new HashMap<>();
    HashMap<String, GuiCode> guiCodes = new HashMap<>();
    HashMap<String, CustomRecipe> allRecipes = new HashMap<>();

    public DecoLunchManager(Core instance) {
        this.instance = instance;

        initSpecialDecoItems();
        initAnnotations();

    }

    private void initSpecialDecoItems() {
        try {
            var tool = instance.getNoteBlockTool();

            // NAME | CUSTOM MODEL DATA | BLOCKID | MATERIAL | CATALOG | RARITY | DECOTAGS

            var iron = "Iron DecoHammer";
            var diamond = "Diamond DecoHammer";
            var netherite = "Netherite DecoHammer";

            put(iron, 10001, "", Material.IRON_PICKAXE, Catalog.ADMIN, Rarity.COMMON,
                    List.of(DecoTag.CRAFT, DecoTag.ITEM), "");
            put(diamond, 10002, "", Material.DIAMOND_PICKAXE, Catalog.ADMIN, Rarity.UNCOMMON,
                    List.of(DecoTag.CRAFT, DecoTag.ITEM), "");
            put(netherite, 10003, "", Material.NETHERITE_PICKAXE, Catalog.ADMIN, Rarity.RARE,
                    List.of(DecoTag.CRAFT, DecoTag.ITEM), "");
                    
            var ironDecoItem = getDecoItem(iron);
            var ironRecipe = getHammerRecipe(new NamespacedKey(instance, "ironhammer"), ironDecoItem.getItemStack(), Material.IRON_INGOT, Material.STICK);
            instance.getServer().addRecipe(ironRecipe);

            var diamondDecoItem = getDecoItem(diamond);
            var diamondRecipe = getHammerRecipe(new NamespacedKey(instance, "diamondhammer"), diamondDecoItem.getItemStack(), Material.DIAMOND, Material.STICK);
            instance.getServer().addRecipe(diamondRecipe);
            
            var netheriteDecoItem = getDecoItem(netherite);
            var netheriteRecipe = getHammerRecipe(new NamespacedKey(instance, "netheritehammer"), netheriteDecoItem.getItemStack(), Material.NETHERITE_INGOT, Material.STICK);
            instance.getServer().addRecipe(netheriteRecipe);

            var blockID = tool.getBlockIDbyData("harp", 1, false);
            var decoLunch = "DecoLunch Table"; 
            put(decoLunch, 10000, blockID, Material.NOTE_BLOCK, Catalog.ADMIN, Rarity.COMMON,
                    List.of(DecoTag.BLOCK, DecoTag.CRAFT), "");
            guiCodes.put(blockID, GuiCode.DECOLUNCH);

            var decoLunchDecoItem = getDecoItem(decoLunch);

            var decoRecipe = getDecoLunchRecipe(new NamespacedKey(instance, "decolunchtable"), decoLunchDecoItem.getItemStack());
            instance.getServer().addRecipe(decoRecipe);
            // NAME | CUSTOM MODEL DATA | BLOCKID | CATALOG | RARITY | DECOTAGS

            initDecoItems();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't register special Deco Items, Json data is not present.");
        }

    }

    private ShapedRecipe getDecoLunchRecipe(NamespacedKey namespacedKey, ItemStack result) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, result);

        shapedRecipe.shape("AAA", "BCD", "EEE");

        shapedRecipe.setIngredient('A', Material.PAPER);

        shapedRecipe.setIngredient('B', Material.IRON_PICKAXE);
        shapedRecipe.setIngredient('C', Material.CRAFTING_TABLE);
        shapedRecipe.setIngredient('D', Material.IRON_AXE);

        shapedRecipe.setIngredient('E', Material.IRON_BLOCK);

        return shapedRecipe;
    }

    private ShapedRecipe getHammerRecipe(NamespacedKey namespacedKey, ItemStack result, Material ingredient, Material ingredient2) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, result);

        shapedRecipe.shape("AAA", "AB ", " B ");

        shapedRecipe.setIngredient('A', ingredient);

        shapedRecipe.setIngredient('B', ingredient2);

        return shapedRecipe;
    }

    private void put(String name, int customModelData, String blockID, Material material, Catalog catalog,
            Rarity rarity, List<DecoTag> decoTags, String prizeString) {
        decoItems.put(name, new DecoItem(name, customModelData, blockID, material, catalog, rarity, decoTags, prizeString));
    }

    private void put(String name, int customModelData, String blockID, Catalog catalog, Rarity rarity,
            List<DecoTag> decoTags, String prizeString) {
        decoItems.put(name,
                new DecoItem(name, customModelData, blockID, Material.RABBIT_HIDE, catalog, rarity, decoTags, prizeString));
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
        if ((meta.hasCustomModelData() && (item.getType() == Material.RABBIT_HIDE || item.getType() == Material.NOTE_BLOCK)) || isDecoHammer(item)) {
            var data = meta.getCustomModelData();
            return !decoItems.values().stream().filter(deco -> deco.getCustomModelData() == data).toList().isEmpty();
        }
        return false;
    }

    public DecoItem getDecoItem(ItemStack item) {
        var meta = item.getItemMeta();
        if (isDecoItem(item)) {
            var data = meta.getCustomModelData();
            return decoItems.values().stream().filter(deco -> deco.getCustomModelData() == data).findAny().orElse(null);
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
        var data = List.of(10001, 10002, 10003);
        return (type == Material.IRON_PICKAXE || type == Material.DIAMOND_PICKAXE || type == Material.NETHERITE_PICKAXE)
                && meta.hasCustomModelData()
                && data.contains(meta.getCustomModelData());
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

        put("Oak Chair", 1, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "OAK_LOG-5");
        put("Spruce Chair", 2, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "SPRUCE_LOG-5");
        put("Birch Chair", 3, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "BIRCH_LOG-5");
        put("Jungle Chair", 4, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "JUNGLE_LOG-5");
        put("Acacia Chair", 5, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "ACACIA_LOG-5");
        put("Dark Oak Chair", 6, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "DARK_OAK_LOG-5");

        put("Oak Bench", 7, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "OAK_LOG-3");
        put("Spruce Bench", 8, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "SPRUCE_LOG-3");
        put("Birch Bench", 9, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "BIRCH_LOG-3");
        put("Jungle Bench", 10, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "JUNGLE_LOG-3");
        put("Acacia Bench", 11, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "ACACIA_LOG-3");
        put("Dark Oak Bench", 12, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "DARK_OAK_LOG-3");

        /*
        NOT USED TWO BLOCKS TABLE
        put("Oak Console Table", 13, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY), "OAK_LOG-6");
        put("Spruce Console Table", 14, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY), "SPRUCE_LOG-6");
        put("Birch Console Table", 15, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY), "BIRCH_LOG-6");
        put("Jungle Console Table", 16, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY), "JUNGLE_LOG-6");
        put("Acacia Console Table", 17, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY), "ACACIA_LOG-6");
        put("Dark Oak Console Table", 18, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY), "DARK_OAK_LOG-6");*/

        put("Oak Table", 19, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW), "OAK_LOG-5");
        put("Spruce Table", 20, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW), "SPRUCE_LOG-5");
        put("Birch Table", 21, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW), "BIRCH_LOG-5");
        put("Jungle Table", 22, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW), "JUNGLE_LOG-5");
        put("Acacia Table", 23, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW), "ACACIA_LOG-5");
        put("Dark Oak Table", 24, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER, DecoTag.SMOOTH_YAW), "DARK_OAK_LOG-5");

        put("Oak Mini Table", 25, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "OAK_LOG-4");
        put("Spruce Mini Table", 26, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "SPRUCE_LOG-4");
        put("Birch Mini Table", 27, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "BIRCH_LOG-4");
        put("Jungle Mini Table", 28, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "JUNGLE_LOG-4");
        put("Acacia Mini Table", 29, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "ACACIA_LOG-4");
        put("Dark Oak Mini Table", 30, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "DARK_OAK_LOG-4");

        put("Oak Dresser", 31, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "OAK_LOG-5;IRON_NUGGET-2");
        put("Spruce Dresser", 32, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "SPRUCE_LOG-5;IRON_NUGGET-2");
        put("Birch Dresser", 33, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "BIRCH_LOG-5;IRON_NUGGET-2");
        put("Jungle Dresser", 34, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "JUNGLE_LOG-5;IRON_NUGGET-2");
        put("Acacia Dresser", 35, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "ACACIA_LOG-5;IRON_NUGGET-2");
        put("Dark Oak Dresser", 36, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.BARRIER), "DARK_OAK_LOG-5;IRON_NUGGET-2");

        put("Oak Armchair", 37, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "OAK_LOG-5;WHITE_WOOL-3");
        put("Spruce Armchair", 38, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "SPRUCE_LOG-5;WHITE_WOOL-3");
        put("Birch Armchair", 39, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "BIRCH_LOG-5;WHITE_WOOL-3");
        put("Jungle Armchair", 40, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "JUNGLE_LOG-5;WHITE_WOOL-3");
        put("Acacia Armchair", 41, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "ACACIA_LOG-5;WHITE_WOOL-3");
        put("Dark Oak Armchair", 42, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.SIT), "DARK_OAK_LOG-5;WHITE_WOOL-3");

        put("Oak Chandelier", 43, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT), "OAK_LOG-5;CHAIN-2;TORCH-4");
        put("Spruce Chandelier", 44, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT), "SPRUCE_LOG-5;CHAIN-2;TORCH-4");
        put("Birch Chandelier", 45, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT), "BIRCH_LOG-5;CHAIN-2;TORCH-4");
        put("Jungle Chandelier", 46, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT) , "JUNGLE_LOG-5;CHAIN-2;TORCH-4");
        put("Acacia Chandelier", 47, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT), "ACACIA_LOG-5;CHAIN-2;TORCH-4");
        put("Dark Oak Chandelier", 48, "", Catalog.RUSTIC, Rarity.COMMON, List.of(DecoTag.ENTITY, DecoTag.LIGHT), "DARK_OAK_LOG-5;CHAIN-2;TORCH-4");

        //DECORATION

        put("Wooden Logs", 1001, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY), "SPRUCE_LOG-5");
        put("Stacked Books", 1002, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY), "BOOK-2");
        put("Mini Lunch", 1003, "", Catalog.DECORATION, Rarity.EPIC, List.of(DecoTag.ENTITY), "BROWN_WOOL-5");
        put("Curtain Closed", 1004, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY), "YELLOW_WOOL-6;OAK_LOG-3");
        put("Curtain Open", 1005, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY), "YELLOW_WOOL-6;OAK_LOG-3");
        put("BieberIV Poster", 1006, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY), "PAINTING-1;YELLOW_DYE-3;CYAN_DYE-2;ORANGE_DYE-2");
        put("Astronaut", 1007, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY), "WHITE_TERRACOTTA-2;GLASS-2;BLACK_DYE-2");
        put("Nimu Puff", 1008, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY), "PINK_WOOL-6");
        put("Corkboard", 1009, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY), "OAK_LOG-3;SPONGE-2");
        put("Mictia Planet", 1010, "", Catalog.DECORATION, Rarity.RARE, List.of(DecoTag.ENTITY), "PURPLE_WOOL-6");
        put("Laptop", 1011, "", Catalog.DECORATION, Rarity.UNCOMMON, List.of(DecoTag.ENTITY), "IRON_INGOT-5;BLACK_STAINED_GLASS_PANE-3;REDSTONE-2");
        put("LunchBox", 1012, "", Catalog.DECORATION, Rarity.LEGENDARY, List.of(DecoTag.ENTITY), "IRON_INGOT-4;YELLOW_DYE-3");
        put("Bingo Space Painting", 1013, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY), "PAINTING-1;BLACK_DYE-4;BLUE_DYE-2");
        put("DEDSAFIO Painting", 1014, "", Catalog.DECORATION, Rarity.COMMON, List.of(DecoTag.ENTITY), "PAINTING-1;GREEN_DYE-4;YELLOW_DYE-2");
        put("Rodolfo", 1015, "", Catalog.DECORATION, Rarity.EPIC, List.of(DecoTag.ENTITY), "RED_WOOL-5;LIGHT_BLUE_WOOL-3;OAK_LOG-3");

        //MODERN
        put("Blinds", 2001, "", Catalog.MODERN, Rarity.UNCOMMON, List.of(DecoTag.ENTITY), "PAPER-5");
        put("Tall lamp", 2002, "", Catalog.MODERN, Rarity.UNCOMMON, List.of(DecoTag.ENTITY), "IRON_INGOT-3;REDSTONE-2" );


    }

}
