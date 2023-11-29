package com.project.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.models.Food;

@Component
public class Nut {
	
	@Autowired
	private DBparserDAO dao;
	private String data = "6	56	14.1	0.2	0.7	\r\n"
			+ "7	18	4	0.9	0.2\r\n"
			+ "22	86	20.1	1.6	0.1\r\n"
			+ "42	197	19.4	7.4	10\r\n"
			+ "56	0	0	0	0";

	public void insertDB() {
		String[] rows = data.split("\n");
		
		for(int i=0; i<rows.length; i++) {
			String[] cols = rows[i].split("\t");
			Food food = new Food(null, null, null);
			
			food.setFoodId(cols[0]);
			food.setCalorie(cols[1]);
			food.setCarbohydrate(cols[2]);
			food.setProtein(cols[3]);
			food.setFat(cols[4]);
			
			dao.addNutritionManual(food);
		}
	}
}
