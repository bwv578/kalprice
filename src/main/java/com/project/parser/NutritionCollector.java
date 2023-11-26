package com.project.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class NutritionCollector {

	// api키
	@Value("${foodsafetykorea.api.key}")
	private String apikey;
	
	public HashMap<String, Object> getInfo(String name, String foodClass, int count) {
		
		HashMap<String, Object> nutrience = new HashMap<>(); // 리턴할 영양정보 해시맵
		// api에 요청할 데이터 인덱스 범위(요청 한번당 1000건으로 제한)
		int startIndex = 1 + ((count - 1) * 1000); 
		int endIndex = count * 1000;
		
		try {
			// api 요청주소
			String apiSource = "http://openapi.foodsafetykorea.go.kr/api/" 
			+ apikey + "/I2790/xml/" + startIndex + "/" + endIndex + "/DESC_KOR=" + name;
			
			URL url = new URL(apiSource);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			
			// 응답 데이터 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            System.out.println(response);
            
            // xml로 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(response.toString())));
			
			// 행 데이터 목록
			NodeList rows = doc.getElementsByTagName("row");
			// 데이터 건수
			Element total = (Element)doc.getElementsByTagName("total_count").item(0);
			System.out.println("total - " + total.getTextContent());
			
			// 각 행에 대한 처리
			for(int i=0; i<rows.getLength(); i++) {
				Element row = (Element)rows.item(i);
				
				String id = row.getAttribute("id");
				String group = row.getElementsByTagName("GROUP_NAME").item(0).getTextContent();
				String desc = row.getElementsByTagName("DESC_KOR").item(0).getTextContent();
				
				//System.out.println(id + " // " + desc + " // " + group);
				
				if(desc.contains(name) && group.contains(foodClass)) {
					// xml로부터 데이터 추출
					double size = Double.parseDouble(row.getElementsByTagName("SERVING_SIZE").item(0).getTextContent()); 
					String unit = row.getElementsByTagName("SERVING_UNIT").item(0).getTextContent();
					double calrorie = Double.parseDouble(row.getElementsByTagName("NUTR_CONT1").item(0).getTextContent());
					double carbohydrate = Double.parseDouble(row.getElementsByTagName("NUTR_CONT2").item(0).getTextContent());
					double protein = Double.parseDouble(row.getElementsByTagName("NUTR_CONT3").item(0).getTextContent());
					double fat = Double.parseDouble(row.getElementsByTagName("NUTR_CONT4").item(0).getTextContent());
					
					// 단위 1당 함량으로 변경
					calrorie = calrorie / size;
					carbohydrate = carbohydrate / size;
					protein = protein / size;
					fat = fat / size;
					
					nutrience.put("calrorie", calrorie);
					nutrience.put("carbohydrate", carbohydrate);
					nutrience.put("protein", protein);
					nutrience.put("fat", fat);
					nutrience.put("unit", unit);
					
					//System.out.println(id + " // " + desc + " // " + group + " // " + size + " // " + unit);
					//break;
				}
			}
			
            conn.disconnect();
            
		}catch(Exception e) {
			System.out.println("영양정보 데이터 가져오기 실패 - " + e.getMessage());
		}
		
		return nutrience;
	}

}
