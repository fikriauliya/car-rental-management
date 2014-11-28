package jp.co.worksap.roster.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@DiscriminatorValue("A")
@Entity
@Table(name="T_ADD_ON_INVENTORY")
public class AddOnInventory extends Inventory {

}
