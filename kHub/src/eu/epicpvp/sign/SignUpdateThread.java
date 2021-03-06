package eu.epicpvp.sign;

import eu.epicpvp.dataserver.protocoll.packets.PacketInLobbyServerRequest;
import eu.epicpvp.dataserver.protocoll.packets.PacketOutLobbyServer;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SignUpdateThread implements Runnable{
	private SignManager manager;

	@Override
	public void run() {
		while (manager.getOwner().getManager().getInstance().isEnabled()) {
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(UtilServer.getClient().getHandle().isConnected()){
				GameType[] types = manager.buildGamesIndex();
				PacketInLobbyServerRequest.GameRequest requests[] = new PacketInLobbyServerRequest.GameRequest[types.length];
				for(int i = 0;i<types.length;i++)
					requests[i] = new PacketInLobbyServerRequest.GameRequest(types[i], -1);
				try{
					PacketOutLobbyServer response = UtilServer.getClient().getLobbies(requests).getSync(2500);
					if(response == null || response.getResponse() == null){
						System.out.println("Cant request signs!");
						continue;
					}
					manager.updateSigns(response.getResponse());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
