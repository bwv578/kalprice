package com.project.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Food {

	private String id; // DB상 음식ID
	private String name;
	private String itemClass;
	private String unit;
	private Object priceAvg;
	private Object fluc;
	private Object priceSeoul;
	private Object priceBusan;
	private Object priceDaegu;
	private Object priceGwangju;
	private Object priceDaejeon;
	
	public Food(String name, String itemClass, String unit) {
		this.name = name;
		this.itemClass = itemClass;
		this.unit = unit;
	}
	
}
