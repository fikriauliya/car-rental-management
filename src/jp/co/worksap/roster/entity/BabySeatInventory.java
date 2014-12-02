package jp.co.worksap.roster.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import jp.co.worksap.roster.entity.validator.FieldLessThan;

@DiscriminatorValue("B")
@Entity
@Table(name="T_BABY_SEAT_INVENTORY")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name="findBabySeatInventories", query="SELECT u FROM BabySeatInventory u WHERE u.owner.id = :ownerId AND u.status <> jp.co.worksap.roster.entity.InventoryStatus.DELETED ORDER BY u.name")
})
@FieldLessThan(first="minWeight", second="maxWeight", message="Minimum weight must be less than maximum weight")
public class BabySeatInventory extends Inventory {
	@NotNull
	@Min(value=0)
	private int minWeight;

	@NotNull
	@Min(value=0)
	private int maxWeight;

	public void copyPropertiesFrom(BabySeatInventory bsi) {
		setMinWeight(bsi.getMinWeight());
		setMaxWeight(bsi.getMaxWeight());
	}

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

	@Override
	public String toString() {
		return "BabySeatInventory [minWeight=" + minWeight + ", maxWeight="
				+ maxWeight + "]";
	}
}
