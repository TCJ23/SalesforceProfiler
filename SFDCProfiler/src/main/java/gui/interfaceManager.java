package gui;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import profile.ProfileManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;

public class interfaceManager extends JFrame {

	private JPanel contentPane;
	
	public JButton inputFolderButton;
	public JButton outputFolderButton;
	public JButton templateFolderButton;
	public JButton runButton;
	
	// LABELS
	public static JLabel templatePathLabel;
	public static JLabel inputPathLabel;
	public static JLabel outputPathLabel;
	
	public  static JLabel messageLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					interfaceManager frame = new interfaceManager();
					frame.setVisible(true);	
					
					String[] args={ProfileManager.metadataDirectoryRoot, ProfileManager.metadataFullPermissionDirectoryRoot, ProfileManager.metadataOutputDirectoryRoot, 
							ProfileManager.metadataClassToRemove, ProfileManager.metadataPagesToRemove, ProfileManager.metadataObjectToRemove};
					ProfileManager.setProperties(args);
					
					templatePathLabel.setText(ProfileManager.metadataFullPermissionDirectoryRoot);	            		
            			inputPathLabel.setText(ProfileManager.metadataDirectoryRoot);	            		
            			outputPathLabel.setText(ProfileManager.metadataOutputDirectoryRoot);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void runClient() {
	    SwingUtilities.invokeLater(new Runnable() {
	    	// @Override
	    	public void run() {
	            String[] args={ProfileManager.metadataDirectoryRoot, ProfileManager.metadataFullPermissionDirectoryRoot, ProfileManager.metadataOutputDirectoryRoot, 
	            		ProfileManager.metadataClassToRemove, ProfileManager.metadataPagesToRemove, ProfileManager.metadataObjectToRemove};
	            
	            try {
	            		
	            		templatePathLabel.setText(ProfileManager.metadataFullPermissionDirectoryRoot);	            		
	            		inputPathLabel.setText(ProfileManager.metadataDirectoryRoot);	            		
	            		outputPathLabel.setText(ProfileManager.metadataOutputDirectoryRoot);
					
	            		ProfileManager.main(args);
					
					// SET messageLabel
					messageLabel.setText(ProfileManager.message.toString());
					
				} catch (Exception e) {

					e.printStackTrace();
				}
	        }
	    });
	}

	/**
	 * Create the frame.
	 */
	public interfaceManager() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 675, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JButton runButton = new JButton("Start!");
		runButton.setBounds(6, 290, 663, 29);
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runClient();				
			}
		});
		contentPane.setLayout(null);
		contentPane.add(runButton);
		
		JButton templateFolderButton = new JButton("Template Folder");
		templateFolderButton.setBounds(6, 6, 170, 39);
		templateFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 JFileChooser fileChooser = new JFileChooser();
				 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 
			        int returnValue = fileChooser.showOpenDialog(null);
			        if (returnValue == JFileChooser.APPROVE_OPTION) {
			        		
			        		ProfileManager.metadataFullPermissionDirectoryRoot =  fileChooser.getSelectedFile().getAbsolutePath();
			        		templatePathLabel.setText(ProfileManager.metadataFullPermissionDirectoryRoot.toString());
			        		 
			        }
			}
		});
		contentPane.add(templateFolderButton);
		
		JButton inputFolderButton = new JButton("Input Profile Folder");
		inputFolderButton.setBounds(6, 47, 170, 39);
		inputFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 
			        int returnValue = fileChooser.showOpenDialog(null);
			        if (returnValue == JFileChooser.APPROVE_OPTION) {
			        		
			        		ProfileManager.metadataDirectoryRoot =  fileChooser.getSelectedFile().getAbsolutePath();
			        		inputPathLabel.setText(ProfileManager.metadataDirectoryRoot.toString());

			        }
				
			}
		});
		contentPane.add(inputFolderButton);
		
		JButton outputFolderButton = new JButton("Output Profile Folder");
		outputFolderButton.setBounds(6, 87, 170, 39);
		outputFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 
			        int returnValue = fileChooser.showOpenDialog(null);
			        if (returnValue == JFileChooser.APPROVE_OPTION) {
			        		
			        		ProfileManager.metadataOutputDirectoryRoot =  fileChooser.getSelectedFile().getAbsolutePath()+"/";
			        		outputPathLabel.setText(ProfileManager.metadataOutputDirectoryRoot.toString());
			        		 
			        }
			        
			}
		});
		contentPane.add(outputFolderButton);
		
		final JCheckBox userPermissionsCheckBox = new JCheckBox("User Permissions");
		userPermissionsCheckBox.setBounds(6, 138, 140, 23);
		userPermissionsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(userPermissionsCheckBox.isSelected() && ProfileManager.userPermission == false){
					ProfileManager.userPermission = true;
				} else {
					ProfileManager.userPermission = false;
				}
			}
		});
		contentPane.add(userPermissionsCheckBox);
		
		final JCheckBox pageAccessesCheckBox = new JCheckBox("Page Accesses");
		pageAccessesCheckBox.setBounds(158, 138, 140, 23);
		pageAccessesCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pageAccessesCheckBox.isSelected() && ProfileManager.pageAccesses == false){
					ProfileManager.pageAccesses = true;
				} else {
					ProfileManager.pageAccesses = false;
				}
			}
		});
		contentPane.add(pageAccessesCheckBox);
		
		final JCheckBox classAccessesCheckBox = new JCheckBox("Class Accesses");
		classAccessesCheckBox.setBounds(310, 138, 140, 23);
		classAccessesCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(classAccessesCheckBox.isSelected() && ProfileManager.classAccesses == false){
					ProfileManager.classAccesses = true;
				} else {
					ProfileManager.classAccesses = false;
				}
			}
		});
		contentPane.add(classAccessesCheckBox);
		
		final JCheckBox objectPermissionsCheckBox = new JCheckBox("Object Permissions");
		objectPermissionsCheckBox.setBounds(6, 173, 153, 23);
		objectPermissionsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(objectPermissionsCheckBox.isSelected() && ProfileManager.objectPermissions == false){
					ProfileManager.objectPermissions = true;
				} else {
					ProfileManager.objectPermissions = false;
				}
			}
		});
		contentPane.add(objectPermissionsCheckBox);
		
		final JCheckBox fieldPermissionsCheckBox = new JCheckBox("Field Permissions");
		fieldPermissionsCheckBox.setBounds(158, 173, 153, 23);
		fieldPermissionsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(fieldPermissionsCheckBox.isSelected() && ProfileManager.fieldPermissions == false){
					ProfileManager.fieldPermissions = true;
				} else {
					ProfileManager.fieldPermissions = false;
				}
			}
		});
		contentPane.add(fieldPermissionsCheckBox);
		
		final JCheckBox removeUserPermissionCheckBox = new JCheckBox("Remove User Permissions");
		removeUserPermissionCheckBox.setBounds(6, 220, 193, 23);
		removeUserPermissionCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(removeUserPermissionCheckBox.isSelected() && ProfileManager.removeUserPermissions == false){
					ProfileManager.removeUserPermissions = true;
				} else {
					ProfileManager.removeUserPermissions = false;
				}
			}
		});
		contentPane.add(removeUserPermissionCheckBox);
		
		final JCheckBox removeClassAccessCheckBox = new JCheckBox("Remove Class Access");
		removeClassAccessCheckBox.setBounds(385, 220, 167, 23);
		removeClassAccessCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(removeClassAccessCheckBox.isSelected() && ProfileManager.removeClassAccesses == false){
					ProfileManager.removeClassAccesses = true;
				} else {
					ProfileManager.removeClassAccesses = false;
				}
			}
		});
		contentPane.add(removeClassAccessCheckBox);
		
		final JCheckBox removePageAccessCheckBox = new JCheckBox("Remove Page Access");
		removePageAccessCheckBox.setBounds(211, 220, 162, 23);
		removePageAccessCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(removePageAccessCheckBox.isSelected() && ProfileManager.removePageAccesses == false){
						ProfileManager.removePageAccesses = true;
				} else {
					ProfileManager.removePageAccesses = false;
				}
			}
		});
		contentPane.add(removePageAccessCheckBox);
		
		final JCheckBox removeObjectsCheckBox = new JCheckBox("Remove Objects");
		removeObjectsCheckBox.setBounds(6, 255, 133, 23);
		removeObjectsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(removeObjectsCheckBox.isSelected() && ProfileManager.removeObjects == false){
						ProfileManager.removeObjects = true;
				} else {
					ProfileManager.removeObjects = false;
				}
			}
		});
		contentPane.add(removeObjectsCheckBox);
		
		messageLabel = new JLabel("");
		messageLabel.setBounds(6, 321, 663, 51);
		contentPane.add(messageLabel);
		
		 templatePathLabel = new JLabel("Template Path");
		templatePathLabel.setBounds(188, 16, 481, 16);
		contentPane.add(templatePathLabel);
		
		 inputPathLabel = new JLabel("Input Path");
		inputPathLabel.setBounds(188, 57, 481, 16);
		contentPane.add(inputPathLabel);
		
		 outputPathLabel = new JLabel("Output Path");
		outputPathLabel.setBounds(188, 97, 481, 16);
		contentPane.add(outputPathLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 125, 663, 12);
		contentPane.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(6, 279, 663, 12);
		contentPane.add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(6, 196, 663, 12);
		contentPane.add(separator_2);
		
	}
}
