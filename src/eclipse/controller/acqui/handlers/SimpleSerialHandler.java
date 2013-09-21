package eclipse.controller.acqui.handlers;


import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import eclipse.controller.acqui.AcquisitionHandler;
import eclipse.controller.util.TelemetrySettings;

/**
 * Interface to use with the XBEE and the JSSC librairie
 * 
 * Do not need driver like RXTX
 * 
 * @author MArco
 *
 */
public class SimpleSerialHandler extends AcquisitionHandler {
	
	private SerialPort serialPort;
	private Boolean connected=false;
	private String namePrt = "HANDLER_SERIAL_PORT";
	private int cpt=0;
	
	/**
	 * Start the handler, search for port
	 */
	public Boolean start() {
		String bault =TelemetrySettings.getInstance().getSetting("HANDLER_SERIAL_BAULT");
		String name = getPort();  
		serialPort = new SerialPort(name);  		
            try {
				serialPort.openPort();
	            serialPort.setParams(Integer.parseInt(bault), 
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
	            connected=true;  
	            TelemetrySettings.getInstance().setSetting(namePrt, name);
	            return true;
			} catch (SerialPortException e) {
				TelemetrySettings.getInstance().setSetting(namePrt, "XXX");
				if(cpt==0){
					cpt++;
					start();
				}
			}  
        return false;
	}
	
	
	private String getPort(){
		if(!TelemetrySettings.getInstance().getSetting(namePrt).contains("XXX"))
			return TelemetrySettings.getInstance().getSetting(namePrt);
		 String[] portNames = SerialPortList.getPortNames();
		 String retour = (String) JOptionPane.showInputDialog(
	                null,
	                "Télémetrie",
	                "Select serial port",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                portNames,
	                portNames[0]);
		 return retour;
	}


	/**
	 * Stop and release the com port
	 */
	public void stop() {
		if(connected){
			try {
				serialPort.closePort();
				connected=false;
			} catch (SerialPortException e) {
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				Logger.getLogger("main").error("Caught exception; decorating with appropriate status template : " + stack.toString());
			}
		}
	}

	
	public boolean isConnected() {
		return connected;
	}


	public String getName() {
		return namePrt;
	}
	
	public void init(String id){
		namePrt=id;
	}


	/**
	 * ReadByteFromBuffer and return it
	 */
	@SuppressWarnings("null")
	public byte readByte() {
		if(connected){
			byte[] bt;
			try {
				bt = serialPort.readBytes(1);
				return bt[0];
			} catch (SerialPortException e) {
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				Logger.getLogger("main").error("Caught exception; decorating with appropriate status template : " + stack.toString());
			}
		}
		return (Byte) null;
	}


	/**
	 * Write Byte to buffer
	 */
	public void writeByte(Byte bt) {
		if(connected){
			try {
				serialPort.writeByte(bt);
			} catch (SerialPortException e) {
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				Logger.getLogger("main").error("Caught exception; decorating with appropriate status template : " + stack.toString());
			}
		}
		
	}
	

}
