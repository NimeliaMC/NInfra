package fr.nimelia.infra.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.nimelia.api.account.AccountManager;
import fr.nimelia.api.common.RankTypes;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

import java.util.*;

public class PlayerListener {

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        AccountManager account = NInfra.getInfra().getCommon().getAccountManager();

        if (!account.isAccountExists(player.getUniqueId())) {
            account.createAccount(player.getUniqueId(), player.getUsername(), player.getRemoteAddress().getAddress().toString(), RankTypes.DEFAULT, true, true, 0, new ArrayList<>());
        }

        if (NInfra.getInfra().isMaintenance() && NInfra.getInfra().getCommon().getAccountManager().getAccount(player.getUniqueId()).getRankTypes().getWeight()< 70) {
            player.disconnect(Component.text("§d@Nimelia §f- §7[§d1.8§7-§d1.12§7] \n\n" +
                    "§cLe server est en maintenance. Patience, nous ouvrirons bientôt !\n" +
                    "§cPour suivre l'actualité rejoingnez notre serveur discord. \n\n" +
                    "§7Discord §8» §ddiscord.gg/nimelia\n"));
        }
    }

    public void initialServer(PlayerChooseInitialServerEvent event){
        List<RegisteredServer> lobby = NInfra.getInfra().getServer().getAllServers().stream()
                .filter(registeredServer -> registeredServer.getServerInfo().getName().contains("Lobby"))
                .toList();

        if(lobby.size() == 0){
            System.out.println("pas de lobby");
            return;
        }

        lobby.sort((server1, server2) -> Integer.compare(server2.getPlayersConnected().size(), server1.getPlayersConnected().size()));

        Collections.reverse(lobby);

        event.setInitialServer(lobby.get(0));

    }




}
