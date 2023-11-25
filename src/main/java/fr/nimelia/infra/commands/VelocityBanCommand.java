package fr.nimelia.infra.commands;

import com.velocitypowered.api.proxy.Player;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

@CommandInfo(name = "velocity", aliases = {"velocity"} )
public class VelocityBanCommand extends VelocityPluginCommand {

    public VelocityBanCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player player)) return;
        player.sendMessage(Component.text(NInfra.getInfra().getCommon().getPrefix() + "§cCommande inconnue </help>"));
    }
}
