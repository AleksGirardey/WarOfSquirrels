package fr.AleksGirardey.Handlers;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import fr.AleksGirardey.Objects.DBObject.*;
import fr.AleksGirardey.Objects.Cuboide.CuboVector;
import fr.AleksGirardey.Objects.Database.GlobalCubo;
import fr.AleksGirardey.Objects.Database.GlobalCuboAssociation;
import fr.AleksGirardey.Objects.Utilitaires.Pair;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CuboHandler {

    private Logger                                      logger;
    private Map<DBPlayer, Pair<Vector3i, Vector3i>>     points = new HashMap<>();
    private Map<Integer, Cubo>                          cubos = new HashMap<>();

    private Map<Cubo, List<CuboAssociation>>            associations = new HashMap<>();

    @Inject
    public              CuboHandler(Logger logger) {
        this.logger = logger;
    }

    public void        populate() {
        String          sql = "SELECT * FROM `" + GlobalCubo.tableName + "`";
        String          sql2 = "SELECT * FROM `" + GlobalCuboAssociation.tableName + "`";
        CuboAssociation association;
        Cubo            cubo;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                cubo = new Cubo(statement.getResult());
                cubos.put(cubo.getId(), cubo);
            }
            statement.Close();
            statement = new Statement(sql2);
            statement.Execute();
            while (statement.getResult().next()) {
                association = new CuboAssociation(statement.getResult());
                if (!associations.containsKey(association.getCubo()))
                    associations.put(association.getCubo(), new ArrayList<>());
                associations.get(association.getCubo()).add(association);
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Logger      getLogger() { return logger; }

    public void         add(DBPlayer player, String name) {
        if (!points.containsKey(player)) {
            player.getUser().getPlayer().get()
                    .sendMessage(Text.of("You need to active the cubo mode first"));
            return;
        }
        Pair<Vector3i, Vector3i>    value = points.get(player);
        CuboVector                  vector = new CuboVector(value.getL(), value.getR());
        Cubo                        cubo, parent = getParent(vector);

        cubo = new Cubo(name, parent, player,
                new Permission(true, true, true), new Permission(false, false, false),
                parent == null ? 0 : parent.getPriority() + 1, vector);

        cubos.put(cubo.getId(), cubo);

        logger.info("[Cubo] "
                    + player.getDisplayName()
                    + " has created a cubo at ["
                    + vector.getOne().getX() + ";" + vector.getOne().getY() + ";" + vector.getOne().getZ()
                    + "] to ["
                    + vector.getEight().getX() + ";" + vector.getEight().getY() + ";" + vector.getEight().getZ()
                    + "]");
    }

    public Cubo                 get(int id) {
        return cubos.get(id);
    }

    public List<Cubo>           getFromCity(City city) {
        List<Cubo>              resultat = new ArrayList<Cubo>();

        for (Cubo c : cubos.values())
            if (c.getOwner().getCity() == city)
                resultat.add(c);

        return resultat;
    }

    public Cubo         get(Vector3i block) {
        Cubo            last = null;

        for (Cubo c : cubos.values())
            if (c.contains(block))
                if (last == null || last.getPriority() < c.getPriority())
                    last = c;
        return  last;
    }

    public Cubo         getParent(CuboVector vector) {
        Cubo            last = null;

        for (Cubo c : cubos.values())
            if (c.contains(vector.getOne()) && c.contains(vector.getEight()))
                if (last == null || last.getPriority() < c.getPriority())
                    last = c;

        return last;
    }

    public void             activateCuboMode(DBPlayer player) {
        points.computeIfAbsent(player, CuboHandler::newCubo);
    }

    public Pair<Vector3i, Vector3i>                 getPoints(DBPlayer player) {
        return points.get(player);
    }

    private static Pair<Vector3i, Vector3i>         newCubo(DBPlayer player) {
        player.getUser().getPlayer().get().sendMessage(Text.of("-=== CuboVector mode [ON] ===-"));
        return (new Pair<>(null, null));
    }

    public void             deactivateCuboMode(DBPlayer player) {
        if (points.get(player) != null) {
            points.remove(player);
            player.sendMessage(Text.of("-=== CuboVector mode [OFF] ===-"));
        }
    }

    public boolean      playerExists(DBPlayer player) {
        return points.containsKey(player);
    }

    public void         set(DBPlayer p, Vector3i block, boolean aorb) {
        if (aorb)
            points.get(p).setL(block);
        else
            points.get(p).setR(block);
    }

    public void         deleteCity(City city) {
        List<Integer>      remove = new ArrayList<>();
        cubos.values().stream().filter(cubo -> cubo.getCity() == city).forEach(cubo -> {
            remove.add(cubo.getId());
            associations.get(cubo).forEach(DBObject::delete);
            associations.remove(cubo);
            cubo.delete();
        });
        remove.forEach(id -> cubos.remove(id));
    }

    public void         updateDependencies() { cubos.values().forEach(Cubo::updateDependencies); }
}