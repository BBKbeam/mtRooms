package bbk_beam.mtRooms.network;

import bbk_beam.mtRooms.admin.authentication.Token;
import bbk_beam.mtRooms.network.exception.Unauthorised;
import bbk_beam.mtRooms.operation.dto.LogisticsInfo;
import bbk_beam.mtRooms.reservation.dto.Building;
import bbk_beam.mtRooms.reservation.dto.Floor;
import bbk_beam.mtRooms.reservation.dto.Room;
import bbk_beam.mtRooms.reservation.exception.FailedDbFetch;
import bbk_beam.mtRooms.revenue.exception.InvalidPeriodException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface IRmiLogisticsServices extends Remote {
    /**
     * Gets all buildings in real estate portfolio
     *
     * @param session_token Administration session token
     * @return List of Building DTOs
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<Building> getBuildings(Token session_token) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets all floors in a building
     *
     * @param session_token Administration session token
     * @param building      Building DTO
     * @return List of Floor DTOs
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<Floor> getFloors(Token session_token, Building building) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets all rooms ina floor
     *
     * @param session_token Administration session token
     * @param floor         Floor DTO
     * @return List of Room DTOs
     * @throws FailedDbFetch   when error occurred during fetching of data from DB
     * @throws Unauthorised    when client is not authorised to access the resource
     * @throws RemoteException when network issues occur during the remote call
     */
    public List<Room> getRooms(Token session_token, Floor floor) throws FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    LogisticsInfo getInfo(Token session_token, Integer building_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param building_id   ID of the building
     * @param floor_id      ID of the floor
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    LogisticsInfo getInfo(Token session_token, Integer building_id, Integer floor_id, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;

    /**
     * Gets logistical information
     *
     * @param session_token Session token
     * @param room          Room DTO
     * @param from          From date
     * @param to            To date
     * @return Logistics information report
     * @throws InvalidPeriodException when the date period is not valid
     * @throws FailedDbFetch          when an error occurred getting the record
     * @throws Unauthorised           when client is not authorised to access the resource
     * @throws RemoteException        when network issues occur during the remote call
     */
    LogisticsInfo getInfo(Token session_token, Room room, Date from, Date to) throws InvalidPeriodException, FailedDbFetch, Unauthorised, RemoteException;
}
