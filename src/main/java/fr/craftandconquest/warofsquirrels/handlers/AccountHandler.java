package fr.craftandconquest.warofsquirrels.handlers;

import fr.craftandconquest.warofsquirrels.objects.dbobject.DBAccount;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalAccount;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AccountHandler {
    private Logger                      logger;
    private Map<Integer, DBAccount>     accounts = new HashMap<>();

    public void         populate() {
        String          sql = "SELECT * FROM `" + GlobalAccount.tableName + "`";
        DBAccount       account;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                account = new DBAccount(statement.getResult());
                this.accounts.put(account.getId(), account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DBAccount    get(int id) { return accounts.get(id); }

    /*
    public void         add(DBPlayer player, int _po, int _pa, int _pb) {
        DBAccount       account = new DBAccount(_po, _pa, _pb);

        player.setAccount(account);
        this.accounts.put(account.getId(), account);
    }

    public void         add(city city, int _po, int _pa, int _pb) {
        DBAccount       account = new DBAccount(_po, _pa, _pb);

        city.setAccount(account);
        this.accounts.put(account.getId(), account);
    }

    public void        delete(DBAccount account) {
        this.accounts.remove(account.getId());
    }

    public void         insert(DBAccount account, int po, int pa, int pb) {
        int             newPO, newPA, newPB;

        newPB = (account.getPb() + pb) > 0 ? newPB = 100 -
    } */
}