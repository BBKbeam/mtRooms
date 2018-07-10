package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.admin.exception.AuthenticationFailureException;
import bbk_beam.mtRooms.db.exception.SessionInvalidException;
import bbk_beam.mtRooms.uaa.exception.ServerSessionInactive;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * IRmiServices
 * <p>
 * Front facing RMI interface for server functionalities
 * </p>
 */
public interface IRmiServices extends Remote {
    /**
     * Create a session for a user
     *
     * @param client   IRmiClient instance
     * @param username Username
     * @param password Password
     * @return Session token
     * @throws AuthenticationFailureException when username/password are not valid
     * @throws ServerSessionInactive          when session has not been initiated
     * @throws RemoteException                when network issues occur during the remote call
     */
    Token login(IRmiClient client, String username, String password) throws AuthenticationFailureException, ServerSessionInactive, RemoteException;

    /**
     * Logout from a session
     *
     * @param session_token Session token for the session to log out from
     * @throws SessionInvalidException when session is not valid
     * @throws ServerSessionInactive   when session has not been initiated
     * @throws RemoteException         when network issues occur during the remote call
     */
    void logout(Token session_token) throws SessionInvalidException, ServerSessionInactive, RemoteException;

    /**
     * Check access rights to administration
     *
     * @return Access rights
     */
    boolean hasAdministrativeAccess(Token token) throws RemoteException;

    /**
     * Check access rights to front desk
     *
     * @return Access rights
     */
    boolean hasFrontDeskAccess(Token token) throws RemoteException;

    /**
     * Check access rights to logistics
     *
     * @return Access rights
     */
    boolean hasLogisticsAccess(Token token) throws RemoteException;

    /**
     * Check access rights to revenue
     *
     * @return Access rights
     */
    boolean hasRevenueAccess(Token token) throws RemoteException;
}
