package eclipse.view.gui;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class DeviceTable extends JPanel {
	private JTable table;

	/**
	 * Create the panel.
	 */
	public DeviceTable() {
		
		table = new JTable();
		
		
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane);

	}

}
