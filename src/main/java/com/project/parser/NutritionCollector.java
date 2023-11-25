package com.project.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class NutritionCollector {

	// api키
	@Value("${foodsafetykorea.api.key}")
	private String apikey;
	
	public void getInfo() {
		try {
			// api 요청주소
			String apiSource = "http://openapi.foodsafetykorea.go.kr/api/" 
			+ apikey 
			+ "/I2790/xml/1/100/DESC_KOR=백미";
			
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
			
            // xml로 파싱
            try {
            	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(response.toString())));
				
				NodeList rows = doc.getElementsByTagName("row");
				
				for(int i=0; i<rows.getLength(); i++) {
					Element row = (Element)rows.item(i);
					
					String id = row.getAttribute("id");
					String name = row.getElementsByTagName("DESC_KOR").item(0).getTextContent();
					System.out.println(id + "  " + name);
				}
				
			} catch (Exception e) {
				System.out.println("xml 파싱 오류 - " + e.getMessage());
			}

            // 응답 출력
            //System.out.println(response);
            conn.disconnect();
            
		}catch(IOException e) {
			System.out.println("영양정보 데이터 가져오기 실패 - " + e.getMessage());
		}
	}

}
