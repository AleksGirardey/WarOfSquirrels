package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.War;

import java.util.ArrayList;
import java.util.List;

public class WarHandler {

    private List<War> wars;

    public WarHandler() {
        wars = new ArrayList<>();
    }

    public void     delete(War war) {
        wars.remove(war);
    }
}
