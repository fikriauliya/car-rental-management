package jp.co.worksap.roster.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum InventoryStatus {
	AVAILABLE, RENTED, BROKEN, DELETED, RETURNED
}
