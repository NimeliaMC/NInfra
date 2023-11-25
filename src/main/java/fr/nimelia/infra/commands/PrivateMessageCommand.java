package fr.nimelia.infra.commands;

import com.velocitypowered.api.proxy.Player;
import fr.nimelia.api.CommonAPI;
import fr.nimelia.api.account.AccountManager;
import fr.nimelia.infra.NInfra;
import lombok.Getter;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import net.kyori.adventure.text.Component;

import java.util.*;

@CommandInfo(name = "msg", description = "Permet de parler en privé à un joueur", aliases = {"tell", "m", "w", "whisper", "t"})
public class PrivateMessageCommand extends VelocityPluginCommand {

    @Getter
    public static Map<UUID, UUID> lastMessage = new HashMap<>();

    public PrivateMessageCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        List<String> tabulation = new ArrayList<>();
        if (invocation.arguments().length == 1) {
            for (Player proxiedPlayer : NInfra.getInfra().getServer().getAllPlayers()) {
                tabulation.add(proxiedPlayer.getUsername());
            }
        }
        return tabulation;
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player player)) return;
        CommonAPI api = NInfra.getInfra().getCommon();
        AccountManager account = api.getAccountManager();

        if (args.length < 2) {
            player.sendMessage(Component.text(api.getPrefix() + "§cUsage: /msg <player> <message>"));
            return;
        }

        Optional<Player> targetOptional = NInfra.getInfra().getServer().getPlayer(args[0]);
        if (targetOptional.isEmpty() || targetOptional.get().getUsername().equalsIgnoreCase(player.getUsername())) {
            player.sendMessage(Component.text(api.getPrefix() + "§cLe joueur n'est pas connecté !"));
            return;
        }
        Player target = targetOptional.get();

        StringBuilder stringBuilder = new StringBuilder();
        args[0] = args[0].replace(target.getUsername(), "");
        for (String l : args) {
            stringBuilder.append(l + " ");
        }
        lastMessage.put(player.getUniqueId(), target.getUniqueId());
        lastMessage.put(target.getUniqueId(), player.getUniqueId());
        player.sendMessage(Component.text("§dMe §7⇆ " + account.getAccount(target.getUniqueId()).getRankTypes().getPrefixChat() + target.getUsername() + " §8:§7 " + String.join(" ", stringBuilder)));
        target.sendMessage(Component.text(account.getAccount(player.getUniqueId()).getRankTypes().getPrefixChat() + player.getUsername() + " §7⇆ §dMe §8:§7" + String.join(" ", stringBuilder)));
    }
}
