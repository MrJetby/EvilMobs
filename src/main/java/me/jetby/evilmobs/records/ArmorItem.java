package me.jetby.evilmobs.records;

import org.bukkit.inventory.ItemStack;


public record ArmorItem(

        String id,
        ItemStack item,
        int dropChance

) {
}
