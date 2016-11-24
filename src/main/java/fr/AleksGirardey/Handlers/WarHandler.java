package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.PartyWar;
import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class WarHandler {

    private List<War> wars;

    public WarHandler() {
        wars = new ArrayList<>();
    }

    public boolean  createWar(City attacker, City defender, PartyWar party) {
        if (Core.getCityHandler().areEnemies(attacker, defender)) {
            int     defenders = Core.getCityHandler().getOnlinePlayers(defender).size() + 1;

            if (Contains(attacker) || Contains(defender)) {
                party.Send("You can't attack this city ");
                return false;
            }
            if (/*defenders < 4 ||*/ party.size() < defenders) {
                wars.add(new War(attacker, defender, party.toList()));
                return true;
            }
            else
                party.getLeader().sendMessage(Text.of("Defenders are not enough"));
        } else
            party.Send("Your city is not enemy with " + defender.getDisplayName());
        return false;
    }

    public War      getWar(DBPlayer player) {
        for (War war : wars)
            if (war.contains(player))
                return war;
        return null;
    }

    public War      getWar(City  city) {
        for (War war : wars)
            if (war.contains(city))
                return war;
        return null;
    }

    public boolean      Contains(DBPlayer player) {
        for (War war : wars)
            if (war.contains(player))
                return true;
        return  false;
    }

    public boolean      Contains(City city) {
        for (War war : wars)
            if (war.contains(city))
                return true;
        return false;
    }

    public boolean      ContainsDefender(City city) {
        for (War war : wars)
            if (war.getDefender() == city)
                return true;
        return false;
    }

    public boolean      ableTo(DBPlayer player, Chunk chunk) {
        City            city = chunk.getCity();

        return  getWar(player).getPhase().equals("War")
                && Contains(player)
                && getWar(player).getDefender() == city;
    }

    public List<String>     getCitiesList() {
        List<String>        list = new ArrayList<>();

        for (War war : wars) {
            list.add(war.getAttacker().getDisplayName());
            list.add(war.getDefender().getDisplayName());
        }
        return list;
    }

    public void     delete(War war) {
        wars.remove(war);
    }

    public void     displayList(DBPlayer player) {
        player.sendMessage(Text.of("---=== War list [" + wars.size() + "] ===---"));

        for (War war : wars)
            player.sendMessage(Text.of(war.getAttacker().getDisplayName() + " [" + war.getAttackerPoints() + "] vs. "
                    + war.getDefender().getDisplayName() + " [" + war.getDefenderPoints() + "]"));
    }

    public void     AddPoints(DBPlayer killer, DBPlayer victim) {
        War         war = getWar(killer);

        if (war.isDefender(killer) && war.isAttacker(victim))
            war.addDefenderPoints();
        else if (war.isAttacker(killer) && war.isDefender(victim))
            war.addAttackerPoints();
    }
}
