package fr.nimelia.infra.commands;

import com.velocitypowered.api.proxy.Player;
import fr.nimelia.api.CommonAPI;
import fr.nimelia.api.account.AccountManager;
import fr.nimelia.api.commands.CommandInfo;
import fr.nimelia.api.commands.VelocityPluginCommand;
import fr.nimelia.infra.NInfra;
import net.kyori.adventure.text.Component;

import java.util.Optional;

@CommandInfo(name = "r", description = "Permet de répondre à un joueur", aliases = {"reply"})
public class ReplyCommand extends VelocityPluginCommand {
    public ReplyCommand() {
        super(NInfra.getInfra().getServer());
    }

    @Override
    public void onCommand(Invocation invocation, String[] args) {
        if (!(invocation.source() instanceof Player player)) return;
        CommonAPI api = NInfra.getInfra().getCommon();
        AccountManager account = api.getAccountManager();

        if (args.length < 1) {
            player.sendMessage(Component.text(api.getPrefix() + "§cUsage: /r <message>"));
            return;
        }


        if (!PrivateMessageCommand.lastMessage.containsKey(player.getUniqueId())) {
            player.sendMessage(Component.text(api.getPrefix() + "§cVous n'avez pas encore envoyé de message !"));
            return;
        }
        Optional<Player> targetOptional = Optional.of(NInfra.getInfra().getServer().getPlayer(PrivateMessageCommand.lastMessage.get(player.getUniqueId())).get());
        if (targetOptional.isEmpty()) return;
        Player target = targetOptional.get();
        player.sendMessage(Component.text("§dMe §7⇆ " + account.getAccount(target.getUniqueId()).getRankTypes().getPrefixChat() + target.getUsername() + " §8:§7 " + String.join(" ", args)));
        target.sendMessage(Component.text(account.getAccount(player.getUniqueId()).getRankTypes().getPrefixChat() + player.getUsername() + " §7⇆ §dMe §8: " + String.join(" ", args)));
    }
}
