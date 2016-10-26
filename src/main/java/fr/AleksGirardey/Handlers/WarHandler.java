package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.War.PartyWar;
import fr.AleksGirardey.Objects.War.War;
=======
import fr.AleksGirardey.Objects.PartyWar;
import fr.AleksGirardey.Objects.War;
import fr.AleksGirardey.Objects.War.WarState;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class WarHandler {

    private List<War> wars;

    public WarHandler() {
        wars = new ArrayList<>();
    }

    public boolean  createWar(int attacker, int defender, PartyWar party) {
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
                party.leader.sendMessage(Text.of("Defenders are not enough"));
        } else
            party.Send("Your city is not enemy with " + Core.getCityHandler().<String>getElement(defender, "city_displayName"));
        return false;
    }

    public War      getWar(Player player) {
        for (War war : wars)
            if (war.contains(player))
                return war;
        return null;
    }

    public War      getWar(int  cityId) {
        for (War war : wars)
            if (war.contains(cityId))
                return war;
        return null;
    }

    public boolean      Contains(Player player) {
        for (War war : wars)
            if (war.contains(player))
                return true;
        return  false;
    }

    public boolean      Contains(int    cityId) {
        for (War war : wars)
            if (war.contains(cityId))
                return true;
        return false;
    }

<<<<<<< HEAD
    public boolean      ContainsDefender(int cityId) {
        for (War war : wars)
            if (war.getDefender() == cityId)
                return true;
        return false;
    }

    public boolean      ableTo(Player player, int chunkId) {
        int cityId = Core.getChunkHandler().getCity(chunkId);

        return  getWar(player).getPhase().equals("War")
                && Contains(player)
=======
    public boolean      ableTo(Player player, int chunkId) {
        int cityId = Core.getChunkHandler().getCity(chunkId);

        return  /*getWar(player).getPhase().equals(WarState.War)
                &&*/ Contains(player)
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
                && getWar(player).getDefender() == cityId;
    }

    public List<String>     getCitiesList() {
        List<String>        list = new ArrayList<>();

        for (War war : wars) {
            list.add(war.getAttackerName());
            list.add(war.getDefenderName());
        }
        return list;
    }

    public void     delete(War war) {
        wars.remove(war);
    }

    public void     displayList(Player player) {
        player.sendMessage(Text.of("---=== War list [" + wars.size() + "] ===---"));

        for (War war : wars)
            player.sendMessage(Text.of(war.getAttackerName() + " [" + war.getAttackerPoints() + "] vs. "
                    + war.getDefenderName() + " [" + war.getDefenderPoints() + "]"));
    }

    public void     AddPoints(Player killer, Player victim) {
        War         war = getWar(killer);

        if (war.isDefender(killer) && war.isAttacker(victim))
            war.addDefenderPoints();
        else if (war.isAttacker(killer) && war.isDefender(victim))
            war.addAttackerPoints();
    }
}
