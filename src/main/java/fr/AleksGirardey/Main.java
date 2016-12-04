package fr.AleksGirardey;

import com.google.inject.Inject;
import fr.AleksGirardey.Commands.AcceptCommand;
import fr.AleksGirardey.Commands.Chat.*;
import fr.AleksGirardey.Commands.City.*;
import fr.AleksGirardey.Commands.City.Cubo.CuboCommandAdd;
import fr.AleksGirardey.Commands.City.Cubo.CuboCommandMode;
import fr.AleksGirardey.Commands.City.Set.*;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.SetAlly;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.SetEnemy;
import fr.AleksGirardey.Commands.City.Set.Diplomacy.SetNeutral;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermAllies;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermCity;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermOutside;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermResident;
import fr.AleksGirardey.Commands.Party.*;
import fr.AleksGirardey.Commands.RefuseCommand;
import fr.AleksGirardey.Commands.War.*;
import fr.AleksGirardey.Listeners.*;
import fr.AleksGirardey.Objects.CommandElements.*;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.nio.file.Path;

@Plugin(id = "wos", name = "War Of Squirrels", version = "1.0", description = "BASTOOOOOOON")
public class Main {

    public static String        path = "WarOfSquirrels";

    @Inject
    private Game    game;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path        privateConfigDir;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        File        f = new File("WarOfSquirrels/config");

        logger.info("Please, wait for the War Of Squirrels plugin to be initialized");
        if (!f.exists())
            if (!f.mkdirs())
                logger.error("Can't create plugin directory");

        Core.initCore(logger, game, this, configManager);
        game.getEventManager().registerListeners(this, new OnPlayerLogin());
        game.getEventManager().registerListeners(this, new OnPlayerMove());
        game.getEventManager().registerListeners(this, new OnPlayerRespawn());
        game.getEventManager().registerListeners(this, new DestroyListener());
        game.getEventManager().registerListeners(this, new OnPlayerContainer());
        game.getEventManager().registerListeners(this, new OnPlayerSwitch());
        game.getEventManager().registerListeners(this, new OnPlayerDeath());
        game.getEventManager().registerListeners(this, new OnPlayerChat());
        game.getEventManager().registerListeners(this, new OnPlayerCubo());
    }

    @Listener
    public void             onServerInit(GameInitializationEvent event) {
        CommandSpec         cityCommandSpec, accept, refuse;
        CommandSpec         info, create, delete, claim, unclaim,
                            set, help, add, remove, list, leave, cubo;
        CommandSpec         setHelp, setSpawn, setAlly, setNeutral,
                            setEnemy, setMayor, setAssistant, setPerm;
        CommandSpec         setAllies, setResident, setOutside, setCubo;
        CommandSpec         party, partyDelete, partyCreate, partyInvite, partyRemove, partyLeave;
        
        info = CommandSpec.builder()
                .description(Text.of("Give city information"))
                .executor(new CityCommandInfo())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.onlyOne(new ElementCity(Text.of("[city]")))))
                .build();

        create = CommandSpec.builder()
                .description(Text.of("Create city"))
                .executor(new CityCommandCreate())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("City name"))))
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
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("[player]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(
                                        GenericArguments.onlyOne(GenericArguments.player(Text.of("<player>"))))
                                , 10))
                .executor(new CityCommandAdd())
                .build();

        remove = CommandSpec.builder()
                .description(Text.of("Kick a player from your city"))
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[citizen]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(
                                        GenericArguments.onlyOne(new ElementCitizen(Text.of("<citizen>"))))
                                , 10))
                .executor(new CityCommandRemove())
                .build();

        leave = CommandSpec.builder()
                .description(Text.of("Leave the city"))
                .executor(new CityCommandLeave())
                .build();

        cubo = CommandSpec.builder()
                .description(Text.of("Activate/Deactivate cubo mode"))
                .executor(new CuboCommandMode())
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
                        GenericArguments.onlyOne(new ElementCity(Text.of("[city]"))),
                        GenericArguments.optional(
                                GenericArguments.repeated(
                                        new ElementCity(Text.of("<city>")),
                                        10)))
                .build();

        setEnemy = CommandSpec.builder()
                .description(Text.of("Set a city as enemy"))
                .executor(new SetEnemy())
                .arguments(
                        GenericArguments.onlyOne(new ElementCity(Text.of("[city]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(
                                        new ElementCity(Text.of("<city>")))
                                ,10))
                .build();

        setNeutral = CommandSpec.builder()
                .description(Text.of("Set a city as neutral"))
                .executor(new SetNeutral())
                .arguments(
                        GenericArguments.onlyOne(new ElementCity(Text.of("[city]"))),
                        GenericArguments.optional(
                                GenericArguments.repeated(
                                        new ElementCity(Text.of("<city>")),
                                        10)))
                .build();

        setMayor = CommandSpec.builder()
                .description(Text.of("Set this citizen as mayor"))
                .executor(new SetMayor())
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[resident]"))))
                .build();

        setAssistant = CommandSpec.builder()
                .description(Text.of("Set this citizen as assistant"))
                .executor(new SetAssistant())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("[resident]"))))
                .build();

        setOutside = CommandSpec.builder()
                .description(Text.of("Set outside build permission"))
                .executor(new PermOutside())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]"))))
                .build();

        setAllies = CommandSpec.builder()
                .description(Text.of("Set allies build permission"))
                .executor(new PermAllies())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]"))))
                .build();

        setResident = CommandSpec.builder()
                .description(Text.of("Set resident build permission"))
                .executor(new PermResident())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]"))))
                .build();

        setPerm = CommandSpec.builder()
                .description(Text.of("Set new permissions"))
                .child(setResident, "resident", "r")
                .child(setAllies, "allies", "a")
                .child(setOutside, "outside", "o")
                .executor(new PermCity())
                .arguments(
                        GenericArguments.onlyOne(new ElementAlly(Text.of("[city]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]")))
                )
                .build();

        setCubo = CommandSpec.builder()
                .description(Text.of("Create cubo"))
                .executor(new CuboCommandAdd())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("[name]"))))
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
                .child(setCubo, "cubo", "c")
                .build();

        help = CommandSpec.builder()
                .description(Text.of("City commands help"))
                .executor(new CityCommandHelp())
                .build();

        list = CommandSpec.builder()
                .description(Text.of("City list"))
                .executor(new CityCommandList())
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
                .child(leave, "leave")
                .child(list, "list", "l")
                .child(cubo, "cubo", "c")
                .executor(new CityCommandInfo())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.onlyOne(new ElementCity(Text.of("[city]")))))
                .build();

        accept = CommandSpec.builder()
                .description(Text.of("Accept a pending invitation."))
                .executor(new AcceptCommand())
                .build();

        refuse = CommandSpec.builder()
                .description(Text.of("Refuse a pending invitation."))
                .executor(new RefuseCommand())
                .build();

        partyCreate = CommandSpec.builder()
                .description(Text.of("Create a party"))
                .executor(new PartyCreate())
                .build();

        partyInvite = CommandSpec.builder()
                .description(Text.of("Invite a player to your party"))
                .executor(new PartyInvite())
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizenOnline(Text.of("[citizen]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(new ElementCitizenOnline(Text.of("<citizen>"))), 10))
                .build();

        partyRemove = CommandSpec.builder()
                .description(Text.of("Remove a player from your party"))
                .executor(new PartyRemove())
                .arguments(GenericArguments.onlyOne(new ElementParty(Text.of("[player]"))))
                .build();

        partyDelete = CommandSpec.builder()
                .description(Text.of("Delete your party"))
                .executor(new PartyDelete())
                .build();

        partyLeave = CommandSpec.builder()
                .description(Text.of("Leave your party"))
                .executor(new PartyLeave())
                .build();

        party = CommandSpec.builder()
                .description(Text.of("Build a party before going on war"))
                .executor(new PartyInfo())
                .child(partyCreate, "create", "c")
                .child(partyInvite, "invite", "add")
                .child(partyRemove, "remove", "r")
                .child(partyDelete, "delete", "d")
                .child(partyLeave, "leave", "l")
                .build();

        CommandSpec war, forceWinAttacker, forceWinDefender;
        CommandSpec warAttack, warjoin, warleave, warlist;

        warjoin = CommandSpec.builder()
                .description(Text.of("Join a war"))
                .executor(new WarJoin())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[ally]"))))
                .build();

        warleave = CommandSpec.builder()
                .description(Text.of("Leave war"))
                .executor(new WarLeave())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[ally]"))))
                .build();

        warlist = CommandSpec.builder()
                .description(Text.of("war list"))
                .executor(new WarList())
                .build();

        warAttack = CommandSpec.builder()
                .description(Text.of("attack a city"))
                .executor(new DeclareWar())
                .arguments(
                        GenericArguments.onlyOne(new ElementEnemy(Text.of("[enemy]"))))
                .build();

        forceWinAttacker = CommandSpec.builder()
                .description(Text.of("Force win : attacker"))
                .permission("minecraft.command.op")
                .executor(new ForceWinAttacker())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[city]"))))
                .build();

        forceWinDefender = CommandSpec.builder()
                .description(Text.of("Force win : attacker"))
                .permission("minecraft.command.op")
                .executor(new ForceWinDefender())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[city]"))))
                .build();

        war = CommandSpec.builder()
                .description(Text.of("Give info on a war"))
                .executor(new WarInfo())
                .arguments(
                        GenericArguments.optional(
                            GenericArguments.onlyOne(new ElementWar(Text.of("[city]")))))
                .child(warAttack, "attack", "a")
                .child(forceWinAttacker, "winattacker", "wa")
                .child(forceWinDefender, "windefender", "wd")
                .child(warjoin, "join", "j")
                .child(warleave, "leave", "l")
                .child(warlist, "list")
                .build();

        CommandCallable     chat, lock, lockShout, lockCity, unlock, normal, shout, city;

        lockShout = CommandSpec.builder()
                .description(Text.of("Lock shout channel"))
                .executor(new ChatShout())
                .build();

        lockCity = CommandSpec.builder()
                .description(Text.of("Lock city channel"))
                .executor(new ChatCity())
                .build();

        lock = CommandSpec.builder()
                .description(Text.of("Lock a specified channel"))
                .child(lockShout, "cri", "c")
                .child(lockCity, "ville", "ville")
                .build();

        unlock = CommandSpec.builder()
                .description(Text.of("Set normal channel"))
                .executor(new ChatGlobal())
                .build();

        chat = CommandSpec.builder()
                .description(Text.of("Lock or unlock chat channel"))
                .child(lock, "lock", "l")
                .child(unlock, "unlock", "u")
                .build();

        normal = CommandSpec.builder()
                .description(Text.of("Send a message global channel"))
                .arguments(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("[text]"))))
                .executor(new SendNormal())
                .build();

        shout = CommandSpec.builder()
                .description(Text.of("Send a loud message"))
                .arguments(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("[text]"))))
                .executor(new SendShout())
                .build();

        city = CommandSpec.builder()
                .description(Text.of("Send a city message"))
                .arguments(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("[text]"))))
                .executor(new SendCity())
                .build();

        CommandCallable     near, plist;

        near = CommandSpec.builder()
                .description(Text.of("Nearest city homeblock"))
                .executor((commandSource, commandContext) -> {
                    if (commandSource instanceof Player) {
                        DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);
                        Text        message = Text.of("La civilization la plus proche est à " + Utils.NearestHomeblock(player.getPosX() / 16, player.getPosZ()  )
                                + " chunks.");
                        player.sendMessage(Text.of(TextColors.DARK_GREEN, message, TextColors.RESET));
                    }
                    return CommandResult.success();
                })
                .build();

        plist = CommandSpec.builder()
                .description(Text.of("List"))
                .executor((commandSource, commandContext) -> {
                    Core.Send("Overload /list");
                    return CommandResult.success();
                })
                .build();

        game.getCommandManager().register(this, cityCommandSpec, "city", "c");
        game.getCommandManager().register(this, party, "party", "p");
        game.getCommandManager().register(this, war, "war", "w");
        game.getCommandManager().register(this, accept, "accept", "a");
        game.getCommandManager().register(this, refuse, "refuse", "r");
        game.getCommandManager().register(this, chat, "chat", "ch");
        game.getCommandManager().register(this, normal, "dire", "d");
        game.getCommandManager().register(this, shout, "cri", "cr");
        game.getCommandManager().register(this, city, "town", "t");
        game.getCommandManager().register(this, near, "near", "n");
        game.getCommandManager().register(this, plist, "list");

        logger.info("Welcome in the War Of Squirrels. Have fun !");
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        logger.info("Closing WOS...");
        ConfigLoader.close();
        Core.close();
    }

    public Logger getLogger() { return logger; }
}
