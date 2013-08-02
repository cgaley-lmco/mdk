package gov.nasa.jpl.mgss.mbee.docgen.model.ui;

import gov.nasa.jpl.mgss.mbee.docgen.model.LibraryMapping;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.jdesktop.swingx.JXTreeTable;

public class LibraryChooserUI {

	private JFrame frame;
	private final LibraryMapping libraryMapping;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LibraryChooserUI window = new LibraryChooserUI(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LibraryChooserUI(LibraryMapping lm) {
		this.libraryMapping = lm;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		getFrame().setBounds(100, 100, 450, 300);
		getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);
		
		JLabel lblCharacterizationChooser = new JLabel("Library Chooser");
		panel_1.add(lblCharacterizationChooser);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		LibraryTreeTableModel treeTableModel = new LibraryTreeTableModel(libraryMapping);
		JXTreeTable treeTable = new JXTreeTable(treeTableModel);
		scrollPane.setViewportView(treeTable);
		treeTable.setHorizontalScrollEnabled(true);
		treeTable.setShowGrid(true);
		treeTable.setGridColor(Color.LIGHT_GRAY);
		treeTable.setBorder(new EtchedBorder(EtchedBorder.RAISED));  
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("Save");
		panel.add(btnOk);
		btnOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				libraryMapping.apply();
				frame.dispose();
			}
		});
		
		
		JButton btnCancel = new JButton("Cancel");
		panel.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				frame.dispose();
			}
		});
	}

	public JFrame getFrame() {
		return frame;
	}

}