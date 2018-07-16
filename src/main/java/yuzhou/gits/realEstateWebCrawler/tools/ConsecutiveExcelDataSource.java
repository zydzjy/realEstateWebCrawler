package yuzhou.gits.realEstateWebCrawler.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.monitorjbl.xlsx.StreamingReader;

public class ConsecutiveExcelDataSource implements DataSource {
	String[] excelFiles;
	Sheet currSheet = null;
	Iterator<Row> rowIt = null;
	int currExcelFileIdx = 0;
	Workbook currWb = null;
	OPCPackage currPkg = null;
	Row currRow = null;
	Map<String,Integer> valIdxMap = null;
	String dir;
	String sheetName;
	public ConsecutiveExcelDataSource(String dir,String[] excelFiles,String sheetName,
			Map<String,Integer> valIdxMap) throws Exception{
		this.dir = dir;
		this.excelFiles = excelFiles;
		this.valIdxMap = valIdxMap;
		this.sheetName = sheetName;
		
	}
	
	@Override
	public Object getSrc() throws Exception {
		return this.currRow;
	}

	@Override
	public boolean next() throws Exception {
		
		if(this.rowIt.hasNext()){
			this.currRow = this.rowIt.next();
			return true;
		}else{
			this.currWb.close();
			//this.currPkg.close();
			if(currExcelFileIdx == this.excelFiles.length)
				return false;
			//next workbook
			InputStream is = new FileInputStream(new File(dir+File.separator+excelFiles[currExcelFileIdx++]));
			this.currWb = StreamingReader.builder()
			        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
			        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
			        .open(is);            // InputStream or File for XLSX file (required)
			this.currSheet = this.currWb.getSheet(sheetName);
			this.rowIt = this.currSheet.rowIterator();
			if(rowIt.hasNext()){
				this.currRow = rowIt.next();//skip titles row
				return true;
			}else{
				return false;
			}
		}
	}

	@Override
	public Object[] getObjVal(String...objNames) throws Exception {
		Object[] vals = new Object[objNames.length];
		for(int i=0;i<vals.length;i++){
			Cell cell = this.currRow.getCell(this.valIdxMap.get(objNames[i]));
			String value = "";
			if(cell == null){
				value = "";
			}else{
				if(cell.getCellTypeEnum() == CellType.STRING){
					value = cell.getStringCellValue();
				}else if(cell.getCellTypeEnum() == CellType.BLANK){
					value = "";
				}else if(cell.getCellTypeEnum() == CellType.NUMERIC){
					value = String.valueOf( cell.getNumericCellValue());
				}else{
					throw new Exception("wrong type");
				}
			}
			vals[i] = value;
		}
		return vals;
	}

	@Override
	public void init(Object... args) throws Exception {
		InputStream is = new FileInputStream(new File(dir+File.separator+excelFiles[currExcelFileIdx++]));
		this.currWb = StreamingReader.builder()
		        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
		        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(is);            // InputStream or File for XLSX file (required)
		this.currSheet = this.currWb.getSheet(sheetName);
		this.rowIt = this.currSheet.rowIterator();
		rowIt.next();//skip titles row
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}
}