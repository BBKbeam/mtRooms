package bbk_beam.mtRooms.ui.model.administration;

import bbk_beam.mtRooms.admin.dto.Account;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.ui.model.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.rmi.RemoteException;
import java.util.List;

public class UserAccountTable {
    private SessionManager sessionManager;
    private ObservableList<UserAccount> user_data = FXCollections.observableArrayList();

    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public UserAccountTable(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        IRmiServices services = sessionManager.getServices();
        try {
            List<Account> accountList = services.getAccounts(sessionManager.getToken());
            for (Account account : accountList) {
                user_data.add(new UserAccount(account));
            }
        } catch (Unauthorised unauthorised) {
            unauthorised.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the list of user account
     *
     * @return Observable list of user accounts
     */
    public ObservableList<UserAccount> getUserData() {
        return this.user_data;
    }
}
