package me.jetby.evilmobs;


import lombok.RequiredArgsConstructor;
import me.jetby.evilmobs.api.event.MobSpawnEvent;
import me.jetby.evilmobs.records.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import static me.jetby.evilmobs.Main.NAMESPACED_KEY;

@RequiredArgsConstructor
public class MobCreator {

    private final Mob mob;

    public void spawn() {
        spawn(mob.spawnlocation());
    }

    public void spawn(Location location) {
        LivingEntity boss = (LivingEntity) location.getWorld().spawnEntity(mob.spawnlocation(), mob.entityType());
        boss.setGlowing(mob.glow());
        boss.setMaxHealth(mob.health());
        boss.setHealth(mob.health());
        boss.setAI(mob.ai());
        boss.setCanPickupItems(mob.canPickupItems());
        boss.setRemoveWhenFarAway(false);
        boss.setPersistent(true);

        if (boss instanceof Ageable age) {
            if (mob.isBaby()) {
                age.setBaby();
            } else {
                age.setAdult();
            }
        }

        boss.setCustomNameVisible(mob.nameVisible());
        if (mob.nameVisible()) {
            boss.setCustomName(mob.name());
        }

         mob.armorItems().forEach(armorItem -> {

             switch (armorItem.id()) {
                 case "helmet": {
                     boss.getEquipment().setHelmet(armorItem.item());
                     boss.getEquipment().setHelmetDropChance(armorItem.dropChance());
                     break;
                 }
                 case "chestplate": {
                     boss.getEquipment().setChestplate(armorItem.item());
                     boss.getEquipment().setChestplateDropChance(armorItem.dropChance());
                     break;
                 }
                 case "leggings": {
                     boss.getEquipment().setLeggings(armorItem.item());
                     boss.getEquipment().setLeggingsDropChance(armorItem.dropChance());
                     break;
                 }
                 case "boots": {
                     boss.getEquipment().setBoots(armorItem.item());
                     boss.getEquipment().setBootsDropChance(armorItem.dropChance());
                     break;
                 }
                 case "hand": {
                     boss.getEquipment().setItemInMainHand(armorItem.item());
                     boss.getEquipment().setItemInMainHandDropChance(armorItem.dropChance());
                     break;
                 }
                 case "offhand": {
                     boss.getEquipment().setItemInOffHand(armorItem.item());
                     boss.getEquipment().setItemInOffHandDropChance(armorItem.dropChance());
                     break;
                 }
             }
         });

        boss.getPersistentDataContainer().set(NAMESPACED_KEY, PersistentDataType.STRING, mob.id());

        String id = boss.getPersistentDataContainer().get(NAMESPACED_KEY, PersistentDataType.STRING);
        Bukkit.getPluginManager().callEvent(new MobSpawnEvent(id, boss));

    }

    public void end() {

    }

}
