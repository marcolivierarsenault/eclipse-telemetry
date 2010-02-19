package simulator;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import util.ByteManipulator;
import util.HexString;

import net.miginfocom.swing.MigLayout;

// Needs the current deviceItem
import eclipseV7.data.Device;
import eclipseV7.data.DeviceItem;

public class SimulatorDeviceItem extends JPanel implements ActionListener, ItemListener, ChangeListener {
	
	private static final long serialVersionUID = 1L;
	
	// Root Logger
	static Logger logger = Logger.getLogger("simulator");
	
	// Visual components
    private JCheckBox jCheckBoxDeviceItemName;
    private JLabel jLabelMaxVal;
    private JLabel jLabelMinVal;
    private JLabel jLabelPlusMinus;
    private JLabel jLabelUnit;
    private JSlider jSliderVal;
    private JTextField jTextFieldValue;
    private JTextField jTextFieldValueDelta;
	private JButton jButtonUpdate;
	private JTextField jTextFieldProtocolFrame;
	
	private DeviceItem item;
	private Device device; // Device that is parent of this deviceItem
	private int factoredIntValue;
	
	// Others
	private boolean enabled = false;
	private Double delta = 0.0;
    
    public SimulatorDeviceItem(Device device, DeviceItem item) {
    	this.item = item;
    	this.device = device;
    	createPanel();
    }
    
    public int getId() {
    	return item.getItemId();
    }
    
    public String getName() {
    	return item.getName();
    }
    
    public int getFactoredIntValue() {
    	return this.factoredIntValue;
    }
    
    private void createPanel() {    	
    	// Components
        jCheckBoxDeviceItemName = new JCheckBox();
        jSliderVal = new JSlider();
        jLabelMinVal = new JLabel();
        jLabelMaxVal = new JLabel();
        jTextFieldValue = new JTextField();
        jLabelUnit = new JLabel();
        jTextFieldValueDelta = new JTextField();
        jLabelPlusMinus = new JLabel();
        jButtonUpdate = new JButton();
        jTextFieldProtocolFrame = new JTextField();

        this.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jCheckBoxDeviceItemName.setText(item.getName()+"["+item.getItemId()+"]"); 
        jCheckBoxDeviceItemName.setName("jCheckBoxDeviceItemName"); 
        jCheckBoxDeviceItemName.setToolTipText(item.getName());
        jCheckBoxDeviceItemName.addItemListener(this);

        jSliderVal.setName("jSliderVal"); 
        jSliderVal.setMinimum(item.getMinValue());
        jSliderVal.setMaximum(item.getMaxValue());
        jSliderVal.setPaintTicks(true);
        jSliderVal.setPaintLabels(true);
        int sliderIncrement;
        if (item.getMinValue() < 0) {
        	if (item.getMaxValue() + Math.abs(item.getMinValue()) < 40) sliderIncrement = 1;
        	else if ((item.getMaxValue() + Math.abs(item.getMinValue()))/100 < 10) sliderIncrement = 50;
        	else if ((item.getMaxValue() + Math.abs(item.getMinValue()))/1000 < 10) sliderIncrement = 500;
        	//else if ((item.getMaxValue() + Math.abs(item.getMinValue()))/10000 < 10) sliderIncrement = 1000;
        	else sliderIncrement = 10000;
        }
        else {
         	if ((item.getMaxValue() - item.getMinValue()) < 40) sliderIncrement = 1;
        	else if ((item.getMaxValue() - item.getMinValue())/100 < 10) sliderIncrement = 50;
        	else if ((item.getMaxValue() - item.getMinValue())/1000 < 10) sliderIncrement = 500;
        	//else if ((item.getMaxValue() - item.getMinValue())/10000 < 10) sliderIncrement = 1000;
        	else sliderIncrement = 10000;
        }
        jSliderVal.setMajorTickSpacing(sliderIncrement);
        jSliderVal.setMinorTickSpacing(sliderIncrement/5);
        if (item.getMinValue() < 0) jSliderVal.setValue(0);
        else jSliderVal.setValue(item.getMinValue());
        jSliderVal.setPaintTicks(true);
        jSliderVal.setPaintLabels(true);
        jSliderVal.addChangeListener(this);

        jLabelMinVal.setText(new Integer(item.getMinValue()).toString()); 
        jLabelMinVal.setName("jLabelMinVal"); 

        jLabelMaxVal.setText(new Integer(item.getMaxValue()).toString()); 
        jLabelMaxVal.setName("jLabelMaxVal"); 

        jTextFieldValue.setText("0.000"); 
        jTextFieldValue.setName("jTextFieldValue"); 

        jLabelUnit.setText(item.getUnit()); 
        jLabelUnit.setToolTipText(item.getUnit());
        jLabelUnit.setName("jLabelUnit"); 

        jTextFieldValueDelta.setText("0"); 
        jTextFieldValueDelta.setName("jTextFieldValueDelta"); 

        jLabelPlusMinus.setText("+/-");
        jLabelPlusMinus.setName("jLabelPlusMinus"); 
        
        jButtonUpdate.setText("Update");
        jButtonUpdate.setName("JButtonUpdate");
        jButtonUpdate.setActionCommand("update");
        jButtonUpdate.addActionListener(this);
        
        jTextFieldProtocolFrame.setText("AAA");
        jTextFieldProtocolFrame.setName("jTextFieldProtocolFrame");
        jTextFieldProtocolFrame.setBackground(Color.LIGHT_GRAY);
        jTextFieldProtocolFrame.setEditable(false);
        
        // Layout
        MigLayout layout = new MigLayout(
        		"insets 5", // Layout constraints
        		"", // Column constraints
        		""); // Row constaints
        this.setLayout(layout);
        
        this.add(jCheckBoxDeviceItemName, "width 120!");
        this.add(jLabelMinVal, "width 60!");
        this.add(jSliderVal, "width 400!");
        this.add(jLabelMaxVal, "width 50!");
        this.add(jTextFieldValue, "width 100!");
        this.add(jLabelUnit, "width 60!");
        this.add(jLabelPlusMinus, "width 10!");
        this.add(jTextFieldValueDelta, "width 80!");
        this.add(jButtonUpdate, "width 80!");
        this.add(jTextFieldProtocolFrame, "width 140!");
        
        // If the item is binary, we need to show the bit definitions
        if (item.isBinaryData()) {
        	JPanel binaryOptionsPanel = new JPanel();
        	binaryOptionsPanel.add(new JLabel("Binary!"));
        	this.add(binaryOptionsPanel, "newline");
        }
    }
    
    public String getSimulatedFrame(){
    	//TODO Temp.. retourner la trame au complet avec les encapsule etc.
    	
    	double textFieldValue = new Double(jTextFieldValue.getText());
		
    	// Gerer le +/-
    	Double randomSign = Math.random() > 0.5 ? -1.0 : 1.0;
    	Double random = Math.random() * delta;
    	Double randomResult = textFieldValue + (random * randomSign);
    	
    	if (randomResult > item.getMaxValue()) randomResult = new Double(item.getMaxValue());
    	if (randomResult < item.getMinValue()) randomResult = new Double(item.getMinValue());
    	
    	//factoredIntValue = (int) (new Double(jTextFieldValue.getText()) * item.getFactor());
    	factoredIntValue = (int) (randomResult * item.getFactor());
    	
    	try {
    		//String start = "0A";
        	String deviceId = Integer.toHexString(device.getId()).toUpperCase();
        	if (deviceId.length() == 1) deviceId = "0" + deviceId;
        	String itemId = Integer.toHexString(item.getItemId()).toUpperCase();
        	if (itemId.length() == 1) itemId = "00" + itemId;
        	if (itemId.length() == 2) itemId = "0" + itemId;
			String data = HexString.bufferToHex(ByteManipulator.intToByteArray(
					this.getFactoredIntValue(), item.getNumBits()/8, item.isSigned()))
					.toUpperCase();
			String CRC = "AA";
	    	//String end = "0D";
	    	
	    	//System.out.println(start);
	    	//System.out.println(deviceId);
	    	//System.out.println(itemId);
	    	//System.out.println(data);
	    	//System.out.println(CRC);
	    	//System.out.println(end);
	    	
	    	//return start + deviceId + itemId + data + CRC + end;
	    	return deviceId + itemId + data + CRC;
	    	
    	} catch (Exception e) {return "0A0000000AA0D";}
    }
    
    public JPanel getJPanel() {
    	return this;
    }
    
    public void setChecked(boolean state) {
    	jCheckBoxDeviceItemName.setSelected(state);
    }
    
    public boolean isActive() {
    	return enabled;
    }
    
	@Override
	public void actionPerformed(ActionEvent ae) {
		// JButton
		if ("update".equals(ae.getActionCommand())) {
			delta = new Double(jTextFieldValueDelta.getText());
		}
		logger.debug("Double in the field: " + jTextFieldValue.getText() + " the Integer: " + factoredIntValue);
	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		// Checkbox
		if (ie.getSource() instanceof JCheckBox) {
			if (ie.getStateChange() == ItemEvent.SELECTED) this.enabled = true;
			if (ie.getStateChange() == ItemEvent.DESELECTED) this.enabled = false;
		}
	}

	@Override
	public void stateChanged(ChangeEvent ce) {
		// JSlider
		if (ce.getSource() instanceof JSlider) {
			JSlider source = (JSlider)ce.getSource();
			jTextFieldValue.setText(new Integer(source.getValue()).toString());
		}
	}
    
}
