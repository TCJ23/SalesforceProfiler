package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {

	public static void main(String[] args) {
		
		generateFile(args[0],args[1]);

	}
	
	
	public static void generateFile(String rootPath, String filePath){
		
		List<String> pathList =new ArrayList<String>();
		listf(pathList,rootPath,rootPath);
		createFile(pathList, filePath);
			
	}
	
	

    public static List<File> listf(List<String> pathList,String directoryName,String rootPath) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile() && !file.isHidden()) {
                pathList.add(convertPath(file.getAbsolutePath(),rootPath));
            } else if (file.isDirectory() && file.listFiles().length!=0 && !file.isHidden()) {  // se non è una directory vuota
                resultList.addAll(listf(pathList,file.getAbsolutePath(),rootPath));
            }
        }
        //System.out.println(fList);
        return resultList;
    } 
    
    
    public static String convertPath(String path,String rootPath){
    	
    	if(path!=null){
    		path=path.replaceFirst(rootPath, "");    		
    	}
    	
    	 System.out.println(path+"\n");
    	
    	return path;
    }
    
    
    public static void createFile(List<String> pathList, String filePath){
    	
    	try {
    	    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
    	    	
    	    	for(int i =0;i<pathList.size();i++){
	    	        writer.write(pathList.get(i));
	    	        writer.newLine();
	    	        writer.flush();
    	    	}
    	        writer.close();
    	} catch (IOException ex) {
    	    System.out.println("Output File could not be created");
    	}
    	
    	
    }
	
	
	
	

}
