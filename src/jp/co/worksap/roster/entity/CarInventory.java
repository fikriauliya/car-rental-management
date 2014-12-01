package jp.co.worksap.roster.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;

@DiscriminatorValue("C")
@Entity
@Table(name="T_CAR_INVENTORY")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name="findCarInventories", query="SELECT u FROM CarInventory u WHERE u.owner.id = :ownerId ORDER BY u.name")
})
public class CarInventory extends Inventory {
	@Min(value=0)
	@Max(value=10000)
	private int length;

	@Min(value=0)
	@Max(value=10000)
	private int width;

	@Min(value=0)
	@Max(value=10000)
	private int height;

	@Min(value=0)
	@Max(value=10000)
	private int weight;

	@Min(value=0)
	@Max(value=10000)
	private int fuelCapacity;

	@Min(value=0)
	@Max(value=3000)
	private int yearOfProduction;

	@Length(max=100)
	private String transmission;

	@Min(value=0)
	@Max(value=10000)
	private float maxSpeed;

	@Min(value=0)
	@Max(value=10000)
	private float zeroToHundredKmPerHrTime;

	@Min(value=0)
	@Max(value=10000)
	private float power;

	@Min(value=0)
	@Max(value=10000)
	private int wheelBase;

	@Min(value=0)
	@Max(value=20)
	private int numOfSeat;

	@Min(value=0)
	@Max(value=20)
	private int numOfDoor;

	@NotNull
	private boolean isRightSideDriver;

	@NotNull
	private FuelType fuelType;

	public void copyPropertiesFrom(CarInventory ci) {
		setLength(ci.getLength());
		setWidth(ci.getWidth());
		setHeight(ci.getHeight());
		setWeight(ci.getWeight());
		setFuelCapacity(ci.getFuelCapacity());
		setYearOfProduction(ci.getYearOfProduction());
		setTransmission(ci.getTransmission());
		setMaxSpeed(ci.getMaxSpeed());
		setZeroToHundredKmPerHrTime(ci.getZeroToHundredKmPerHrTime());
		setPower(ci.getPower());
		setWheelBase(ci.getWheelBase());
		setNumOfSeat(ci.getNumOfSeat());
		setNumOfDoor(ci.getNumOfDoor());
		setRightSideDriver(ci.isRightSideDriver());
		setFuelType(ci.getFuelType());
	}

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
	@Override
	public String toString() {
		return "CarInventory [length=" + length + ", width=" + width
				+ ", height=" + height + ", weight=" + weight
				+ ", fuelCapacity=" + fuelCapacity + ", yearOfProduction="
				+ yearOfProduction + ", transmission=" + transmission
				+ ", maxSpeed=" + maxSpeed + ", zeroToHundredKmPerHrTime="
				+ zeroToHundredKmPerHrTime + ", power=" + power
				+ ", wheelBase=" + wheelBase + ", numOfSeat=" + numOfSeat
				+ ", numOfDoor=" + numOfDoor + ", isRightSideDriver="
				+ isRightSideDriver + ", fuelType=" + fuelType + "]";
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

