package jp.co.worksap.roster.rest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import org.glassfish.api.Param;

import jp.co.worksap.roster.ejb.BranchEJB;
import jp.co.worksap.roster.ejb.CustomerEJB;
import jp.co.worksap.roster.ejb.InventoryEJB;
import jp.co.worksap.roster.ejb.ReservationEJB;
import jp.co.worksap.roster.ejb.UserAgendaEJB;
import jp.co.worksap.roster.ejb.UserEJB;
import jp.co.worksap.roster.entity.BabySeatInventory;
import jp.co.worksap.roster.entity.Branch;
import jp.co.worksap.roster.entity.CarInventory;
import jp.co.worksap.roster.entity.Customer;
import jp.co.worksap.roster.entity.GpsInventory;
import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.entity.InventoryStatus;
import jp.co.worksap.roster.entity.Reservation;
import jp.co.worksap.roster.entity.ReservationStatus;
import jp.co.worksap.roster.entity.User;
import jp.co.worksap.roster.entity.UserAgenda;
import jp.co.worksap.roster.entity.UserRole;
import jp.co.worksap.roster.rest.modelview.ReservationInfo;
import jp.co.worksap.roster.rest.modelview.ReservationUpdateData;
import jp.co.worksap.roster.utilities.EmailServices;

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
	@Path("/")
	public List<Reservation> index(@Context SecurityContext context) {
		String userId = context.getUserPrincipal().getName();
		return reservationEJB.findReservations(userId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{branchId}")
	public List<Reservation> index(@PathParam("branchId") int branchId,
			@QueryParam("startTime") long startTime, @DefaultValue("0") @QueryParam("endTime") long endTime) {
		if (endTime == 0) {
			System.out.println("With out endtime");
			return reservationEJB.findReservations(branchId, timestampToDate(startTime));
		} else {
			System.out.println("With endtime");
			return reservationEJB.findReservations(branchId, timestampToDate(startTime), timestampToDate(endTime));
		}
	}

	private Date timestampToDate(long timestamp) {
		return new Date(new Timestamp(timestamp).getTime());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Reservation create(ReservationInfo reservationInfo, @Context SecurityContext context, @Context HttpServletRequest request) {
		//recheck inventory availability
		String userId;
		if (reservationInfo.getReservedForUserId() != null) {
			userId = reservationInfo.getReservedForUserId();
		} else {
			userId = context.getUserPrincipal().getName();
		}

		long timestamp = (new Date()).getTime();

		Customer customer = null;
		try {
			customer = customerEJB.findCustomerByUserId(userId);
		} catch (Exception ex) {
			throw new WebServiceException("This user is not a customer");
		}
		Reservation firstCreatedReservation = null;

		boolean driverAssigned = false;
		User assignedDriver = null;
		BigDecimal driverFee = null;

		Branch branch = null;

		Set<Integer> reservedInventories = inventoryEJB.findReservedInventories(reservationInfo.getBranchId(), timestampToDate(reservationInfo.getStartTime().getTime()), timestampToDate(reservationInfo.getEndTime().getTime()), -1);

		BigDecimal totalFee = new BigDecimal(0);
		List<Reservation> allReservations = new LinkedList<Reservation>();

		for (int inventoryId : reservationInfo.getInventoryIds()) {
			Reservation reservation = new Reservation();
			reservation.setGroupId(timestamp);
			reservation.setCardCIV(reservationInfo.getCardCIV());
			reservation.setCardExpiryDate(reservationInfo.getCardExpiryDate());
			reservation.setCardName(reservationInfo.getCardName());
			reservation.setCardNumber(reservationInfo.getCardNumber());

			Inventory inventory = inventoryEJB.findInventory(inventoryId);
			if (reservedInventories.contains(inventory.getId())) {
				throw new WebServiceException("Some of your reservation items are already reserved by someone else. Please go back to the home page and make reservation again");
			}

			reservation.setInventory(inventory);
			reservation.setInventoryVersion(inventory.getVersion());
			reservation.setStartTime(reservationInfo.getStartTime());
			reservation.setEndTime(reservationInfo.getEndTime());
			reservation.setCustomer(customer);

			reservation.setInventoryFee(inventory.getPrice().multiply(new BigDecimal((reservationInfo.getEndTime().getTime() - reservationInfo.getStartTime().getTime() + 1) / (60.0 * 60 * 1000))));
			totalFee = totalFee.add(reservation.getInventoryFee());

			if (!reservationInfo.isCardPayment()) {
				reservation.setCardCIV(" ");
				reservation.setCardName(" ");
				reservation.setCardNumber(" ");
			}

			if (!driverAssigned && reservationInfo.isDriverRequired()) {
				List<String> busyEmployeeIds = userAgendaEJB.findReservedUsersByDate(inventory.getOwner().getId(), reservation.getStartTime(), reservation.getEndTime());
				branch = branchEJB.findBranch(inventory.getOwner().getId());

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
							ua.setTitle(String.valueOf(branch.getId()) + "-" + String.valueOf(reservation.getGroupId() + "-" + customer.getUser().getFirstName() + " " + customer.getUser().getLastName()));
							ua.setUser(employee);
							ua.setTimezone(branch.getTimezone());
							userAgendaEJB.createUserAgenda(ua);

							driverFee = branch.getDriverFee().multiply(new BigDecimal((reservationInfo.getEndTime().getTime() - reservationInfo.getStartTime().getTime() + 1) / (24.0 * 60 * 60 * 1000)));

							reservation.setAssignedDriver(employee);
							reservation.setDriverFee(driverFee);
							assignedDriver = employee;

							driverAssigned = true;
							break;
						}
					}
				}
			} else if (driverAssigned) {
				reservation.setAssignedDriver(assignedDriver);
				reservation.setDriverFee(driverFee);
			}

			reservationEJB.createReservation(reservation);
			if (firstCreatedReservation == null) firstCreatedReservation = reservation;

			allReservations.add(reservation);
		}

		if (!driverAssigned && reservationInfo.isDriverRequired()) {
			throw new WebServiceException("No driver is available");
		}

		if (reservationInfo.isCardPayment()) {
			totalFee = totalFee.add(driverFee);
			reservationEJB.updatePaidAmount(allReservations, totalFee);
			reservationEJB.markAsFullyPaid(allReservations);
		}

		EmailServices.sendEmail(customer.getUser().getEmail(), "CRM+ Booking confirmation",
				"<h2>Your invoice</h2>" +
				"<p>Thank you for your reservation. Your reservation id is: " + timestamp + "</p>" +
				"<p>You could check your reservations detail here: <a href='http://localhost:9090/CarRentalManagement/customers/check-reservation.jsf'>here</a></p>");

		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
	    String basePath = request.getContextPath();

		if (assignedDriver != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(branch.getTimezone()));

			EmailServices.sendEmail(assignedDriver.getEmail(), "New driving service request",
				"<p>" +
				"<b>Customer name: </b>" +
				customer.getUser().getFirstName() + " " + customer.getUser().getLastName() + "</td>" +
				"<br/>" +
				"<b>From: </b>" +
				sdf.format(reservationInfo.getStartTime()) +
				"<br/>" +
				"<b>To: </b>" +
				sdf.format(reservationInfo.getEndTime()) +
				"<br/>" +
				"<p>For detail information " +
				"<a href='" + baseUrl + basePath + "/reservations/index.jsf#/" + String.valueOf(branch.getId()) + "/reservations/" + timestamp + "'>click here</a>" +
				"<br>" +
				"To check your agenda " +
				"<a href='" + baseUrl + basePath + "/users/agenda.jsf?userId=" + assignedDriver.getId() + "'>click here</a>" +
				"</p>");
		}
		return firstCreatedReservation;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(ReservationUpdateData data, @Context HttpServletRequest request) {
		long groupId = data.getGroupId();
		String operation = data.getOperation();

		List<Reservation> reservations = reservationEJB.findReservations(groupId);

		if (operation.equals("startRental")) {
			if (reservations.get(0).getStatus() != ReservationStatus.SCHEDULED) {
				throw new WebServiceException("The status of this reservation has been modified & refreshed. Please check again");
			}
			if (!reservations.get(0).isFullyPaid()) {
				throw new WebServiceException("Please make the payment first before starting the rental");
			}
			if (reservationEJB.isEligibleForRent(reservations)) {
				reservationEJB.updateInventories(reservations, InventoryStatus.RENTED);
				reservationEJB.updateStatus(reservations, ReservationStatus.STARTED);
			} else {
	            throw new WebServiceException("You can't start renting on this reservation. Please make sure all inventories reserved are available");
			}
		} else if (operation.equals("finishRental")) {
			if (reservations.get(0).getStatus() != ReservationStatus.STARTED) {
				throw new WebServiceException("The status of this reservation has been modified & refreshed. Please check again");
			}
			reservationEJB.updateInventories(reservations, InventoryStatus.RETURNED);
			reservationEJB.updateStatus(reservations, ReservationStatus.FINISHED);

			Inventory inventory = reservations.get(0).getInventory();
			List<String> busyEmployeeIds = userAgendaEJB.findReservedUsersByDate(inventory.getOwner().getId(), reservations.get(0).getStartTime(), reservations.get(0).getEndTime());
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

					if (rolesStr.contains("technician")) {
						//found available driver

						SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
						sdf.setTimeZone(TimeZone.getTimeZone(branch.getTimezone()));

						String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
					    String basePath = request.getContextPath();

						StringBuffer reservationsSummary = new StringBuffer();
						reservationsSummary.append("<ol>");
						for (Reservation r:reservations) {
							String inventoryName = r.getInventory().getName();
							String entityType = "";

							reservationsSummary.append("<li>");
							if (r.getInventory() instanceof CarInventory) {
								entityType = "car";
							} else if (r.getInventory() instanceof BabySeatInventory) {
								entityType = "baby_seat";
							} else if (r.getInventory() instanceof GpsInventory) {
								entityType = "gps";
							}
							reservationsSummary.append("<a href='" + baseUrl + basePath + "/inventories/index.jsf#/" + branch.getId() +
									"/inventories/" + r.getInventory().getId() + "/detail?entity=" + entityType + "'>" + inventoryName + "</a>");
							reservationsSummary.append("</li>");
						}
						reservationsSummary.append("</ol>");

						EmailServices.sendEmail(employee.getEmail(), "New technician service request",
							"<p>" + reservationsSummary.toString() + "</p>");
					}
				}
			}


		} else if (operation.equals("cancelRental")) {
			if (reservations.get(0).getStatus() != ReservationStatus.SCHEDULED) {
				throw new WebServiceException("The status of this reservation has been modified & refreshed. Please check again");
			}
			reservationEJB.updateInventories(reservations, InventoryStatus.AVAILABLE);
			reservationEJB.updateStatus(reservations, ReservationStatus.CANCELED);
			userAgendaEJB.deleteUserAgendaByTitle(String.valueOf(data.getBranchId()) + "-" + String.valueOf(data.getGroupId()) + "-" + reservations.get(0).getCustomer().getUser().getFirstName() + " " + reservations.get(0).getCustomer().getUser().getLastName());
		} else if (operation.equals("markFullyPaid")) {
			if (reservations.get(0).isFullyPaid() ||  (reservations.get(0).getStatus() == ReservationStatus.CANCELED)) {
				throw new WebServiceException("The status of this reservation has been modified & refreshed. Please check again");
			}
			reservationEJB.markAsFullyPaid(reservations);
		} else if (operation.equals("finishChecking")) {
			if (reservations.get(0).getStatus() != ReservationStatus.FINISHED) {
				throw new WebServiceException("The status of this reservation has been modified & refreshed. Please check again");
			}
			reservationEJB.updateInventories(reservations, InventoryStatus.AVAILABLE);
		} else if (operation.startsWith("updatePayment")) {
			BigDecimal paidAmount = new BigDecimal("0");
			BigDecimal penaltyFee = new BigDecimal("0");

			try {
				paidAmount = new BigDecimal(operation.split("_")[1]);
				penaltyFee = new BigDecimal(operation.split("_")[2]);
			} catch (Exception ex) {
				throw new WebServiceException("Invalid amount");
			}
			reservationEJB.markAsFullyUnpaid(reservations);
			reservationEJB.updatePaidAmount(reservations, paidAmount);
			reservationEJB.updatePenaltyFee(reservations, penaltyFee);
		} else if (operation.startsWith("addReservation")) {
			int inventoryId = -1;
			try {
				inventoryId = Integer.valueOf(operation.split("_")[1]);
			} catch (Exception ex) {
				throw new WebServiceException("Invalid operation");
			}
			Inventory inventory = inventoryEJB.findInventory(inventoryId);

			if (!reservationEJB.addReservation(groupId, inventory)) {
				throw new WebServiceException("The inventory is already reserved by someone");
			}
			reservationEJB.markAsFullyUnpaid(reservations);
		}
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response destroy(@QueryParam("id") int id) {
		Reservation reservation = reservationEJB.findReservation(id);
		List<Reservation> reservations = reservationEJB.findReservations(reservation.getGroupId());

		int removedIndex = -1;

		int i = 0;
		for (Reservation curReservation:reservations) {
			if (curReservation.getId() == reservation.getId()) {
				removedIndex = i;
			}
			i++;
		}
		reservations.remove(removedIndex);

		reservationEJB.delete(reservation);
		reservationEJB.markAsFullyUnpaid(reservations);
		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}
}
