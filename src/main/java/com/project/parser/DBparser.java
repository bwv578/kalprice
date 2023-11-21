package com.project.parser;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class DBparser {
	
	public void parse() {
		
		try {
			
			String fileSource = "C:\\Java\\smh-workspace\\kalprice\\src\\main\\resources\\static\\20231115 주간생활물가 동향 및 시세표.pdf";
			File source = new File(fileSource);
			PDDocument pdfDoc = PDDocument.load(source);
			String text = new PDFTextStripper().getText(pdfDoc);
			
			System.out.println(text);
			
		}catch (IOException e) {
			System.out.println("DB파싱 오류 : " + e.getMessage());
		}
		
	}
	
}
