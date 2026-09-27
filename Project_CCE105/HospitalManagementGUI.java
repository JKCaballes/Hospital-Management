package Project_CCE105;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Vector;
import java.util.Arrays;
import java.util.PriorityQueue;

public class HospitalManagementGUI extends JPanel {
	private static final String FILE_PATH = "HospitalManagementFile.txt";
	private static final String FILE = "PatientsServed.txt";
	private JTextField nameField, ageField, genderField, doctorField, sicknessField;
	private JComboBox<String> priorityBox;

	private DefaultTableModel tableModel;
	private JTable table;

	private JButton addBtn, editBtn, serveBtn, clearBtn, orgBtn;

	private final Vector<Vector<Object>> allData = new Vector<>();

	private static final String[] COLUMNS = { "Patient Name", "Age", "Gender", "Doctor Appointed", "Sickness",
			"Priority Level" };
	private static final String[] PRIORITY_LEVELS = { "Critical", "Serious", "Good" };

	public HospitalManagementGUI() {
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(950, 600));

		add(buildDisplayPanel(), BorderLayout.CENTER);

		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		bottom.add(buildInputPanel());
		bottom.add(Box.createVerticalStrut(10));
		bottom.add(buildButtonPanel());
		add(bottom, BorderLayout.SOUTH);

		loadFromFile();
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
				setFormFromRow(table.getSelectedRow());
			}
		});
	}

	private JPanel buildDisplayPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		tableModel = new DefaultTableModel(COLUMNS, 0) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		table = new JTable(tableModel);
		table.setRowHeight(24);
		table.getSelectionModel().addListSelectionListener(e -> loadSelectedRowIntoForm());

		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildInputPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 6, 15, 0));

		nameField = new JTextField();
		ageField = new JTextField();
		genderField = new JTextField();
		doctorField = new JTextField();
		sicknessField = new JTextField();
		priorityBox = new JComboBox<>(PRIORITY_LEVELS);

		panel.add(labeled("Patient Name:", nameField));
		panel.add(labeled("Age:", ageField));
		panel.add(labeled("Gender:", genderField));
		panel.add(labeled("Doctor Appointed:", doctorField));
		panel.add(labeled("Sickness:", sicknessField));
		panel.add(labeled("Priority Level:", priorityBox));

		return panel;
	}

	private JPanel labeled(String labelText, JComponent field) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel label = new JLabel(labelText);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		field.setAlignmentX(Component.LEFT_ALIGNMENT);
		field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
		p.add(label);
		p.add(field);
		return p;
	}

	private JPanel buildButtonPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));

		addBtn = new JButton("Add");
		editBtn = new JButton("Edit");
		orgBtn = new JButton("Organize");
		serveBtn = new JButton("Serve");
		clearBtn = new JButton("Clear");

		addBtn.addActionListener(this::onAdd);
		editBtn.addActionListener(this::onEdit);
		orgBtn.addActionListener(this::organize);
		serveBtn.addActionListener(this::onServe);
		clearBtn.addActionListener(e -> clearForm());

		panel.add(addBtn);
		panel.add(editBtn);
		panel.add(orgBtn);
		panel.add(serveBtn);
		panel.add(clearBtn);

		return panel;
	}

	private void setFormFromRow(int row) {
		nameField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
		ageField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
		genderField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
		doctorField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
		sicknessField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
		priorityBox.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));

	}

	private void onAdd(ActionEvent e) {
		String name = nameField.getText().trim();
		String age = ageField.getText().trim();
		String gender = genderField.getText().trim();
		String doctor = doctorField.getText().trim();
		String sickness = sicknessField.getText().trim();
		String priority = (String) priorityBox.getSelectedItem();

		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Patient Name is required.", "Missing Information",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!age.isEmpty() && !age.matches("\\d+")) {
			JOptionPane.showMessageDialog(this, "Age must be a number.", "Invalid Age", JOptionPane.WARNING_MESSAGE);
			return;
		}

		Vector<Object> row = new Vector<>();
		row.add(name);
		row.add(age);
		row.add(gender);
		row.add(doctor);
		row.add(sickness);
		row.add(priority);
		allData.add(row);
		refreshTable();
		clearForm();
		saveToFile();
	}

	private void onEdit(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Select a row in the table to edit.", "No Selection",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String name = nameField.getText().trim();
		String age = ageField.getText().trim();
		String gender = genderField.getText().trim();
		String doctor = doctorField.getText().trim();
		String sickness = sicknessField.getText().trim();
		String priority = (String) priorityBox.getSelectedItem();

		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Patient Name is required.", "Missing Information",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		Vector<Object> row = new Vector<>();
		row.add(name);
		row.add(age);
		row.add(gender);
		row.add(doctor);
		row.add(sickness);
		row.add(priority);
		allData.set(selectedRow, row);
		refreshTable();
		clearForm();
	}

	private int getPriority(String Priority) {
		switch (Priority) {
		case "Critical":
			return 1;
		case "Serious":
			return 2;
		case "Good":
			return 3;
		default:
			return 4;
		}
	}

	private void organize(ActionEvent e) {
		String[] options = { "Minimum Heap", "Maximum Heap" };
		int choice = JOptionPane.showOptionDialog(this, "Select a Heap type: ", "Organizing the patients",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (choice == -1) {
			return;
		}
		PriorityQueue<Vector<Object>> queue;
		if (choice == 1) {
			queue = new PriorityQueue<>((a, b) -> {
				int priorityA = getPriority(a.get(5).toString());
				int priorityB = getPriority(b.get(5).toString());
				return Integer.compare(priorityA, priorityB);

			});
		} else {
			queue = new PriorityQueue<>((a, b) -> {
				int priorityA = getPriority(a.get(5).toString());
				int priorityB = getPriority(b.get(5).toString());
				return Integer.compare(priorityB, priorityA);

			});
		}

		Vector<Vector<Object>> organizeData = new Vector<>();
		for (Vector<Object> patient : allData) {
			queue.add(patient);
		}
		while (!queue.isEmpty()) {
			Vector<Object> patient = queue.poll();
			organizeData.add(patient);
		}

		allData.clear();
		allData.addAll(organizeData);

		refreshTable();

	}

	private void onServe(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Select a patient to be serve.", "No Selection",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this, "Serve this patient?", "Confirm Serve",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			Vector<Object> servedPatient = allData.get(selectedRow);
			saveServePatient(servedPatient);
			allData.remove(selectedRow);
			refreshTable();
			saveToFile();
			clearForm();
		}
	}

	private void loadSelectedRowIntoForm() {
		int row = table.getSelectedRow();
		if (row == -1)
			return;
		nameField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
		ageField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
		genderField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
		doctorField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
		sicknessField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
		priorityBox.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
	}

	private void clearForm() {
		nameField.setText("");
		ageField.setText("");
		genderField.setText("");
		doctorField.setText("");
		sicknessField.setText("");
		priorityBox.setSelectedIndex(0);
		table.clearSelection();
	}

	private void refreshTable() {
		tableModel.setRowCount(0);
		for (Vector<Object> row : allData) {
			tableModel.addRow(row);
		}
	}

	private void loadFromFile() {
		File f = new File(FILE_PATH);
		if (!f.exists())
			return;

		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",", -1);
				if (parts.length < COLUMNS.length)
					parts = Arrays.copyOf(parts, COLUMNS.length);

				Vector<Object> row = new Vector<>();
				for (String part : parts) {
					row.add(part);
				}

				allData.add(row);

			}

			refreshTable();

		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error while packing the file", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void saveToFile() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
			for (int r = 0; r < tableModel.getRowCount(); r++) {
				for (int c = 0; c < tableModel.getColumnCount(); c++) {
					if (c > 0)
						bw.write(",");
					bw.write(String.valueOf(tableModel.getValueAt(r, c)));
				}
				bw.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error in packing the file. Please try again", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void saveServePatient(Vector<Object> patient) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))) {
			for (int i = 0; i < patient.size(); i++) {
				bw.write(patient.get(i).toString());

				if (i < patient.size() - 1) {
					bw.write(",");
				}
			}
			bw.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new HospitalManagementGUI();
	}

}
