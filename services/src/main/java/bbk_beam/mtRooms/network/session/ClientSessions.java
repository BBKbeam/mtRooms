package bbk_beam.mtRooms.network.session;


import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.network.exception.DuplicateClient;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import eadjlib.logger.Logger;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * ClientSessions
 * <p>
 * Observable-like data structure holding all connected IRmiClient sessions to server
 * </p>
 */
public class ClientSessions {
    private final Logger log = Logger.getLoggerInstance(ClientSessions.class.getName());
    private static HashMap<Token, ClientWrapper> sessions;

    /**
     * Constructor
     */
    public ClientSessions() {
        sessions = new HashMap<>();
    }

    /**
     * Add client to observers
     *
     * @param client RmiClient to add
     * @throws DuplicateClient when a client with the same Token already exists in the observers
     * @throws RemoteException when an client communication issue occurs
     */
    public synchronized void addClient(ClientWrapper client) throws DuplicateClient, RemoteException {
        log.log("Adding client [", client.getToken(), "].");
        if (!sessions.containsKey(client.getToken())) {
            sessions.put(client.getToken(), client);
        } else {
            log.log_Error("Client [", client.getToken(), "] already exists in ClientSessions.");
            throw new DuplicateClient("Client [" + client.getToken() + "] already exists in ClientSessions.");
        }
    }

    /**
     * Gets the client associated with a token
     *
     * @param token Session Token
     * @return Locally tracked client
     */
    public synchronized ClientWrapper getClient(Token token) {
        if (sessions.containsKey(token))
            return sessions.get(token);
        else
            throw new IllegalArgumentException("No client found in tracked ClientSessions with token [" + token + "]");
    }

    /**
     * Removes a client from observers
     *
     * @param client RmiClient to remove
     * @throws RemoteException when an client communication issue occurs
     */
    public synchronized void removeClient(ClientWrapper client) throws RemoteException {
        removeClient(client.getToken());
    }

    /**
     * Removes a client from observers
     *
     * @param client_token Token for the RmiClient to remove
     * @throws RemoteException when a client communication issue occurs
     */
    public synchronized void removeClient(Token client_token) throws RemoteException {
        log.log("Removing client [", client_token, "].");
        if (sessions.containsKey(client_token)) {
            try {
                sessions.get(client_token).closeReservationSession();
            } catch (Unauthorised unauthorised) {
                log.log_Debug("Client [", client_token, "] does not have IAuthorisedFrontDesk access.");
            }
            sessions.remove(client_token);
        } else {
            log.log_Error("Tried to remove non existent client [", client_token, "] from ClientSessions.");
        }
    }

    /**
     * Notifies a client
     *
     * @param token Token of the client to notify
     * @param arg   Notification message
     * @throws RemoteException when an client communication issue occurs
     */
    public void notifyClient(Token token, Object arg) throws RemoteException {
        if (sessions.containsKey(token)) {
            sessions.get(token).update(this, arg);
        }
    }

    /**
     * Notifies all clients
     *
     * @param arg Notification message
     * @throws RemoteException when an client communication issue occurs
     */
    public void notifyClients(Object arg) throws RemoteException {
        for (Map.Entry<Token, ClientWrapper> entry : sessions.entrySet()) {
            entry.getValue().update(this, arg);
        }
    }

    /**
     * Clears all clients from observers
     */
    public synchronized void deleteClients() {
        log.log("Removing all clients.");
        sessions.clear();
    }

    /**
     * Gets the number of observing clients
     *
     * @return Client in observers
     */
    public synchronized int countObservers() {
        return sessions.size();
    }
}
