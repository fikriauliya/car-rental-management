package jp.co.worksap.roster.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="T_RESERVATION")
public class Reservation {
	@GeneratedValue @Id
	private int id;

	private Customer customer;

	private Inventory inventory;

	private Timestamp inventoryVersion;

	private String cardNumber;

	private String cardCIV;

	@Temporal(TemporalType.TIMESTAMP)
	private Date cardExpiryDate;
}
