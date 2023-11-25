package fr.nimelia.infra.commands;

import com.velocitypowered.api.proxy.Player;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.api.common.RankTypes;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.*;

@CommandInfo(name = "group", description = "Regroupe plusieurs joueurs", aliases = {"g", "groups"})
public class GroupCommand extends VelocityPluginCommand {

    private final HashMap<UUID, UUID> invites = new HashMap<>();
    private final HashMap<UUID, List<UUID>> groups = new HashMap<>();
    private Player target;
    private Optional<Player> targetOptional;

    public GroupCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> tabulation = new ArrayList<>();
        if (invocation.source() instanceof Player player)
            if (invocation.arguments().length == 1) {
                tabulation.add("invite");
                tabulation.add("leave");
                tabulation.add("list");
                tabulation.add("delete");
            }
        if (invocation.arguments().length == 2) {
            for (Player players : NInfra.getInfra().getServer().getAllPlayers()) {
                if (players.getUsername().toLowerCase().startsWith(invocation.arguments()[1].toLowerCase())) {
                    tabulation.add(players.getUsername());
                }
            }
        }
        return tabulation;
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player player)) return;
        String prefix = " §9§lGroupe §7» §f";
        //              ""

        if (args.length == 0 || args.length > 2) {
            player.sendMessage(Component.text(prefix + "Voici la liste des commandes:\n"));
            player.sendMessage(Component.text("  §1/§9g invite §8- §fInviter un joueur"));
            player.sendMessage(Component.text("  §1/§9g leave §8- §fQuitter le groupe"));
            player.sendMessage(Component.text("  §1/§9g list §8- §fListe des joueurs du groupe"));
            player.sendMessage(Component.text("  §1/§9g delete §8- §fSupprimer le groupe\n"));
            return;
        }
        if (args.length == 2) {
            targetOptional = NInfra.getInfra().getServer().getPlayer(args[1]);
            if (targetOptional.isEmpty() || targetOptional.get().getUsername().equalsIgnoreCase(player.getUsername())) {
                player.sendMessage(Component.text(prefix + "§cLe joueur n'est pas connecté !"));
                return;
            }
            target = targetOptional.get();
        }


        switch (args[0]) {
            case "invite":
                if (args.length != 2) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /group invite <player>"));
                    return;
                }
                if (containsPlayers(target.getUniqueId()) != null) {
                    player.sendMessage(Component.text(prefix + "§cCe joueur déjà dans un groupe !"));
                    return;
                }
                if (invites.containsKey(player.getUniqueId()) && invites.get(player.getUniqueId()).equals(target.getUniqueId())) {
                    player.sendMessage(Component.text(prefix + "§cVous avez déjà une invitation en cours !"));
                    return;
                }
                invites.put(player.getUniqueId(), target.getUniqueId());
                invites.put(target.getUniqueId(), player.getUniqueId());
                player.sendMessage(Component.text(prefix + "Vous avez invité §9" + target.getUsername() + " §fà rejoindre votre groupe !"));
                target.sendMessage(Component.text(prefix + "§9" + player.getUsername() + " §fvous a invité à rejoindre son groupe !\n"));
                target.sendMessage(Component.text("                   §7[§aAccepter§7]").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/group accept")).append(Component.text("  §fou  ")).append(Component.text("§7[§cRefuser§7] \n").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/group deny"))));
                break;
            case "accept":
                if (args.length != 1) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /group accept"));
                    return;
                }
                if (!invites.containsKey(player.getUniqueId())) {
                    player.sendMessage(Component.text(prefix + "§cVous n'avez pas d'invitation en cours !"));
                    return;
                }
                if (containsPlayers(player.getUniqueId()) != null) {
                    player.sendMessage(Component.text(prefix + "§cVous êtes déjà dans un groupe </groupe leave> !"));
                    return;
                }
                targetOptional = NInfra.getInfra().getServer().getPlayer(invites.get(player.getUniqueId()));
                if (targetOptional.isEmpty() || targetOptional.get().getUsername().equalsIgnoreCase(player.getUsername())) {
                    player.sendMessage(Component.text(prefix + "§cLe joueur n'est pas connecté !"));
                    return;
                }
                target = targetOptional.get();
                invites.remove(player.getUniqueId());
                invites.remove(target.getUniqueId());
                //playerAdd
                List<UUID> players = groups.get(target.getUniqueId());
                if (players == null) {
                    players = new ArrayList<>();
                    players.add(target.getUniqueId());
                }
                players.add(player.getUniqueId());
                groups.put(target.getUniqueId(), players);
                for (UUID uuid : containsPlayers(target.getUniqueId())) {
                    Optional<Player> playerOptional = NInfra.getInfra().getServer().getPlayer(uuid);
                    if (playerOptional.isEmpty()) continue;
                    Player Gplayers = playerOptional.get();
                    Gplayers.sendMessage(Component.text(prefix + "§9" + player.getUsername() + " §fa rejoint le groupe !"));
                }
                break;
            case "deny":
                if (args.length != 1) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /group deny"));
                    return;
                }
                if (!invites.containsKey(player.getUniqueId())) {
                    player.sendMessage(Component.text(prefix + "§cVous n'avez pas d'invitation en cours !"));
                    return;
                }
                if (containsPlayers(player.getUniqueId()) != null) {
                    player.sendMessage(Component.text(prefix + "§cVous êtes déjà dans un groupe </groupe leave> !"));
                    return;
                }
                targetOptional = NInfra.getInfra().getServer().getPlayer(invites.get(player.getUniqueId()));
                if (targetOptional.isEmpty() || targetOptional.get().getUsername().equalsIgnoreCase(player.getUsername())) {
                    player.sendMessage(Component.text(prefix + "§cLe joueur n'est pas connecté !"));
                    return;
                }
                target = targetOptional.get();
                invites.remove(player.getUniqueId());
                invites.remove(target.getUniqueId());
                target.sendMessage(Component.text(prefix + "§9" + player.getUsername() + " §fa refusé votre invitation !"));
                player.sendMessage(Component.text(prefix + "Vous avez refusé l'invitation de §9" + target.getUsername() + "§f !"));
                break;
            case "leave":
                if (args.length != 1) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /group leave"));
                    return;
                }
                if (containsPlayers(player.getUniqueId()) == null) {
                    player.sendMessage(Component.text(prefix + "§cVous n'êtes pas dans un groupe !"));
                    return;
                }
                for (UUID uuid : containsPlayers(player.getUniqueId())) {
                    Optional<Player> playerOptional = NInfra.getInfra().getServer().getPlayer(uuid);
                    if (playerOptional.isEmpty()) continue;
                    Player Gplayers = playerOptional.get();
                    if (Gplayers.getUniqueId().equals(player.getUniqueId())) continue;
                    Gplayers.sendMessage(Component.text(prefix + "§9" + player.getUsername() + " §fa quitté le groupe !"));
                }
                if (groups.containsKey(player.getUniqueId())) {
                    groups.remove(player.getUniqueId());
                    groups.put(groups.get(player.getUniqueId()).get(0), groups.get(player.getUniqueId()));
                    groups.remove(player.getUniqueId());
                    return;
                }
                if (containsPlayers(player.getUniqueId()).size() == 2) {
                    groups.remove(player.getUniqueId());
                    return;
                }
                containsPlayers(player.getUniqueId()).remove(player.getUniqueId());
                player.sendMessage(Component.text(prefix + "Vous avez quitté le groupe !"));
                break;
            case "list":
                if (args.length != 1) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /group list"));
                    return;
                }
                if (containsPlayers(player.getUniqueId()) == null) {
                    player.sendMessage(Component.text(prefix + "§cVous n'êtes pas dans un groupe !"));
                    return;
                }
                player.sendMessage(Component.text(prefix + "§7Liste des joueurs du groupe:\n"));
                for (UUID uuid : containsPlayers(player.getUniqueId())) {
                    Optional<Player> playerOptional = NInfra.getInfra().getServer().getPlayer(uuid);
                    if (playerOptional.isEmpty()) continue;
                    Player Gplayers = playerOptional.get();
                    if (groups.containsKey(Gplayers.getUniqueId())) {
                        if (Gplayers.getUniqueId().equals(player.getUniqueId())) {
                            player.sendMessage(Component.text("  §1♛ §9" + Gplayers.getUsername() + " §7(§aVous§7)"));
                            continue;
                        }
                        player.sendMessage(Component.text("  §1♛ §9" + Gplayers.getUsername()));
                        continue;
                    }
                    if (Gplayers.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(Component.text("  §1• §9" + Gplayers.getUsername() + " §7(§aVous§7)"));
                        continue;
                    }
                    player.sendMessage(Component.text("  §1•§9" + Gplayers.getUsername()));
                }
                break;
            case "delete":
                if (args.length != 1) {
                    player.sendMessage(Component.text(prefix + "§cUsage: /group delete"));
                    return;
                }
                if (containsPlayers(player.getUniqueId()) == null) {
                    player.sendMessage(Component.text(prefix + "§cVous n'êtes pas dans un groupe !"));
                    return;
                }
                if (!groups.containsKey(player.getUniqueId())) {
                    player.sendMessage(Component.text(prefix + "§cVous n'êtes pas le chef du groupe !"));
                    return;
                }
                for (UUID uuid : containsPlayers(player.getUniqueId())) {
                    Optional<Player> playerOptional = NInfra.getInfra().getServer().getPlayer(uuid);
                    if (playerOptional.isEmpty()) continue;
                    if (playerOptional.get().getUniqueId().equals(player.getUniqueId())) continue;
                    Player Gplayers = playerOptional.get();
                    Gplayers.sendMessage(Component.text(prefix + "§9" + player.getUsername() + " §fa supprimé le groupe !"));
                }
                groups.remove(player.getUniqueId());
                player.sendMessage(Component.text(prefix + "Vous avez supprimé le groupe !"));
                break;
        }
    }

    public List<UUID> containsPlayers(UUID uuid) {
        if (groups.containsKey(uuid)) {
            return groups.get(uuid);
        }
        for (List<UUID> players : groups.values()) {
            if (players.contains(uuid)) {
                return players;
            }
        }
        return null;
    }
}
