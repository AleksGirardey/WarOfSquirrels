package fr.craftandconquest.warofsquirrels.utils;

import fr.craftandconquest.warofsquirrels.object.scoring.Score;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ScoreComparable implements Comparable<ScoreComparable> {
    private String name;
    private Score score;

    @Override
    public String toString() {
        return name + " [" + score + "]";
    }

    @Override
    public int compareTo(@NotNull ScoreComparable o) {
        int delta = score.getAllScore() - o.score.getAllScore();
        if (delta > 0)
            return 1;
        else if (delta < 0)
            return -1;

        return name.compareTo(o.name);
    }
}
