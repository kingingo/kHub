package me.kingingo.khub.Pet;

import java.util.HashMap;
import java.util.List;

import me.kingingo.kcore.Permission.Permission;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface PetInterface {
void AddPlayer(Player p);
void remove(Player p);
void Join(String[] i,Player pl);
void Save(Player p);
Permission getPermission();
Entity getEntity(Player p);
HashMap<Player, RideInterface> getList();
List<String> Description();
int getTokens();
int getCoins();
Inventory getOptionInv();
Inventory getBuyInv();
Inventory getCointInv();
Inventory getSelectInv();
ItemStack getIcon();
String getName();
}
