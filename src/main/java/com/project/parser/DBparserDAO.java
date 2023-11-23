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
	
	// DB에 새로운 음식정보 추가
	public int addFood(Food food) {
		int addFood = mapper.addFood(food);
		//int addPrice = mapper.addPrice(food);
		
		int result = 0;
		if(addFood == 1) result = 1;
		
		return result;
	}
	
}
