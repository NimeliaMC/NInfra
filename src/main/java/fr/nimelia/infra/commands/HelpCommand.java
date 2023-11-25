package fr.nimelia.infra.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.nimelia.api.account.AccountManager;
import fr.nimelia.api.commands.PluginCommand;
import fr.nimelia.api.common.RankTypes;
import fr.nimelia.infra.NInfra;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "help" , description = "Affiche la liste des commandes")
public class HelpCommand extends VelocityPluginCommand {

    private final AccountManager account = NInfra.getInfra().getCommon().getAccountManager();

    public HelpCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> tabulation = new ArrayList<>();
        if (invocation.source() instanceof Player player)
        if (invocation.arguments().length == 1) {
            if (account.getAccount(player.getUniqueId()).getRankTypes().equals(RankTypes.ADMIN)) {
                tabulation.add("admin");
                tabulation.add("moderator");
            }
            if (account.getAccount(player.getUniqueId()).getRankTypes().getWeight() >= 70) {
                tabulation.add("moderator");
            }
        }
        return tabulation;
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player player)) return;
        String prefix = " §d§lHelp §7» §f";

        if (args.length == 0) {
            invocation.source().sendMessage(Component.text(prefix + "Toutes les commandes:\n"));
            for (PluginCommand commands : NInfra.getInfra().getCommon().getCommandList()) {
                if (commands.getInfo().name().equalsIgnoreCase("help") || commands.getInfo().name().equalsIgnoreCase("velocity")) continue;
                if (commands.getInfo().permission() == 0) {
                    invocation.source().sendMessage(Component.text("  §5/§d" + commands.getInfo().name() + " §8- §f" + commands.getInfo().description()));
                }
            }
            invocation.source().sendMessage(Component.text(" "));
            return;
        } else if (args.length > 1) {
            player.sendMessage(Component.text(prefix + "§cCommande inconnue"));
            return;
        }

        switch (args[0]) {
            case "admin":
                if (account.getAccount(player.getUniqueId()).getRankTypes().equals(RankTypes.ADMIN)) {
                    invocation.source().sendMessage(Component.text(prefix + "Toutes les commandes Admin:\n"));
                    for (PluginCommand commands : NInfra.getInfra().getCommon().getCommandList()) {
                        if (commands.getInfo().name().equalsIgnoreCase("help") || commands.getInfo().name().equalsIgnoreCase("velocity") || commands.getInfo().permission() < 100) continue;
                        invocation.source().sendMessage(Component.text("  §5/§d" + commands.getInfo().name() + " §8- §f" + commands.getInfo().description()));
                    }
                    invocation.source().sendMessage(Component.text(" "));
                    return;
                }
                player.sendMessage(Component.text(prefix + "§cCommande inconnue"));
                break;
            case "moderator":
                if (account.getAccount(player.getUniqueId()).getRankTypes().getWeight() >= 70) {
                    invocation.source().sendMessage(Component.text(prefix + "Toutes les commandes de Moderation:\n"));
                    for (PluginCommand commands : NInfra.getInfra().getCommon().getCommandList()) {
                        if (commands.getInfo().name().equalsIgnoreCase("help") || commands.getInfo().name().equalsIgnoreCase("velocity") || commands.getInfo().permission() != 70) continue;
                        invocation.source().sendMessage(Component.text("  §5/§d" + commands.getInfo().name() + " §8- §f" + commands.getInfo().description()));
                    }
                    invocation.source().sendMessage(Component.text(" "));
                }
                break;
        }
    }
}
