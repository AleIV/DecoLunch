package me.aleiv.core.paper.objects;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class HammerCraft extends CustomRecipe{

    public HammerCraft(NamespacedKey namespacedKey, ItemStack item, Material material) {
        super(namespacedKey);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, item);
        recipe.shape("III", "ISA", "ASA");
        recipe.setIngredient('I', material);
        recipe.setIngredient('A', Material.AIR);
        recipe.setIngredient('S', Material.STICK);

        setRecipe(recipe);
    }
    
}
