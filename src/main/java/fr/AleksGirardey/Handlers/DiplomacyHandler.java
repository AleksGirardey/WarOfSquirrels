package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;

import java.util.Map;
import java.util.logging.Logger;

public class DiplomacyHandler {
    private Logger  logger;

    private Map<Integer, Diplomacy>

    @Inject
    public DiplomacyHandler(Logger logger) { this.logger = logger; }


}