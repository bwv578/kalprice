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
	private double priceAvg;
	private double fluc;
	private double priceSeoul;
	private double priceBusan;
	private double priceDaegu;
	private double priceGwangju;
	private double priceDaejeon;
	
	public Food(String name, String itemClass, String unit) {
		this.name = name;
		this.itemClass = itemClass;
		this.unit = unit;
	}
	
}
