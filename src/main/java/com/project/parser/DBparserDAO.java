package com.project.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.mappers.ParserMapperInter;
import com.project.models.Food;

@Repository
public class DBparserDAO {
	
	@Autowired
	private ParserMapperInter mapper;
	
	// 음식정보가 DB에 존재하는지 여부 확인
	public int doesFoodExist(Food food) {
		int existence  = mapper.doesFoodExist(food);
		int result = 0;
		if(existence != 0) result = 1;
		
		return result;
	}
	
	// DB에서 특정 음식의 id 검색
	public String searchFoodId(Food food) {
		String id = mapper.searchFoodId(food);
		return id;
	}
	
	// DB에 새로운 음식정보 추가
	public int addFood(Food food) {
		int result = mapper.addFood(food);
		return result;
	}
	
	// 해당 항목과 동일한 분류 및 이름, 날짜를 가진 항목의 물가정보가 존재하는지 확인
	public int doesPriceExist(String id, String date) {
		int result = mapper.doesPriceExist(id, date);
		return result;
	}
	
	// DB에 새로운 음식 물가정보 추가
	public int addPrice(Food food) {
		int result = mapper.addPrice(food);
		return result;
	}
	
	// DB에 새로운 음식 영양정보 추가
	public int addNutrition(Food food) {
		int result = mapper.addNutrition(food);
		return result;
	}
	
	// DB에 누락된 음식 영양정보 수동으로 추가
	public int addNutritionManual(Food food) {
		int result = mapper.addNutritionManual(food);
		return result;
	}
}
