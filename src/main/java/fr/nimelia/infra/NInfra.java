package fr.nimelia.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import fr.nimelia.api.CommonAPI;
import fr.nimelia.api.commands.VelocityRegister;
import fr.nimelia.infra.listeners.PlayerListener;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.slf4j.Logger;

import java.io.IOException;

@Plugin(
        id = "ninfra",
        name = "NInfra",
        version = "1.0"
)

@Getter
public class NInfra {

    private final Logger logger;
    private final ProxyServer server;
    @Getter
    public static NInfra infra;
    public CommonAPI common;
    private VelocityRegister velocityRegister;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public boolean maintenance;

    @Inject
    public NInfra(Logger logger, ProxyServer server) {
        this.logger = logger;
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        infra = this;
        this.common = new CommonAPI();
        logger.info("NInfra has been loaded!");

        getCollection().find().forEach(document -> {
            maintenance = document.getBoolean("status");
        });

        server.getEventManager().register(this, new PlayerListener());
        velocityRegister = new VelocityRegister(this.server, this.logger);
        try {
            velocityRegister.registerCommands(this.getClass().getPackageName() + ".commands");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        getCollection().updateOne(new Document("status", maintenance), new Document("$set", new Document("status", maintenance)));
        common.getAccountManager().saveAccounts();
        common.getPunishmentManager().savePunishments();
        common.getReportManager().saveReports();
        common.getMongo().getClient().close();
        logger.info("MongoDB has been close!");
        common.getRedis().close();
        logger.info("NInfra has been unloaded!");
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();
        ServerPing.Version protocol = ping.getVersion();
        int playerCount = this.getServer().getPlayerCount();
        int max = 100;
        if (this.isMaintenance()) {
            protocol = new ServerPing.Version(0, "§cMaintenance...");
        } else if (max <= playerCount) {
            protocol = new ServerPing.Version(0, "§cPlein §8- §7" + playerCount + "§8/§7" + max);
        }

        String newLine = System.getProperty("line.separator");
        Component motd = Component.text("§d@Nimelia §f» §c§lBeta §r(§fsoon§7) §8- §7[§d1.8§7-§d1.12§7]" + newLine + "§7➥  §fnimelia.fr §8- §bdiscord.nimelia.fr");
        event.setPing(new ServerPing(protocol , event.getPing().getPlayers().get(), motd, event.getPing().getFavicon().get()));
    }

    public MongoCollection<Document> getCollection() {
        return common.getMongo().getCollection("maintenance");
    }
}
