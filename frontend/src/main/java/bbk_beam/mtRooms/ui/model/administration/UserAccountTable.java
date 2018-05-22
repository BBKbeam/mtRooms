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
     * @throws LoginException  when method is used outside a session
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public UserAccountTable(SessionManager sessionManager) throws LoginException, Unauthorised, RemoteException {
        this.sessionManager = sessionManager;
        loadData();
    }

    /**
     * Loads the table with updated data from backend
     *
     * @throws LoginException  when method is used outside a session
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public void loadData() throws LoginException, Unauthorised, RemoteException {
        IRmiServices services = sessionManager.getServices();
        List<Account> accountList = services.getAccounts(sessionManager.getToken());
        for (Account account : accountList) {
            user_data.add(new UserAccount(account));
        }
    }

    /**
     * Reload the table with updated data from backend
     *
     * @throws LoginException  when method is used outside a session
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public void reloadData() throws LoginException, Unauthorised, RemoteException {
        this.user_data.clear();
        loadData();
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
