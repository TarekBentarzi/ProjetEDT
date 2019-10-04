package modeles;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class JsonFileManager  implements FileManager{
	static ObjectMapper objectMapper;
	static JsonFileManager jsonFileManager;
	public static int lastId=0;
	private String MismatchFile="Format de fichier invalid";


	/*
	 *utilisation de design singleton dans Constructeur pour l
assuré une seule instociation de JsonFileManage et ObjectMapper
 */
	public static JsonFileManager getInstance() {
		if(jsonFileManager==null)
			jsonFileManager=new JsonFileManager();
		if(objectMapper==null)
			objectMapper=new ObjectMapper();
		return jsonFileManager;
	}

	/*
	 * (non-Javadoc)
	 * @see modeles.FileManager#load(java.io.File)
	 */
	@Override
	public TimeTable load(File file) {
		TimeTableV2 tmv2;
		try {
			tmv2=objectMapper.readValue(file,TimeTableV2.class);
		} 
		catch(MismatchedInputException e1) {
			JOptionPane.showMessageDialog(null,modeles.Constants.errLoadFile+MismatchFile,modeles.Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return null;	
		}
		catch (IOException e) {
			
			JOptionPane.showMessageDialog(null,modeles.Constants.errLoadFile+e.getMessage(),modeles.Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return null;
		}
		return tmv2.toTimeTable();
	}


/*
 * (non-Javadoc)
 * @see modeles.FileManager#save(modeles.TimeTable, java.lang.String)
 */
	@Override
	public boolean save(TimeTable tm , String path) {
		TimeTableV2 listCr=tm.toTimeTableV2();
		
		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.writeValue( new File(path+".json"),listCr );
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,modeles.Constants.errSaveFile+e.getMessage() ,modeles.Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return false;
		}
		return true;
	}


}
