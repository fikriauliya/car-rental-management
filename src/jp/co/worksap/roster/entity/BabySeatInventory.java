package jp.co.worksap.roster.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@DiscriminatorValue("B")
@Entity
@Table(name="T_BABY_SEAT_INVENTORY")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name="findBabySeatInventories", query="SELECT u FROM BabySeatInventory u ORDER BY u.name")
})
public class BabySeatInventory extends Inventory {
	@NotNull
	private int minWeight;

	@NotNull
	private int maxWeight;

	public int getMinWeight() {
		return minWeight;
	}
	public void setMinWeight(int minWeight) {
		this.minWeight = minWeight;
	}
	public int getMaxWeight() {
		return maxWeight;
	}
	public void setMaxWeight(int maxWeight) {
		this.maxWeight = maxWeight;
	}
}
