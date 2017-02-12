package com.anting;

import com.alibaba.fastjson.JSON;
import com.anting.dao.FilingMapper;
import com.anting.entity.Filing;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SchoolMgmtApplication.class)
@WebAppConfiguration
public class SchoolMgmtApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private FilingMapper filingMapper;



    private static final int TIME_PERIOD_ROW_NUM = 1;

    private static final int FIRST_TYPE_ROW_NUM = 2;

    private static final int BODY_START_ROW_NUM = 4;


    @Test
    public void excel() throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(this.getClass().getResource("/1.xls").getFile())));

        HSSFWorkbook wb = new HSSFWorkbook(fs);

        HSSFSheet sheet = wb.getSheetAt(0);

        parseExcel(sheet);
        /*List<CellRangeAddress> cellRangeAddressList = getCombineCell(sheet);
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);
            Map<String, Object> map = new HashMap<>();
            for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {

                Object obj = getMergedRegionValue(cellRangeAddressList, sheet, i, j, row);


                map.put(i + "--" + j, obj);
                // map.put(i+"---" +j, row.getCell(j));

            }
            list.add(map);

        }

        System.err.println(JSON.toJSONString(list));*/

        //getMergedRegionValue(cellRangeAddressList, sheet)


//        //获取一共有多少个sheet
//        System.err.println(wb.getNumberOfSheets());
//

//
//
//        System.err.println(wb.getSheetName(2));
//        System.err.println();
//
//        HSSFRow row = sheet.getRow(5);
//
//        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
//
//            System.err.println(row.getCell(i) + "--" + i);
//        }

    }

    public void parseExcel(HSSFSheet sheet) {


        int boxNum = 0;
        String typeName = null;

        //解析类别
        HSSFRow typeRow = sheet.getRow(FIRST_TYPE_ROW_NUM);

        for(int i = 0; i < typeRow.getPhysicalNumberOfCells(); i++) {
            String cellVal =  getCellValue(typeRow.getCell(i));

            if(cellVal == null) continue;

            if(cellVal.contains("第")&& cellVal.contains("盒")) {
                boxNum =  Integer.parseInt(cellVal.replaceAll("[^(0-9)]", ""));
            } else {

                if(cellVal.contains("、")) {
                    cellVal = cellVal.substring(cellVal.indexOf("、") + 1, cellVal.length());
                }
                typeName = cellVal;
            }
        }
        System.err.println(typeName);

        //解析时间周期
        String timePeriod = null;
        HSSFRow timePeriodRow = sheet.getRow(TIME_PERIOD_ROW_NUM);
        for(int i = 0; i < timePeriodRow.getPhysicalNumberOfCells(); i++) {
            String cellVal = getCellValue(timePeriodRow.getCell(i));
            if(cellVal == null) continue;
            timePeriod = cellVal;
            break;
        }

        Integer filingTypeId =  1;

        List<CellRangeAddress> cellRangeAddressList = getCombineCell(sheet);

        int k = 1;

        for (int i = BODY_START_ROW_NUM; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);

            Filing filing = new Filing();

            for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                Object obj = getMergedRegionValue(cellRangeAddressList, sheet, i, j, row);
                if(obj == null) obj = "";
                if(j ==  1) {
                    String type = obj.toString();
                } else if(j == 2) {
                    filing.setTitle(obj.toString());
                } else if(j == 3) {
                    filing.setRemark(obj.toString());
                }
            }


            filing.setBoxNum(boxNum);
            filing.setTimePeriod(timePeriod);
            filing.setFilingTypeId(filingTypeId);


            System.err.println(k + "---" + JSON.toJSONString(filing));

            filingMapper.insert(filing);

        }






        System.err.println(boxNum);

        System.err.println(timePeriod);





    }

    public void parseBody() {

    }



    public List<CellRangeAddress> getCombineCell(HSSFSheet sheet) {
        List<CellRangeAddress> list = new ArrayList<>();

        // 获得一个 sheet 中合并单元格的数量
        int sheetmergerCount = sheet.getNumMergedRegions();
        // 遍历合并单元格
        for (int i = 0; i < sheetmergerCount; i++) {
            // 获得合并单元格加入list中
            CellRangeAddress ca = sheet.getMergedRegion(i);
            list.add(ca);
        }
        return list;
    }


    //获取合并格的值
    public String getMergedRegionValue(List<CellRangeAddress> listCombineCell, Sheet sheet, int row, int column, HSSFRow hssfRow) {

        for (CellRangeAddress ca : listCombineCell) {
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }

        return getCellValue(hssfRow.getCell(column));
    }



    public String getCellValue(Cell cell) {

        if (cell == null) return null;

        String str;

        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

            str =  cell.getStringCellValue();

        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {

            str =  String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

            str =  cell.getCellFormula();

        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

            str =  String.valueOf(cell.getNumericCellValue());
        } else {
            str = "";
        }

        if("".equals(str.trim())) return null;

        return str;
    }


}
