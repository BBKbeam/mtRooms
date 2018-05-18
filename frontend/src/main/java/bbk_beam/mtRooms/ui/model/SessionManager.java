package bbk_beam.mtRooms.ui.model;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.exception.ClientInitFailure;
import bbk_beam.mtRooms.exception.LoginException;
import bbk_beam.mtRooms.exception.RemoteFailure;
import bbk_beam.mtRooms.network.IRmiClient;
import bbk_beam.mtRooms.network.IRmiServices;
import bbk_beam.mtRooms.network.RmiClient;
import bbk_beam.mtRooms.uaa.exception.ServerSessionInactive;
import eadjlib.logger.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class SessionManager {
    private final Logger log = Logger.getLoggerInstance(SessionManager.class.getName());
    private IRmiServices services;
    private IRmiClient rmiClient;
    private boolean login_flag;
    private Token token;

    /**
     * Constructor
     *
     * @param address Server address
     * @param port    Port
     */
    public SessionManager(String address, Integer port) throws ClientInitFailure {
        this.login_flag = false;
        String url = "//" + address + ":" + port + "/RmiServices";
        try {
            log.log("Trying to connect to services on ", url);
            this.services = (IRmiServices) Naming.lookup(url);
            this.rmiClient = new RmiClient();
        } catch (NotBoundException e) {
            log.log_Fatal("Server services does not look to be bound to RMI registry.");
            throw new ClientInitFailure("Server services does not look to be bound to RMI registry.", e);
        } catch (MalformedURLException e) {
            log.log_Fatal("Naming lookup URL (", url, ") is malformed.");
            throw new ClientInitFailure("Naming lookup URL (" + url + ") is malformed.", e);
        } catch (RemoteException e) {
            log.log_Fatal("Problems occurred during remote call.");
            throw new ClientInitFailure("Problems occurred during remote call.", e);
        }
    }

    /**
     * Login client
     *
     * @param username User name
     * @param password Password
     * @return Success
     * @throws RemoteFailure when server-side or connection issues occur
     */
    public boolean login(String username, String password) throws RemoteFailure {
        if (login_flag) {
            log.log_Warning("Currently logged-in. Please log out before trying to login again.");
            return false;
        } else {
            try {
                this.token = this.services.login(this.rmiClient, username, password);
                this.login_flag = true;
                return true;
            } catch (AuthenticationFailureException e) {
                log.log_Error("Username/Password not valid.");
                return false;
            } catch (ServerSessionInactive e) {
                log.log_Error("Server instance is not active.");
                throw new RemoteFailure("Server instance is not active.", e);
            } catch (RemoteException e) {
                log.log_Error("Network issues occurred.");
                throw new RemoteFailure("Network issues occurred.", e);
            }
        }
    }

    /**
     * Logout client
     *
     * @throws RemoteFailure                  when server-side or connection issues occur
     * @throws AuthenticationFailureException when token used cannot be authenticated by server
     */
    public void logout() throws RemoteFailure, AuthenticationFailureException {
        if (this.login_flag) {
            try {
                this.services.logout(this.token);
                this.token = null;
                this.login_flag = false;
                log.log("User session logged out.");
            } catch (SessionInvalidException e) {
                log.log_Error("User session invalid. Bad token?");
                throw new AuthenticationFailureException("Token could not be validated server-side.", e);
            } catch (ServerSessionInactive e) {
                log.log_Error("Server instance is not initialised.");
                throw new RemoteFailure("Server instance is not initialised.", e);
            } catch (RemoteException e) {
                log.log_Error("Network issues occurred.");
                throw new RemoteFailure("Network issues occurred.", e);
            }
        }
    }

    /**
     * Check the login status
     *
     * @return Login state
     */
    public boolean isLoggedIn() {
        return login_flag;
    }

    /**
     * Gets the current token
     *
     * @return Current token for the logged-in session
     * @throws LoginException when there is not a current session
     */
    public Token getToken() throws LoginException {
        if (this.login_flag && this.token != null)
            return this.token;
        else
            throw new LoginException("User is not logged in.");
    }

    /**
     * Gets the IRmiServices instance
     *
     * @return IRmiServices instance
     */
    public IRmiServices getServices() {
        return this.services;
    }
}
