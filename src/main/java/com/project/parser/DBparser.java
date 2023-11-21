package com.project.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

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
				
				System.out.println(text);
			}
			
			// 소스파일이 엑셀인 경우
			if(fileSource.endsWith(".xls") || fileSource.endsWith(".xlsx")){
				FileInputStream fis = new FileInputStream(fileSource);
				
				if(fileSource.endsWith(".xls")) {
					// xls
					Workbook workbook = new HSSFWorkbook(fis);
					Sheet sheet = workbook.getSheetAt(1);
					StringBuilder result = new StringBuilder();
					FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
					
					Iterator<Row> rowIterator = sheet.iterator();

		            // 첫 번째 행은 헤더로 처리하고 건너뜀
		            if (rowIterator.hasNext()) {
		                rowIterator.next();
		            }
		            
		            while (rowIterator.hasNext()) {
		                Row row = rowIterator.next();
		                Iterator<Cell> cellIterator = row.cellIterator();
		                
		                while(cellIterator.hasNext()) {
		                	// 셀 객체 특정 및 타입 파악
		                	Cell cell = cellIterator.next();
		                	CellType type = cell.getCellType();
		                	//System.out.println(type);
		                	
		                	// 셀 타입에 따른 처리
		                	if(type.toString().equals("FORMULA")) {
		                		// 셀 타입이 수식인 경우
		                		result.append(evaluator.evaluate(cell).getNumberValue() + " ");
		                	}else if(type.toString().equals("NUMERIC")) {
		                		// 셀 타입이 숫자인 경우
		                		result.append(cell.getNumericCellValue() + " ");
		                	}else {
		                		// 셀 타입이 문자열인 경우
		                		result.append(cell.getStringCellValue() + " ");
		                	}
		                }
		                
		                result.append("\n");
		            }
					
		            System.out.println(result);
					workbook.close();
				}else {
					// xlsx
				}
				
			}
			
		}catch (IOException e) {
			System.out.println("DB파싱 오류 : " + e.getMessage());
		}
		
	}
	
}
