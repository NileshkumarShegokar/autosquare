package com.myriad.auto2.engine.writer;

import com.fasterxml.jackson.databind.JsonNode;
import com.myriad.auto2.controllers.SidebarController;
import com.myriad.auto2.edith.util.Language;
import com.myriad.auto2.engine.util.DataGenerator;
import com.myriad.auto2.engine.util.NameResolver;
import com.myriad.auto2.engine.util.PatternEvaluator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by nileshkumar_shegokar on 6/19/2017.
 */
public class ExcelWriter {

	static Logger log = LogManager.getLogger(ExcelWriter.class.getName());
	private static DataGenerator generator;

	public static void createExcel(List<JsonNode> events, String location, String artifactId, String className,
								   Integer noOfTests, List<Language> languages, Boolean overWrite) throws IOException {

		className= NameResolver.toDisplayCase(className,NameResolver.CLASS,false);
		for (Language language : languages) {
			log.info("cREATE excel FILE : " + language);
			generator = DataGenerator.initialiseDataGenerator(language);

			int rowIndex = 0;
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(className);

			XSSFRow firstRow = sheet.createRow(rowIndex++);
			int cellIndex = 0;
			for (int index = 0; index < events.size(); index++) {
				JsonNode event = events.get(index);
				JsonNode data = event.path("data");
				String cmd = data.path("cmd").asText();
				if (cmd.equalsIgnoreCase("sendKeys") || cmd.equalsIgnoreCase("SELECT")) {
					XSSFCell cell = firstRow.createCell(cellIndex++);
					JsonNode dataData = data.path("data");
					cell.setCellValue(dataData.path("path").asText());
				}
			}

			for (int testCase = 0; testCase < noOfTests; testCase++) {
				XSSFRow row = sheet.createRow(rowIndex++);
				cellIndex = 0;
				for (int index = 0; index < events.size(); index++) {
					JsonNode event = events.get(index);
					JsonNode data = event.path("data");
					String cmd = data.path("cmd").asText();
					if (cmd.equalsIgnoreCase("sendKeys") || cmd.equalsIgnoreCase("SELECT")) {
						XSSFCell cell = row.createCell(cellIndex++);
						cell.setCellValue((String) getValue(event, language));
					}
				}
			}
			FileOutputStream outputStream = null;

			String newPath = location + File.separator + artifactId + File.separator + "src" + File.separator + "main"
					+ File.separator + "resources" + File.separator + "test_data";
			Files.createDirectories(Paths.get(newPath));
			log.info("writing excel content to file :"+newPath);
			outputStream = new FileOutputStream(newPath + File.separator + className + "_" + language + ".xlsx");
			workbook.write(outputStream);
			workbook.close();
			log.info("excel file writing completed :"+newPath);
		}
	}

	public static Object getValue(JsonNode event, Language language) {
		/*
		 * if(event.getFixed()){ return event.getValue(); }
		 */
		JsonNode data=event.path("data");
		JsonNode dataData=data.path("data");
		String pattern = data.path("pattern").asText();
		if (pattern.isEmpty()) {
			String value=dataData.path("keys").asText();
			if(value.contains("{"))
			{
				//keys exists crop the keys from value
				while(value.contains("{"))
				{
					int start=value.indexOf("{");
					int end = value.indexOf("}",start);
					String val=value.substring(start,end+1);
					value=value.replace(val,"");
				}
			}
			return value;
		}
		return PatternEvaluator.solvePattern(pattern, language);
	}
}
