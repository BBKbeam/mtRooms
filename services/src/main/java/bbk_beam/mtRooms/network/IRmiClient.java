package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * IRmiClient
 * <p>
 * Expected front facing interface for any clients that connect to the server to implement.
 * </p>
 */
public interface IRmiClient extends Remote {

    /**
     * Sets the client's session token
     *
     * @param token Session token
     * @throws RemoteException when network issues occur during the remote call
     */
    void setToken(Token token) throws RemoteException;

    /**
     * Gets the client's session token
     *
     * @return Session Token
     * @throws RemoteException when network issues occur during the remote call
     */
    Token getToken() throws RemoteException;

    /**
     * Updates the observing client
     *
     * @param observable Provenance of the message
     * @param update     Update message
     * @throws RemoteException when network issues occur during the remote call
     */
    void update(Object observable, Object update) throws RemoteException;
}
