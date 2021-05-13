package fr.craftandconquest.warofsquirrels.handler.broadcast;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IChannelTarget {
    @JsonIgnore BroadCastTarget getBroadCastTarget();
}
