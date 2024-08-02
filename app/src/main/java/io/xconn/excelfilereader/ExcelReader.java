package io.xconn.excelfilereader;

import android.content.Context;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public List<LocationData> readExcelData(Context context, String fileName) {
        List<LocationData> locationDataList = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                double latitude = row.getCell(0).getNumericCellValue();
                double longitude = row.getCell(1).getNumericCellValue();
                String id = row.getCell(2).getStringCellValue();
                locationDataList.add(new LocationData(id, latitude, longitude));
            }

            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationDataList;
    }
}
