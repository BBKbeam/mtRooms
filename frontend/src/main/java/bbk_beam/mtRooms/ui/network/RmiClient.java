package bbk_beam.mtRooms.ui.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.network.IRmiClient;
import eadjlib.logger.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient extends UnicastRemoteObject implements IRmiClient {
    private final Logger log = Logger.getLoggerInstance(RmiClient.class.getName());
    private Token token = null;

    /**
     * Constructor
     *
     * @throws RemoteException when network issues occur during the remote call
     */
    public RmiClient() throws RemoteException {
        super();
    }

    @Override
    public void setToken(Token token) throws RemoteException {
        this.token = token;
    }

    @Override
    public Token getToken() throws RemoteException {
        return this.token;
    }

    @Override
    public void update(Object observable, Object update) throws RemoteException {
        System.out.println("[" + this.token + "] Observable " + observable + " sent update: " + update);
    }

}
