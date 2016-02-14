package fr.AleksGirardey;

import com.google.inject.Inject;
import fr.AleksGirardey.Commands.AcceptCommand;
import fr.AleksGirardey.Commands.City.*;
import fr.AleksGirardey.Commands.City.Set.*;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.SetAlly;
import fr.AleksGirardey.Commands.City.Set.SetAssistant;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.SetEnemy;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.SetNeutral;
import fr.AleksGirardey.Commands.City.Set.Permissions.Build.PermBuild;
import fr.AleksGirardey.Commands.City.Set.Permissions.Build.PermBuildAllies;
import fr.AleksGirardey.Commands.City.Set.Permissions.Build.PermBuildOutside;
import fr.AleksGirardey.Commands.City.Set.Permissions.Build.PermBuildResident;
import fr.AleksGirardey.Commands.City.Set.Permissions.Container.PermContainer;
import fr.AleksGirardey.Commands.City.Set.Permissions.Container.PermContainerAllies;
import fr.AleksGirardey.Commands.City.Set.Permissions.Container.PermContainerOutside;
import fr.AleksGirardey.Commands.City.Set.Permissions.Container.PermContainerResident;
import fr.AleksGirardey.Commands.City.Set.Permissions.Switch.PermSwitch;
import fr.AleksGirardey.Commands.City.Set.Permissions.Switch.PermSwitchAllies;
import fr.AleksGirardey.Commands.City.Set.Permissions.Switch.PermSwitchOutside;
import fr.AleksGirardey.Commands.City.Set.Permissions.Switch.PermSwitchResident;
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
        CommandSpec setHelp, setSpawn, setAlly, setNeutral,
                setEnemy, setMayor, setAssistant, setPerm,
                setContainerAll, setBuildAll, setSwitchAll;
        CommandSpec setBuild, setBuildO, setBuildA, setBuildR;
        CommandSpec setContainer, setContainerO, setContainerA, setContainerR;
        CommandSpec setSwitch, setSwitchO, setSwitchA, setSwitchR;

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
                    .executor(new CityCommandAdd())
                    .build();

            remove = CommandSpec.builder()
                    .description(Text.of("Kick a player from your city"))
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[citizen]"))),
                            GenericArguments.optional(
                                    GenericArguments.repeated(
                                            GenericArguments.onlyOne(GenericArguments.string(Text.of("<citizen>"))),
                                            10)))
                    .executor(new CityCommandRemove())
                    .build();

            setHelp = CommandSpec.builder()
                    .description(Text.of("Display /city set help"))
                    .executor(new SetHelp())
                    .build();

            setSpawn = CommandSpec.builder()
                    .description(Text.of("Set a new spawn for the city"))
                    .executor(new SetSpawn())
                    .build();

            setAlly = CommandSpec.builder()
                    .description(Text.of("Set a city as ally"))
                    .executor(new SetAlly())
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
                    .executor(new SetEnemy())
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
                    .executor(new SetNeutral())
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
                    .executor(new SetMayor())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.string(Text.of("[resident]")))
                    )
                    .build();

            setAssistant = CommandSpec.builder()
                .description(Text.of("Set this citizen as assistant"))
                .executor(new SetAssistant())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("[resident]")))
                )
                .build();

            setBuildO = CommandSpec.builder()
                    .description(Text.of("Set outside build permission"))
                    .executor(new PermBuildOutside())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]")))                    )
                    .build();

            setBuildA = CommandSpec.builder()
                .description(Text.of("Set allies build permission"))
                .executor(new PermBuildAllies())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]")))                    )
                .build();

            setBuildR = CommandSpec.builder()
                .description(Text.of("Set resident build permission"))
                .executor(new PermBuildResident())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]")))                    )
                .build();

            setBuild = CommandSpec.builder()
                    .description(Text.of("Set build permission"))
                    .child(setBuildO, "outside", "o")
                    .child(setBuildA, "allies", "a")
                    .child(setBuildR, "resident", "r")
                    .build();

            setContainerO = CommandSpec.builder()
                    .description(Text.of("Set outside container permission"))
                    .executor(new PermContainerOutside())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                    .build();

            setContainerA = CommandSpec.builder()
                .description(Text.of("Set allies container permission"))
                .executor(new PermContainerAllies())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                .build();

            setContainerR = CommandSpec.builder()
                .description(Text.of("Set allies container permission"))
                .executor(new PermContainerResident())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                .build();

            setContainer = CommandSpec.builder()
                    .description(Text.of("Set container permission"))
                    .child(setContainerO, "outside", "o")
                    .child(setContainerA, "allies", "a")
                    .child(setContainerR, "resident", "r")
                    .build();

            setSwitchO = CommandSpec.builder()
                    .description(Text.of("Set outside switch permission"))
                    .executor(new PermSwitchOutside())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                    .build();

            setSwitchA = CommandSpec.builder()
                .description(Text.of("Set outside switch permission"))
                .executor(new PermSwitchAllies())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                .build();

            setSwitchR = CommandSpec.builder()
                .description(Text.of("Set outside switch permission"))
                .executor(new PermSwitchResident())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                .build();

            setSwitch = CommandSpec.builder()
                    .description(Text.of("Set switch permission"))
                    .child(setSwitchO, "outside", "o")
                    .child(setSwitchA, "allies", "a")
                    .child(setSwitchR, "resident", "r")
                    .build();

            setSwitchAll = CommandSpec.builder()
                    .description(Text.of("Set switch permission"))
                    .executor(new PermSwitch())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                    .build();

            setContainerAll = CommandSpec.builder()
                    .description(Text.of("Set container permission"))
                    .executor(new PermContainer())
                    .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]"))))
                    .build();

            setBuildAll = CommandSpec.builder()
                    .description(Text.of("Set build permission"))
                    .executor(new PermBuild())
                    .arguments(
                            GenericArguments.onlyOne(GenericArguments.bool(Text.of("[value]")))
                    )
                    .build();

            setPerm = CommandSpec.builder()
                    .description(Text.of("Set new permissions"))
                    .child(setBuild, "build", "b")
                    .child(setBuildAll, "buildAll", "bAll")
                    .child(setContainer, "container", "c")
                    .child(setContainerAll, "containerAll", "cAll")
                    .child(setSwitch, "switch", "s")
                    .child(setSwitchAll, "switchAll", "sAll")
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
                    .child(setPerm, "perm", "p")
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
