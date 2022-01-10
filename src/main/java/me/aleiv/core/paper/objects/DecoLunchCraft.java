package me.aleiv.core.paper.objects;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class DecoLunchCraft extends CustomRecipe{
    public DecoLunchCraft(NamespacedKey namespacedKey, ItemStack item) {
        super(namespacedKey);

        final ShapedRecipe recipe = new ShapedRecipe(namespacedKey, item);
        recipe.shape("PPP", "ICA", "BBB");
        recipe.setIngredient('P', Material.PAPER);
        recipe.setIngredient('I', Material.IRON_PICKAXE);
        recipe.setIngredient('A', Material.IRON_AXE);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);
        recipe.setIngredient('B', Material.IRON_BLOCK);

        setRecipe(recipe);
    }
}
