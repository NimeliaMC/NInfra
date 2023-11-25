package fr.nimelia.infra.commands.moderator;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CommandInfo(name = "serveur", aliases = {"server", "send"}, permission = 70, description = "Envoyer un joueur sur un serveur")
public class ServeurCommand extends VelocityPluginCommand {

    public ServeurCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> tabulation = new ArrayList<>();
        if (invocation.arguments().length == 1) {
            String prefix = invocation.arguments()[0].toLowerCase();
            return NInfra.getInfra().getServer().getAllPlayers().stream().map(Player::getUsername).filter(name -> name.toLowerCase().startsWith(prefix)).collect(Collectors.toList());
        }

        if (invocation.arguments().length == 2) {
            if (invocation.arguments().length == 2) {
                String prefix = invocation.arguments()[1].toLowerCase();
                return NInfra.getInfra().getServer().getAllServers().stream().map(registeredServer -> registeredServer.getServerInfo().getName()).filter(name -> name.toLowerCase().startsWith(prefix)).collect(Collectors.toList());
            }
        }
        return tabulation;
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if(!(invocation.source() instanceof Player player)) return;

        if(invocation.arguments().length != 2){
            player.sendMessage(Component.text(NInfra.getInfra().getCommon().getPrefix() + "§cUsage: /serveur <joueur> <serveur>"));
            return;
        }

        Optional<RegisteredServer> server = NInfra.getInfra().getServer().getServer(args[1]);
        Optional<Player> target = NInfra.getInfra().getServer().getPlayer(args[0]);

        if(target.isEmpty()){
            player.sendMessage(Component.text("§cLe joueur n'est pas connecté."));
            return;
        }

        if(server.isEmpty()){
            player.sendMessage(Component.text("§cLe serveur n'existe pas."));
            return;
        }

        target.get().createConnectionRequest(server.get()).fireAndForget();
        player.sendMessage(Component.text(NInfra.getInfra().getCommon().getPrefix() + "§aLe joueur a été envoyé sur le serveur."));
    }
}
