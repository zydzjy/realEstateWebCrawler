package yuzhou.gits.realEstateWebCrawler.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import yuzhou.gits.realEstateWebCrawler.tools.DataItemMapper.DataMapper;

public class MongoExcelTools {
	private String destDir;
	private String host;
	private DataMapper mapper;
	private String port;
	private String dbName;
	private String user;
	private String pwd;
	private String xlsxBaseFileName;
	String[] sheetNames;
	String[][] sheetTitles;
	String[] sheetCollections;
	String[][] srcKeys;
	public final static int XLXS_FLUSH_SIZE = 888;
	public final static int MAX_ROWS_FILE = 666666;// xlxs file max rows 1048576

	public void loadCfg(String path) throws Exception {
		System.out.println(new Date(1495641600000L).toString());
		List<String> sheetList = new ArrayList<String>();
		List<String> tableList = new ArrayList<String>();
		List<String[]> sheetTitleList = new ArrayList<String[]>();
		List<String[]> tableColList = new ArrayList<String[]>();
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "GBK"));
		
		try {
			
			String newLine = "";
			if ((newLine = buffReader.readLine()) != null) {//host,port,user,pwd
				String[] vals = newLine.split(",");
				this.host = vals[0];
				this.port = vals[1];
				this.dbName = vals[2];
			}else{
				throw new Exception("配置文件错误,没有对应的主机，用户名，密码,数据库");
			}
			newLine = null;
			if ((newLine = buffReader.readLine()) != null) {// 
				String mapperClzName = newLine;
				if("NULL".equalsIgnoreCase(newLine)){
					this.mapper = null;
				}else{
					this.mapper = (DataMapper) Class.forName(newLine).newInstance();
				}
			}else{
				throw new Exception("配置文件错误,没有对应的数据字段映射类名配置或配置NULL");//mapper
			}
			newLine = null;
			if ((newLine = buffReader.readLine()) != null) {// 
				this.destDir = newLine;
			}else{
				throw new Exception("配置文件错误,没有对应的生成EXCEL的目录");// excel dest dir
			}
			newLine = null;
			if ((newLine = buffReader.readLine()) != null) {// 
				this.xlsxBaseFileName = newLine;
			}else{
				throw new Exception("配置文件错误,没有对应的生成EXCEL文件名");// excel file name
			}
			newLine = null;
			while ((newLine = buffReader.readLine()) != null) {
				sheetList.add(newLine);// sheet name
				newLine = buffReader.readLine();
				if (newLine == null)
					throw new Exception("配置文件错误,没有对应的表名称配置");// excel titles
				else {
					tableList.add(newLine);
				}
				newLine = buffReader.readLine();
				if (newLine == null)
					throw new Exception("配置文件错误,没有对应的标题配置");// excel titles
				else {
					sheetTitleList.add(newLine.split(","));
				}
				newLine = buffReader.readLine();
				if (newLine == null)
					throw new Exception("配置文件错误,没有对应的表字段");// table columns
				else {
					tableColList.add(newLine.split(","));
				}
			}
		} finally {
			buffReader.close();
		}

		this.sheetNames = new String[sheetList.size()];sheetList.toArray(this.sheetNames);
		this.sheetTitles = new String[sheetTitleList.size()][];sheetTitleList.toArray(this.sheetTitles);
		this.sheetCollections = new String[tableList.size()];tableList.toArray(this.sheetCollections);
		this.srcKeys = new String[tableColList.size()][];tableColList.toArray(this.srcKeys);
		
	}

	public static void main(String[] args) {
 
		try {
			MongoExcelTools tool = new MongoExcelTools();
			tool.loadCfg("e:\\wzExcel");
			tool.dataSrcFactory = new MongoDBSourceFactory();
			Object[] mongodbHost = { tool.host, Integer.parseInt(tool.port) };
			tool.dataSrcFactory.init(mongodbHost);

			tool.export(tool.destDir, tool.xlsxBaseFileName);
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
			String[] mongoArgs = sheetCollection.split(";");
			if(mongoArgs.length>1){
				this.dataSrcFactory.prepareSource(dbName, mongoArgs[0],mongoArgs[1]);
			}
			else{
				this.dataSrcFactory.prepareSource(dbName, sheetCollection);
			}
			// this.dataSrcFactory.prepareSource((Object[]) sheetTables);
			DataSource sheetDataSrc = this.dataSrcFactory.getDataSrc();
			while (true) {
				try {
					exportTableData(srcKeysOfSheet, sheetDataSrc, xlsFileStream, wb, sheet, lastRowIdx,
							firstRowInSheet);
				} catch (Exception _e) {
					if (_e instanceof MaxExcelRowsReachedException) {
						MaxExcelRowsReachedException e = (MaxExcelRowsReachedException) _e;
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
					} else if (_e instanceof DataSrcDrainedReachedException) {
						DataSrcDrainedReachedException e = (DataSrcDrainedReachedException) _e;
						((SXSSFSheet) sheet).flushRows();
						//this.dataSrcFactory.cleanSource();
						lastRowIdx = e.rowIdx;
						firstRowInSheet = e.rowIdx;
						break;
					} else {
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

	private void exportTableData(String[] srcKeysOfSheet, DataSource dataSrc, OutputStream xlsFileStream, Workbook wb,
			Sheet sheet, int lastRowIdx, int firstRowInSheet) throws Exception {
		// ResultSet rs = (ResultSet) dataSrc.getSrc();
		// int colCnt = rs.getMetaData().getColumnCount();

		while (dataSrc.next()) {
			// rs = (ResultSet) dataSrc.getSrc();
			// flush ?
			if (this.createdRowsInFile > 0 && (this.createdRowsInFile) % XLXS_FLUSH_SIZE == 0) {
				((SXSSFSheet) sheet).flushRows(XLXS_FLUSH_SIZE);
			}

			Row dataR = sheet.createRow(firstRowInSheet);
			Cell cell = dataR.createCell(0);
			cell.setCellValue(lastRowIdx);
			String[] vals = (String[]) dataSrc.getObjVal(srcKeysOfSheet);
			if(this.mapper != null){
				vals = this.mapper.map(sheet.getSheetName(),vals);
			}
			for (int i = 0; i < vals.length; i++) {
				cell = dataR.createCell(i + 1);
				cell.setCellValue(vals[i]);
			}
			this.createdRowsInFile++;
			firstRowInSheet++;
			lastRowIdx++;
			if (this.createdRowsInFile == MAX_ROWS_FILE) {
				((SXSSFSheet) sheet).flushRows();
				MaxExcelRowsReachedException ex = new MaxExcelRowsReachedException();
				ex.lastRowIdx = lastRowIdx;
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