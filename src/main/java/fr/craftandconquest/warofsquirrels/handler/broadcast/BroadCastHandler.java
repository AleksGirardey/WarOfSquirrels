package fr.craftandconquest.warofsquirrels.handler.broadcast;

import fr.craftandconquest.warofsquirrels.object.city.City;

public class BroadCastHandler {

    public void BroadCastMessage(BroadCastTarget target, Object ... objects) {
        switch (target) {
            case CITY:
                BroadCastToCity((City) objects[0], (String) objects[1]);
            default:
                // Nothing to do
        }
    }

    private void BroadCastToCity(City city, String message) {

    }
}
