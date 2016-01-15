package fr.AleksGirardey;

import com.google.inject.Inject;
import fr.AleksGirardey.Commands.AcceptCommand;
import fr.AleksGirardey.Commands.City.*;
import fr.AleksGirardey.Commands.City.Set.*;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.CityCommandSetAlly;
import fr.AleksGirardey.Commands.City.Set.CityCommandSetAssistant;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.CityCommandSetEnemy;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.CityCommandSetNeutral;
import fr.AleksGirardey.Commands.RefuseCommand;
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
        CommandSpec cityCommandSpec, accept, refuse;
        CommandSpec info, create, delete, claim, unclaim, set, help, add, remove;
        CommandSpec setHelp, setSpawn, setAlly, setNeutral, setEnemy, setMayor, setAssistant;

        logger.info("Please, wait for the War Of Squirrels plugin to be initialized");
        if (!f.exists())
            if (!f.mkdir())
                logger.error("Can't create plugin directory");

            Core.initCore(logger, game, this);
            game.getEventManager().registerListeners(this, new OnPlayerLogin());
            game.getEventManager().registerListeners(this, new OnPlayerMove());
            game.getEventManager().registerListeners(this, new OnPlayerRespawn());
            game.getEventManager().registerListeners(this, new OnPlayerBuild());
            game.getEventManager().registerListeners(this, new OnPlayerContainer());
            game.getEventManager().registerListeners(this, new OnPlayerSwitch());
            game.getEventManager().registerListeners(this, new OnPlayerDestroy());

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

            add = CommandSpec.builder()
                    .description(Text.of("Invite a player to join your city"))
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[player]"))),
                            GenericArguments.optional(
                                    GenericArguments.repeated(
                                            GenericArguments.onlyOne(GenericArguments.string(Text.of("<player>"))),
                                            10)))
                    .executor(new CityAddCommand())
                    .build();

            remove = CommandSpec.builder()
                    .description(Text.of("Invite a player to join your city"))
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[player]"))),
                            GenericArguments.optional(
                                    GenericArguments.repeated(
                                            GenericArguments.onlyOne(GenericArguments.string(Text.of("<player>"))),
                                            10)))
                    .executor(new CityRemoveCommand())
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

            setMayor = CommandSpec.builder()
                    .description(Text.of("Set this citizen as mayor"))
                    .executor(new CityCommandSetMayor())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[resident]")))
                    )
                    .build();

            setAssistant = CommandSpec.builder()
                .description(Text.of("Set this citizen as assistant"))
                .executor(new CityCommandSetAssistant())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("[resident]")))
                )
                .build();

            set = CommandSpec.builder()
                    .description(Text.of("Commands related to new attribution in your city"))
                    .child(setHelp, "help", "?")
                    .child(setSpawn, "spawn")
                    .child(setAlly, "ally")
                    .child(setEnemy, "enemy")
                    .child(setNeutral, "neutral")
                    .child(setMayor, "mayor")
                    .child(setAssistant, "assistant")
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
                    .child(add, "add", "invite")
                    .child(remove, "remove", "kick")
                    .build();

            accept = CommandSpec.builder()
                    .description(Text.of("Accept a pending invitation."))
                    .executor(new AcceptCommand())
                    .build();

            refuse = CommandSpec.builder()
                    .description(Text.of("Refuse a pending invitation."))
                    .executor(new RefuseCommand())
                    .build();

            game.getCommandManager().register(this, cityCommandSpec, "city", "c");
            game.getCommandManager().register(this, accept, "accept", "a");
            game.getCommandManager().register(this, refuse, "refuse", "r");
            logger.info("Welcome in the War Of Squirrels. Have fun !");
    }

    public Logger getLogger() { return logger; }
}
