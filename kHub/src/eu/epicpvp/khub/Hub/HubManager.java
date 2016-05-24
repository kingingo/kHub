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
import eu.epicpvp.kcore.Command.Admin.CommandGivePro;
import eu.epicpvp.kcore.Command.Admin.CommandURang;
import eu.epicpvp.kcore.Disguise.DisguiseShop;
import eu.epicpvp.kcore.GagdetShop.GadgetHandler;
import eu.epicpvp.kcore.GagdetShop.GadgetShop;
import eu.epicpvp.kcore.GagdetShop.Gagdet.MobGun;
import eu.epicpvp.kcore.GagdetShop.Gagdet.Pearl;
import eu.epicpvp.kcore.GagdetShop.Gagdet.PowerAxe;
import eu.epicpvp.kcore.GagdetShop.Gagdet.Ragebow;
import eu.epicpvp.kcore.GagdetShop.Gagdet.SlimeHead;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventoryCopy;
import eu.epicpvp.kcore.Listener.Chat.ChatListener;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.MysteryBox.MysteryBoxManager;
import eu.epicpvp.kcore.MysteryBox.Store.MysteryStore;
import eu.epicpvp.kcore.Particle.WingShop;
import eu.epicpvp.kcore.Pet.Shop.PetShop;
import eu.epicpvp.kcore.Pet.Shop.PlayerPetHandler;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.UtilInv;
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
	private InventoryPageBase shop;
	
	public HubManager(kHub instance,CommandHandler cmdHandler,MySQL mysql){
		super(instance,cmdHandler,mysql);
		
		if(!kHub.hubType.equalsIgnoreCase("LoginHub")){
			getPermissionManager();
			getHologram().RemoveText();
			
			getCmdHandler().register(CommandGiveCoins.class, new CommandGiveCoins(getMoney()));
			getCmdHandler().register(CommandGiveGems.class, new CommandGiveGems(getMoney()));
			getCmdHandler().register(CommandGivePro.class, new CommandGivePro());
			
			this.shop=new InventoryPageBase(InventorySize._45, "Shop");
			UtilInv.getBase().addPage(shop);
			
			getPetManager().setHandler(new PlayerPetHandler(ServerType.GAME, mysql, getPetManager(), getPermissionManager()));
			getPetManager().setPetShop(new PetShop(getPetManager().getHandler(), getMoney()));
			this.shop.addButton(11, new ButtonOpenInventory(getPetManager().getPetShop(), UtilItem.Item(new ItemStack(Material.BONE), new String[]{"§bKlick mich um in den Pet Shop zukommen."}, "§7Pets")));
			UtilInv.getBase().addPage(getPetManager().getPetShop());
			
			getDisguiseManager().setDisguiseShop(new DisguiseShop(mysql, getPermissionManager(),getMoney(),getDisguiseManager()));
			this.shop.addButton(15, new ButtonOpenInventory(getDisguiseManager().getDisguiseShop(), UtilItem.Item(new ItemStack(Material.NAME_TAG), new String[]{"§bKlick mich um in den Disguise Shop zukommen."}, "§7Disguises")));
			UtilInv.getBase().addPage(getDisguiseManager().getDisguiseShop());
			
			getDisguiseManager().getDisguiseShop().setAsync(true);
			getPetManager().getHandler().setAsync(true);
			
			GadgetHandler handler = new GadgetHandler(getInstance());
			handler.addGadget(new MobGun(handler));
			handler.addGadget(new Ragebow(handler));
			handler.addGadget(new PowerAxe(handler));
			handler.addGadget(new SlimeHead(handler));
			handler.addGadget(new Pearl(handler));
			GadgetShop gadgetShop = new GadgetShop(handler);
			getShop().addButton(13, new ButtonOpenInventoryCopy(gadgetShop, UtilInv.getBase(), UtilItem.Item(new ItemStack(Material.PISTON_BASE), new String[]{"§bKlick mich um in den Gadget Shop zukommen."}, "§7Gadgets")));
			WingShop wingShop = new WingShop(getInstance());
			getShop().addButton(29, new ButtonOpenInventoryCopy(wingShop, UtilInv.getBase(), UtilItem.Item(new ItemStack(Material.FEATHER), new String[]{"§bKlick mich um in den Wings Shop zukommen."}, "§7Wings")));
			MysteryBoxManager boxManager = new MysteryBoxManager(getInstance());
			MysteryStore store = new MysteryStore(boxManager.getChest("MysteryBox"));
			getShop().addButton(31, new ButtonOpenInventoryCopy(store, UtilInv.getBase(), UtilItem.Item(new ItemStack(Material.ENDER_CHEST), new String[]{"§bKlick mich um in den Mystery Box Shop zukommen."}, "§7MysteryBox")));
			
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
			this.shop.fill(Material.STAINED_GLASS_PANE,(byte)7);
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
		
		getCmdHandler().register(CommandURang.class, new CommandURang(getPermissionManager()));
		getCmdHandler().register(CommandBroadcast.class, new CommandBroadcast());
	}
}
