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

public class DBparser {
	
	public void parse(String fileSource) {
		
		try {
			File source = new File(fileSource);
			
			// 소스파일이 pdf인 경우
			if(fileSource.endsWith(".pdf")) {
				PDDocument pdfDoc = PDDocument.load(source);
				String text = new PDFTextStripper().getText(pdfDoc);
				
				// 본문을 페이지 단위로 분할 
				String[] sheets = text.split("중분류 소분류 조사품목 원산지\\(제조사\\) 조사규격\\(품종\\) 조사단위 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비 전주 금주 전주대비");		
				int totalPages = sheets.length;
				
				//System.out.println("총 페이지 수 - " + totalPages);
				//System.out.println(text);
				
				// 2페이지부터 처리 (1페이지는 개황 및 전망자료)
				for(int i=1; i<totalPages; i++) {
					System.out.println("@@@@@@@" + i + "페이지@@@@@@@" );
					
					// 페이지 내용을 행단위로 분할
					String[] lines = sheets[i].split("\n");
					
					// 각 행단위 처리
					for(int j=0; j<lines.length; j++) {
						String name; // 품목의 이름
						int unit = 0; // 품목의 기준 단위(DB상 단위코드)
						String itemClass; // 품목의 분류
						String priceAvg; // 전국 평균가격
						String fluc; // 전국 평균가격의 전주대비 등락
						String priceSeoul; // 서울가격
						String priceBusan; // 부산가격
						String priceDaegu; // 대구가격
						String priceGwangju; // 광주가격
						String priceDaejeon; // 대전가격
						String separator = ""; // 행 데이터에서 품목 구성요소 영역과 가격영역 구분자
						
						// 품목 행의 조사단위 파악
						//if(lines[j].contains(" 개 ")) unit = 1;
						//if(lines[j].contains(" 마리 ")) unit = 2;
						if(lines[j].contains(" 10g ")) unit = 3;
						if(lines[j].contains(" 50g ")) unit = 4;
						if(lines[j].contains(" 100g ")) unit = 5;
						if(lines[j].contains(" 200g ")) unit = 6;
						if(lines[j].contains(" 300g ")) unit = 7;
						if(lines[j].contains(" 400g ")) unit = 8;
						if(lines[j].contains(" 500g ")) unit = 9;
						if(lines[j].contains(" kg ") || lines[j].contains(" 1kg ")) unit = 10;
						if(lines[j].contains(" 360㎖ ")) unit = 11;
						if(lines[j].contains(" 500㎖ ")) unit = 12;
						if(lines[j].contains(" 950㎖ ")) unit = 13;
						if(lines[j].contains(" 1000㎖ ") || lines[j].contains(" ℓ ") || lines[j].contains(" 1ℓ ")) unit = 14;
						if(lines[j].contains(" 1.8ℓ ")) unit = 15;
						
						// 특정 품목의 유효한 물가정보를 담고있는 행인지 판별
						boolean isValidData = false;
						if(unit != 0) isValidData = true;
						
						// 유효한 정보를 가진 행인 경우 -> 품목의 나머지 구성요소 특정
						if(isValidData) {
							// 품목정보와 가격영역 사이의 구분자 특정
							if(lines[j].contains(" 100g ")) separator = " 100g ";
							if(lines[j].contains(" 600g ")) separator = " 600g ";
							if(lines[j].contains(" kg ")) separator = " kg ";
							
							if(lines[j].contains(" 포 ")) separator = " 포 ";
							if(lines[j].contains(" 송이 ")) separator = " 송이 ";
							if(lines[j].contains(" 통 ")) separator = " 통 ";
							if(lines[j].contains(" 봉 ")) separator = " 봉 ";
							if(lines[j].contains(" 포기 ")) separator = " 포기 ";
							if(lines[j].contains(" 단 ")) separator = " 단 ";
							if(lines[j].contains(" 마리 ")) separator = " 마리 ";
							if(lines[j].contains(" 묶음 ")) separator = " 묶음 ";
							if(lines[j].contains(" 상자 ")) separator = " 상자 ";
							if(lines[j].contains(" 팩 ")) separator = " 팩 ";
							if(lines[j].contains(" PET ")) separator = " PET ";
							if(lines[j].contains(" 캔 ")) separator = " 캔 ";
							if(lines[j].contains(" 병 ")) separator = " 병 ";
							
							if(separator != "") {
								// 구분자를 통해 행을 기본정보와 가격정보 영역으로 분할
								String infos = lines[j].split(separator)[0];
								String[] prices = lines[j].split(separator)[lines[j].split(separator).length - 1].split(" ");
								
								// 기본정보로부터 분류, 이름 특정 
								itemClass = infos.split(" ")[0];
								name = infos.split(" ")[1];
								
								// 디버깅용
								/*
								System.out.println("sep: " + separator + " lineNum: " + (j+1) + " prices: " + prices.length);
								System.out.println(lines[j]);
								System.out.println(lines[j].split(separator)[lines[j].split(separator).length - 1]);
								for(int k=0; k<prices.length; k++) {
									System.out.println(prices[k]);
								}
								*/
								
								priceAvg = prices[1];
								fluc = prices[2];
								priceSeoul = prices[4];
								priceBusan = prices[7];
								priceDaegu = prices[10];
								priceGwangju = prices[13];
								priceDaejeon = prices[16];
								
								// 처리결과 출력
								System.out.println(
										"분류:" + itemClass 
										+ " 품목:" + name 
										+ " 단위:" + unit
										+ " 평균가격:" + priceAvg 
										+ " 변동:" + fluc 
										+ " 서울:" + priceSeoul
										+ " 부산:" + priceBusan 
										+ " 대구: " + priceDaegu
										+ " 광주:" + priceGwangju 
										+ "대전: " + priceDaejeon
										);
							}
						}
					}
				}
			}
			
			// 소스파일이 엑셀인 경우
			if(fileSource.endsWith(".xls") || fileSource.endsWith(".xlsx")){
				FileInputStream fis = new FileInputStream(fileSource);
				Workbook workbook = null;
				
				// 출력결과 테스트
                StringBuilder result = new StringBuilder();
				
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
	    	                		else if(words[i].equals("1.8ℓ")) unit = 14;
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
