package fr.nimelia.infra.commands.admin;


import com.velocitypowered.api.proxy.Player;
import fr.nimelia.api.CommonAPI;
import fr.nimelia.api.account.Account;
import fr.nimelia.api.account.AccountManager;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.api.common.RankTypes;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CommandInfo(name="nrank", permission = 100, description = "Permet de gérer les ranks")
public final class RankCommmand extends VelocityPluginCommand {

    private CommonAPI api = NInfra.getInfra().getCommon();

    public RankCommmand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> tabulation = new ArrayList<>();
        if (invocation.arguments().length == 1) {
            tabulation.add("add");
            tabulation.add("remove");
            tabulation.add("list");
        }
        if (invocation.arguments().length == 2) {
            for (Player proxiedPlayer : NInfra.getInfra().getServer().getAllPlayers()) {
                tabulation.add(proxiedPlayer.getUsername());
            }
        }
        if (invocation.arguments().length == 3) {
            for (RankTypes rankTypes : RankTypes.values()) {
                tabulation.add(rankTypes.name());
            }
        }
        return tabulation;
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player player)) return;
        String prefix = " §6§lRanks §7» §f";
        if (args.length <= 2) {
            invocation.source().sendMessage(Component.text(prefix + "Voici la liste des commandes:\n"));
            player.sendMessage(Component.text("  §e/§6nrank add §8- §fAjouter un rank à un joueur"));
            player.sendMessage(Component.text("  §e/§6nrank remove §8- §fRetirer un rank à un joueur\n"));
            return;
        }
        Optional<Player> targetOptional = NInfra.getInfra().getServer().getPlayer(args[1]);
        if (targetOptional.isEmpty() || targetOptional.get().getUsername().equalsIgnoreCase(player.getUsername())) {
            player.sendMessage(Component.text(prefix + "§cLe joueur n'est pas connecté !"));
            return;
        }
        Player target = targetOptional.get();
        AccountManager account = api.getAccountManager();
        Account accountTarget = account.getAccount(target.getUniqueId());
        accountTarget.setCoins(accountTarget.getCoins() + 1000);

        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length != 3) {
                    invocation.source().sendMessage(Component.text(prefix + "§cUsage: /nrank add <player> <rank>"));
                    return;
                }
                if (Arrays.stream(RankTypes.values()).filter(rankTypes -> rankTypes.name().equalsIgnoreCase(args[2])).findFirst().isEmpty()) {
                    invocation.source().sendMessage(Component.text(prefix + "§cLe rank n'existe pas."));
                    return;
                }
                if (account.getAccount(target.getUniqueId()).getRankTypes().equals(RankTypes.valueOf(args[2].toUpperCase()))) {
                    invocation.source().sendMessage(Component.text(prefix + "§cLe joueur a déjà ce rank."));
                    return;
                }
                accountTarget.setRankTypes(RankTypes.valueOf(args[2].toUpperCase()));
                account.updateAccount(accountTarget);
                invocation.source().sendMessage(Component.text(prefix + "§aLe rank a bien été ajouté."));
                target.sendMessage(Component.text(prefix + "§aVous avez reçu le rank " + RankTypes.valueOf(args[2].toUpperCase()).getName() + "§a."));
                break;
            case "remove":
                if (args.length != 3) {
                    invocation.source().sendMessage(Component.text(prefix + "§cUsage: /nrank remove <player> <rank>"));
                    return;
                }
                accountTarget.setRankTypes(RankTypes.valueOf(args[2].toUpperCase()));
                account.updateAccount(accountTarget);
                invocation.source().sendMessage(Component.text(prefix + "§aLe rank a bien été retiré."));
                break;
            default:
                invocation.source().sendMessage(Component.text(prefix + "§cUsage: /nrank <add/remove> <player>"));
                break;
        }
    }
}
