package fr.craftandconquest.warofsquirrels.handlers;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import fr.craftandconquest.warofsquirrels.objects.dbobject.*;
import fr.craftandconquest.warofsquirrels.objects.cuboide.CuboVector;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalCubo;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalCuboAssociation;
import fr.craftandconquest.warofsquirrels.objects.utils.Pair;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.sqlite.core.DB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CuboHandler {

    private Logger                                      logger;
    private Map<DBPlayer, Pair<Vector3i, Vector3i>>     points = new HashMap<>();
    private Map<Integer, Cubo>                          cubos = new HashMap<>();
    private Map<String, Cubo>                           cuboMap = new HashMap<>();

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
                cuboMap.put(cubo.getName(), cubo);
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

    public boolean         add(DBPlayer player, String name) {
        if (!points.containsKey(player)) {
            player.getUser().getPlayer().get()
                    .sendMessage(Text.of("You need to active the cubo mode first"));
            return false;
        }
        Pair<Vector3i, Vector3i>    value = points.get(player);
        CuboVector                  vector = new CuboVector(value.getL(), value.getR());
        Cubo                        cubo, parent = getParent(vector);

        cubo = new Cubo(name, parent, player,
                new Permission(true, true, true), new Permission(false, false, false),
                parent == null ? 0 : parent.getPriority() + 1, vector);

        cubos.put(cubo.getId(), cubo);
        cuboMap.put(cubo.getName(), cubo);

        player.sendMessage(Text.of(TextColors.GREEN, "Création du cubo ", cubo, TextColors.RESET));
        logger.info(String.format("[cubo] %s has created a cubo at %s.", player.getDisplayName(), vector));
        return true;
    }

    public void              remove(String name) {
        Cubo cubo = this.cuboMap.get(name);
        this.cuboMap.remove(name);
        this.cubos.remove(cubo.getId());
        cubo.delete();
    }

    public Cubo                 get(int id) {
        return cubos.get(id);
    }

    public Cubo                 getFromName(String name) { return cuboMap.get(name); }

    public List<Cubo>           getFromCity(City city) {
        List<Cubo>              resultat = new ArrayList<Cubo>();

        for (Cubo c : cubos.values())
            if (c.getOwner().getCity() == city)
                resultat.add(c);

        return resultat;
    }

    public Cubo         get(Vector3i block) {
        Cubo            last = null;

        for (Cubo c : cubos.values()) {
            if (c.contains(block)) {
                if (last == null || last.getPriority() < c.getPriority()) {
                    last = c;
                }
            }
        }
        return  last;
    }

    public Cubo         getParent(CuboVector vector) {
        Cubo            last = null;

        for (Cubo c : cubos.values())
            if (c.contains(vector.getA()) && c.contains(vector.getB()))
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
        Text.Builder message = Text.builder().append(Text.of(TextColors.LIGHT_PURPLE));

        if (aorb) {
            points.get(p).setL(block);
            message.append(Text.of("Block A"));
        }
        else {
            points.get(p).setR(block);
            message.append(Text.of("Block B"));
        }

        message.append(Text.of("défini à la position [" + block.getX() + ";" + block.getY() + ";" + block.getZ() + "]"));
        p.sendMessage(message.build());
    }

    public void         delete(Cubo cubo) {
        cubos.remove(cubo.getId());
        cuboMap.remove(cubo.getName());
        cubo.delete();
    }

    public void         deleteCity(City city) {
        List<Integer>      remove = new ArrayList<>();
        cubos.values().stream().filter(cubo -> cubo.getCity() == city).forEach(cubo -> {
            remove.add(cubo.getId());
            if (associations.get(cubo) != null) {
                associations.get(cubo).forEach(DBObject::delete);
                associations.remove(cubo);
            }
            cubo.delete();
        });
        remove.forEach(id -> cubos.remove(id));
    }

    public List<String>     getStringFromPlayer(DBPlayer player) {
        List<String>        names = new ArrayList<>();
        List<Cubo>          cubos = getFromPlayer(player);

        for (Cubo c : cubos) {
            names.add(c.getName());
        }

        return names;
    }

    public List<Cubo>       getFromPlayer(DBPlayer player) {
        return cubos.values().stream().filter(cubo -> cubo.getOwner() == player || cubo.getLoan().getLoaner() == player).collect(Collectors.toList());
    }

    public void         updateDependencies() { cubos.values().forEach(Cubo::updateDependencies); }
}