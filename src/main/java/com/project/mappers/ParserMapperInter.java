package com.project.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.project.models.Food;

public interface ParserMapperInter {
	
	// 음식정보 존재여부 확인
	@Select("select count(*) from food where name=#{foodName} and class=#{foodClass}")
	public abstract int doesFoodExist(Food food);
	
	// 음식 항목의 ID 검색
	@Select("select id from food where name=#{foodName} and class=#{foodClass}")
	public abstract String searchFoodId(Food food); 
	
	// 새로운 음식항목 추가 
	@Insert("insert into food values(0, #{foodName}, #{foodClass}, #{foodUnit})")
	public abstract int addFood(Food food);
	
	// 특정 항목과 동일한 분류 및 이름, 날짜를 가진 항목의 물가정보가 존재하는지 확인
	@Select("select count(*) from price where id=#{id} and date=#{date}")
	public abstract int doesPriceExist(String id, String date);
	
	// 물가정보 추가
	@Insert("insert into price values("
			+ "now(), #{foodId}, #{fluc}, #{seoulPrice}, "
			+ "#{busanPrice}, #{daeguPrice}, #{gwangjuPrice}, "
			+ "#{daejeonPrice}, #{avg})")
	public abstract int addPrice(Food food);

	// 영양정보 추가
	@Insert("insert into nutrient values(#{foodId}, #{calorie}, #{carbohydrate}, #{protein}, #{fat})")
	public abstract int addNutrition(Food food);
	
	// 누락된 영양정보 수동 추가
	@Update("update nutrient set kcal=#{calorie}, carbohydrate=#{carbohydrate}, "
			+ "protein=#{protein}, fat=#{fat} where id=#{foodId}")
	public abstract int addNutritionManual(Food food);
}
