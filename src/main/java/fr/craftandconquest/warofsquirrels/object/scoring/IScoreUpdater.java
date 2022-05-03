package fr.craftandconquest.warofsquirrels.object.scoring;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IScoreUpdater {
    @JsonIgnore Score getScore();
    void updateScore();
}