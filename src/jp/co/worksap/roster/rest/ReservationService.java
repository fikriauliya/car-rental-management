package jp.co.worksap.roster.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.xml.ws.WebServiceException;

import jp.co.worksap.roster.ejb.BranchEJB;
import jp.co.worksap.roster.ejb.CustomerEJB;
import jp.co.worksap.roster.ejb.InventoryEJB;
import jp.co.worksap.roster.ejb.ReservationEJB;
import jp.co.worksap.roster.ejb.UserAgendaEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.Branch;
import jp.co.worksap.roster.entity.Customer;
import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.InventoryStatus;
import jp.co.worksap.roster.entity.Reservation;
import jp.co.worksap.roster.entity.ReservationStatus;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.entity.UserAgenda;
import jp.co.worksap.roster.entity.UserRole;
import jp.co.worksap.roster.rest.modelview.ReservationInfo;
import jp.co.worksap.roster.rest.modelview.ReservationUpdateData;

@Stateless
@Path("/reservations/")
public class ReservationService {
	@EJB
	InventoryEJB inventoryEJB;

	@EJB
	BranchEJB branchEJB;

	@EJB
	ReservationEJB reservationEJB;

	@EJB
	CustomerEJB customerEJB;

	@EJB
	UserAgendaEJB userAgendaEJB;

	@EJB
	UserEJB userEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{branchId}/{groupId}")
	public List<Reservation> show(@PathParam("branchId") int branchId, @PathParam("groupId") long groupId) {
		System.out.println("show");
		return reservationEJB.findReservations(groupId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{branchId}")
	public List<Reservation> index(@PathParam("branchId") int branchId,
			@QueryParam("startTime") long startTime, @QueryParam("endTime") long endTime) {
		return reservationEJB.findReservations(branchId, timestampToDate(startTime), timestampToDate(endTime));
	}

	private Date timestampToDate(long timestamp) {
		return new Date(new Timestamp(timestamp).getTime());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(ReservationInfo reservationInfo, @Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		long timestamp = (new Date()).getTime();
		Customer customer = customerEJB.findCustomerByUserId(userId);

		boolean driverAssigned = false;

		for (int inventoryId : reservationInfo.getInventoryIds()) {
			Reservation reservation = new Reservation();
			reservation.setGroupId(timestamp);
			reservation.setCardCIV(reservationInfo.getCardCIV());
			reservation.setCardExpiryDate(reservationInfo.getCardExpiryDate());
			reservation.setCardName(reservationInfo.getCardName());
			reservation.setCardNumber(reservationInfo.getCardNumber());
			reservation.setCustomer(reservation.getCustomer());

			Inventory inventory = inventoryEJB.findInventory(inventoryId);
			reservation.setInventory(inventory);
			reservation.setInventoryVersion(inventory.getVersion());
			reservation.setStartTime(reservationInfo.getStartTime());
			reservation.setEndTime(reservationInfo.getEndTime());
			reservation.setCustomer(customer);
			reservation.setPaid(reservationInfo.isCardPayment());

			if (!reservationInfo.isCardPayment()) {
				reservation.setCardCIV(" ");
				reservation.setCardName(" ");
				reservation.setCardNumber(" ");
			}

			if (!driverAssigned && reservationInfo.isDriverRequired()) {
				List<String> busyEmployeeIds = userAgendaEJB.findReservedUsersByDate(inventory.getOwner().getId(), reservation.getStartTime(), reservation.getEndTime());
				Branch branch = branchEJB.findBranch(inventory.getOwner().getId());
				List<User> allEmployees = branch.getUsers();

				for (User employee:allEmployees) {
					if (!busyEmployeeIds.contains(employee.getId())) {
						List<UserRole> roles = userEJB.findAllAssignedRoles(employee.getId());
						List<String> rolesStr = new LinkedList<String>();
						for (UserRole role:roles) {
							rolesStr.add(role.getRoleName());
							System.out.println(role.getRoleName());
						}

						if (rolesStr.contains("driver")) {
							//found available driver
							UserAgenda ua = new UserAgenda();
							ua.setAssignedBy(employee);
							ua.setStartTime(reservationInfo.getStartTime());
							ua.setEndTime(reservationInfo.getEndTime());
							ua.setTitle(String.valueOf(branch.getId()) + "-" + String.valueOf(reservation.getGroupId()));
							ua.setUser(employee);
							ua.setTimezone(branch.getTimezone());
							userAgendaEJB.createUserAgenda(ua);

							reservation.setAssignedDriver(employee);
							reservation.setDriverFee(branch.getDriverFee());

							driverAssigned = true;
							break;
						}
					}
				}
			}

			reservationEJB.createReservation(reservation);
		}

		if (!driverAssigned && reservationInfo.isDriverRequired()) {
			throw new WebServiceException("No driver is available");
		}

		return Response.status(Status.CREATED).type(MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(ReservationUpdateData data) {
		long groupId = data.getGroupId();
		String operation = data.getOperation();

		List<Reservation> reservations = reservationEJB.findReservations(groupId);

		if (operation.equals("startRental")) {
			if (reservationEJB.isEligibleForRent(reservations)) {
				reservationEJB.updateInventories(reservations, InventoryStatus.RENTED);
				reservationEJB.updateStatus(reservations, ReservationStatus.STARTED);
			} else {
				return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
			}
		} else if (operation.equals("finishRental")) {
			reservationEJB.updateInventories(reservations, InventoryStatus.AVAILABLE);
			reservationEJB.updateStatus(reservations, ReservationStatus.FINISHED);
		} else if (operation.equals("cancelRental")) {
			reservationEJB.updateInventories(reservations, InventoryStatus.AVAILABLE);
			reservationEJB.updateStatus(reservations, ReservationStatus.CANCELED);
		} else if (operation.equals("markPaid")) {
			reservationEJB.markAsPaid(reservations);
		}

		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}
}
