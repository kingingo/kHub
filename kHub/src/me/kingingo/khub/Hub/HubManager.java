package me.kingingo.khub.Hub;

import lombok.Getter;
import me.kingingo.kcore.Addons.AddonDay;
import me.kingingo.kcore.Addons.AddonNight;
import me.kingingo.kcore.Calendar.Calendar;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandCoins;
import me.kingingo.kcore.Command.Admin.CommandGiveGems;
import me.kingingo.kcore.Command.Admin.CommandGroup;
import me.kingingo.kcore.Command.Admin.CommandURang;
import me.kingingo.kcore.Disguise.DisguiseShop;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.Inventory.InventoryBase;
import me.kingingo.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Pet.Shop.PetShop;
import me.kingingo.kcore.Pet.Shop.PlayerPetHandler;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.khub.kHub;
import me.kingingo.khub.kManager;
import me.kingingo.khub.Command.CommandBroadcast;
import me.kingingo.khub.Hub.Listener.HubListener;
import me.kingingo.khub.Hub.Listener.HubLoginListener;
import me.kingingo.khub.Hub.Listener.HubPremiumListener;
import me.kingingo.khub.Hub.Listener.HubVersusListener;
import me.kingingo.khub.Hub.Listener.Holidays.BirthdayListener;
import me.kingingo.khub.Hub.Listener.Holidays.ChristmasListener;
import me.kingingo.khub.Hub.Listener.Holidays.HalloweenListener;
import me.kingingo.khub.Hub.Listener.Holidays.SilvesterListener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HubManager extends kManager{
	@Getter
	private InventoryBase shop;
	
	public HubManager(kHub instance,CommandHandler cmdHandler,MySQL mysql,PacketManager packetManager){
		super(instance,cmdHandler,mysql,packetManager);
		
		if(!kHub.hubType.equalsIgnoreCase("LoginHub")){
			getPermissionManager();
			getHologram().RemoveText();
			
			getCmdHandler().register(CommandCoins.class, new CommandCoins(getCoins(),getPacketManager()));
			getCmdHandler().register(CommandGiveGems.class, new CommandGiveGems(getGems(),getPacketManager()));
			
			this.shop=new InventoryBase(getInstance(), 9, "Shop");
			getPetManager().setHandler(new PlayerPetHandler(ServerType.GAME, getPetManager(), this.shop, getPermissionManager()));
			getPetManager().setPetShop(new PetShop(getPetManager().getHandler(), getGems(),getCoins()));
			getDisguiseManager().setDisguiseShop(new DisguiseShop(this.shop,getPermissionManager(),getGems(),getCoins(),getDisguiseManager()));
			this.shop.getMain().addButton(2, new ButtonOpenInventory(getPetManager().getPetShop(), UtilItem.Item(new ItemStack(Material.BONE), new String[]{"§bKlick mich um in den Pet Shop zukommen."}, "§7PetShop")));
			this.shop.addPage(getPetManager().getPetShop());
			this.shop.getMain().addButton(6, new ButtonOpenInventory(getDisguiseManager().getDisguiseShop(), UtilItem.Item(new ItemStack(Material.NAME_TAG), new String[]{"§bKlick mich um in den Disguise Shop zukommen."}, "§7DisguiseShop")));
			this.shop.addPage(getDisguiseManager().getDisguiseShop());
			this.shop.getMain().fill(Material.STAINED_GLASS_PANE,(byte)7);
			
			getDisguiseManager().getDisguiseShop().setAsync(true);
			getPetManager().getHandler().setAsync(true);
			getGems().setAsync(true);
			getCoins().setAsync(true);
			
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
		
		switch(kHub.hubType){
			case "LoginHub":
			new HubLoginListener(this);
				break;
			case "VersusHub":
				new HubVersusListener(this);
				break;
			case "PremiumHub":
				new HubPremiumListener(this);
				break;
			default:
				new HubListener(this);
				break;
		}
		
		getCmdHandler().register(CommandGroup.class, new CommandGroup(getPermissionManager()));
		getCmdHandler().register(CommandURang.class, new CommandURang(getPermissionManager(),getMysql()));
		getCmdHandler().register(CommandBroadcast.class, new CommandBroadcast());
	}
}
