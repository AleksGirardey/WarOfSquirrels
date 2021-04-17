package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateHandler {
    private Timer currentDailyUpdateTimer;
    private Timer currentBiomeUpdateTimer;
    private final Logger LOGGER;

    private final Pair<Integer, Integer> plusPlus = new Pair<>(0, 0);
    private final Pair<Integer, Integer> plusMinus = new Pair<>(0, -1);
    private final Pair<Integer, Integer> minusPlus = new Pair<>(-1, 0);
    private final Pair<Integer, Integer> minusMinus = new Pair<>(-1, -1);

    public UpdateHandler(Logger logger) {
        LOGGER = logger;
        this.CreateDailyUpdate();
        if (!WarOfSquirrels.instance.config.getConfiguration().isTerritoriesGenerated())
            this.CreateBiomeUpdate();
    }

    public void DailyUpdate() {
        StringTextComponent message = new StringTextComponent("A new day begin..");

        message.applyTextStyle(TextFormatting.GOLD);
        message.applyTextStyle(TextFormatting.BOLD);
        message.applyTextStyle(TextFormatting.ITALIC);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
        WarOfSquirrels.instance.getTerritoryHandler().update();
//        WarOfSquirrels.instance.getLoanHandler().update();
        this.CreateDailyUpdate();
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

    public void BiomeUpdate(Pair<Integer, Integer> chunkPosition) {
        Pair<Integer, Integer> territoryPosition = Utils.ChunkToTerritoryCoordinates(chunkPosition.getKey(), chunkPosition.getValue());

        World world = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD);
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(territoryPosition.getKey(), territoryPosition.getValue(), world.dimension.getType().getId());

        BiomeContainer container = world.getChunk(chunkPosition.getKey(), chunkPosition.getValue()).func_225549_i_();

        if (container == null || territory == null) return;

        for (int biomeId : container.func_227055_a_()) {
            territory.getBiomeMap().compute(biomeId, (k, v) -> (v == null) ? 1 : v + 1);
        }
    }

    public void CreateBiomeUpdate() {
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, ZoneId.systemDefault());
        ZonedDateTime zonedNext = zonedNow.plusNanos(10);
        long delay = Duration.between(zonedNow, zonedNext).getNano();

        currentBiomeUpdateTimer = new Timer();
        currentBiomeUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                BiomeUpdate(plusPlus);
                BiomeUpdate(plusMinus);
                BiomeUpdate(minusPlus);
                BiomeUpdate(minusMinus);
                LOGGER.info("[UpdateHandler][BiomeUpdate] "
                        + "{" + plusPlus.getKey() + ";" + plusPlus.getValue() + "} "
                        + "{" + plusMinus.getKey() + ";" + plusMinus.getValue() + "} "
                        + "{" + minusPlus.getKey() + ";" + minusPlus.getValue() + "} "
                        + "{" + minusMinus.getKey() + ";" + minusMinus.getValue() + "} ");
                WarOfSquirrels.instance.getTerritoryHandler().Save();

                int halfMapSize = (WarOfSquirrels.instance.config.getConfiguration().getMapSize() / 2) / 16;
                int key;
                int value;
                int offset = 1;

                key = plusPlus.getKey();
                value = plusPlus.getValue();

                if (key + offset >= halfMapSize) {
                    plusPlus.setKey(0);
                    if (value + offset >= halfMapSize)
                        return;
                    plusPlus.setValue(value + offset);
                } else
                    plusPlus.setKey(key + offset);

                key = plusMinus.getKey();
                value = plusMinus.getValue();

                if (key + offset >= halfMapSize) {
                    plusMinus.setKey(0);
                    plusMinus.setValue(value - offset);
                } else
                    plusMinus.setKey(key + offset);

                key = minusPlus.getKey();
                value = minusPlus.getValue();

                if (key - offset < -halfMapSize) {
                    minusPlus.setKey(-1);
                    minusPlus.setValue(value + offset);
                } else
                    minusPlus.setKey(key - offset);

                key = minusMinus.getKey();
                value = minusMinus.getValue();

                if (key - offset < -halfMapSize) {
                    minusMinus.setKey(-1);
                    minusMinus.setValue(value - offset);
                } else
                    minusMinus.setKey(key - offset);
                CreateBiomeUpdate();
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
        currentDailyUpdateTimer.cancel();
        currentBiomeUpdateTimer.cancel();
    }
}
