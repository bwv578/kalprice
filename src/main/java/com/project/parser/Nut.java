package com.project.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.models.Food;

@Component
public class Nut {
	
	@Autowired
	private DBparserDAO dao;
	private String data = "8	66	17	0.6	0.4\r\n"
			+ "9	53	13.34	0.81	0.31\r\n"
			+ "10	32	8	0.7	0.3\r\n"
			+ "11	30	8	0.6	0.2\r\n"
			+ "12	32	7.8	0.8	0.1\r\n"
			+ "13	39	9	1.9	0.4\r\n"
			+ "16	26	7	1	0.1\r\n"
			+ "17	41	10	0.9	0.2\r\n"
			+ "21	85	20	1.6	0.1\r\n"
			+ "28	33	7	1.9	0.4\r\n"
			+ "32	278	22.5	12.2	17.6\r\n"
			+ "33	45	9.14	3.03	0.64\r\n"
			+ "40	340	69	9	3\r\n"
			+ "41	500	79	10	16\r\n"
			+ "45	150	34.6	2.3	0.46\r\n"
			+ "46	216	0	18.9	15.6\r\n"
			+ "48	417	75	0	13.3\r\n"
			+ "50	65	7	8	0.1\r\n"
			+ "51	225	50	3.5	1\r\n"
			+ "52	128	14.5	12	4.1\r\n"
			+ "53	281	50	13	14\r\n"
			+ "54	364	76	10	1\r\n"
			+ "55	386	100	0	0\r\n"
			+ "57	828	0	0	92\r\n"
			+ "58	830	0	0	92\r\n"
			+ "59	679	0.6	1	75\r\n"
			+ "60	111	26	1.3	0.2\r\n"
			+ "61	450	65	25	10\r\n"
			+ "62	35	0.6	7.6	0.1\r\n"
			+ "63	321	83	0	0\r\n"
			+ "64	4	1	0	0\r\n"
			+ "66	340	2	13	31\r\n"
			+ "67	230	16.5	1	0\r\n"
			+ "68	453.6	0.36	0	0\r\n"
			+ "69	0	0	0	0\r\n"
			+ "70	427.5	95	9.5	1.9\r\n"
			+ "71	700	50	30	40\r\n"
			+ "72	756	194.4	0	0\r\n"
			+ "6	56	14.1	0.2	0.7	\r\n"
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
