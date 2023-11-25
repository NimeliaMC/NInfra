package fr.nimelia.infra.commands.admin;

import com.velocitypowered.api.proxy.Player;
import fr.nimelia.api.account.Account;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

@CommandInfo(name = "coins", permission = 100, aliases = {"coin"}, description = "Gestion des coins")
public class CoinsCommand extends VelocityPluginCommand {

    public CoinsCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player player)) return;
        String prefix = " §2§lCoins §7» §f";

        if (args.length == 0 || args.length > 3) {
            player.sendMessage(Component.text(prefix + "Voici la liste des commandes:\n"));
            player.sendMessage(Component.text("  §2/§acoins add <player> <int> §8- §fAjouter des coins a un joueur"));
            player.sendMessage(Component.text("  §2/§acoins remove <player> <int> §8- §fEnlever des coins a un joueur"));
            player.sendMessage(Component.text("  §2/§acoins amount <player> §8- §fVoir les coins d'un joueur"));
            player.sendMessage(Component.text("  §2/§acoins clear <player> §8- §fSupprimer les coins a un joueur\n"));
            return;
        }

        if (NInfra.getInfra().getCommon().getAccountManager().getAccountsByName(args[1]) == null) {
            player.sendMessage(Component.text(prefix + "§cLe joueur n'existe pas"));
            return;
        }

        Account account = NInfra.getInfra().getCommon().getAccountManager().getAccountsByName(args[1]);
        String target = args[1];

        switch (args[0]) {
            case "add":
                if (args.length != 3) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /coins add <player> <int>"));
                    return;
                }
                if (!args[2].matches("[0-9]+")) {
                    player.sendMessage(Component.text(prefix + "§cLe nombre doit être un entier"));
                    return;
                }
                account.setCoins(account.getCoins() + Integer.parseInt(args[2]));
                player.sendMessage(Component.text(prefix + "§fVous avez ajouté §a" + args[2] + " §fcoins à §2" + target));
                break;
            case "remove":
                if (args.length != 3) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /coins remove <player> <int>"));
                    return;
                }
                if (!args[2].matches("[0-9]+")) {
                    player.sendMessage(Component.text(prefix + "§cLe nombre doit être un entier"));
                    return;
                }
                account.setCoins(account.getCoins() - Integer.parseInt(args[2]));
                player.sendMessage(Component.text(prefix + "§fVous avez retiré §a" + args[2] + " §fcoins à §2" + target));
                break;
            case "clear":
                if (args.length != 2) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /coins clear <player>"));
                    return;
                }
                account.setCoins(0);
                player.sendMessage(Component.text(prefix + "§fVous avez supprimé les coins de §2" + target));
                break;
            case "amount":
                if (args.length != 2) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /coins amount <player>"));
                    return;
                }
                player.sendMessage(Component.text(prefix + "§fLe joueur §2" + target + " §fpossède §a" + account.getCoins() + " §fcoins"));
        }

    }
}
