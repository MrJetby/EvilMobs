package me.jetby.evilmobs.actions;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.EvilMobs;
import me.jetby.evilmobs.configurations.Items;
import me.jetby.evilmobs.records.Mask;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static me.jetby.evilmobs.EvilMobs.LOGGER;
import static me.jetby.evilmobs.gui.MainMenu.CHANCE;

@UtilityClass
public class DropManager {

    private final Random RANDOM = new Random();

    public void dropRandomItems(Location location, Mob mob) {

        String lootAmount = mob.lootAmount();
        List<Items.ItemsData> items = mob.items();
        if (items == null || items.isEmpty()) {
            LOGGER.warn("No items configured for mob: " + mob.id());
            return;
        }
        int minLoot, maxLoot;
        try {
            if (lootAmount.contains("-")) {
                String[] parts = lootAmount.split("-");
                minLoot = Integer.parseInt(parts[0].trim());
                maxLoot = Integer.parseInt(parts[1].trim());
            } else {
                minLoot = maxLoot = Integer.parseInt(lootAmount.trim());
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid lootAmount format: " + lootAmount);
            return;
        }

        int lootToDrop = minLoot + RANDOM.nextInt((maxLoot - minLoot) + 1);

        List<ItemStack> itemsToDrop = new ArrayList<>();
        for (Items.ItemsData item : items) {
            if (RANDOM.nextInt(100) < item.chance()) {
                ItemStack itemStack = item.itemStack();
                ItemMeta meta = itemStack.getItemMeta();
                meta.getPersistentDataContainer().remove(CHANCE);
                itemStack.setItemMeta(meta);
                itemsToDrop.add(itemStack);
            }
        }

        if (itemsToDrop.isEmpty()) {
            LOGGER.warn("No items passed chance check, adding all possible items");
            for (Items.ItemsData item : items) {
                itemsToDrop.add(item.itemStack());
            }
        }

        Collections.shuffle(itemsToDrop);
        for (int i = 0; i < Math.min(lootToDrop, itemsToDrop.size()); i++) {
            location.getWorld().dropItemNaturally(location, itemsToDrop.get(i));
        }
    }


    private final NamespacedKey key = new NamespacedKey("evilmobs", "no_stack");

    public void dropItem(Mob mob, ItemStack originalItem, Location location) {

        ItemStack itemToDrop;
        if (originalItem == null || location == null) {
            itemToDrop = new ItemStack(Material.DIAMOND);
        } else {
            itemToDrop = originalItem;
        }
        Map<String, Mask> masks = mob.masks();
        if (mob.isMask() && !masks.isEmpty()) {
            List<String> maskKeys = new ArrayList<>(masks.keySet());
            String randomMaskKey = maskKeys.get(RANDOM.nextInt(maskKeys.size()));
            Mask randomMask = masks.get(randomMaskKey);


            ItemStack maskedItem = new ItemStack(randomMask.material());
            ItemMeta meta = maskedItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(randomMask.name());
                if (randomMask.enchanted()) {
                    meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
                }
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, UUID.randomUUID().toString());
                maskedItem.setItemMeta(meta);
            }
            itemToDrop = maskedItem;
        }
        Item droppedItem = location.getWorld().dropItemNaturally(location, itemToDrop);
        droppedItem.setPickupDelay(mob.dropParticle().pickupDelay());
        if (mob.isMask()) {
            if (itemToDrop.getItemMeta() != null && itemToDrop.getItemMeta().hasDisplayName()) {
                droppedItem.setCustomName(itemToDrop.getItemMeta().getDisplayName());
                droppedItem.setCustomNameVisible(true);
            }
            Bukkit.getScheduler().runTask(EvilMobs.getInstance(), () -> droppedItem.setMetadata("evilmobs_originalItem", new FixedMetadataValue(EvilMobs.getInstance(), originalItem)));
        }

        double targetY = location.getY() + RANDOM.nextInt((int) (RANDOM.nextInt((int) (mob.dropParticle().maxY() - mob.dropParticle().minY() + 1)) + mob.dropParticle().minY()));
        double vertSpeed = Math.sqrt(2 * 0.08 * (targetY - location.getY()));
        double angle = RANDOM.nextDouble() * Math.PI * 2;
        double horSpeed = mob.dropParticle().minSpeed() + (mob.dropParticle().maxSpeed() - mob.dropParticle().minSpeed()) * RANDOM.nextDouble();
        org.bukkit.util.Vector velocity = new org.bukkit.util.Vector(Math.cos(angle) * horSpeed, vertSpeed, Math.sin(angle) * horSpeed);
        droppedItem.setVelocity(velocity);


        location.getWorld().playSound(location, mob.dropParticle().sound(),
                mob.dropParticle().volume(),
                mob.dropParticle().pitch());
        new BukkitRunnable() {
            final org.bukkit.util.Vector lastPosition = droppedItem.getLocation().toVector();

            @Override
            public void run() {
                if (!droppedItem.isValid()) {
                    cancel();
                    return;
                }
                org.bukkit.util.Vector currentPosition = droppedItem.getLocation().toVector();
                org.bukkit.util.Vector step = currentPosition.clone().subtract(lastPosition).multiply(1.0 / mob.dropParticle().amount());
                for (int i = 0; i < mob.dropParticle().amount(); i++) {
                    Vector particlePos = lastPosition.clone().add(step.clone().multiply(i));
                    if (!mob.flyingDropParticle()) continue;
                    location.getWorld().spawnParticle(mob.dropParticle().particle(),
                            particlePos.toLocation(location.getWorld()), 0, 0, 0, 0, 0);
                }
                lastPosition.copy(currentPosition);
            }
        }.runTaskTimerAsynchronously(EvilMobs.getInstance(), 0, 1);
    }

    public void dropItem(Integer amount, Mob mob, Location location) {
        long time = 1L;
        for (int i = 0; i < amount; i++) {
            Bukkit.getScheduler().runTaskLater(EvilMobs.getInstance(), () -> dropItem(mob, getRandomItem(mob.id()), location), time);
            time = time + 1L;
        }
    }

    public ItemStack getRandomItem(String id) {
        List<ItemStack> weightedItems = new ArrayList<>();
        List<Items.ItemsData> items = EvilMobs.getInstance().getItems().getData().get(id);
        if (items == null) {
            return new ItemStack(Material.DIAMOND);
        }
        for (Items.ItemsData itemData : items) {
            ItemStack item = itemData.itemStack();
            if (item == null) continue;

            int chance = itemData.chance();
            for (int i = 0; i < chance; i++) {
                weightedItems.add(item);
            }
        }
        return weightedItems.isEmpty() ? new ItemStack(Material.DIAMOND) :
                weightedItems.get(RANDOM.nextInt(weightedItems.size()));
    }
}
