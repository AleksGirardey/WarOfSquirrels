package fr.craftandconquest.warofsquirrels.handler.guild;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.UpdatableHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.IUpdate;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.guild.Guild;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class GuildHandler extends UpdatableHandler<Guild> implements IUpdate {
    private Map<Faction, List<Guild>> guildByFaction;
    public GuildHandler(Logger logger) {
        super("[WoS][GuildHandler]", logger);
    }

    @Override
    protected void InitVariables() {
        guildByFaction = new HashMap<>();
    }

    @Override
    protected boolean add(Guild value) {
        super.add(value);

        Faction related = value.getCityHeadQuarter().getFaction();

        if (!guildByFaction.containsKey(related))
            guildByFaction.put(related, new ArrayList<>());
        guildByFaction.get(related).add(value);

        return false;
    }

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, Guild.class);
    }

    @Override
    public boolean Delete(Guild value) {
        WarOfSquirrels.instance.spreadPermissionDelete(value);

        if (!WarOfSquirrels.instance.getCuboHandler().deleteGuild(value)) return false;
        if (!WarOfSquirrels.instance.getChunkHandler().deleteEstablishment(value)) return false;

        for (FullPlayer player : value.getMembers()) {
            player.setGuild(null);
            player.setAssistantGuild(false);
        }

        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Guilds generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    protected String getDirName() {
        return super.getDirName() + "/Faction/Guild";
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        for (Guild guild : dataArray)
            guild.getCustomPermission().remove(target);
    }

    public List<Guild> getAll(Faction faction) { return guildByFaction.get(faction); }

    public Collection<String> getAllAsCollection() {
        List<String> guilds = new ArrayList<>();

        for (Guild guild : dataArray)
            guilds.add(guild.getDisplayName());

        return guilds;
    }
}
