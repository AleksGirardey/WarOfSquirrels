package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.OnSaveListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class UpdateHandler {
    public static Collection<OnSaveListener> OnSaveUpdate = new HashSet<>();

    private Timer currentDailyUpdateTimer;
    private Timer currentSaveUpdateTimer;
    private final Logger LOGGER;

    public UpdateHandler(Logger logger) {
        LOGGER = logger;

        OnSaveUpdate.clear();

        this.CreateDailyUpdate();
        this.CreateSaveUpdate();
    }

    public void ScoreUpdate() {
        WarOfSquirrels.instance.getBastionHandler().updateScore();
        WarOfSquirrels.instance.getCityHandler().updateScore();
        WarOfSquirrels.instance.getFactionHandler().updateScore();
        WarOfSquirrels.instance.getPlayerHandler().updateScore();
    }

    public void DailyUpdate() {
        WarOfSquirrels.instance.getCityHandler().update();
        WarOfSquirrels.instance.getBastionHandler().update();
        WarOfSquirrels.instance.getTerritoryHandler().update();
        WarOfSquirrels.instance.getFactionHandler().update();
        WarOfSquirrels.instance.getPlayerHandler().update();
//        WarOfSquirrels.instance.getLoanHandler().update();

        ScoreUpdate();
        MutableComponent message = ChatText.Colored("A new day begin..", ChatFormatting.GOLD);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
    }

    public void SaveTask() {
        LOGGER.info("[WoS][UpdateHandler] Saving...");
        for (OnSaveListener listener : OnSaveUpdate) {
            listener.Save();
        }
        WarOfSquirrels.instance.config.Save();
    }

    public void SaveUpdate() {
        SaveTask();
    }

    public void CreateDailyUpdate() {
        long delay = DelayBeforeReset();
//        long period = 1000L * 60L * 60L * 24L;
        long period = 1000L * 60L * 60L * 4L;
        delay *= 1000;

        currentDailyUpdateTimer = new Timer("Daily Update Timer");
        currentDailyUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DailyUpdate();
            }
        }, delay, period);
    }

    public void CreateSaveUpdate() {
        long delay = 0L;
//        long period = 60000L;
        long period = 300000L;

        currentSaveUpdateTimer = new Timer();
        currentSaveUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SaveUpdate();
            }
        }, delay, period);
    }

    public long DelayBeforeReset() {
        //TODO: Modifier la detection du prochain reset (00:00) pour ne pas déclencher plusieurs fois l'update lors du passage à minuit
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
        ZonedDateTime zonedNext = zonedNow.withHour(9).withMinute(0).withSecond(0);
        if (zonedNow.compareTo(zonedNext) > 0)
            zonedNext = zonedNext.plusDays(1);
        return Duration.between(zonedNow, zonedNext).getSeconds();
    }

    public void CancelTask() {
        if (currentDailyUpdateTimer != null) currentDailyUpdateTimer.cancel();
        if (currentSaveUpdateTimer != null) currentSaveUpdateTimer.cancel();
    }
}
