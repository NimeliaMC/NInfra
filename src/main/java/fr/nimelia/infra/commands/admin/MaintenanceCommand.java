package fr.nimelia.infra.commands.admin;

import fr.nimelia.api.CommonAPI;
import fr.nimelia.infra.NInfra;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "maintenance", permission = 100, description = "Active ou désactive la maintenance")
public class MaintenanceCommand extends VelocityPluginCommand {

    private CommonAPI api = NInfra.getInfra().getCommon();

    public MaintenanceCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> tabulation = new ArrayList<>();
        if (invocation.arguments().length == 1) {
            tabulation.add("on");
            tabulation.add("off");
        }
        return tabulation;
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        String prefix = " §c§lMaintenance §7» §f";

        if (args.length != 1) {
            invocation.source().sendMessage(Component.text(prefix + "Voici la liste des commandes:\n"));
            invocation.source().sendMessage(Component.text("  §4/§cmaintenance on §8- §fActive la maintenance"));
            invocation.source().sendMessage(Component.text("  §4/§cmaintenance off §8- §fDésactive la maintenance\n"));
            return;
        }
        
        switch (args[0]) {
            case "on":
                NInfra.getInfra().maintenance = true;
                invocation.source().sendMessage(Component.text(prefix + "§cLe serveur est maintenant en maintenance !"));
                break;
            case "off":
                NInfra.getInfra().maintenance = false;
                invocation.source().sendMessage(Component.text(prefix + "§aLe serveur n'est plus en maintenance !"));
                break;
            default:
                invocation.source().sendMessage(Component.text(prefix + "§cUsage: /maintenance <on/off>"));
                break;
        }
    }
}
