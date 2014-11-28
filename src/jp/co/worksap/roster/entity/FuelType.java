package jp.co.worksap.roster.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum FuelType {
	COMPRESSED_NATURAL_GAS, DIESEL, ALL_ELECTRIC, FLEX_FUEL, HYBRID, PLUG_IN_HYBRID
}
