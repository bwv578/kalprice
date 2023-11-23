package com.project.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.project.models.Food;

public interface ParserMapperInter {
	
	// 음식정보 존재여부 확인
	@Select("select count(*) from food where name=#{name} and class=#{itemClass}")
	public abstract int doesFoodExist(Food food);
	
	// 음식 항목의 ID 검색
	@Select("select id from food where name=#{name} and class=#{itemClass}")
	public abstract String searchFoodId(Food food); 
	
	// 새로운 음식항목 추가 
	@Insert("insert into food values(0, #{name}, #{itemClass}, #{unit})")
	public abstract int addFood(Food food);
	
	// 특정 항목과 동일한 분류 및 이름, 날짜를 가진 항목의 물가정보가 존재하는지 확인
	@Select("select count(*) from price where id=#{id} and date=#{date}")
	public abstract int doesPriceExist(String id, String date);
	
	// 물가정보 추가
	@Insert("insert into price values("
			+ "now(), #{id}, #{fluc}, #{priceSeoul}, "
			+ "#{priceBusan}, #{priceDaegu}, #{priceGwangju}, "
			+ "#{priceDaejeon}, #{priceAvg})")
	public abstract int addPrice(Food food);

}
