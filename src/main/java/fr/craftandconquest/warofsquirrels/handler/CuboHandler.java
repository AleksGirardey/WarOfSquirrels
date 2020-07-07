package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CuboHandler extends Handler<Cubo> {
    private Logger logger;
    private final Map<Player, Pair<Vector3, Vector3>> points = new HashMap<>();

    public CuboHandler(Logger logger) {
        super("[WoS][CuboHandler]", logger);
    }

    @Override
    protected boolean add(Cubo value) {
        return false;
    }

    @Override
    public boolean Delete(Cubo value) {
        return false;
    }

    @Override
    public void Log() {

    }

    @Override
    public String getConfigDir() {
        return null;
    }

    @Override
    protected String getConfigPath() {
        return null;
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {

    }
}
