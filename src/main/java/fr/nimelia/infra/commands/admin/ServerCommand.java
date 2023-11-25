package fr.nimelia.infra.commands.admin;

import com.velocitypowered.api.proxy.Player;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.api.server.ServerManager;
import fr.nimelia.api.server.ServerType;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

@CommandInfo(name="instance", permission = 100, description = "Gestion des instances")
public class ServerCommand extends VelocityPluginCommand {

    public ServerCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player)) return;
        Player player = (Player) invocation.source();
        ServerManager serverManager = NInfra.getInfra().getCommon().getServerManager();

        if (invocation.arguments().length !=2) {
            invocation.source().sendMessage(Component.text("§cUsage: /instance <create/delete/start> <type>"));
            return;
        }

        switch (args[0]) {
            case "create":
                serverManager.createInstance(ServerType.valueOf(args[1].toUpperCase()));
                player.sendMessage(Component.text("§aCréation du serveur " + args[1] + " en cours..."));
                break;
            case "delete":
                serverManager.deleteInstance(args[1]);
                player.sendMessage(Component.text("§aSuppression du serveur " + args[1] + " en cours..."));
                break;
        }
    }
}
