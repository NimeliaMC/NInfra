package fr.nimelia.infra.commands.admin;

import com.velocitypowered.api.proxy.ProxyServer;
import fr.nimelia.api.CommonAPI;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

import java.util.List;

@CommandInfo(name = "ups", permission = 100, description = "Savoir la nombre de joueur unique")
public class UpCommand extends VelocityPluginCommand {
    public UpCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public void onCommand(Invocation invocation, String[] strings) {
        invocation.source().sendMessage(Component.text("il y a " + CommonAPI.getApi().getAccountManager().getAccounts().size() + " joueurs uniques"));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return super.suggest(invocation);
    }
}
