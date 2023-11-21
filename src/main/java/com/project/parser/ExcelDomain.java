package com.project.parser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelDomain {

	private String kind;
	private String item;
	private String standard;
	private String unit;
	private String lastweek;
	private String seoul;
	private String fluc;
	private String busan;
	private String daegu;
	private String gangju;
	private String daejeon;
	
	public ExcelDomain(String kind, String item, String standard, String unit, 
			String lastweek, String seoul, String fluc, String busan, 
			String daegu, String gangju, String daejeon) {
		
		this.kind = kind;
		this.item = item;
		this.standard = standard;
		this.unit = unit;
		this.lastweek = lastweek;
		this.seoul = seoul;
		this.fluc = fluc;
		this.busan = busan;
		this.daegu = daegu;
		this.gangju = gangju;
		this.daejeon = daejeon;
	}
}
