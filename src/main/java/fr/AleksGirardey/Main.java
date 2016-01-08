package fr.AleksGirardey;

import com.google.inject.Inject;
import fr.AleksGirardey.Commands.CityCommand.*;
import fr.AleksGirardey.Commands.CityCommand.Set.*;
import fr.AleksGirardey.Commands.CityCommand.Set.Diplomacy.CityCommandSetAlly;
import fr.AleksGirardey.Commands.CityCommand.Set.Diplomacy.CityCommandSetEnemy;
import fr.AleksGirardey.Commands.CityCommand.Set.Diplomacy.CityCommandSetNeutral;
import fr.AleksGirardey.Listeners.*;
import fr.AleksGirardey.Objects.Core;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;

import java.io.File;

@Plugin(id = "WOS", name = "War Of Squirrels", version = "1.0")
public class Main {

    @Inject
    private Logger logger;

    @Inject
    private Game    game;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        File        f = new File ("WarOfSquirrels");
        CommandSpec cityCommandSpec;
        CommandSpec info, create, delete, claim, unclaim, set, help;
        CommandSpec setHelp, setSpawn, setAlly, setNeutral, setEnemy;

        logger.debug("Please, wait for the War Of Squirrels plugin to be initialized");
        if (!f.exists())
            if (!f.mkdir())
                logger.error("Can't create plugin directory");

            Core.initCore(logger, game);
            logger.info("Registering events..");
            game.getEventManager().registerListeners(this, new OnPlayerLogin());
            game.getEventManager().registerListeners(this, new OnPlayerMove());
            game.getEventManager().registerListeners(this, new OnPlayerRespawn());
            game.getEventManager().registerListeners(this, new OnPlayerBuild());
            game.getEventManager().registerListeners(this, new OnPlayerContainer());
            game.getEventManager().registerListeners(this, new OnPlayerSwitch());
            game.getEventManager().registerListeners(this, new OnPlayerDestroy());
            logger.info("Event registered.\n" +
                    "Command builder...");

            info = CommandSpec.builder()
                    .description(Text.of("Give city information"))
                    .executor(new CityCommandInfo())
                    .arguments(
                            GenericArguments.optional(
                                    GenericArguments.onlyOne(
                                            GenericArguments.string(Text.of("[city]"))))
                    )
                    .build();

            create = CommandSpec.builder()
                    .description(Text.of("Create city"))
                    .executor(new CityCommandCreate())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("City name")))
                    )
                    .build();

            delete = CommandSpec.builder()
                    .description(Text.of("Delete city"))
                    .executor(new CityCommandDelete())
                    .build();

            claim = CommandSpec.builder()
                    .description(Text.of("Claim chunk for your city"))
                    .executor(new CityCommandClaim())
                    .build();

            unclaim = CommandSpec.builder()
                    .description(Text.of("Make this chunk belongs to mother nature"))
                    .executor(new CityCommandUnclaim())
                    .build();

            setHelp = CommandSpec.builder()
                    .description(Text.of("Display /city set help"))
                    .executor(new CityCommandSetHelp())
                    .build();

            setSpawn = CommandSpec.builder()
                    .description(Text.of("Set a new spawn for the city"))
                    .executor(new CityCommandSetSpawn())
                    .build();

            setAlly = CommandSpec.builder()
                    .description(Text.of("Set a city as ally"))
                    .executor(new CityCommandSetAlly())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[city]"))),
                            GenericArguments.optional(
                                    GenericArguments.repeated(
                                            GenericArguments.onlyOne(GenericArguments.string(Text.of("<city>"))),
                                            10))
                    )
                    .build();

            setEnemy = CommandSpec.builder()
                    .description(Text.of("Set a city as enemy"))
                    .executor(new CityCommandSetEnemy())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[city]"))),
                            GenericArguments.optional(
                                    GenericArguments.repeated(
                                            GenericArguments.onlyOne(GenericArguments.string(Text.of("<city>"))),
                                            10))
                    )
                    .build();

            setNeutral = CommandSpec.builder()
                    .description(Text.of("Set a city as neutral"))
                    .executor(new CityCommandSetNeutral())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[city]"))),
                            GenericArguments.optional(
                                    GenericArguments.repeated(
                                            GenericArguments.onlyOne(GenericArguments.string(Text.of("<city>"))),
                                            10))
                    )
                    .build();

            set = CommandSpec.builder()
                    .description(Text.of("Commands related to new attribution in your city"))
                    .child(setHelp, "help", "?")
                    .child(setSpawn, "spawn")
                    .child(setAlly, "ally")
                    .child(setEnemy, "enemy")
                    .child(setNeutral, "neutral")
                    .build();

            help = CommandSpec.builder()
                    .description(Text.of("City commands help"))
                    .executor(new CityCommandHelp())
                    .build();

            cityCommandSpec = CommandSpec.builder()
                    .description(Text.of("Commands related to your city"))
                    .child(help, "help", "?")
                    .child(info, "info", "i")
                    .child(create, "create", "new")
                    .child(delete, "delete")
                    .child(claim, "claim")
                    .child(unclaim, "unclaim")
                    .child(set, "set")
                    .build();


            logger.info("Command build.\n" +
                    "Register Command...");
            game.getCommandManager().register(this, cityCommandSpec, "city", "c");
            logger.info("Command registered.\n" +
                    "Welcome in the War Of Squirrels. Have fun !");
    }

    public Logger getLogger() { return logger; }
}
