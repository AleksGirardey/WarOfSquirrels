package fr.craftandconquest.warofsquirrels.handlers;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalLoan;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;
import fr.craftandconquest.warofsquirrels.objects.dbobject.*;
import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoanHandler {
    private Logger  logger;

    private Map<Sign, Loan> loans = new HashMap<>();
    private Map<Cubo, Loan>  loanMap = new HashMap<>();

    public LoanHandler(Logger logger) { this.logger = logger; }

    public void         populate() {
        String          sql = "SELECT * FROM `" + GlobalLoan.TABLENAME +"`";
        Loan            loan;

        try {
            Statement statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                loan = new Loan(statement.getResult());
                this.loans.put(loan.getSign(), loan);
                this.loanMap.put(loan.getCubo(), loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void         add(DBPlayer player, SignData datas, Sign sign) {
        String          name = datas.lines().get(0).toPlain();
        String          cuboName = datas.lines().get(1).toPlain();
        String          buy = datas.lines().get(2).toPlain();
        String          rent = datas.lines().get(3).toPlain();

        DBPlayer        owner = null;
        City            city = null;
        Cubo            cubo = Core.getCuboHandler().getFromName(cuboName);
        name = name.replaceAll("[<>]", "");


        if (!name.equals("Loan")) {
            owner = Core.getPlayerHandler().getFromName(name);
            city = Core.getCityHandler().get(name);
        } else
            owner = player;

        Loan            loan;

        if (owner != null)
            loan = new Loan(owner, cubo, sign, Integer.parseInt(buy), Integer.parseInt(rent));
        else
            loan = new Loan(city, cubo, sign, Integer.parseInt(buy), Integer.parseInt(rent));

        this.loans.put(sign, loan);
        this.loanMap.put(loan.getCubo(), loan);

        Core.getPlugin()
                .getScheduler()
                .createTaskBuilder().execute(loan::actualize)
                .delay(1, TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    public Loan         get(Cubo cubo) {
        return loanMap.get(cubo);
    }

    public Loan         get(Vector3i loc) {
        for (Loan loan : loans.values()) {
            if (loan.getSignLocation().equals(loc))
                return loan;
        }
        return null;
    }

    public void delete(Vector3i loc) {
        Loan toDelete = get(loc);

        if (toDelete != null) {
            this.loans.remove(toDelete.getSign());
            toDelete.delete();
        }
    }
}
