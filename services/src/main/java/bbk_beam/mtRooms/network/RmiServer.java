package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.network.exception.FailedServerInit;
import eadjlib.logger.Logger;
import org.apache.commons.cli.*;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * mtRooms Server
 * <p>
 * Provides RMI access to backend services
 * </p>
 */
public class RmiServer extends RmiServices {
    private final Logger log = Logger.getLoggerInstance(RmiServer.class.getName());
    private static final Thread mainThread = Thread.currentThread();
    private Integer port = 9999; //default
    private Registry rmiRegistry;
    private IRmiServices rmiServices;
    private IRmiAdministrationServices rmiAdministrationServices;
    private IRmiReservationServices rmiReservationServices;
    private IRmiLogisticsServices rmiLogisticsServices;
    private IRmiRevenueServices rmiRevenueServices;

    private synchronized void loadAdministrationServices() throws RemoteException {
        rmiAdministrationServices = (IRmiAdministrationServices) UnicastRemoteObject.exportObject(new RmiAdministrationServices(sessions), port);
        rmiRegistry.rebind("RmiAdministrationServices", rmiAdministrationServices);
    }

    private synchronized void loadReservationServices() throws RemoteException {
        rmiReservationServices = (IRmiReservationServices) UnicastRemoteObject.exportObject(new RmiReservationServices(sessions), port);
        rmiRegistry.rebind("RmiReservationServices", rmiReservationServices);
    }

    private synchronized void loadLogisticsServices() throws RemoteException {
        rmiLogisticsServices = (IRmiLogisticsServices) UnicastRemoteObject.exportObject(new RmiLogisticsServices(sessions), port);
        rmiRegistry.rebind("RmiLogisticsServices", rmiLogisticsServices);
    }

    private synchronized void loadRevenueServices() throws RemoteException {
        rmiRevenueServices = (IRmiRevenueServices) UnicastRemoteObject.exportObject(new RmiRevenueServices(sessions), port);
        rmiRegistry.rebind("RmiRevenueServices", rmiRevenueServices);
    }

    private synchronized void shutdownAdministrationServices() throws RemoteException {
        try {
            rmiRegistry.unbind("RmiAdministrationServices");
            log.log_Debug("RmiAdministrationServices unbound from RMI registry.");
            UnicastRemoteObject.unexportObject(rmiAdministrationServices, true);
            log.log_Debug("RmiAdministrationServices un-exported.");
        } catch (NotBoundException e) {
            log.log_Warning("An RmiAdministrationServices instance was not bound to registry.");
            try {
                UnicastRemoteObject.unexportObject(rmiAdministrationServices, true);
                log.log_Debug("RmiAdministrationServices un-exported.");
            } catch (NoSuchObjectException e1) {
                log.log_Error("Could terminate RmiAdministrationServices: not found.");
            }
        }
    }

    private synchronized void shutdownReservationServices() throws RemoteException {
        try {
            rmiRegistry.unbind("RmiReservationServices");
            log.log_Debug("RmiReservationServices unbound from RMI registry.");
            UnicastRemoteObject.unexportObject(rmiReservationServices, true);
            log.log_Debug("RmiReservationServices un-exported.");
        } catch (NotBoundException e) {
            log.log_Warning("An RmiReservationServices instance was not bound to registry.");
            try {
                UnicastRemoteObject.unexportObject(rmiReservationServices, true);
                log.log_Debug("RmiReservationServices un-exported.");
            } catch (NoSuchObjectException e1) {
                log.log_Error("Could terminate RmiReservationServices: not found.");
            }
        }
    }

    private synchronized void shutdownLogisticsServices() throws RemoteException {
        try {
            rmiRegistry.unbind("RmiLogisticsServices");
            log.log_Debug("RmiLogisticsServices unbound from RMI registry.");
            UnicastRemoteObject.unexportObject(rmiLogisticsServices, true);
            log.log_Debug("RmiLogisticsServices un-exported.");
        } catch (NotBoundException e) {
            log.log_Warning("An RmiLogisticsServices instance was not bound to registry.");
            try {
                UnicastRemoteObject.unexportObject(rmiLogisticsServices, true);
                log.log_Debug("RmiLogisticsServices un-exported.");
            } catch (NoSuchObjectException e1) {
                log.log_Error("Could terminate RmiLogisticsServices: not found.");
            }
        }
    }

    private synchronized void shutdownRevenueServices() throws RemoteException {
        try {
            rmiRegistry.unbind("RmiRevenueServices");
            log.log_Debug("RmiReservationServices unbound from RMI registry.");
            UnicastRemoteObject.unexportObject(rmiRevenueServices, true);
            log.log_Debug("RmiRevenueServices un-exported.");
        } catch (NotBoundException e) {
            log.log_Warning("An RmiRevenueServices instance was not bound to registry.");
            try {
                UnicastRemoteObject.unexportObject(rmiRevenueServices, true);
                log.log_Debug("RmiRevenueServices un-exported.");
            } catch (NoSuchObjectException e1) {
                log.log_Error("Could terminate RmiRevenueServices: not found.");
            }
        }
    }

    /**
     * Constructor
     *
     * @throws FailedServerInit when server instantiation fails
     */
    private RmiServer() throws FailedServerInit {
        super();
    }

    /**
     * Starts the remote services
     *
     * @throws RemoteException       when issues occurred during network communication
     */
    public synchronized void init() throws RemoteException {
        Logger log = Logger.getLoggerInstance(RmiServer.class.getName());
        rmiRegistry = LocateRegistry.createRegistry(port);
        rmiServices = (IRmiServices) UnicastRemoteObject.exportObject(this, port);
        log.log("Loading RMI services on port: ", port);
        rmiRegistry.rebind("RmiServices", rmiServices);
        log.log("Loading component services...");
        loadAdministrationServices();
        loadReservationServices();
        loadLogisticsServices();
        loadRevenueServices();
    }

    /**
     * Shuts down the remote services
     *
     * @return Success
     */
    public synchronized boolean shutdown() {
        Logger log = Logger.getLoggerInstance(RmiServer.class.getName());
        try {
            log.log("Terminating server...");
            shutdownRevenueServices();
            shutdownLogisticsServices();
            shutdownReservationServices();
            shutdownAdministrationServices();

            rmiRegistry.unbind("RmiServices");
            log.log_Debug("RmiServices unbound from RMI registry.");
            UnicastRemoteObject.unexportObject(rmiRegistry, true);
            log.log_Debug("RMI registry un-exported.");

            int count;
            if ((count = sessions.countObservers()) > 0) {
                sessions.deleteClients();
                log.log_Debug("Cleared ", count, " clients from RmiServices session tracker.");
            }
            return true;
        } catch (RemoteException e) {
            log.log_Error("Issue encountered on network communication.");
            log.log_Exception(e);
            return false;
        } catch (NotBoundException e) {
            log.log_Warning("An RMI service instance was not bound to registry.");
            try {
                UnicastRemoteObject.unexportObject(this, true);
                log.log_Debug("RmiServices un-exported.");
            } catch (NoSuchObjectException e1) {
                log.log_Error("Could terminate RmiServices: not found.");
            }
            try {
                UnicastRemoteObject.unexportObject(rmiRegistry, true);
                log.log_Debug("RMI registry un-exported.");
            } catch (NoSuchObjectException e1) {
                log.log_Error("Could terminate RmiRegistry: not found.");
            }
            return true;
        }
    }

    /**
     * Sets the port for the server
     *
     * @param port Port to use
     */
    public void setPort(Integer port) {
        this.port = port;
    }


    //TODO do regular maintenance on expired tokens/client sessions
    //TODO (combined with above) add heartbeat on both client and server to check live status?
    public static void main(String argv[]) {
        Logger log = Logger.getLoggerInstance(RmiServer.class.getName());
        //CLI parser options
        Options options = new Options();
        options.addOption(Option.builder("p")
                .longOpt("port")
                .desc("port number to use for server")
                .hasArg()
                .argName("PORT")
                .type(Integer.class)
                .build()
        );
        options.addOption(Option.builder("h")
                .longOpt("help")
                .build()
        );
        CommandLineParser parser = new DefaultParser();

        try {
            RmiServer server = new RmiServer();

            CommandLine cmd = parser.parse(options, argv);
            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("backend", options);
                System.exit(0);
            }
            if (cmd.hasOption("port"))
                server.setPort(Integer.parseInt(cmd.getOptionValue("port")));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (server.shutdown()) {
                    try {
                        mainThread.join();
                        System.out.println("Server shutdown successfully.");
                    } catch (InterruptedException e) {
                        log.log_Warning("Killing main running thread...");
                        log.log_Exception(e);
                    }
                }
            }));

            log.log("Server working directory: ", System.getProperty("user.dir"));
            server.init();
            log.log("mtRooms server init successful.");
            System.out.println("mtRooms server ready... Press Ctrl+C to terminate.");

        } catch (ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        } catch (RemoteException e) {
            log.log_Fatal("A problem occurred during RMI setup for the server.");
            log.log_Exception(e);
            e.printStackTrace();
        } catch (FailedServerInit e) {
            log.log_Fatal("Could not fully instantiate a new RmiServer.");
            log.log_Exception(e);
            e.printStackTrace();
        }
    }
}
