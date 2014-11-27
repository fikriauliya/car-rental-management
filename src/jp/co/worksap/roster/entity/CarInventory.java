package jp.co.worksap.roster.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@DiscriminatorValue("C")
@Entity
public class CarInventory extends Inventory {
	private int length;
	private int width;
	private int height;
	private int weight;
	private int fuelCapacity;
	private int yearOfProduction;
	private String transmission;
	private float maxSpeed;
	private float zeroToHundredKmPerHrTime;
	private float power;
	private int wheelBase;
	private int numOfSeat;
	private int numOfDoor;
	private boolean isRightSideDriver;
	private FuelType fuelType;

	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getFuelCapacity() {
		return fuelCapacity;
	}
	public void setFuelCapacity(int fuelCapacity) {
		this.fuelCapacity = fuelCapacity;
	}
	public int getYearOfProduction() {
		return yearOfProduction;
	}
	public void setYearOfProduction(int yearOfProduction) {
		this.yearOfProduction = yearOfProduction;
	}
	public String getTransmission() {
		return transmission;
	}
	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}
	public float getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public float getZeroToHundredKmPerHrTime() {
		return zeroToHundredKmPerHrTime;
	}
	public void setZeroToHundredKmPerHrTime(float zeroToHundredKmPerHrTime) {
		this.zeroToHundredKmPerHrTime = zeroToHundredKmPerHrTime;
	}
	public float getPower() {
		return power;
	}
	public void setPower(float power) {
		this.power = power;
	}
	public int getWheelBase() {
		return wheelBase;
	}
	public void setWheelBase(int wheelBase) {
		this.wheelBase = wheelBase;
	}
	public int getNumOfSeat() {
		return numOfSeat;
	}
	public void setNumOfSeat(int numOfSeat) {
		this.numOfSeat = numOfSeat;
	}
	public int getNumOfDoor() {
		return numOfDoor;
	}
	public void setNumOfDoor(int numOfDoor) {
		this.numOfDoor = numOfDoor;
	}
	public boolean isRightSideDriver() {
		return isRightSideDriver;
	}
	public void setRightSideDriver(boolean isRightSideDriver) {
		this.isRightSideDriver = isRightSideDriver;
	}
	public FuelType getFuelType() {
		return fuelType;
	}
	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}
}

