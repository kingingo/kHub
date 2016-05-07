package eu.epicpvp.khub.Hub.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.dataserver.protocoll.packets.PacketInStatsEdit.Action;
import eu.epicpvp.kcore.GagdetShop.GadgetHandler;
import eu.epicpvp.kcore.GagdetShop.GadgetShop;
import eu.epicpvp.kcore.GagdetShop.Gagdet.MobGun;
import eu.epicpvp.kcore.GagdetShop.Gagdet.Pearl;
import eu.epicpvp.kcore.GagdetShop.Gagdet.PowerAxe;
import eu.epicpvp.kcore.GagdetShop.Gagdet.Ragebow;
import eu.epicpvp.kcore.GagdetShop.Gagdet.SlimeHead;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Listener.VoteListener.VoteListener;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.khub.Hub.HubManager;

public class HubPremiumListener extends HubListener{
	
	public HubPremiumListener(final HubManager manager) {
		super(manager,true);
		GadgetHandler handler = new GadgetHandler(manager.getInstance());
		handler.addGadget(new MobGun(handler));
		handler.addGadget(new Ragebow(handler));
		handler.addGadget(new PowerAxe(handler));
		handler.addGadget(new SlimeHead(handler));
		handler.addGadget(new Pearl(handler));
		GadgetShop store = new GadgetShop(handler);
		manager.getShop().addButton(13, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				if(player.isOp()){
					store.open(player, UtilInv.getBase());
				}
			}
			
		}, UtilItem.Item(new ItemStack(Material.PISTON_BASE), new String[]{"§bKlick mich um in den Gadget Shop zukommen."}, "§7Gadgets")));
		
		new VoteListener(manager.getInstance(), false,new Callback<String>() {
			
			@Override
			public void call(String playerName) {
				LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(playerName);
				
				loadedplayer.changeGems(Action.ADD, 5);
				loadedplayer.changeCoins(Action.ADD, 100);
				UtilServer.getClient().brotcastMessage(null, TranslationHandler.getText("PREFIX")+ "§b" + playerName + " hat gevotet und §a5 Gems + 100 Coins §berhalten§l! §7>>§5§l /Vote");
			}
		});
	}	
}