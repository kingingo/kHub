package eu.epicpvp.khub.Hub;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.dataserver.gamestats.ServerType;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Addons.AddonNight;
import eu.epicpvp.kcore.Calendar.Calendar;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Command.Admin.CommandGiveCoins;
import eu.epicpvp.kcore.Command.Admin.CommandGiveGems;
import eu.epicpvp.kcore.Command.Admin.CommandURang;
import eu.epicpvp.kcore.Disguise.DisguiseShop;
import eu.epicpvp.kcore.Inventory.InventoryBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import eu.epicpvp.kcore.Listener.Chat.ChatListener;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Pet.Shop.PetShop;
import eu.epicpvp.kcore.Pet.Shop.PlayerPetHandler;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.khub.kHub;
import eu.epicpvp.khub.kManager;
import eu.epicpvp.khub.Command.CommandBroadcast;
import eu.epicpvp.khub.Hub.Listener.HubListener;
import eu.epicpvp.khub.Hub.Listener.HubLoginListener;
import eu.epicpvp.khub.Hub.Listener.HubPremiumListener;
import eu.epicpvp.khub.Hub.Listener.HubVersusListener;
import eu.epicpvp.khub.Hub.Listener.Holidays.BirthdayListener;
import eu.epicpvp.khub.Hub.Listener.Holidays.ChristmasListener;
import eu.epicpvp.khub.Hub.Listener.Holidays.HalloweenListener;
import eu.epicpvp.khub.Hub.Listener.Holidays.SilvesterListener;
import lombok.Getter;

public class HubManager extends kManager{
	@Getter
	private InventoryBase shop;
	
	public HubManager(kHub instance,CommandHandler cmdHandler,MySQL mysql){
		super(instance,cmdHandler,mysql);
		
		if(!kHub.hubType.equalsIgnoreCase("LoginHub")){
			getPermissionManager();
			getHologram().RemoveText();
			
			getCmdHandler().register(CommandGiveCoins.class, new CommandGiveCoins(getMoney()));
			getCmdHandler().register(CommandGiveGems.class, new CommandGiveGems(getMoney()));
			
			this.shop=new InventoryBase(getInstance(), 9, "Shop");
			getPetManager().setHandler(new PlayerPetHandler(ServerType.GAME, mysql, getPetManager(), this.shop, getPermissionManager()));
			getPetManager().setPetShop(new PetShop(getPetManager().getHandler(), getMoney()));
			getDisguiseManager().setDisguiseShop(new DisguiseShop(mysql, this.shop,getPermissionManager(),getMoney(),getDisguiseManager()));
			this.shop.getMain().addButton(2, new ButtonOpenInventory(getPetManager().getPetShop(), UtilItem.Item(new ItemStack(Material.BONE), new String[]{"§bKlick mich um in den Pet Shop zukommen."}, "§7PetShop")));
			this.shop.addPage(getPetManager().getPetShop());
			this.shop.getMain().addButton(6, new ButtonOpenInventory(getDisguiseManager().getDisguiseShop(), UtilItem.Item(new ItemStack(Material.NAME_TAG), new String[]{"§bKlick mich um in den Disguise Shop zukommen."}, "§7DisguiseShop")));
			this.shop.addPage(getDisguiseManager().getDisguiseShop());
			this.shop.getMain().fill(Material.STAINED_GLASS_PANE,(byte)7);
			
			getDisguiseManager().getDisguiseShop().setAsync(true);
			getPetManager().getHandler().setAsync(true);
			
			if(Calendar.getHoliday()!=null){
				switch(Calendar.holiday){
				case HALLOWEEN:
					new HalloweenListener(this);
					new AddonNight(getInstance(), Bukkit.getWorld("world"));
					break;
				case GEBURSTAG:
					new BirthdayListener(this);
					new AddonDay(instance, Bukkit.getWorld("world"));
					break;
				case WEIHNACHTEN:
						new ChristmasListener(this);
						new AddonNight(getInstance(), Bukkit.getWorld("world"));
					break;
				case SILVESTER:
						new SilvesterListener(this);
						new AddonNight(getInstance(), Bukkit.getWorld("world"));
					break;
				default:
					new AddonDay(instance, Bukkit.getWorld("world"));
				}
			}else{
				new AddonDay(instance, Bukkit.getWorld("world"));
			}

			new ChatListener(instance, null,getPermissionManager(),null);
		}

		switch(kHub.hubType.toLowerCase()){
			case "loginhub":
			new HubLoginListener(this);
				break;
			case "versushub":
				new HubVersusListener(this);
				break;
			case "premiumhub":
				new HubPremiumListener(this);
				break;
			default:
				new HubListener(this);
				break;
		}
		
		//getCmdHandler().register(CommandURang.class, new CommandURang(getPermissionManager(),getMysql()));
		getCmdHandler().register(CommandBroadcast.class, new CommandBroadcast());
	}
}
