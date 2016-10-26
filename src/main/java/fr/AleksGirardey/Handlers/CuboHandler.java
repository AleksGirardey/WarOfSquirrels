package fr.AleksGirardey.Handlers;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Cuboide.Cubo;
import fr.AleksGirardey.Objects.Cuboide.CuboVector;
import fr.AleksGirardey.Objects.Database.GlobalCubo;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Pair;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CuboHandler {

    private Logger                                      logger;
    private Map<Player, Pair<Vector3i, Vector3i>>       points = new HashMap<>();
    private Map<Integer, Cubo>                          cubos = new HashMap<>();

    @Inject
    public              CuboHandler(Logger logger) {
        this.generate();
        this.logger = logger;
    }

    private void        generate() {
        String          sql = "SELECT * FROM `Cubo`;";
        Statement       _statement = null;
        ResultSet       _rs = null;

        try {
            _statement = new Statement(sql);
            _rs = _statement.Execute();
            while (_rs.next()) {
                cubos.put(_rs.getInt("cubo_id"), new Cubo(
                        _rs.getInt("cubo_id"),
                        _rs.getString("cubo_name"),
                        Core.getCuboHandler().get(_rs.getInt("cubo_parent")),
                        _rs.getString("cubo_owner"),
                        new CuboVector(_rs)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Logger      getLogger() { return logger; }

    public void         add(Player player, String name) {
        if (!points.containsKey(player)) {
            player.sendMessage(Text.of("You need to active the cubo mode first"));
            return;
        }
        Pair<Vector3i, Vector3i>    value = points.get(player);
        CuboVector                  vector = new CuboVector(value.getL(), value.getR());
        String                      sql = "INSERT INTO `Cubo` (`cubo_nom`, `cubo_parent`," +
                " `cubo_permissionInList`, `cubo_permissionOutside`," +
                " `cubo_owner`, `cubo_priority`," +
                " `cubo_AposX`, `cubo_AposY`, `cubo_AposZ`," +
                " `cubo_BposX`, `cubo_BposY`, `cubo_BposZ`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Statement                   _statement;
        int                         priority = 0;
        PermissionHandler           ph = Core.getPermissionHandler();

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setString(1, name);
            Cubo    parent = this.getParent(vector);
            if (parent == null) {
                _statement.getStatement().setObject(2, null);
                priority = 0;
            }
            else {
                _statement.getStatement().setInt(2, parent.getId());
                priority = parent.getPriority() + 1;
            }
            _statement.getStatement().setInt(3, ph.add(true, false, true));
            _statement.getStatement().setInt(4, ph.add(false, false, true));
            _statement.getStatement().setString(5, player.getUniqueId().toString());
            _statement.getStatement().setInt(6, priority);
            _statement.getStatement().setInt(7, vector.getOne().getX());
            _statement.getStatement().setInt(8, vector.getOne().getY());
            _statement.getStatement().setInt(9, vector.getOne().getZ());
            _statement.getStatement().setInt(10, vector.getEight().getX());
            _statement.getStatement().setInt(11, vector.getEight().getY());
            _statement.getStatement().setInt(12, vector.getEight().getZ());
            ResultSet   rs = _statement.Execute();
            cubos.put(rs.getInt(GlobalCubo.id), new Cubo(
                    rs.getInt(GlobalCubo.id),
                    name,
                    parent,
                    player.getUniqueId().toString(),
                    vector));
            _statement.Close();
            rs.close();
            logger.info("[Cubo] "
                    + Core.getPlayerHandler().getElement(player, GlobalPlayer.displayName)
                    + " has created a cubo at ["
                    + vector.getOne().getX() + ";" + vector.getOne().getY() + ";" + vector.getOne().getZ()
                    + "] to ["
                    + vector.getEight().getX() + ";" + vector.getEight().getY() + ";" + vector.getEight().getZ()
                    + "]");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Cubo         get(int id) {
        return          cubos.get(id);
    }

    public List<Cubo>         getFromCity(int cityId) {
        List<Cubo> resultat = new ArrayList<Cubo>();

        for (Map.Entry<Integer, Cubo>   entry : cubos.entrySet())
            if (Core.getPlayerHandler().<Integer>getElement(entry.getValue().getOwner(), GlobalPlayer.cityId) == cityId)
                resultat.add(entry.getValue());

        return resultat;
    }

    public Cubo         get(Vector3i block) {
        Cubo            last = null;

        for (Map.Entry<Integer, Cubo> entry : cubos.entrySet())
            if (entry.getValue().contains(block))
                if (last == null || last.getPriority() < entry.getValue().getPriority())
                    last = entry.getValue();

        return  last;
    }

    public Cubo         getParent(CuboVector vector) {
        Cubo            last = null;

        for (Map.Entry<Integer, Cubo> entry : cubos.entrySet()) {
            Cubo        value = entry.getValue();
            if (value.contains(vector.getOne()) && value.contains(vector.getEight()))
                if (last == null || last.getPriority() < value.getPriority())
                    last = value;
        }
        return last;
    }

    public void             activateCuboMode(Player player) {
        points.computeIfAbsent(player, CuboHandler::newCubo);
    }

    public Pair<Vector3i, Vector3i>                 getPoints(Player player) {
        return points.get(player);
    }

    private static Pair<Vector3i, Vector3i>         newCubo(Player player) {
        player.sendMessage(Text.of("-=== CuboVector mode [ON] ===-"));
        return (new Pair<>(null, null));
    }

    public void             deactivateCuboMode(Player player) {
        if (points.get(player) != null) {
            points.remove(player);
            player.sendMessage(Text.of("-=== CuboVector mode [OFF] ===-"));
        }
    }

    public boolean      playerExists(Player player) {
        return points.containsKey(player);
    }

    public void         set(Player p, Vector3i block, boolean aorb) {
        if (aorb)
            points.get(p).setL(block);
        else
            points.get(p).setR(block);
    }
}