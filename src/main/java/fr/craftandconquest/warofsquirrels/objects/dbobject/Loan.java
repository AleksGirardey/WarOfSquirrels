package fr.craftandconquest.warofsquirrels.objects.dbobject;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalLoan;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Loan extends DBObject {
    private static String       _fields =
            "`" + GlobalLoan.PLAYER +
            "`, `" + GlobalLoan.CITY +
            "`, `" + GlobalLoan.LOANER +
            "`, `" + GlobalLoan.SIGNX +
            "`, `" + GlobalLoan.SIGNY +
            "`, `" + GlobalLoan.SIGNZ +
            "`, `" + GlobalLoan.CUBO +
            "`, `" + GlobalLoan.BUYPRICE +
            "`, `" + GlobalLoan.RENTPRICE +
            "`, `" + GlobalLoan.WORLD + "`";

    /* -- DB Fields -- */
    private DBPlayer    player = null;
    private City        city = null;
    private DBPlayer    loaner = null;
    private Vector3i    signLocation;
    private Cubo        cubo;
    private int         buyPrice;
    private int         rentPrice;
    private World       world;

    /* -- Extra fields -- */
    private Sign        sign;

    /*
    ** Constructors
    */

    public Loan(DBPlayer player, Cubo cubo, Sign sign, int buyPrice, int rentPrice) {
        this(player, null, cubo, sign, buyPrice, rentPrice);
    }

    public Loan(City city, Cubo cubo, Sign sign, int buyPrice, int rentPrice) {
        this(null, city, cubo, sign, buyPrice, rentPrice);
    }

    private Loan(DBPlayer player, City city, Cubo cubo, Sign sign, int buyPrice, int rentPrice) {
        super(GlobalLoan.ID, GlobalLoan.TABLENAME, _fields);

        this.player = player;
        this.city = city;
        this.loaner = null;
        this.signLocation = sign.getLocation().getBlockPosition();
        this.cubo = cubo;
        this.buyPrice = buyPrice;
        this.rentPrice = rentPrice;
        this.world = sign.getWorld();
        this._primaryKeyValue = "" + this.add(
                "" + (player == null ? "NULL" : "'" + player.getId() + "'")
                + ", " + (city == null ? "NULL" : city.getId())
                + ", NULL"
                + ", " + signLocation.getX()
                + ", " + signLocation.getY()
                + ", " + signLocation.getZ()
                + ", " + cubo.getId()
                + ", " + buyPrice
                + ", " + rentPrice
                + ", '" + world.getUniqueId() + "'");

        cubo.setLoan(this);
        this.sign = sign;
        writeLog();
    }

    public      Loan(ResultSet rs) throws SQLException {
        super(GlobalLoan.ID, GlobalLoan.TABLENAME, _fields);

        this._primaryKeyValue = rs.getString(GlobalLoan.ID);
        if (rs.getString(GlobalLoan.PLAYER) != null)
            this.player = Core.getPlayerHandler().get(rs.getString(GlobalLoan.PLAYER));
        if (rs.getString(GlobalLoan.CITY) != null)
            this.city = Core.getCityHandler().get(rs.getString(GlobalLoan.CITY));
        if (rs.getString(GlobalLoan.LOANER) != null)
            this.loaner = Core.getPlayerHandler().get(rs.getString(GlobalLoan.LOANER));
        this.signLocation = new Vector3i(rs.getInt(GlobalLoan.SIGNX),
                rs.getInt(GlobalLoan.SIGNY), rs.getInt(GlobalLoan.SIGNZ));
        this.cubo = Core.getCuboHandler().get(rs.getInt(GlobalLoan.CUBO));
        this.buyPrice = rs.getInt(GlobalLoan.BUYPRICE);
        this.rentPrice = rs.getInt(GlobalLoan.RENTPRICE);
        this.world = Core.getPlugin().getServer().getWorld(UUID.fromString(rs.getString(GlobalLoan.WORLD))).orElse(null);

        this.sign = (Sign) world.getLocation(signLocation).getTileEntity().orElse(null);
        this.cubo.setLoan(this);
    }

    public void actualize() {
        Optional<SignData>      optSignData = sign.get(SignData.class);
        Text header = Text.of("<", (this.player != null ? this.player.getDisplayName() : this.city.getDisplayName()), ">");
        Text price = Text.of("A > ", this.buyPrice, " : ", this.rentPrice, " < L");
        Text locataire = Text.of((this.loaner == null ? "---" : this.loaner.getDisplayName()));

        if (optSignData.isPresent()) {
            final SignData d = optSignData.get();

            d.set(d.getValue(Keys.SIGN_LINES).get().set(0, header));
            d.set(d.getValue(Keys.SIGN_LINES).get().set(1, Text.of(cubo.getName())));
            d.set(d.getValue(Keys.SIGN_LINES).get().set(2, price));
            d.set(d.getValue(Keys.SIGN_LINES).get().set(3, locataire));

            sign.offer(d);
        }
    }

    public int      getId() { return Integer.parseInt(_primaryKeyValue); }

    public DBPlayer getPlayer() { return player; }

    public void setPlayer(DBPlayer player) {
        this.player = player;
        this.edit(GlobalLoan.PLAYER, player == null ? "NULL" : "'" + player.getId() + "'");
    }

    public City getCity() { return city; }

    public void setCity(City city) {
        this.city = city;
        this.edit(GlobalLoan.CITY, city == null ? "NULL" : "" + city.getId());
    }

    public DBPlayer getLoaner() { return loaner; }

    public void setLoaner(DBPlayer loaner) {
        this.loaner = loaner;
        this.edit(GlobalLoan.LOANER, loaner == null ? "NULL" : "'" + loaner.getId() + "'");
    }

    public Vector3i getSignLocation() { return signLocation; }

    public void setSignLocation(Vector3i signLocation) {
        this.signLocation = signLocation;
        this.edit(GlobalLoan.SIGNX, "" + signLocation.getX());
        this.edit(GlobalLoan.SIGNY, "" + signLocation.getY());
        this.edit(GlobalLoan.SIGNZ, "" + signLocation.getZ());
    }

    public Cubo getCubo() { return cubo; }

    public void setCubo(Cubo cubo) {
        this.cubo = cubo;
        this.edit(GlobalLoan.CUBO, "" + cubo.getId());
    }

    public int getBuyPrice() { return buyPrice; }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
        this.edit(GlobalLoan.BUYPRICE, "" + buyPrice);
    }

    public int getRentPrice() { return rentPrice; }

    public void setRentPrice(int rentPrice) {
        this.rentPrice = rentPrice;
        this.edit(GlobalLoan.RENTPRICE, "" + rentPrice);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        this.edit(GlobalLoan.WORLD, "" + world.getUniqueId());
    }

    public Sign getSign() { return sign; }

    public void setSign(Sign sign) { this.sign = sign; }

    public void leave(Player player) {
        if (this.loaner == Core.getPlayerHandler().get(player)) {
            setLoaner(null);
            player.sendMessage(Text.of(TextColors.BLUE, "Vous n'êtes plus locataire du cubo ",
                    TextColors.GOLD, this.cubo.getName(), TextColors.RESET));
            announce(Text.of(TextColors.GOLD, Core.getPlayerHandler().get(player).getDisplayName(),
                    TextColors.BLUE, " n'est plus locataire du cubo ",
                    TextColors.GOLD, cubo.getName(), TextColors.RESET));
        }
    }

    public void loan(Player player) {
        DBPlayer dbPlayer = Core.getPlayerHandler().get(player);
        if (this.loaner != null) {
            dbPlayer.sendMessage(Text.of(TextColors.RED, "Il y a déjà un locataire.", TextColors.RESET));
            return;
        } else if (dbPlayer.getBalance() < buyPrice) {
            dbPlayer.sendMessage(Text.of(TextColors.RED, "Vous n'avez pas assez d'argent pour faire ça.", TextColors.RESET));
            return;
        }

        this.setLoaner(dbPlayer);
        this.loaner.withdraw(buyPrice);
        if (this.player != null)
            this.player.insert(buyPrice);
        else
            this.city.insert(buyPrice);
        announce(Text.of(TextColors.GOLD, dbPlayer.getDisplayName(), TextColors.GREEN, " est maintenant le locataire du cubo ", TextColors.GOLD, cubo.getName(), TextColors.RESET));
        player.sendMessage(Text.of(TextColors.BLUE, "Vous êtes maintenant locataire du cubo ",
                TextColors.GOLD, this.cubo.getName(),
                TextColors.BLUE, " un montant de ", TextColors.GOLD, buyPrice,
                TextColors.BLUE, " vous a été débité.", TextColors.RESET));
        this.actualize();
    }

    private void            announce(Text message) {
        List<DBPlayer>      list = new ArrayList<>();

        list.add(cubo.getOwner());
        if (player != null && !list.contains(player)) {
            list.add(player);
        } else if (city != null) {
            if (!list.contains(city.getOwner()))
                list.add(city.getOwner());
            city.getAssistants().forEach(p -> {
                if (!list.contains(p))
                    list.add(p);
            });
        }

        list.forEach(p -> p.sendMessage(message));
    }

    public void payDay() {
        if (loaner == null) return;

        if (this.loaner.getBalance() >= this.rentPrice) {
            if(this.player != null) this.player.insert(this.rentPrice);
            else this.city.insert(rentPrice);
        } else this.leave(this.player.getUser().getPlayer().get());
    }

    @Override
    protected void writeLog() {
        Core.getLogger().info("[Loan] (" + _fields + ") : #" + _primaryKeyValue
                + "," + (this.player == null ? "null" : this.player.getDisplayName())
                + "," + (this.city == null ? "null" : this.city.getDisplayName())
                + "," + (this.loaner == null ? "null" : this.loaner.getDisplayName())
                + ",[" + this.signLocation.getX() + ";" + this.signLocation.getY()
                + ";" + this.signLocation.getZ() + "]"
                + "," + this.cubo.getName()
                + "," + this.buyPrice + "," + this.rentPrice + "," + this.world.getUniqueId().toString());
    }
}
