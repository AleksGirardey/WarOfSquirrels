package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateHandler {
    private Timer currentUpdateTimer;
    private final Logger LOGGER;

    public UpdateHandler(Logger logger) {
        LOGGER = logger;
        this.Create();
    }

    public void Update() {
        StringTextComponent message = new StringTextComponent("A new day begin..");

        message.applyTextStyle(TextFormatting.GOLD);
        message.applyTextStyle(TextFormatting.BOLD);
        message.applyTextStyle(TextFormatting.ITALIC);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        WarOfSquirrels.instance.getTerritoryHandler().update();
//        WarOfSquirrels.instance.getLoanHandler().update();
        this.Create();
    }

    public void Create() {
        long delay = DelayBeforeMidnight();
        delay *= 1000;

        currentUpdateTimer = new Timer();
        currentUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Update();
            }
        }, delay);
    }

    public long DelayBeforeMidnight() {
        //TODO: Modifier la detection du prochain reset (00:00) pour ne pas déclencher plusieurs fois l'update lors du passage à minuit
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
        ZonedDateTime zonedNext = zonedNow.withHour(0).withMinute(0).withSecond(0);
        if(zonedNow.compareTo(zonedNext) > 0)
            zonedNext = zonedNext.plusDays(1);
        return Duration.between(zonedNow, zonedNext).getSeconds();
    }

    public void CancelTask() {
        currentUpdateTimer.cancel();
    }
}
