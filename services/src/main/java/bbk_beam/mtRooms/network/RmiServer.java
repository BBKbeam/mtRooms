package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.network.exception.FailedServerInit;
import eadjlib.logger.Logger;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer extends RmiServices {
    private final Logger log = Logger.getLoggerInstance(RmiServer.class.getName());

    /**
     * Constructor
     *
     * @throws FailedServerInit when server instantiation fails
     */
    private RmiServer() throws FailedServerInit {
        super();
    }

    //TODO pass optional port number to Server argv?
    //TODO do regular maintenance on expired tokens/client sessions
    //TODO (combined with above) add heartbeat on both client and server to check live status?
    public static void main(String argv[]) {
        Logger log = Logger.getLoggerInstance(RmiServer.class.getName());
        int port = 9999;
        try {
            Registry rmiRegistry = LocateRegistry.createRegistry(port);
            IRmiServices rmiServices = (IRmiServices) UnicastRemoteObject.exportObject(new RmiServer(), port);
            log.log("Loading RMI services on port:", port);
            rmiRegistry.bind("RmiServices", rmiServices);
            log.log("mtRooms server ready...");
        } catch (RemoteException e) {
            log.log_Fatal("A problem occurred during RMI setup for the server.");
            log.log_Exception(e);
            e.printStackTrace();
        } catch (FailedServerInit e) {
            log.log_Fatal("Could not fully instantiate a new RmiServer.");
            log.log_Exception(e);
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            log.log_Fatal("An instance of RmiServices is already bound to the registry.");
            log.log_Exception(e);
            e.printStackTrace();
        }
    }
}
