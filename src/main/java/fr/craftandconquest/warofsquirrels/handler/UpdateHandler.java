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

//        this.CreateDailyUpdate();
        this.CreateSaveUpdate();
    }

    public void DailyUpdate() {
        MutableComponent message = ChatText.Colored("A new day begin..", ChatFormatting.GOLD);
        message.withStyle(ChatFormatting.BOLD);
        message.withStyle(ChatFormatting.ITALIC);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        WarOfSquirrels.instance.getTerritoryHandler().update();
//        WarOfSquirrels.instance.getLoanHandler().update();
        this.CreateDailyUpdate();
    }

    public void SaveTask() {
        LOGGER.info("[WoS][UpdateHandler] Saving...");
        for (OnSaveListener listener : OnSaveUpdate) {
            listener.Save();
        }
    }

    public void SaveUpdate() {
        SaveTask();

        this.CreateSaveUpdate();
    }

    public void CreateDailyUpdate() {
        long delay = DelayBeforeMidnight();
        delay *= 1000;

        currentDailyUpdateTimer = new Timer();
        currentDailyUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DailyUpdate();
            }
        }, delay);
    }

    public void CreateSaveUpdate() {
        long delay = 60000L;
//        long delay = 300000L;

        currentSaveUpdateTimer = new Timer();
        currentSaveUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SaveUpdate();
            }
        }, delay);
    }

    public long DelayBeforeMidnight() {
        //TODO: Modifier la detection du prochain reset (00:00) pour ne pas déclencher plusieurs fois l'update lors du passage à minuit
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
        ZonedDateTime zonedNext = zonedNow.withHour(9).withMinute(0).withSecond(0);
        if (zonedNow.compareTo(zonedNext) > 0)
            zonedNext = zonedNext.plusDays(1);
        return Duration.between(zonedNow, zonedNext).getSeconds();
    }

    public void CancelTask() {
//        currentDailyUpdateTimer.cancel();
        currentSaveUpdateTimer.cancel();
    }
}