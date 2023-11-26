package com.project.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Food {

	private String foodId = null;
	private String foodName = null;
    private String foodUnit = null;
    private String foodClass = null;
    
    private Object seoulPrice = null;
    private Object busanPrice = null;
    private Object daeguPrice = null;
    private Object daejeonPrice = null;
    private Object gwangjuPrice = null;
    private Object avg = 0;
    private Object fluc = null;
    
    private Object calorie;
    private Object carbohydrate;
    private Object protein;
    private Object fat;
    
	public Food(String name, String foodClass, String unit) {
		this.foodName = name;
		this.foodClass = foodClass;
		this.foodUnit = unit;
	}
	
}
