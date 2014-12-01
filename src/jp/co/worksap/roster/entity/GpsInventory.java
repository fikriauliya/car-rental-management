package jp.co.worksap.roster.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

@DiscriminatorValue("G")
@Entity
@Table(name="T_GPS_INVENTORY")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name="findGpsInventories", query="SELECT u FROM GpsInventory u WHERE u.owner.id = :ownerId ORDER BY u.name")
})
public class GpsInventory extends Inventory {
	@Range(min=0, max=10000000)
	@NotNull
	private Integer numOfWayPoints;

	@NotNull
	private boolean hasTextToSpeech;

	@NotNull
	private boolean isWaasEnabled;

	@NotNull
	private boolean hasDownloadCapability;

	@NotNull
	private boolean hasBuiltInBaseMap;

	@NotNull
	private boolean hasVoicePrompts;

	@NotNull
	private boolean hasColourDisplay;

	@Range(min=0, max=1000)
	@NotNull
	private Integer screenSize;

	public void copyPropertiesFrom(GpsInventory gi) {
		setNumOfWayPoints(gi.getNumOfWayPoints());
		setHasTextToSpeech(gi.isHasTextToSpeech());
		setWaasEnabled(gi.isWaasEnabled());
		setHasDownloadCapability(gi.isHasDownloadCapability());
		setHasBuiltInBaseMap(gi.isHasBuiltInBaseMap());
		setHasVoicePrompts(gi.isHasVoicePrompts());
		setHasColourDisplay(gi.isHasColourDisplay());
		setScreenSize(gi.getScreenSize());
	}

	public Integer getNumOfWayPoints() {
		return numOfWayPoints;
	}
	public void setNumOfWayPoints(Integer numOfWayPoints) {
		this.numOfWayPoints = numOfWayPoints;
	}
	public boolean isHasTextToSpeech() {
		return hasTextToSpeech;
	}
	public void setHasTextToSpeech(boolean hasTextToSpeech) {
		this.hasTextToSpeech = hasTextToSpeech;
	}
	public boolean isHasDownloadCapability() {
		return hasDownloadCapability;
	}
	public void setHasDownloadCapability(boolean hasDownloadCapability) {
		this.hasDownloadCapability = hasDownloadCapability;
	}
	public boolean isHasBuiltInBaseMap() {
		return hasBuiltInBaseMap;
	}
	public void setHasBuiltInBaseMap(boolean hasBuiltInBaseMap) {
		this.hasBuiltInBaseMap = hasBuiltInBaseMap;
	}
	public boolean isHasVoicePrompts() {
		return hasVoicePrompts;
	}
	public void setHasVoicePrompts(boolean hasVoicePrompts) {
		this.hasVoicePrompts = hasVoicePrompts;
	}
	public boolean isHasColourDisplay() {
		return hasColourDisplay;
	}
	public void setHasColourDisplay(boolean hasColourDisplay) {
		this.hasColourDisplay = hasColourDisplay;
	}
	public Integer getScreenSize() {
		return screenSize;
	}
	public void setScreenSize(Integer screenSize) {
		this.screenSize = screenSize;
	}
	public boolean isWaasEnabled() {
		return isWaasEnabled;
	}
	public void setWaasEnabled(boolean isWaasEnabled) {
		this.isWaasEnabled = isWaasEnabled;
	}

	@Override
	public String toString() {
		return "GpsInventory [numOfWayPoints=" + numOfWayPoints
				+ ", hasTextToSpeech=" + hasTextToSpeech + ", isWaasEnabled="
				+ isWaasEnabled + ", hasDownloadCapability="
				+ hasDownloadCapability + ", hasBuiltInBaseMap="
				+ hasBuiltInBaseMap + ", hasVoicePrompts=" + hasVoicePrompts
				+ ", hasColourDisplay=" + hasColourDisplay + ", screenSize="
				+ screenSize + "]";
	}
}
