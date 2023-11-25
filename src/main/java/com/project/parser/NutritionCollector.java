package com.project.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NutritionCollector {

	// api키
	@Value("${foodsafetykorea.api.key}")
	private String apikey;
	
	// api 요청주소
	String apiSource = "http://openapi.foodsafetykorea.go.kr/api/" + apikey + "/I0960/xml/1/5";
	
	public void getInfo() {
		try {
			System.out.println(apikey);
			
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
            conn.disconnect();
            
		}catch(IOException e) {
			System.out.println("영양정보 데이터 가져오기 실패 - " + e.getMessage());
		}
	}

}
