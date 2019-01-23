package fr.craftandconquest.warofsquirrels.objects.dbobject;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalInfluence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Influence extends DBObject {
    private static final String     FIELDS = "`" + GlobalInfluence.FACTION
            + "`, `" + GlobalInfluence.TERRITORY
            + "`, `" + GlobalInfluence.INFLUENCE + "`";

    private Faction     faction;
    private Territory   territory;
    private int         influence;

    public Influence(Faction faction, Territory territory, int influence) {
        super(GlobalInfluence.ID, GlobalInfluence.TABLENAME, FIELDS);
        this.faction = faction;
        this.territory = territory;
        this.influence = influence;
    }

    public Influence(Faction faction, Territory territory) {
        this(faction, territory, 0);
        this._primaryKeyValue = this.add("'" + faction.getId()
                + "', '" + territory.getId() + "', '0'");
    }

    public Influence(ResultSet resultSet) throws SQLException {
        this(Core.getFactionHandler().get(resultSet.getInt(GlobalInfluence.FACTION)),
                Core.getTerritoryHandler().get(resultSet.getInt(GlobalInfluence.TERRITORY)),
                resultSet.getInt(GlobalInfluence.INFLUENCE));
        this._primaryKeyValue = resultSet.getString(GlobalInfluence.ID);
    }

    public void addInfluence(int influence) {
        this.influence += influence;
        this.edit(GlobalInfluence.INFLUENCE,"" + this.influence);
    }

    public void subInfluence(int influence) {
        this.influence -= influence;
        this.edit(GlobalInfluence.INFLUENCE,"" + this.influence);
    }

    @Override
    protected void writeLog() {
        Core.getLogger().info("[Influence] {} influence le territoire {} à hauteur de {}",
                faction.getDisplayName(), territory.getName(), influence);
    }

    public int          getId() { return Integer.parseInt(_primaryKeyValue); }
    public Faction      getFaction() { return faction; }
    public Territory    getTerritory() { return territory; }
    public int          getInfluence() { return influence; }

    public void setFaction(Faction faction) {
        this.faction = faction;
        this.edit(GlobalInfluence.FACTION, "'" + faction.getId() + "'");
    }
}