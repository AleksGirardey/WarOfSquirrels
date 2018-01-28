package fr.AleksGirardey;

import com.google.inject.Inject;
import fr.AleksGirardey.Commands.AcceptCommand;
import fr.AleksGirardey.Commands.Chat.*;
import fr.AleksGirardey.Commands.City.*;
import fr.AleksGirardey.Commands.City.Cubo.CuboCommandAdd;
import fr.AleksGirardey.Commands.City.Cubo.CuboCommandMode;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermAllies;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermOutside;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermRecruit;
import fr.AleksGirardey.Commands.City.Set.Permissions.PermResident;
import fr.AleksGirardey.Commands.City.Set.*;
import fr.AleksGirardey.Commands.Faction.*;
import fr.AleksGirardey.Commands.Faction.Set.Diplomacy.SetAlly;
import fr.AleksGirardey.Commands.Faction.Set.Diplomacy.SetEnemy;
import fr.AleksGirardey.Commands.Faction.Set.Diplomacy.SetNeutral;
import fr.AleksGirardey.Commands.Faction.Set.FactionSetHelp;
import fr.AleksGirardey.Commands.Party.*;
import fr.AleksGirardey.Commands.RefuseCommand;
import fr.AleksGirardey.Commands.Shop.ShopDelete;
import fr.AleksGirardey.Commands.Shop.ShopReassign;
import fr.AleksGirardey.Commands.Utils.*;
import fr.AleksGirardey.Commands.War.*;
import fr.AleksGirardey.Listeners.*;
import fr.AleksGirardey.Objects.CommandElements.*;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
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
import org.spongepowered.api.plugin.PluginContainer;
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
    private PluginContainer pluginContainer;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
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

        Core.initCore(logger, game, this);
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
                .childArgumentParseExceptionFallback(false)
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
                        GenericArguments.optional(
                                GenericArguments.repeated(GenericArguments.player(Text.of("<player>")), 10)))
                .childArgumentParseExceptionFallback(false)
                .executor(new CityCommandAdd())
                .build();

        city_remove = CommandSpec.builder()
                .description(Text.of("Kick a player from your city"))
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[citizen]"))),
                        GenericArguments.optional(
                                GenericArguments.repeated(GenericArguments.onlyOne(new ElementCitizen(Text.of("<citizen>"))), 10)))
                .childArgumentParseExceptionFallback(false)
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
                .executor(new CityCommandInfo())
                .arguments(GenericArguments.optional(new ElementCity(Text.of("[city]"))))
                .childArgumentParseExceptionFallback(false)
                .child(city_help, "help", "?")
                .child(city_info, "info", "i")
                //.child(city_create, "create", "new")
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
                .build());
    }

    private CommandSpec     commandCitySet() {
        CommandSpec         setHelp, setSpawn, setMayor, setAssistant, setResident, setRecruit,
                setOutside, setAllies, setPermResident, setPermRecruit, setPerm, setCubo, setHomeblock;

        setHelp = CommandSpec.builder()
                .description(Text.of("Display /city set help"))
                .executor(new SetHelp())
                .build();

        setSpawn = CommandSpec.builder()
                .description(Text.of("Set a new spawn for the city"))
                .executor(new SetSpawn())
                .build();

        setMayor = CommandSpec.builder()
                .description(Text.of("Set this citizen as mayor"))
                .executor(new SetMayor())
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[citizen]"))))
                .build();

        setAssistant = CommandSpec.builder()
                .description(Text.of("Set this citizen as assistant"))
                .executor(new SetAssistant())
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[citizen]"))))
                .build();

        setOutside = CommandSpec.builder()
                .description(Text.of("Set outside permission"))
                .executor(new PermOutside())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]"))))
                .build();

        setAllies = CommandSpec.builder()
                .description(Text.of("Set allies permission"))
                .executor(new PermAllies())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]"))))
                .build();

        setPermResident = CommandSpec.builder()
                .description(Text.of("Set resident permission"))
                .executor(new PermResident())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]"))))
                .build();

        setPermRecruit = CommandSpec.builder()
                .description(Text.of("Set recruit permission"))
                .executor(new PermRecruit())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[build]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[container]"))),
                        GenericArguments.onlyOne(GenericArguments.bool(Text.of("[switch]"))))
                .build();

        setPerm = CommandSpec.builder()
                .description(Text.of("Set new permissions"))
                .child(setPermResident, "resident", "r")
                .child(setAllies, "allies", "a")
                .child(setOutside, "outside", "o")
                .child(setPermRecruit, "recruit", "rec")
                .build();

        setCubo = CommandSpec.builder()
                .description(Text.of("Create cubo"))
                .executor(new CuboCommandAdd())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("[name]"))))
                .build();

        setResident = CommandSpec.builder()
                .description(Text.of("Set a citizen resident"))
                .executor(new setResident())
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[citizen]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(GenericArguments.onlyOne(new ElementCitizen(Text.of("<citizen>")))), 10))
                .build();

        setRecruit = CommandSpec.builder()
                .description(Text.of("Set a citizen recruit"))
                .executor(new setRecruit())
                .arguments(
                        GenericArguments.onlyOne(new ElementCitizen(Text.of("[citizen]"))),
                        GenericArguments.repeated(
                                GenericArguments.optional(GenericArguments.onlyOne(new ElementCitizen(Text.of("<citizen>")))), 10))
                .build();

        setHomeblock = CommandSpec.builder()
                .description(Text.of("Set city homeblock"))
                .executor(new setHomeblock())
                .build();

        return (CommandSpec.builder()
                .description(Text.of("Commands related to new attribution in your city"))
                .child(setHelp, "help", "?")
                .child(setSpawn, "spawn")
                .child(setMayor, "mayor")
                .child(setAssistant, "assistant")
                .child(setPerm, "perm", "p")
                .child(setCubo, "cubo", "c")
                .child(setResident, "resident", "r")
                .child(setRecruit, "recruit", "rec")
                .child(setHomeblock, "homeblock", "hb")
                .build());
    }

    private CommandSpec     commandFaction() {
        CommandSpec         faction_help, faction_info, faction_list, faction_create, faction_delete, faction_set;

        faction_help = CommandSpec.builder()
                .description(Text.of("Display help commands"))
                .executor(new FactionHelp())
                .build();

        faction_info = CommandSpec.builder()
                .description(Text.of("Display faction information"))
                .executor(new FactionInfo())
                .arguments(GenericArguments.optional(
                        GenericArguments.onlyOne(new ElementFaction(Text.of("<faction>")))))
                .build();

        faction_list = CommandSpec.builder()
                .description(Text.of("Affiche la liste des factions"))
                .executor(new FactionList())
                .build();

        faction_create = CommandSpec.builder()
                .description(Text.of("Create a new faction"))
                .executor(new FactionCreate())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("[faction_name]"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("[capital_name]"))))
                .build();

        faction_delete = CommandSpec.builder()
                .description(Text.of("Delete the faction"))
                .executor(new FactionDelete())
                .build();

        faction_set = commandFactionSet();

        return (CommandSpec.builder()
                .description(Text.of("Commands related to your city"))
                .child(faction_help, "help", "?")
                .child(faction_info, "info", "i")
                .child(faction_list, "list", "l")
                .child(faction_create, "create", "c")
                .child(faction_delete, "delete", "d")
                .child(faction_set, "set", "s")
                .executor(new FactionInfo())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.onlyOne(new ElementFaction(Text.of("<faction>")))))
                .build());
    }

    private CommandSpec     commandFactionSet() {
        CommandSpec         setAlly, setNeutral, setEnemy;

        setAlly = CommandSpec.builder()
                .description(Text.of("Set a city as ally"))
                .executor(new SetAlly())
                .arguments(
                        GenericArguments.onlyOne(new ElementFaction(Text.of("[faction]"))),
                        GenericArguments.optional(
                                GenericArguments.seq(
                                        GenericArguments.bool(Text.of("<build>")),
                                        GenericArguments.bool(Text.of("<container>")),
                                        GenericArguments.bool(Text.of("<switch>")))))
                .build();

        setEnemy = CommandSpec.builder()
                .description(Text.of("Set a city as enemy"))
                .executor(new SetEnemy())
                .arguments(
                        GenericArguments.onlyOne(new ElementFaction(Text.of("[faction]"))),
                        GenericArguments.optional(
                                GenericArguments.seq(
                                        GenericArguments.bool(Text.of("<build>")),
                                        GenericArguments.bool(Text.of("<container>")),
                                        GenericArguments.bool(Text.of("<switch>")))))
                .build();

        setNeutral = CommandSpec.builder()
                .description(Text.of("Set a city as neutral"))
                .executor(new SetNeutral())
                .arguments(
                        GenericArguments.onlyOne(new ElementFaction(Text.of("[faction]"))),
                        GenericArguments.optional(
                                GenericArguments.seq(
                                        GenericArguments.bool(Text.of("<build>")),
                                        GenericArguments.bool(Text.of("<container>")),
                                        GenericArguments.bool(Text.of("<switch>")))))
                .build();

        return (CommandSpec.builder()
                .description(Text.of("Commands related to new attribution in your city"))
                .child(setAlly, "ally", "a")
                .child(setNeutral, "neutral", "n")
                .child(setEnemy, "enemy", "e")
                .executor(new FactionSetHelp())
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
                        GenericArguments.optional(
                                GenericArguments.repeated(GenericArguments.player(Text.of("<player>")), 10)))
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
                .childArgumentParseExceptionFallback(false)
                .build();

        warLeave = CommandSpec.builder()
                .description(Text.of("Leave war"))
                .executor(new WarLeave())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[ally]"))))
                .childArgumentParseExceptionFallback(false)
                .build();

        warList = CommandSpec.builder()
                .description(Text.of("war list"))
                .executor(new WarList())
                .build();

        warAttack = CommandSpec.builder()
                .description(Text.of("attack a city"))
                .executor(new DeclareWar())
                .arguments(
                        GenericArguments.onlyOne(new ElementAttackable(Text.of("[target]"))))
                .childArgumentParseExceptionFallback(false)
                .build();

        warWinAtt = CommandSpec.builder()
                .description(Text.of("Force win : attacker"))
                .permission("minecraft.command.op")
                .executor(new ForceWinAttacker())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[city]"))))
                .childArgumentParseExceptionFallback(false)
                .build();

        warWinDef = CommandSpec.builder()
                .description(Text.of("Force win : defender"))
                .permission("minecraft.command.op")
                .executor(new ForceWinDefender())
                .arguments(
                        GenericArguments.onlyOne(new ElementWar(Text.of("[city]"))))
                .childArgumentParseExceptionFallback(false)
                .build();

        warPeace = CommandSpec.builder()
                .description(Text.of("Bring (or take back) peace to this world"))
                .permission("minecraft.command.op")
                .executor(new WarPeace())
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("[peace]"))))
                .childArgumentParseExceptionFallback(false)
                .build();

        warTarget = CommandSpec.builder()
                .description(Text.of("Set a player as the new target"))
                .executor(new WarTarget())
                .arguments(GenericArguments.onlyOne(new ElementDefender(Text.of("[player]"))))
                .childArgumentParseExceptionFallback(false)
                .build();

        return (CommandSpec.builder()
                .description(Text.of("Give info on a war"))
                .executor(new WarInfo())
                .arguments(
                        GenericArguments.optional(
                                GenericArguments.onlyOne(new ElementWar(Text.of("[city]")))))
                .childArgumentParseExceptionFallback(false)
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

    private CommandSpec     commandAdmin() {
        CommandSpec setSpawn, levelUp, setadmin, moneyAdd, moneyRemove;

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


        levelUp = CommandSpec.builder()
                .description(Text.of("Level up a city"))
                .executor(new LevelUp())
                .arguments(
                        GenericArguments.onlyOne(new ElementCity(Text.of("[city]"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("[level]")))
                ).build();

        setadmin = CommandSpec.builder()
                .description(Text.of("Donne le status d'admin à un joueur"))
                .executor(new SetAdmin())
                .arguments(GenericArguments.optional(new ElementDBPlayer(Text.of("[joueur]"))))
                .permission("minecraft.command.op")
                .build();


        moneyAdd = CommandSpec.builder()
                .description(Text.of("Ajoute de l'argent au solde du joueur"))
                .executor(new MoneyAdd())
                .permission("minecraft.command.op")
                .arguments(
                        GenericArguments.firstParsing(new ElementCity(Text.of("[city]")), new ElementDBPlayer(Text.of("[joueur]"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("[montant]"))))
                .build();

        moneyRemove = CommandSpec.builder()
                .description(Text.of("Enlève de l'argent au solde du joueur"))
                .executor(new MoneyRemove())
                .permission("minecraft.command.op")
                .arguments(
                        GenericArguments.firstParsing(new ElementCity(Text.of("[city]")), new ElementDBPlayer(Text.of("[joueur]"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("[montant]"))))
                .build();

        return CommandSpec.builder()
                .child(setSpawn, "setspawn", "ss")
                .child(levelUp, "setlevel", "sl")
                .child(setadmin, "setadmin", "sa")
                .child(moneyAdd, "moneyadd", "ma")
                .child(moneyRemove, "moneyremove", "mr")
                .build();
    }

    @Listener
    public void             onServerInit(GameInitializationEvent event) {
        CommandSpec         city, faction, party, war, shop, accept, refuse,
                chat, say, shout, town, near, list, pay, me, admin;

        city = commandCity();

        faction = commandFaction();

        party = commandParty();

        war = commandWar();

        shop = commandShop();

        admin = commandAdmin();

        chat = commandChat();

        accept = CommandSpec.builder()
                .description(Text.of("Accept a pending invitation."))
                .executor(new AcceptCommand())
                .build();

        refuse = CommandSpec.builder()
                .description(Text.of("Refuse a pending invitation."))
                .executor(new RefuseCommand())
                .build();

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
                        int         value = Utils.NearestHomeblock(player.getLastChunkX(), player.getLastChunkZ());
                        Text        message;

                        if (value != -1)
                            message = Text.of("La civilization la plus proche est à " + value + " chunks.");
                        else
                            message = Text.of("Aucune ville n'a encore été créé dans ce monde.");
                        player.sendMessage(Text.of(TextColors.DARK_GREEN, message, TextColors.RESET));
                    }
                    return CommandResult.success();
                })
                .build();

        list = CommandSpec.builder()
                .description(Text.of("List"))
                .executor((commandSource, commandContext) -> {
            Utils.displayCommandList((Player) commandSource);
            return CommandResult.success();
        })
                .build();
        pay = CommandSpec.builder()
                .description(Text.of("Give money to someone"))
                .executor(new CommandPay())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("[player]"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("[amount]")))
                )
                .build();

        me = CommandSpec.builder()
                .description(Text.of("Donne les informations lié à son compte"))
                .executor(new Me())
                .build();

        game.getCommandManager().register(this, city, "city", "c");
        game.getCommandManager().register(this, faction, "faction", "f");
        game.getCommandManager().register(this, party, "party", "p");
        game.getCommandManager().register(this, war, "war", "w");
        game.getCommandManager().register(this, admin, "admin", "ad");
        game.getCommandManager().register(this, accept, "accept", "a");
        game.getCommandManager().register(this, refuse, "refuse", "r");
        game.getCommandManager().register(this, chat, "chat", "ch");
        game.getCommandManager().register(this, say, "dire", "d");
        game.getCommandManager().register(this, shout, "cri", "cr");
        game.getCommandManager().register(this, town, "town", "t");
        game.getCommandManager().register(this, near, "near", "n");
        game.getCommandManager().register(this, list, "list");
        game.getCommandManager().register(this, pay, "pay");
        game.getCommandManager().register(this, shop, "shop", "s");
        game.getCommandManager().register(this, me, "me");

        logger.info("Welcome in the War Of Squirrels. Have fun !");
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        logger.info("Closing WOS...");
        Core.getConfig().close();
        Core.close();
    }

    public Logger getLogger() { return logger; }

    public PluginContainer  getPluginContainer() { return pluginContainer; }
}
