package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Guild;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class GuildHandler extends Handler<Guild> {

    private final Map<Faction, List<Guild>> guildByFaction;
    private final Map<UUID, Guild> guildMap;

    protected static String DirName = "/WorldData";
    protected static String JsonName = "/GuildHandler.json";
    public GuildHandler(Logger logger) {
        super("GuildHandler", logger);
        guildByFaction = new HashMap<>();
        guildMap = new HashMap<>();

        if (!Init()) return;
        if (!Load()) return;

        Log();
    }

    @Override
    protected boolean add(Guild value) {
        if (!dataArray.contains(value))
            dataArray.add(value);

        Faction related = value.getCityHeadQuarter().getFaction();

        if (!guildMap.containsKey(value.getUuid()))
            guildMap.put(related.getUuid(), value);
        if (!guildByFaction.containsKey(related))
            guildByFaction.put(related, new ArrayList<>());
        guildByFaction.get(related).add(value);

        return false;
    }

    @Override
    public boolean Delete(Guild value) {
        WarOfSquirrels.instance.spreadPermissionDelete(value);

        if (!WarOfSquirrels.instance.getCuboHandler().deleteGuild(value)) return false;
        if (!WarOfSquirrels.instance.getChunkHandler().deleteGuild(value)) return false;

        for (FullPlayer player : value.getMembers()) {
            player.setGuild(null);
            player.setAssistantGuild(false);
        }

        guildMap.remove(value.getUuid());

        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Guilds generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    public String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName;
    }

    @Override
    protected String getConfigPath() {
        return getConfigDir() + JsonName;
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        for (Guild guild : dataArray)
            guild.getCustomPermission().remove(target);
    }

    public Guild get(String targetName) {
        return dataArray.stream().filter(g -> g.getDisplayName().equals(targetName)).findFirst().orElse(null);
    }

    public Guild get(UUID uuid) {
        return dataArray.stream().filter(g -> g.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}
