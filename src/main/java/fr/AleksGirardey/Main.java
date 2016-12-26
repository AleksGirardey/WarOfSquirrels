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
import fr.AleksGirardey.Commands.Shop.ShopDelete;
import fr.AleksGirardey.Commands.Shop.ShopReassign;
import fr.AleksGirardey.Commands.Utils.CommandPay;
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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
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
        game.getEventManager().registerListeners(this, new OnPlayerMove());

        game.getEventManager().registerListeners(this, new BlockListener());
        game.getEventManager().registerListeners(this, new PlayerListener());
        game.getEventManager().registerListeners(this, new EntityListener());
        game.getEventManager().registerListeners(this, new OnPlayerChat());
        game.getEventManager().registerListeners(this, new OnPlayerCubo());
    }

    private CommandSpec     commandCity() {
        CommandSpec         city_help, city_info, city_create, city_delete,
                city_claim, city_unclaim, city_set, city_add, city_remove, city_leave,
                city_list, city_cubo, city_deposit, city_withdraw;

        city_help = CommandSpec.builder()
                .description(Text.of("City commands help"))
                .executor(new CityCommandHelp())
                .build();

        city_info = CommandSpec.builder()
                .description(Text.of("Give city information"))
                .executor(new CityCommandInfo())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.onlyOne(new ElementCity(Text.of("[city]")))))
                .build();

        city_create = CommandSpec.builder()
                .description(Text.of("Create city"))
                .executor(new CityCommandCreate())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("City name"))))
                .build();

        city_delete = CommandSpec.builder()
                .description(Text.of("Delete city"))
                .executor(new CityCommandDelete())
                .build();

        city_claim = CommandSpec.builder()
                .description(Text.of("Claim chunk for your city"))
                .executor(new CityCommandClaim())
                .build();

        city_unclaim = CommandSpec.builder()
                .description(Text.of("Make this chunk belongs to mother nature"))
                .executor(new CityCommandUnclaim())
                .build();

        city_set = commandCitySet();

        city_add = CommandSpec.builder()
                .description(Text.of("Invite a player to join your city"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("[player]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(
                                        GenericArguments.onlyOne(GenericArguments.player(Text.of("<player>"))))
                                , 10))
                .executor(new CityCommandAdd())
                .build();

        city_remove = CommandSpec.builder()
                .description(Text.of("Kick a player from your city"))
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[citizen]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(
                                        GenericArguments.onlyOne(new ElementCitizen(Text.of("<citizen>"))))
                                , 10))
                .executor(new CityCommandRemove())
                .build();

        city_leave = CommandSpec.builder()
                .description(Text.of("Leave the city"))
                .executor(new CityCommandLeave())
                .build();

        city_list = CommandSpec.builder()
                .description(Text.of("City list"))
                .executor(new CityCommandList())
                .build();

        city_cubo = CommandSpec.builder()
                .description(Text.of("Activate/Deactivate cubo mode"))
                .executor(new CuboCommandMode())
                .build();

        city_deposit = CommandSpec.builder()
                .description(Text.of("Deposit money on city account"))
                .executor(new CityCommandDeposit())
                .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("[amount]"))))
                .build();

        city_withdraw = CommandSpec.builder()
                .description(Text.of("Withdraw money from city account"))
                .executor(new CityCommandWithdraw())
                .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("[amount]"))))
                .build();

        return (CommandSpec.builder()
                .description(Text.of("Commands related to your city"))
                .child(city_help, "help", "?")
                .child(city_info, "info", "i")
                .child(city_create, "create", "new")
                .child(city_delete, "delete")
                .child(city_claim, "claim")
                .child(city_unclaim, "unclaim")
                .child(city_set, "set")
                .child(city_add, "add", "invite")
                .child(city_remove, "remove", "kick")
                .child(city_leave, "leave")
                .child(city_list, "list", "l")
                .child(city_cubo, "cubo", "c")
                .child(city_deposit, "deposit", "d")
                .child(city_withdraw, "withdraw", "w")
                .executor(new CityCommandInfo())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.onlyOne(new ElementCity(Text.of("[city]")))))
                .build());
    }

    private CommandSpec     commandCitySet() {
        CommandSpec         setHelp, setSpawn, setAlly, setEnemy,
                setNeutral, setMayor, setAssistant, setOutside,
                setAllies, setResident, setPerm, setCubo;

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

         return (CommandSpec.builder()
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
                .build());
    }

    private CommandSpec     commandParty() {
        CommandSpec         partyCreate, partyInvite, partyRemove,
                partyDelete, partyLeave;

        partyCreate = CommandSpec.builder()
                .description(Text.of("Create a party"))
                .executor(new PartyCreate())
                .build();

        partyInvite = CommandSpec.builder()
                .description(Text.of("Invite a player to your party"))
                .executor(new PartyInvite())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("[player]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(GenericArguments.player(Text.of("<player>"))), 10))
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

        return (CommandSpec.builder()
                .description(Text.of("Build a party before going on war"))
                .executor(new PartyInfo())
                .child(partyCreate, "create", "c")
                .child(partyInvite, "invite", "add")
                .child(partyRemove, "remove", "r")
                .child(partyDelete, "delete", "d")
                .child(partyLeave, "leave", "l")
                .build());
    }

    private CommandSpec     commandWar() {
        CommandSpec         warAttack, warWinAtt, warWinDef, warJoin,
                warLeave, warList, warPeace, warTarget;

        warJoin = CommandSpec.builder()
                .description(Text.of("Join a war"))
                .executor(new WarJoin())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[ally]"))))
                .build();

        warLeave = CommandSpec.builder()
                .description(Text.of("Leave war"))
                .executor(new WarLeave())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[ally]"))))
                .build();

        warList = CommandSpec.builder()
                .description(Text.of("war list"))
                .executor(new WarList())
                .build();

        warAttack = CommandSpec.builder()
                .description(Text.of("attack a city"))
                .executor(new DeclareWar())
                .arguments(
                        GenericArguments.onlyOne(new ElementEnemy(Text.of("[enemy]"))))
                .build();

        warWinAtt = CommandSpec.builder()
                .description(Text.of("Force win : attacker"))
                .permission("minecraft.command.op")
                .executor(new ForceWinAttacker())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[city]"))))
                .build();

        warWinDef = CommandSpec.builder()
                .description(Text.of("Force win : attacker"))
                .permission("minecraft.command.op")
                .executor(new ForceWinDefender())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[city]"))))
                .build();

        warPeace = CommandSpec.builder()
                .description(Text.of("Bring (or take back) peace to this world"))
                .permission("minecraft.command.op")
                .executor(new WarPeace())
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("[peace]"))))
                .build();

        warTarget = CommandSpec.builder()
                .description(Text.of("Set a player as the new target"))
                .executor(new WarTarget())
                .arguments(GenericArguments.onlyOne(new ElementDefender(Text.of("[player]"))))
                .build();

        return (CommandSpec.builder()
                .description(Text.of("Give info on a war"))
                .executor(new WarInfo())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.onlyOne(new ElementWar(Text.of("[city]")))))
                .child(warAttack, "attack", "a")
                .child(warWinAtt, "winattacker", "wa")
                .child(warWinDef, "windefender", "wd")
                .child(warJoin, "join", "j")
                .child(warLeave, "leave", "l")
                .child(warList, "list")
                .child(warPeace, "peace", "p")
                .child(warTarget, "target", "t")
                .build());
    }

    private CommandSpec     commandChat() {
        CommandSpec         lock, lockCri, lockVille, unlock;

        lockCri = CommandSpec.builder()
                .description(Text.of("Lock shout channel"))
                .executor(new ChatShout())
                .build();

        lockVille = CommandSpec.builder()
                .description(Text.of("Lock city channel"))
                .executor(new ChatCity())
                .build();

        lock = CommandSpec.builder()
                .description(Text.of("Lock a specified channel"))
                .child(lockCri, "cri", "c")
                .child(lockVille, "ville", "ville")
                .build();

        unlock = CommandSpec.builder()
                .description(Text.of("Set normal channel"))
                .executor(new ChatGlobal())
                .build();

        return (CommandSpec.builder()
                .description(Text.of("Lock or unlock chat channel"))
                .child(lock, "lock", "l")
                .child(unlock, "unlock", "u")
                .build());
    }

    private CommandSpec     commandShop() {
        CommandSpec         delete, reassign;

        reassign = CommandSpec.builder()
                .description(Text.of("Reassign the targeted shop"))
                .executor(new ShopReassign())
                .arguments(GenericArguments.onlyOne(new ElementDBPlayer(Text.of("[player]"))))
                .build();

        delete = CommandSpec.builder()
                .description(Text.of("Delete the targeted shop"))
                .executor(new ShopDelete())
                .build();

        return (CommandSpec.builder()
                .description(Text.of("Shop admin commands"))
                .permission("minecraft.command.op")
                .child(reassign, "reassign", "r")
                .child(delete, "delete", "d")
                .build());
    }

    @Listener
    public void             onServerInit(GameInitializationEvent event) {
        CommandSpec         city, party, war, shop, accept, refuse, chat, say, shout, town, near, list, setSpawn, pay;

        city = commandCity();

        party = commandParty();

        war = commandWar();

        shop = commandShop();

        accept = CommandSpec.builder()
                .description(Text.of("Accept a pending invitation."))
                .executor(new AcceptCommand())
                .build();

        refuse = CommandSpec.builder()
                .description(Text.of("Refuse a pending invitation."))
                .executor(new RefuseCommand())
                .build();

        chat = commandChat();

        say = CommandSpec.builder()
                .description(Text.of("Send a message global channel"))
                .arguments(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("[text]"))))
                .executor(new SendNormal())
                .build();

        shout = CommandSpec.builder()
                .description(Text.of("Send a loud message"))
                .arguments(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("[text]"))))
                .executor(new SendShout())
                .build();

        town = CommandSpec.builder()
                .description(Text.of("Send a city message"))
                .arguments(GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("[text]"))))
                .executor(new SendCity())
                .build();

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

        list = CommandSpec.builder()
                .description(Text.of("List"))
                .executor((commandSource, commandContext) -> {
            Core.Send("Overload /list");
            return CommandResult.success();
        })
                .build();

        setSpawn = CommandSpec.builder()
                .description(Text.of("Set World spawn"))
                .permission("minecraft.command.op")
                .executor((commandSource, commandContext) -> {
            if (!(commandSource instanceof Player))
                return CommandResult.empty();
            Player  player = (Player) commandSource;
            player.getWorld().getProperties().setSpawnPosition(player.getLocation().getBlockPosition());
            player.sendMessage(Text.of("Spawn of '" + player.getWorld().getName() + "' is now at ["
                    + player.getLocation().getBlockPosition().getX() + ";"
                    + player.getLocation().getBlockPosition().getY() + ";"
                    + player.getLocation().getBlockPosition().getZ() + "]"));
            return CommandResult.success();
        })
                .build();

        pay = CommandSpec.builder()
                .description(Text.of("Give money to someone"))
                .executor(new CommandPay())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("[amount]"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("[amount]")))
                )
                .build();

        game.getCommandManager().register(this, city, "city", "c");
        game.getCommandManager().register(this, party, "party", "p");
        game.getCommandManager().register(this, war, "war", "w");
        game.getCommandManager().register(this, accept, "accept", "a");
        game.getCommandManager().register(this, refuse, "refuse", "r");
        game.getCommandManager().register(this, chat, "chat", "ch");
        game.getCommandManager().register(this, say, "dire", "d");
        game.getCommandManager().register(this, shout, "cri", "cr");
        game.getCommandManager().register(this, town, "town", "t");
        game.getCommandManager().register(this, near, "near", "n");
        game.getCommandManager().register(this, list, "list");
        game.getCommandManager().register(this, setSpawn, "setSpawn");
        game.getCommandManager().register(this, pay, "pay");
        game.getCommandManager().register(this, shop, "shop", "s");

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
