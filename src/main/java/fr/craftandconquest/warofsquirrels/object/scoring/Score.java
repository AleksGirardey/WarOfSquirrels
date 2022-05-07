package fr.craftandconquest.warofsquirrels.object.scoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Score {
    @JsonProperty @Getter @Setter private int todayScore = 0;
    @JsonProperty @Getter @Setter private int globalScore = 0;
    @JsonProperty @Getter @Setter private int scoreLifePoints = 0;

    public void AddScore(int points) {
        todayScore += points;
    }

    public void AddScoreLifePoints(int lifePoints) {
        if (scoreLifePoints + lifePoints >= 800) {
            AddScore(scoreLifePoints + lifePoints - 800);
            scoreLifePoints = 800;
        } else {
            scoreLifePoints += lifePoints;
        }
    }

    public void RemoveScoreLifePoints(int lifePoints) {
        scoreLifePoints = Math.max(0, scoreLifePoints - lifePoints);
    }

    public void UpdateScore() {
        globalScore += todayScore;
        todayScore = 0;
    }

    public void ResetScore() {
        todayScore = 0;
        globalScore = 0;
    }

    public void ResetScoreLifePoints() {
        scoreLifePoints = 0;
    }
}
