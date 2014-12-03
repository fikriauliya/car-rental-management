package jp.co.worksap.roster.rest.modelview;

import java.util.Date;

public class ReservationInfo {
	private int[] inventoryIds;
	private String cardName;
	private String cardNumber;
	private String cardCIV;
	private Date cardExpiryDate;
	private Date startTime;
	private Date endTime;

	public int[] getInventoryIds() {
		return inventoryIds;
	}
	public void setInventoryIds(int[] inventoryIds) {
		this.inventoryIds = inventoryIds;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardCIV() {
		return cardCIV;
	}
	public void setCardCIV(String cardCIV) {
		this.cardCIV = cardCIV;
	}
	public Date getCardExpiryDate() {
		return cardExpiryDate;
	}
	public void setCardExpiryDate(Date cardExpiryDate) {
		this.cardExpiryDate = cardExpiryDate;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
