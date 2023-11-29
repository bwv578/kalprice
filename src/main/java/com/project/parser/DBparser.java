package com.project.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.models.Food;

@Component
public class DBparser {
	
	@Autowired
	private DBparserDAO dao;
	@Autowired
	private NutritionCollector nutritionCollector;
	
	public void parse(String fileSource, String date) {
		
		try {
			File source = new File(fileSource);
			
			// 소스파일이 pdf인 경우
			if(fileSource.endsWith(".pdf")) {
				PDDocument pdfDoc = PDDocument.load(source);
				String text = new PDFTextStripper().getText(pdfDoc);
				//System.out.println(text);
				
				// 본문을 페이지 단위로 분할 
				String[] sheets = text.split("중분류 소분류 조사품목 원산지\\(제조사\\) 조사규격\\(품종\\) 조사단위 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비");		
				int totalPages = sheets.length;
				
				// 2페이지부터 처리 (1페이지는 개황 및 전망자료)
				for(int i=1; i<totalPages; i++) {
					System.out.println("@@@@@@@ " + i + "페이지 @@@@@@@");
					
					// 페이지 내용을 행단위로 분할
					String[] lines = sheets[i].split("\n");
					
					// 각 행단위 처리
					for(int j=0; j<lines.length; j++) {
						String name; // 품목의 이름
						String unit = ""; // 품목의 기준 단위
						String itemClass; // 품목의 분류
						Object priceAvg; // 전국 평균가격
						Object fluc; // 전국 평균가격의 전주대비 등락
						Object priceSeoul; // 서울가격
						Object priceBusan; // 부산가격
						Object priceDaegu; // 대구가격
						Object priceGwangju; // 광주가격
						Object priceDaejeon; // 대전가격
						String separator = ""; // 행 데이터에서 품목 구성요소 영역과 가격영역 구분자
						
						// 행 데이터를 단어 단위로 분할, 단위정보 추출
						String[] words = lines[j].split(" ");
						for(int k=0; k<words.length; k++) {
							boolean isUnit = 
									words[k].contains("g") ||
									words[k].contains("G") ||
									words[k].contains("kg") ||
									words[k].contains("㎖") ||
									words[k].contains("ℓ");
							
							if(isUnit) unit = words[k];
							
							if(unit.contains("G")) unit = unit.replaceAll("G", "g");
							if(unit.contains("K")) unit = unit.replaceAll("K", "k");
							
							if(unit.equals("kg")) unit = "1kg";
							else if(unit.equals("7-8kg")) unit = "7kg";
							else if(unit.contains("×")) unit = unit.substring(0, unit.indexOf("×"));
							else if(unit.startsWith("(")) unit = unit.replaceAll("\\(", "");
							else if(unit.endsWith(")")) unit = unit.substring(0, unit.indexOf("g") + 1);
							
							if(unit.startsWith("살코기참치")) unit = unit.replaceAll("살코기참치", "");
							else if(unit.startsWith("치약")) unit = unit.replaceAll("치약", "");
						}
						
						// 특정 품목의 유효한 물가정보를 담고있는 행인지 판별
						boolean isValidData = false;
						if(!unit.equals("")) isValidData = true;
						
						// 유효한 정보를 가진 행인 경우 -> 품목의 나머지 구성요소 특정
						if(isValidData) {
							// 품목정보와 가격영역 사이의 구분자 특정
							if(lines[j].contains(" 100g ")) separator = " 100g ";
							else if(lines[j].contains(" 600g ")) separator = " 600g ";
							else if(lines[j].contains(" kg ")) separator = " kg ";
							
							if(lines[j].contains(" 개 ")) separator = " 개 ";
							else if(lines[j].contains(" 포 ")) separator = " 포 ";
							else if(lines[j].contains(" 송이 ")) separator = " 송이 ";
							else if(lines[j].contains(" 통 ")) separator = " 통 ";
							else if(lines[j].contains(" 봉 ")) separator = " 봉 ";
							else if(lines[j].contains(" 포기 ")) separator = " 포기 ";
							else if(lines[j].contains(" 단 ")) separator = " 단 ";
							else if(lines[j].contains(" 마리 ")) separator = " 마리 ";
							else if(lines[j].contains(" 묶음 ")) separator = " 묶음 ";
							else if(lines[j].contains(" 상자 ")) separator = " 상자 ";
							else if(lines[j].contains(" 팩 ")) separator = " 팩 ";
							else if(lines[j].contains(" PET ")) separator = " PET ";
							else if(lines[j].contains(" 캔 ")) separator = " 캔 ";
							else if(lines[j].contains(" 병 ")) separator = " 병 ";
							
							if(separator != "") {
								// 구분자를 통해 행을 기본정보와 가격정보 영역으로 분할
								String infos = lines[j].split(separator)[0];
								//lines[j] = lines[j].replaceAll("- ", "null ");
								String[] prices = lines[j].split(separator)[lines[j].split(separator).length - 1].split(" ");
								
								// 기본정보로부터 분류, 이름 특정 
								itemClass = infos.split(" ")[0];
								name = infos.split(" ")[1];
								if(itemClass.equals("세제류") || itemClass.equals("위생용품") || itemClass.equals("연료류") || itemClass.equals("귀금속류")) continue;
	
								// 영양성분 api 기준으로 분류표기 변경
								if(itemClass.equals("과일류")) itemClass = "과실류";
								else if(itemClass.equals("과채류") || 
										itemClass.equals("나물류") ||
										itemClass.equals("양채류") ||
										itemClass.equals("엽채류") ||
										itemClass.equals("조미채류")) itemClass = "채소류";
								else if(itemClass.equals("패류") ||
										name.equals("멸치")) itemClass = "어패류 및 기타 수산물";
								else if(itemClass.equals("서류")) itemClass = "감자 및 전분류";
								else if(itemClass.equals("난류")) itemClass = "알가공품류";
								else if(itemClass.equals("계육류")) itemClass = "육류";
								else if(itemClass.equals("유제품")) itemClass = "유가공품";
								else if(name.equals("미역"))	itemClass = "해조류";	
							
								// 무게단위의를 가진 항목의 경우 100그램 기준으로 가격계산
								double devider = 1;
								if(unit.contains("k")) {
									devider = Double.parseDouble(unit.substring(0, unit.indexOf("k"))) * 10;
									unit = "100g";
								}else if(unit.contains("g")) {
									devider = Double.parseDouble(unit.substring(0, unit.indexOf("g"))) / 100;
									unit = "100g";
								}else if(itemClass.equals("조미료류∙장류∙유지류") || name.equals("생수")) {
									if(unit.contains("㎖")) {
										devider = Double.parseDouble(unit.substring(0, unit.indexOf("㎖"))) / 100;
									}else {
										devider = Double.parseDouble(unit.substring(0, unit.indexOf("ℓ"))) * 10;
									}
									unit = "100㎖";
								}
								
								// 라면 예외처리
								if(name.equals("라면")) {
									devider = 1;
									unit = "120g";
								}
								
								if(prices[1].equals("-")) priceAvg = null;								
								else{
									priceAvg = Double.parseDouble(prices[1].replaceAll(",", "")) / devider;
									priceAvg = Math.round((Double)priceAvg * 100.0) / 100.0;
								}
								if(prices[2].equals("-")) fluc = null; 
								else {
									fluc = Double.parseDouble(prices[2].replaceAll(",", ""));
									fluc = Math.round((Double)fluc * 100.0) / 100.0;
								}
								if(prices[4].equals("-")) priceSeoul = null;
								else {
									priceSeoul = Double.parseDouble(prices[4].replaceAll(",", "")) / devider;
									priceSeoul = Math.round((Double)priceSeoul * 100.0) / 100.0;
								}
								if(prices[7].equals("-")) priceBusan = null;
								else {
									priceBusan = Double.parseDouble(prices[7].replaceAll(",", "")) / devider;
									priceBusan = Math.round((Double)priceBusan * 100.0) / 100.0;
								}
								if(prices[10].equals("-")) priceDaegu = null;
								else {
									priceDaegu = Double.parseDouble(prices[10].replaceAll(",", "")) / devider;
									priceDaegu = Math.round((Double)priceDaegu * 100.0) / 100.0;
								}
								if(prices[13].equals("-")) priceGwangju = null;
								else {
									priceGwangju = Double.parseDouble(prices[13].replaceAll(",", "")) / devider;
									priceGwangju = Math.round((Double)priceGwangju * 100.0) / 100.0;
								}
								if(prices[16].equals("-")) priceDaejeon = null;
								else {
									priceDaejeon = Double.parseDouble(prices[16].replaceAll(",", "")) / devider;
									priceDaejeon = Math.round((Double)priceDaejeon * 100.0) / 100.0;
								}
								
								// 물가정보상의 용량 계산
								Double amount;
								if(unit.contains("k")) amount = Double.parseDouble(unit.replaceAll("kg", ""));
								else if(unit.contains("g")) amount = Double.parseDouble(unit.replaceAll("g", ""));
								else if(unit.contains("㎖")) amount = Double.parseDouble(unit.replaceAll("㎖", ""));
								else amount = Double.parseDouble(unit.replaceAll("ℓ", ""));
								
								// DB기준으로 unit 코드 수정
								if(unit.equals("100g")) unit = "1";
								else if(unit.equals("120g")) unit = "2";
								else if(unit.equals("100㎖")) unit = "3";
								else if(unit.equals("320㎖")) unit = "4";
								else if(unit.equals("360㎖")) unit = "5";
								else if(unit.equals("500㎖")) unit = "6";
								else if(unit.equals("860㎖")) unit = "7";
								else if(unit.equals("900㎖")) unit = "8";
								else if(unit.equals("950㎖")) unit = "9";
								else if(unit.equals("1000㎖")) unit = "10";
								else if(unit.equals("1.8ℓ")) unit = "11";

								// DB통신
								Food food = new Food(name, itemClass, unit);
								food.setFluc(fluc);
								food.setAvg(priceAvg);
								food.setSeoulPrice(priceSeoul);
								food.setBusanPrice(priceBusan);
								food.setDaeguPrice(priceDaegu); 
								food.setGwangjuPrice(priceGwangju);
								food.setDaejeonPrice(priceDaejeon);
								food.setDate(date);
								
								int foodExistence = dao.doesFoodExist(food);
								int foodAdded = 0;
								int priceAdded = 0;
								
								// 새로운 항목인 경우
								if(foodExistence == 0) foodAdded = dao.addFood(food);
								String id = dao.searchFoodId(food);

								int priceExistence = dao.doesPriceExist(id, date);
								food.setFoodId(id);
								if(priceExistence == 0) priceAdded = dao.addPrice(food);
								
								// 처리결과 출력		
								System.out.println(
										"분류|" + itemClass 
										+ "  품목|" + name 
										+ "  단위|" + unit
										+ "  평균가격|" + priceAvg 
										+ "  변동|" + fluc 
										+ "  서울|" + priceSeoul
										+ "  부산|" + priceBusan 
										+ "  대구|" + priceDaegu
										+ "  광주|" + priceGwangju 
										+ "  대전|" + priceDaejeon
										+ "  음식존재|" + foodExistence
										+ "  음식추가|" + foodAdded
										+ "  가격존재" + priceExistence
										+ "  가격추가|" + priceAdded
										);
								
								// 영양정보 수집
								int count = 1;
								HashMap<String, Object> nutrition = new HashMap<>();
								while(true) {
									nutrition = nutritionCollector.getInfo(name, itemClass, count);
									if(nutrition.get("total").equals("999") && nutrition.get("calorie").equals(null)) count++;
									else break;
								}
								
								// 새로운 항목인 경우 영양정보 새로 추가
								if(foodExistence == 0) {
									if(unit.equals(null)) continue;

									Object calorie = (Double)nutrition.get("calorie");
									Object carbohydrate = (Double)nutrition.get("carbohydrate");
									Object protein = (Double)nutrition.get("protein");
									Object fat = (Double)nutrition.get("fat");
									
									if(calorie != null) calorie = (Double)calorie * amount;
									if(carbohydrate != null) carbohydrate = (Double)carbohydrate * amount;
									if(protein != null) protein = (Double)protein * amount;
									if(fat != null) fat = (Double)fat * amount;
									
									// DB통신
									food.setCalorie(calorie);
									food.setCarbohydrate(carbohydrate);
									food.setProtein(protein);
									food.setFat(fat);			
									dao.addNutrition(food);
									
									// 테스트
									System.out.println("열량 - " + nutrition.get("calorie"));
								}
							}
						}
					}
				}
			}
			
			// 소스파일이 엑셀인 경우
			if(fileSource.endsWith(".xls") || fileSource.endsWith(".xlsx")){
				FileInputStream fis = new FileInputStream(fileSource);
				Workbook workbook = null;

				// 엑셀 확장자에 따른 워크북 객체 생성
				if(fileSource.endsWith(".xls")) {
					// xls
					workbook = new HSSFWorkbook(fis);
				}else {
					// xlsx
					workbook = new XSSFWorkbook(fis);
				}
				
				Sheet sheet = workbook.getSheetAt(1);
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();		
				Iterator<Row> rowIterator = sheet.iterator();

				String itemClass = null; // 품목의 분류
				
	            // 첫 세줄 건너뜀
				rowIterator.next();
				rowIterator.next();
				rowIterator.next();
  
	            while (rowIterator.hasNext()) {
	                Row row = rowIterator.next();
	                Iterator<Cell> cellIterator = row.cellIterator();
	                String cellContent;

	                String name = null; // 품목의 이름
	                String standard = null; // 규격
					int unit = 0; // 품목의 기준 단위(DB상 단위코드)
					String priceAvg = null; // 전국 평균가격
					String fluc = null; // 전국 평균가격의 전주대비 등락
					String priceSeoul = null; // 서울가격
					String priceBusan = null; // 부산가격
					String priceDaegu = null; // 대구가격
					String priceGwangju = null; // 광주가격
					String priceDaejeon = null; // 대전가격

	                while(cellIterator.hasNext()) {
	                	// 셀 객체 특정 및 타입 파악
	                	Cell cell = cellIterator.next();
	                	CellType type = cell.getCellType();
	                	String cellAddr = cell.getAddress().toString();
	                	String colAddr = cellAddr.substring(0, 1);
	                	
	                	// 규격에서 벗어난 셀 건너뛰기
	                	if(cellAddr.length() > 3) continue;

	                	// 셀 타입에 따른 처리
	                	if(type.toString().equals("FORMULA")) {
	                		// 셀 타입이 수식인 경우
	                		cellContent = String.valueOf(evaluator.evaluate(cell).getNumberValue());
	                	}else if(type.toString().equals("NUMERIC")) {
	                		// 셀 타입이 숫자인 경우
	                		cellContent = String.valueOf(cell.getNumericCellValue());
	                	}else if(type.toString().equals("STRING")){
	                		// 셀 타입이 문자열인 경우
	                		cellContent = cell.getStringCellValue();
	                	}else{
	                		// 셀이 비어있거나 기타 타입인 경우
	                		continue;
	                	}

	                	// 셀단위 어드레스 출력 테스트
	                	//System.out.println(type.toString() + "  " 
	                	//+ cellContent + "  " + cellAddr + "  " + itemClass);
	                	
	                	if(colAddr.equals("A")) itemClass = cellContent;
	                	else if(colAddr.equals("B")) name = cellContent;
	                	else if(colAddr.equals("C")) standard = cellContent;
	                	else if(colAddr.equals("G")) priceSeoul = cellContent;
	                	else if(colAddr.equals("I")) fluc = cellContent;
	                	else if(colAddr.equals("L")) priceBusan = cellContent;
	                	else if(colAddr.equals("P")) priceDaegu = cellContent;
	                	else if(colAddr.equals("T")) priceGwangju = cellContent;
	                	else if(colAddr.equals("X")) priceDaejeon = cellContent;
	                	else if(colAddr.equals("D")) {
	                		// 단위 처리	
	                		if(cellContent.equals("10g")) unit = 3;
	                		else if(cellContent.equals("50g")) unit = 4;
	                		else if(cellContent.equals("100g")) unit = 5;
	                		else if(cellContent.equals("200g")) unit = 6;
	                		else if(cellContent.equals("300g")) unit = 7;
	                		else if(cellContent.equals("400g")) unit = 8;
	                		else if(cellContent.equals("500g")) unit = 9;
	                		else if(cellContent.equals("1kg")) unit = 10;
	                		else if(cellContent.equals("360㎖")) unit = 11;
	                		else if(cellContent.equals("500㎖")) unit = 12;
	                		else if(cellContent.equals("950㎖")) unit = 13;
	                		else if(cellContent.equals("1ℓ")) unit = 14;
	                		else if(cellContent.equals("1.8ℓ")) unit = 14;
	                		else {
	                			// 단위가 정확한 용량으로 기재되어있지 않은 경우 규격항목 참조
	                			String[] words = standard.split(" ");
	                			for(int i=0; i<words.length; i++) {
	                				if(words[i].equals("10g")) unit = 3;
	    	                		else if(words[i].equals("50g")) unit = 4;
	    	                		else if(words[i].equals("100g")) unit = 5;
	    	                		else if(words[i].equals("200g")) unit = 6;
	    	                		else if(words[i].equals("300g")) unit = 7;
	    	                		else if(words[i].equals("400g")) unit = 8;
	    	                		else if(words[i].equals("500g")) unit = 9;
	    	                		else if(words[i].equals("1kg")) unit = 10;
	    	                		else if(words[i].equals("360㎖")) unit = 11;
	    	                		else if(words[i].equals("500㎖")) unit = 12;
	    	                		else if(words[i].equals("950㎖")) unit = 13;
	    	                		else if(words[i].equals("1ℓ")) unit = 14;
	    	                		else if(words[i].equals("1.8ℓ")) unit = 15;
	                			}
	                		}
	                	}	
	                }
	                
	                // 결과출력
	                System.out.println("분류/" + itemClass 
	                		+ "  품목/" + name
	                		+ "  단위/" + unit 
	                		+ "  규격/" + standard
	                		+ "  등락/" + fluc
	                		+ "  서울/" + priceSeoul
	                		+ "  부산/" + priceBusan
	                		+ "  대구/" + priceDaegu
	                		+ "  광주/" + priceGwangju
	                		+ "  대전/" + priceDaejeon
	                		);	                
	            }

				workbook.close();
			}
		}catch (IOException e) {
			System.out.println("DB파싱 오류 : " + e.getMessage());
		}	
	}
}
