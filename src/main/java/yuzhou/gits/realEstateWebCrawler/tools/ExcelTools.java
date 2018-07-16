package yuzhou.gits.realEstateWebCrawler.tools;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelTools {
	String[] sheetNames;
	String[][] sheetTitles;
	String[] sheetCollections;
	String[][] srcKeys;
	public final static int XLXS_FLUSH_SIZE = 888;
	public final static int MAX_ROWS_FILE = 666666;//  xlxs file max rows 1048576

	public static void main(String[] args) {

		String[] sheetNames = { "楼栋表"};
		String[][] sheetTitles = { {"序号","项目名称","楼栋表","房号","单元号","户型名称","更新时间"}};
		String[][] srcKeys = { {"projectName","bulidName","roomNum","bulidNum","bulidType","date"}};
		String[] sheetCollections = { "km_building" };
		String dir = "e:\\crawlerData\\";
		String xlsxBaseFileName = "昆明";
		
		/*String[] sheetNames = { "备案信息","楼栋"};
		String[][] sheetTitles = { { "序号", "预售许可证", "楼盘名称", "房屋用途", "总套数", "已备案套数", "未备案套数", "总面积（平方米）", "已备案（平方米）","时间" }
				  ,{"序号", "预售许可证", "楼盘名称", "楼栋名称", "单元", "房间号", "销售状态", "用途", "状态", "时间" }};
		String[][] tables = { {"cz_projba"},{ "cz_room"} };
		String dir = "e:\\crawlerData\\";
		String xlsxBaseFileName = "常州房号采集";
		
		String[] sheetNames = { "房号数据"};
		String[][] sheetTitles = { {"序号", "项目名称", "物业类型", "项目位置", "开发商", "售楼电话", 
			"累计均价", "预售证号", "预售审批项目名称", "开盘时间", "楼盘状态", "售楼部地址", "售楼部电话", 
			"楼栋", "房号", "建筑面积", "套内建筑面积", "得房率", "申请毛坯单价", "装修价", "总价","房间状态","时间" }};
		String[][] tables = {{ "tz_room"} };
		String dir = "e:\\crawlerData\\";
		String xlsxBaseFileName = "台州房号采集";*/
		
		try {
			ExcelTools tool = new ExcelTools();
			tool.sheetNames = sheetNames;
			tool.sheetTitles = sheetTitles;
			tool.sheetCollections = sheetCollections;
			tool.dataSrcFactory = new MongoDBSourceFactory();//new DatabaseSourceFactory();
			Object[] mongodbHost = {"127.0.0.1",27027};
			tool.dataSrcFactory.init(mongodbHost);
			tool.srcKeys = srcKeys;
			tool.export(dir, xlsxBaseFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected DataSourceFactory dataSrcFactory;

	public Workbook createEmptyWb() {
		SXSSFWorkbook wb = new SXSSFWorkbook(-1);
		for (int i = 0; i < this.sheetNames.length; i++) {
			String sheetName = this.sheetNames[i];
			String[] sheetTitle = this.sheetTitles[i];
			this.createEmptySheetWithTitles(wb, sheetName, sheetTitle);
		}
		return wb;
	}

	public void export(String dir, String xlsxBaseFileName) throws Exception {
		// 1,create a xlsx file
		int fileIdx = 1;
		OutputStream xlsFileStream = new FileOutputStream(dir + xlsxBaseFileName + fileIdx + ".xlsx");
		Workbook wb = this.createEmptyWb();

		for (int sheetIdx = 0; sheetIdx < this.sheetNames.length; sheetIdx++) {
			String sheetName = this.sheetNames[sheetIdx];
			Sheet sheet = wb.getSheet(sheetName);
			String sheetCollection = this.sheetCollections[sheetIdx];
			String[] srcKeysOfSheet = srcKeys[sheetIdx];
			int lastRowIdx = 1;
			int firstRowInSheet = 1;
			this.dataSrcFactory.prepareSource("test", sheetCollection);
			//this.dataSrcFactory.prepareSource((Object[]) sheetTables);
			DataSource sheetDataSrc = this.dataSrcFactory.getDataSrc();
			while (true) {
				try {
					exportTableData(srcKeysOfSheet,
							sheetDataSrc, xlsFileStream, wb, sheet, lastRowIdx, firstRowInSheet);
				}catch(Exception _e){
					if(_e instanceof MaxExcelRowsReachedException){
						MaxExcelRowsReachedException e = (MaxExcelRowsReachedException)_e;
						this.createdRowsInFile = 0;
						wb.write(xlsFileStream);
						wb.close();
						((SXSSFWorkbook) wb).dispose();
						xlsFileStream.close();
						fileIdx++;
						// create a new file
						xlsFileStream = new FileOutputStream(dir + xlsxBaseFileName + fileIdx + ".xlsx");
						wb = this.createEmptyWb();
						lastRowIdx = e.lastRowIdx;
						firstRowInSheet = 1;
						sheet = wb.getSheet(sheetName);
					}else if(_e instanceof DataSrcDrainedReachedException){
						DataSrcDrainedReachedException e = (DataSrcDrainedReachedException)_e;
						((SXSSFSheet) sheet).flushRows();
						this.dataSrcFactory.cleanSource();
						lastRowIdx = e.rowIdx;
						firstRowInSheet = e.rowIdx;
						break;
					}else{
						_e.printStackTrace();
					}
				}
			}
		}
		
		this.createdRowsInFile = 0;
		wb.write(xlsFileStream);
		wb.close();
		((SXSSFWorkbook) wb).dispose();
		xlsFileStream.close();
	}

	int createdRowsInFile = 0;

	private void exportTableData(String[] srcKeysOfSheet,DataSource dataSrc, OutputStream xlsFileStream, 
			Workbook wb, Sheet sheet, int lastRowIdx,int firstRowInSheet) throws Exception {
		//ResultSet rs = (ResultSet) dataSrc.getSrc();
		//int colCnt = rs.getMetaData().getColumnCount();
		
		while (dataSrc.next()) {
			//rs = (ResultSet) dataSrc.getSrc();
			// flush ?
			if (this.createdRowsInFile > 0 && (this.createdRowsInFile) % XLXS_FLUSH_SIZE == 0) {
				((SXSSFSheet) sheet).flushRows(XLXS_FLUSH_SIZE);
			}

			Row dataR = sheet.createRow(firstRowInSheet);
			Cell cell = dataR.createCell(0);
			cell.setCellValue(lastRowIdx);
			String[] vals = (String[]) dataSrc.getObjVal(srcKeysOfSheet);
			for (int i = 0; i < vals.length; i++) {
				cell = dataR.createCell(i+1);
				cell.setCellValue(vals[i]);
			}
			this.createdRowsInFile++;
			firstRowInSheet++;
			lastRowIdx++;
			if (this.createdRowsInFile == MAX_ROWS_FILE ) {
				((SXSSFSheet) sheet).flushRows();
				MaxExcelRowsReachedException ex = new MaxExcelRowsReachedException();
				ex.lastRowIdx =lastRowIdx;
				throw ex;
			}
		}
	}

	private Sheet createEmptySheetWithTitles(Workbook wb, String sheetName, String[] sheetTitle) {
		int sheetIdx = wb.getNumberOfSheets();
		Sheet sheet = wb.createSheet();
		wb.setSheetName(sheetIdx, sheetName);
		Row titleRow = sheet.createRow(0);
		this.createdRowsInFile++;
		for (int j = 0; j < sheetTitle.length; j++) {
			Cell cell = titleRow.createCell(j);
			cell.setCellValue(sheetTitle[j]);
		}
		return sheet;
	}
}