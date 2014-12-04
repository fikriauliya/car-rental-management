package jp.co.worksap.roster.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

@XmlRootElement
@Entity
@Table(name="T_INVENTORY")
@NamedQueries({
	@NamedQuery(name="findInventory", query="SELECT u FROM Inventory u WHERE u.id = :id"),
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Inventory {
	@Id @GeneratedValue
	private int id;

	@NotEmpty
	@Column(nullable=false)
	private String name;

	@NotNull
	@OneToOne
	private Branch owner;

	@NotNull
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@NotNull
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Version
	private Timestamp version;

	@NotNull
	@Column(nullable=false)
	private InventoryStatus status;

	@Min(value=0)
	private BigDecimal price;

	private int primaryImageId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Branch getOwner() {
		return owner;
	}

	public void setOwner(Branch owner) {
		this.owner = owner;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public InventoryStatus getStatus() {
		return status;
	}

	public void setStatus(InventoryStatus status) {
		this.status = status;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getPrimaryImageId() {
		return primaryImageId;
	}

	public void setPrimaryImageId(int primaryImageId) {
		this.primaryImageId = primaryImageId;
	}

	public static enum InventoryType {
		CAR, BABY_SEAT, GPS
	}
}
