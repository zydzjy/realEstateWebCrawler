package yuzhou.gits.realEstateWebCrawler.app;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleMsgManager implements Runnable {
	
	public String createHeaderMsgLine(String time){
		String msg = "开始全量采集:"+time+"\r";
		return msg;
	}
	
	public String createTotalsLine(int projTotalCnts,int currFinishedCnts){
		String msg = "本次采集约项目数:"+projTotalCnts+"个，"
				+ "已完成:"+currFinishedCnts+"个\r";
		return msg;
	}
	
	Thread t = null;
	public ConsoleMsgManager(){
		 
	}
	public final static SimpleDateFormat yyyyMMdd_HHmmss = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	StringBuffer msgBuf = new StringBuffer();
	public static final Object lock = new Object();
	  
	public void run() {
		while(true){
			synchronized (lock) {
				try {
					lock.wait();
					String msg = this.msgBuf.toString();
					String backspaces = predictedMsgPrinted;
					if(msg.length() > predictedMsgPrinted.length()){
						for(int i=msg.length()-predictedMsgPrinted.length();i>0;i--){
							backspaces += "\b";
						}
					}else{
						backspaces = predictedMsgPrinted.substring(0, msg.length());
					}
					System.out.print("\\x1b[1A");
					System.out.print(backspaces);
					System.out.print(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected static final String predictedMsgPrinted = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b"
			+ "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"
			+ "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"
			+ "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b"
			+"\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
	
	
	public void notifyProjsChanged(int totalProjs,int finishedProjs){
		msgBuf.delete(0, msgBuf.length());
		msgBuf.append( this.createHeaderMsgLine(yyyyMMdd_HHmmss
				.format(new Date())));
		msgBuf.append(this.createTotalsLine(totalProjs,finishedProjs));
		synchronized(lock){
			lock.notifyAll();
		}
	}
	
	public static void main(String...args) throws InterruptedException{
		System.out.println("this line");
		System.out.println("\\x1b[1A");
		/*ConsoleMsgManager instance = new ConsoleMsgManager();
		new Thread(instance).start();
		int projsFinished = 31;
		while(true){
			instance.notifyProjsChanged(1000,projsFinished++);
			Thread.sleep(3100);
		}*/
	}
}
