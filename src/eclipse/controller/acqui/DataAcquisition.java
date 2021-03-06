package eclipse.controller.acqui;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import eclipse.view.gui.DesktopManager;



/**
 * Acquisition thread, this is the most important thread,
 * 
 * It is responsable to get data from the OS socket
 * 
 * @author Eclipse
 *
 */
public class DataAcquisition implements Runnable {

	private AcquisitionHandler handler = null;
	private Desencapsulator de = null;
	private Byte currentByte = null;
	static Logger logger = Logger.getLogger("main");
	static private DataAcquisition acqui = new DataAcquisition();
	private boolean acquisition = false;
	
	public void Ititalize(AcquisitionHandler ah, Desencapsulator de) 
	{
		this.handler = ah;
		this.de=de;
	}
	
	private DataAcquisition(){
		
	}
	
	public static DataAcquisition getInstance(){
		return acqui;
	}
	
	/**
	 * Stop acquisition and reset curent array
	 */
	public void stopAcquiring(){
		logger.info("Acquisition stop");
		acquisition=false;
		handler.stop();
		de.clearData();
		DesktopManager.getIstance().menuStart();
	}

	/**
	 * Start acquisition
	 */
	public void startAcquiring() 
	{
		if(handler.start())
		{
			logger.info("Acquisition start");
			DesktopManager.getIstance().menuStop();
			acquisition=true;
		}
		else
		{
			logger.error("Error with interface");
		}			
	}
	
	/**
	 * Program wil loop here forever, will decode byte and transfer it to createArray
	 */
	public void listen()
	{
		while(true)
		{
			while(acquisition)
			{
				currentByte=handler.readByte();
				
				if (handler.isConnected() == true)
				{
					de.receiveChar(currentByte);
				}
				else
				{
					logger.info("Disconnected from TCP server");
					acquisition = false;					
				}
			}

			try 
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				Logger.getLogger("main").error("Caught exception; decorating with appropriate status template : " + stack.toString());
			}
		}
	}
	
	/**
	 * Send byte to the telemetry
	 * @param bt
	 */
	public void sendByte(byte bt){
		if(acquisition)
			handler.writeByte(bt);
	}

	public void run() {
		listen();		
	}
	
	/**
	 * Return true if app is started, false if not
	 * @return
	 */
	public boolean getAcquiStatus(){
		return acquisition;
	}
	
	
		
}

