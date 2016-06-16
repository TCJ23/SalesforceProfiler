package profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import file.FileManager;
import parser.ParserManager;

public class ProfileManager {

	//Parser
	public ParserManager metadataParser											= null;  // Current Metadata
	public ParserManager metadataFullPermissionParser 					= null;  // Profile Metadata with All Permission Setted
	
	//File Manager
	public FileManager inputManager 													= null;
	public FileManager templateManager 											= null;
	
	// Constants
	public static String NODENAME_USER_PERMISSION 					= "userPermissions";
	public static String NODENAME_PAGE_ACCESSES						= "pageAccesses";
	public static String NODENAME_USER_LICENSE 						= "userLicense";
	public static String NODENAME_CLASS_ACCESSES					= "classAccesses";
	public static String ROOTNODENAME_PROFILE 							= "Profile";
	public static String REMOVE_NODE_LOGINIPRANGES				= "loginIpRanges";
	public static String NODENAME_OBJECT_PERMISSIONS 			= "objectPermissions";
	public static String NODENAME_FIELD_PERMISSIONS 				= "fieldPermissions";
	public static String NODENAME_TAB_VISIBILITIES 					= "tabVisibilities";
	public static String NODENAME_LAYOUT_ASSIGNMENTS 		= "layoutAssignments";
	
	// Metadata Status Attribute
	public static String metadataDirectoryRoot 									= null;
	public static String metadataFullPermissionDirectoryRoot			= null;
	public static String metadataOutputDirectoryRoot						= null;
	
	// Metadata Files containing Nodes to Remove
	public static String metadataClassToRemove 								= null;
	public static String metadataPagesToRemove    							= null;
	public static String metadataObjectToRemove 							= null;
	public static String objectName 														= null;
	
	// Filters Application
	public static Boolean userPermission 											= false;
	public static Boolean pageAccesses												= false;
	public static Boolean classAccesses 												= false;
	public static Boolean objectPermissions 										= false;
	public static Boolean fieldPermissions											= false;
	
	// Remove Filters
	public static Boolean removeUserPermissions 								= false;
	public static Boolean removeClassAccesses 								= false;
	public static Boolean removePageAccesses 								= false;
	public static Boolean removeObjects 											= false;
	
	
	// Alert Template Message
	public static String message = "";
	
	
	
	public ProfileManager(String metadataDirectoryRoot, String metadataFullPermissionDirectoryRoot, String metadataOutputDirectoryRoot, String metadataClassToRemove, String metadataPagesToRemove, String metadataObjectToRemove){
		this.metadataDirectoryRoot = metadataDirectoryRoot;
		this.metadataFullPermissionDirectoryRoot = metadataFullPermissionDirectoryRoot;
		this.metadataOutputDirectoryRoot = metadataOutputDirectoryRoot;
		this.metadataClassToRemove = metadataClassToRemove;
		this.metadataPagesToRemove = metadataPagesToRemove;
		this.metadataObjectToRemove = metadataObjectToRemove;
	}
	
	
	public void processMetadataList(){
		inputManager= new FileManager(this.metadataDirectoryRoot);
		
		System.out.println("Root Directory: "+this.metadataDirectoryRoot);
		System.out.println("Template Directory: "+this.metadataFullPermissionDirectoryRoot);		
		
		File[] files 	= inputManager.getFileList();
		System.out.println("Metadata to process: "+files.length);
		
		templateManager= new FileManager(this.metadataFullPermissionDirectoryRoot);
		File[] templates = templateManager.getFileList();
		System.out.println("Template to process: "+templates.length);
		
		ArrayList<String> filesUserLicenses = new ArrayList<String>();
		ArrayList<String> templateUserLicenses = new ArrayList<String>();

		for(File f:files){

			if(!f.isHidden()){
				
				metadataParser = loadMetadata(f);
				
				for(File tfp:templates){
					
					if(!tfp.isHidden()){
						
						metadataFullPermissionParser = loadMetadata(tfp);
						
						String fileUserLicense = metadataParser.document.getElementsByTagName(NODENAME_USER_LICENSE).item(0).getTextContent();
						System.out.println(fileUserLicense);
						String templateUserLicense = metadataFullPermissionParser.document.getElementsByTagName(NODENAME_USER_LICENSE).item(0).getTextContent();
						
						if(templateUserLicense.equalsIgnoreCase(fileUserLicense)){
							if(!templateUserLicenses.contains(templateUserLicense)){
								templateUserLicenses.add(templateUserLicense);
							}
							System.out.println(f.getName());
							processMetadata(f);
							FileManager.saveXmlFile(metadataParser.document,new File(metadataOutputDirectoryRoot+f.getName()));

						}						
						else {
							if(!filesUserLicenses.contains(fileUserLicense)){
								filesUserLicenses.add(fileUserLicense);
							}
						}
					}
					
				}
			}
		}
		
		for(String missingTemplate: filesUserLicenses){
			if(!templateUserLicenses.contains(missingTemplate)){
				
				message = "Attenzione!!! Non esiste il template per la User License: -- "+missingTemplate+" --";
				
				System.out.println(message);
				
			}
		}
	}
	

	public ParserManager loadMetadata(File metadata){
		return new ParserManager(metadata, true);	
	}
	
	
	public void processMetadata(File metadata) {
		
		metadataParser = loadMetadata(metadata);		

		System.out.println("Removed Login IP Ranges");
		removeLoginIpRanges(metadataParser.document);
		
		if(userPermission){
			System.out.println("Selected User Permission!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			setUserPermission(metadataFullPermissionParser.document, metadataParser.document);		
			
		} 
		
		if(pageAccesses){
			System.out.println("Selected Page Accesses!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			setPageAccesses(metadataFullPermissionParser.document, metadataParser.document);
			
		}
		
		if(classAccesses){
			System.out.println("Selected Class Accesses!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			setClassAccesses(metadataFullPermissionParser.document, metadataParser.document);
			
		} 
		
		if(objectPermissions){
			System.out.println("Selected Object Permissions!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			setObjectPermission(metadataFullPermissionParser.document, metadataParser.document);
		}
		
		if(fieldPermissions){
			System.out.println("Selected Field Permissions!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			setFieldPermission(metadataFullPermissionParser.document, metadataParser.document);
		}
		
		if(removeUserPermissions){
			System.out.println("Selected Remove User Permissions!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			removeUserPermissions(metadataParser.document);

		}
		
		if(removeClassAccesses) {
			System.out.println("Selected Remove Class Accesses!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			removeClassAccesses(metadataParser.document);
		}
		
		if(removePageAccesses){
			System.out.println("Selected Remove Page Accesses!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			removePageAccesses(metadataParser.document);
		}
		
		if(removeObjects){
			System.out.println("Selected Remove Objects!");
			System.out.println("Process Metadata: "+metadata.getName());
			
			removeObjects(metadataParser.document);
		}
		
		
	}	
	
	
	public void setUserPermission(Document fullProfile, Document targetProfile){
	
		System.out.println("  -  -  -  SET USER PERMISSION  -  -  -");
		NodeList fp_nList = fullProfile.getElementsByTagName(NODENAME_USER_PERMISSION);
		NodeList tp_nList = targetProfile.getElementsByTagName(NODENAME_USER_PERMISSION);
		
	    //CreateMap
		HashMap fp_permission_map= new HashMap();
		HashMap tp_permission_map= new HashMap();
	
		
		for (int i = 0; i < tp_nList.getLength(); i++) {
			 
			Node nNode = tp_nList.item(i);
	 
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionName 	 = eElement.getElementsByTagName("name").item(0).getTextContent();
				String permissionEnabled = eElement.getElementsByTagName("enabled").item(0).getTextContent();
				
				System.out.println("TARGET PROFILE - Permission Name : " + permissionName+ ", Enabled: "+permissionEnabled);
				tp_permission_map.put(permissionName,eElement);
				
			}
		}
		
		for (int i = 0; i < fp_nList.getLength(); i++) {
	 
			Node nNode = fp_nList.item(i);
	 	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionName 	 =eElement.getElementsByTagName("name").item(0).getTextContent();
				String permissionEnabled =eElement.getElementsByTagName("enabled").item(0).getTextContent();
				
				System.out.println("FULL PERMISSION PROFILE - Permission Name : " + permissionName + ", Enabled: "+permissionEnabled);
				fp_permission_map.put(permissionName,eElement);
				
				addMissingPermissionsToUser(targetProfile, tp_permission_map, eElement);
			}
		}				
	}
	
	
	public void addMissingPermissionsToUser(Document targetPermissionDoc, HashMap targetPermissionMap, Element permissionFullElem){
		
		System.out.println("- - - ADD MISSING PERMISSIONS TO FALSE - - -");
		
		String permissionName 	 = "";

		permissionName = permissionFullElem.getElementsByTagName("name").item(0).getTextContent();

		if(!targetPermissionMap.containsKey(permissionName)){
			System.out.println("Aggiungo User Permissions!");
		    // Create a duplicate node
		    Element newPermission = (Element)permissionFullElem.cloneNode(true);
		    // Set False Permission
		    newPermission.getElementsByTagName("enabled").item(0).setTextContent("false");
		    // Transfer ownership of the new node into the destination document
		    targetPermissionDoc.adoptNode(newPermission);
		    //Append New Permission Node
		    targetPermissionDoc.getElementsByTagName(ROOTNODENAME_PROFILE).item(0).appendChild(newPermission);
		}
}
	
	
	public void setPageAccesses(Document fullProfile, Document targetProfile){
		System.out.println("  -  -  -  SET PAGE ACCESSES   -  -  -");

		
		//Take pageAccesses Node List
		NodeList fp_nList = fullProfile.getElementsByTagName(NODENAME_PAGE_ACCESSES);
		NodeList tp_nList = targetProfile.getElementsByTagName(NODENAME_PAGE_ACCESSES);
		
	    //CreateMap
		HashMap fp_permission_map= new HashMap();
		HashMap tp_permission_map= new HashMap();
	
		
		for (int i = 0; i < tp_nList.getLength(); i++) {
			 
			Node nNode = tp_nList.item(i);
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				String permissionName 	 = eElement.getElementsByTagName("apexPage").item(0).getTextContent();
				String permissionEnabled = eElement.getElementsByTagName("enabled").item(0).getTextContent();
				
				System.out.println("TARGET PROFILE - Permission Name : " + permissionName+ ", Enabled: "+permissionEnabled);
				
				tp_permission_map.put(permissionName,eElement);
				
			}
		}
		
		for (int i = 0; i < fp_nList.getLength(); i++) {
	 
			Node nNode = fp_nList.item(i);
	 	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionName 	 =eElement.getElementsByTagName("apexPage").item(0).getTextContent();
				String permissionEnabled = eElement.getElementsByTagName("enabled").item(0).getTextContent();
				
				System.out.println("TARGET PROFILE - Permission Name : " + permissionName+ ", Enabled: "+permissionEnabled);
				
				fp_permission_map.put(permissionName,eElement);
				
				addMissingPermissionsToPage(targetProfile, tp_permission_map, eElement);
			}
		}	
	}
	
	
	public void addMissingPermissionsToPage(Document targetPermissionDoc, HashMap targetPermissionMap, Element permissionFullElem){
		
		System.out.println("- - - ADD MISSING PERMISSIONS TO FALSE - - -");
		
		String permissionName 	 = "";		
		
		permissionName = permissionFullElem.getElementsByTagName("apexPage").item(0).getTextContent();
		
		System.out.println(permissionFullElem);
		System.out.println(permissionName);

		if(!targetPermissionMap.containsKey(permissionName)){
			System.out.println("Aggiungo Page Accesses!");

		    // Create a duplicate node
		    Element newPermission = (Element)permissionFullElem.cloneNode(true);
		    // Set False Permission
		    newPermission.getElementsByTagName("enabled").item(0).setTextContent("false");
		    // Transfer ownership of the new node into the destination document
		    targetPermissionDoc.adoptNode(newPermission);
		    //Append New Permission Node
		    targetPermissionDoc.getElementsByTagName(ROOTNODENAME_PROFILE).item(0).appendChild(newPermission);
		}
}
	
	
	public void setClassAccesses(Document fullProfile, Document targetProfile){
	System.out.println("  -  -  -  SET CLASS ACCESSES   -  -  -");

		//Take classAccesses Node List
		NodeList fp_nList = fullProfile.getElementsByTagName(NODENAME_CLASS_ACCESSES);
		NodeList tp_nList = targetProfile.getElementsByTagName(NODENAME_CLASS_ACCESSES);
		
	    //CreateMap
		HashMap fp_permission_map= new HashMap();
		HashMap tp_permission_map= new HashMap();
	
		
		for (int i = 0; i < tp_nList.getLength(); i++) {
			 
			Node nNode = tp_nList.item(i);
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionName 	 = eElement.getElementsByTagName("apexClass").item(0).getTextContent();
				String permissionEnabled = eElement.getElementsByTagName("enabled").item(0).getTextContent();
				
				System.out.println("TARGET PROFILE - Permission Name : " + permissionName+ ", Enabled: "+permissionEnabled);
				
				tp_permission_map.put(permissionName,eElement);
				
			}
		}
		
		for (int i = 0; i < fp_nList.getLength(); i++) {
	 
			Node nNode = fp_nList.item(i);
	 	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionName 	 =eElement.getElementsByTagName("apexClass").item(0).getTextContent();
				
				String permissionEnabled = eElement.getElementsByTagName("enabled").item(0).getTextContent();
				
				System.out.println("TARGET PROFILE - Permission Name : " + permissionName+ ", Enabled: "+permissionEnabled);
				
				fp_permission_map.put(permissionName,eElement);
				
				addMissingPermissionsToClass(targetProfile, tp_permission_map, eElement);
			}
		}				
	}

	
	public void addMissingPermissionsToClass(Document targetPermissionDoc, HashMap targetPermissionMap, Element permissionFullElem){
		
		System.out.println("- - - ADD MISSING CLASS PERMISSIONS TO FALSE - - -");
		
		String permissionName 	 = "";	
	
		permissionName = permissionFullElem.getElementsByTagName("apexClass").item(0).getTextContent();
		
		System.out.println(permissionName);

		if(!targetPermissionMap.containsKey(permissionName)){
			System.out.println("Aggiungo Class Accesses!");

		    // Create a duplicate node
		    Element newPermission = (Element)permissionFullElem.cloneNode(true);
		    // Set False Permission
		    newPermission.getElementsByTagName("enabled").item(0).setTextContent("false");
		    // Transfer ownership of the new node into the destination document
		    targetPermissionDoc.adoptNode(newPermission);
		    //Append New Permission Node
		    targetPermissionDoc.getElementsByTagName(ROOTNODENAME_PROFILE).item(0).appendChild(newPermission);
		}
		
}
	
	public void setObjectPermission(Document fullProfile, Document targetProfile){
		
		System.out.println("  -  -  -  SET OBJECT PERMISSION  -  -  -");
		NodeList fp_nList = fullProfile.getElementsByTagName(NODENAME_OBJECT_PERMISSIONS);
		NodeList tp_nList = targetProfile.getElementsByTagName(NODENAME_OBJECT_PERMISSIONS);
		
	    //CreateMap
		HashMap fp_permission_map= new HashMap();
		HashMap tp_permission_map= new HashMap();
	
		
		for (int i = 0; i < tp_nList.getLength(); i++) {
			 
			Node nNode = tp_nList.item(i);
	 
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionName 	 = eElement.getElementsByTagName("object").item(0).getTextContent();
				String permissionCreate = eElement.getElementsByTagName("allowCreate").item(0).getTextContent();
				String permissionDelete = eElement.getElementsByTagName("allowDelete").item(0).getTextContent();
				String permissionEdit = eElement.getElementsByTagName("allowEdit").item(0).getTextContent();
				String permissionRead = eElement.getElementsByTagName("allowRead").item(0).getTextContent();
				String permissionModifyAll = eElement.getElementsByTagName("modifyAllRecords").item(0).getTextContent();
				String permissionViewAll = eElement.getElementsByTagName("viewAllRecords").item(0).getTextContent();
				
				System.out.println("TARGET PROFILE - Permission Name : " + permissionName+ ",\nCreate: "+permissionCreate + ",\nDelete: "+permissionDelete + ",\nEdit: "+permissionEdit +
													",\nRead: "+permissionRead + ",\nModify All: "+permissionModifyAll + ",\nView All: "+permissionViewAll);
				tp_permission_map.put(permissionName,eElement);
				
			}
		}
		
		for (int i = 0; i < fp_nList.getLength(); i++) {
	 
			Node nNode = fp_nList.item(i);
	 	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element eElement = (Element) nNode;
				
				String permissionName 	 = eElement.getElementsByTagName("object").item(0).getTextContent();
				String permissionCreate = eElement.getElementsByTagName("allowCreate").item(0).getTextContent();
				String permissionDelete = eElement.getElementsByTagName("allowDelete").item(0).getTextContent();
				String permissionEdit = eElement.getElementsByTagName("allowEdit").item(0).getTextContent();
				String permissionRead = eElement.getElementsByTagName("allowRead").item(0).getTextContent();
				String permissionModifyAll = eElement.getElementsByTagName("modifyAllRecords").item(0).getTextContent();
				String permissionViewAll = eElement.getElementsByTagName("viewAllRecords").item(0).getTextContent();
				
				System.out.println("FULL PERMISSION PROFILE - Permission Name : " + permissionName+ ",\nCreate: "+permissionCreate + ",\nDelete: "+permissionDelete + ",\nEdit: "+permissionEdit +
													",\nRead: "+permissionRead + ",\nModify All: "+permissionModifyAll + ",\nView All: "+permissionViewAll);
				fp_permission_map.put(permissionName,eElement);
				
				
				addMissingPermissionsToObject(targetProfile, tp_permission_map, eElement);
			}
		}				
	}
	
	
	public void addMissingPermissionsToObject(Document targetPermissionDoc, HashMap targetPermissionMap, Element permissionFullElem){
		
		System.out.println("- - - ADD MISSING OBJECT PERMISSIONS TO FALSE - - -");
		
		String permissionName 	 = "";	
	
		permissionName = permissionFullElem.getElementsByTagName("object").item(0).getTextContent();
		
		System.out.println(permissionFullElem);
		System.out.println(permissionName);

		if(!targetPermissionMap.containsKey(permissionName)){
			System.out.println("Aggiungo Object Permission!");

		    // Create a duplicate node
		    Element newPermission = (Element)permissionFullElem.cloneNode(true);
		    
		    System.out.println(newPermission);
		    // Set False Permission
		    newPermission.getElementsByTagName("allowCreate").item(0).setTextContent("false");
		    newPermission.getElementsByTagName("allowDelete").item(0).setTextContent("false");
		    newPermission.getElementsByTagName("allowEdit").item(0).setTextContent("false");
		    newPermission.getElementsByTagName("allowRead").item(0).setTextContent("false");
		    newPermission.getElementsByTagName("modifyAllRecords").item(0).setTextContent("false");
		    newPermission.getElementsByTagName("viewAllRecords").item(0).setTextContent("false");
		    // Transfer ownership of the new node into the destination document
		    targetPermissionDoc.adoptNode(newPermission);
		    //Append New Permission Node
		    targetPermissionDoc.getElementsByTagName(ROOTNODENAME_PROFILE).item(0).appendChild(newPermission);
		}
		
		
	
}
	
	
	public void setFieldPermission(Document fullProfile, Document targetProfile){
		
		System.out.println("  -  -  -  SET FIELD PERMISSION  -  -  -");
		NodeList fp_nList = fullProfile.getElementsByTagName(NODENAME_FIELD_PERMISSIONS);
		NodeList tp_nList = targetProfile.getElementsByTagName(NODENAME_FIELD_PERMISSIONS);
		
	    //CreateMap
		HashMap fp_permission_map= new HashMap();
		HashMap tp_permission_map= new HashMap();
	
		
		for (int i = 0; i < tp_nList.getLength(); i++) {
			 
			Node nNode = tp_nList.item(i);
	 
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionField 	 = eElement.getElementsByTagName("field").item(0).getTextContent();
				String permissionEditable = eElement.getElementsByTagName("editable").item(0).getTextContent();
				String permissionReadable = eElement.getElementsByTagName("editable").item(0).getTextContent();
				
				System.out.println("TARGET PROFILE - Permission Field : " + permissionField+ ", Editable: "+permissionEditable+ ", Readable: "+ permissionReadable);
				tp_permission_map.put(permissionField,eElement);
				
			}
		}
		
		for (int i = 0; i < fp_nList.getLength(); i++) {
	 
			Node nNode = fp_nList.item(i);
	 	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				
				String permissionField 	 = eElement.getElementsByTagName("field").item(0).getTextContent();
				String permissionEditable = eElement.getElementsByTagName("editable").item(0).getTextContent();
				String permissionReadable = eElement.getElementsByTagName("editable").item(0).getTextContent();
				
				System.out.println("FULL PERMISSION PROFILE - Permission Field : " + permissionField+ ", Editable: "+permissionEditable+ ", Readable: "+ permissionReadable);
				fp_permission_map.put(permissionField,eElement);
				
				addMissingPermissionsToField(targetProfile, tp_permission_map, eElement);
			}
		}				
	}
	
	
	public void addMissingPermissionsToField(Document targetPermissionDoc, HashMap targetPermissionMap, Element permissionFullElem){
		
		System.out.println("- - - ADD MISSING PERMISSIONS TO FALSE - - -");
		
		String permissionField 	 = "";

		permissionField = permissionFullElem.getElementsByTagName("field").item(0).getTextContent();

		if(!targetPermissionMap.containsKey(permissionField)){
			System.out.println("Aggiungo Field Permissions!");
		    // Create a duplicate node
		    Element newPermission = (Element)permissionFullElem.cloneNode(true);
		    // Set False Permission
		    newPermission.getElementsByTagName("editable").item(0).setTextContent("false");
		    newPermission.getElementsByTagName("readable").item(0).setTextContent("false");
		    // Transfer ownership of the new node into the destination document
		    targetPermissionDoc.adoptNode(newPermission);
		    //Append New Permission Node
		    targetPermissionDoc.getElementsByTagName(ROOTNODENAME_PROFILE).item(0).appendChild(newPermission);
		}
}
	
	
	public void removeClassAccesses(Document targetProfile){
		System.out.println("  -  -  -  REMOVE CLASS ACCESSES   -  -  -");

		//Take classAccesses Node List
		NodeList classAccesses = targetProfile.getElementsByTagName(NODENAME_CLASS_ACCESSES);
		
		File classToRemoveFile = new File(this.metadataClassToRemove);
		
		System.out.println(classToRemoveFile.getName());
		
		List<Object> list = new ArrayList<>();
		try {	
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(classToRemoveFile)));
			list = br.lines().collect(Collectors.toList());			
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		for(int i = 0; i < classAccesses.getLength(); i++){
			Node classAccess = classAccesses.item(i);
			
			Element apexClass = (Element)classAccess;
			String name = apexClass.getElementsByTagName("apexClass").item(0).getTextContent();

			if(list.contains(name)){
					classAccess.getParentNode().removeChild(classAccess);
					i--;
			}	
			
		}				
	}
	
	
	public void removePageAccesses(Document targetProfile){
		System.out.println("  -  -  -  REMOVE PAGE ACCESSES   -  -  -");

		//Take classAccesses Node List
		NodeList pageAccesses = targetProfile.getElementsByTagName(NODENAME_PAGE_ACCESSES);
		
		File pageToRemoveFile = new File(this.metadataPagesToRemove);
		
		System.out.println(pageToRemoveFile.getName());
		
		List<Object> list = new ArrayList<>();
		try {	
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pageToRemoveFile)));
			list = br.lines().collect(Collectors.toList());			
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		for(int i = 0; i < pageAccesses.getLength(); i++){
			Node pageAccess = pageAccesses.item(i);
			
			Element apexPage = (Element)pageAccess;
			String name = apexPage.getElementsByTagName("apexPage").item(0).getTextContent();
			
			
			if(list.contains(name)){
				pageAccess.getParentNode().removeChild(pageAccess);
				i--;
			}	
		}				
	}

	
	public void removeUserPermissions(Document profile){
		System.out.println("  -  -  -  REMOVE USER PERMISSION  -  -  -");
		
		NodeList userPermissions = profile.getElementsByTagName(NODENAME_USER_PERMISSION);
		
		while(userPermissions.getLength() > 0){
			Node node = userPermissions.item(0);
			node.getParentNode().removeChild(node);
		}
	}
	
	
	public void removeLoginIpRanges(Document profile){
		System.out.println("  -  -  -  REMOVE LOGIN IP RANGES  -  -  -");
		
		NodeList userPermissions = profile.getElementsByTagName(REMOVE_NODE_LOGINIPRANGES);
		
		while(userPermissions.getLength() > 0){
			Node node = userPermissions.item(0);
			node.getParentNode().removeChild(node);
		}
	}
	
	
	public void removeObjects(Document profile){
			System.out.println("  -  -  -  REMOVE OBJECTS  -  -  -");
			
			//Take classAccesses Node List
			NodeList objectPermissions = profile.getElementsByTagName(NODENAME_OBJECT_PERMISSIONS);
			NodeList fieldPermissions = profile.getElementsByTagName(NODENAME_FIELD_PERMISSIONS);
			NodeList tabVisibilities = profile.getElementsByTagName(NODENAME_TAB_VISIBILITIES);
			NodeList layoutAssignments = profile.getElementsByTagName(NODENAME_LAYOUT_ASSIGNMENTS);
			
			File objectToRemove = new File(this.metadataObjectToRemove);
			
//			System.out.println(objectToRemove.getName());
			
			List<Object> list = new ArrayList<>();
			try {	
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(objectToRemove)));
				list = br.lines().collect(Collectors.toList());			
				br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			for(int i = 0; i < objectPermissions.getLength(); i++){
				Node objectPermission = objectPermissions.item(i);
				
				Element object = (Element)objectPermission;
				objectName = object.getElementsByTagName("object").item(0).getTextContent();
				
				if(list.contains(objectName)){
					objectPermission.getParentNode().removeChild(objectPermission);
					i--;
				}	
			}	
			
			for(int i=0; i < fieldPermissions.getLength(); i++){
				Node fieldPermission = fieldPermissions.item(i);
				
				Element field = (Element)fieldPermission;
				String fullFieldName = field.getElementsByTagName("field").item(0).getTextContent();
				String fieldName = fullFieldName.substring(0, fullFieldName.indexOf('.'));
				
				if(list.contains(fieldName)){
					fieldPermission.getParentNode().removeChild(fieldPermission);
					i--;
				}
			}
			
			for(int i = 0; i < tabVisibilities.getLength(); i++){
				Node tabVisibility = tabVisibilities.item(i);
				
				Element tab = (Element)tabVisibility;
				String tabName = tab.getElementsByTagName("tab").item(0).getTextContent();
				
				if(list.contains(tabName)){
					tabVisibility.getParentNode().removeChild(tabVisibility);
					i--;
				}	
			}	
			
			for(int i = 0; i < layoutAssignments.getLength(); i++){
				Node layoutAssignment = layoutAssignments.item(i);
				
				Element layout = (Element)layoutAssignment;
				String fullLayoutName = layout.getElementsByTagName("layout").item(0).getTextContent();
				String layoutName = fullLayoutName.substring(0, fullLayoutName.indexOf('-'));
				
				if(list.contains(layoutName)){
					layoutAssignment.getParentNode().removeChild(layoutAssignment);
					i--;
				}	
			}
	}
	
	
	public static void setProperties(String[] args){
		Properties prop = new Properties();
		final InputStream inStream = ProfileManager.class.getResourceAsStream("config.properties");
		
		try {			
			prop.load(inStream);
			
			metadataDirectoryRoot = prop.getProperty("metadataDirectoryRoot").toString();
			metadataOutputDirectoryRoot = prop.getProperty("metadataOutputDirectoryRoot").toString();
			metadataFullPermissionDirectoryRoot = prop.getProperty("metadataFullPermissionDirectoryRoot").toString();
			
			metadataClassToRemove = prop.getProperty("metadataClassToRemove").toString();
			metadataPagesToRemove = prop.getProperty("metadataPagesToRemove").toString();
			metadataObjectToRemove = prop.getProperty("metadataObjectToRemove").toString();
			
			args[0] = metadataDirectoryRoot;
			args[1] = metadataFullPermissionDirectoryRoot;
			args[2] = metadataOutputDirectoryRoot;
			args[3] = metadataClassToRemove;
			args[4] = metadataPagesToRemove;
			args[5] = metadataObjectToRemove;
			
			inStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	public static void main(String[] args) throws Exception {				
		
		ProfileManager manager= new ProfileManager(args[0], args[1], args[2], args[3], args[4], args[5]);
		
		manager.processMetadataList(); 

	}

}
