package modeles;

import java.io.File;

public interface FileManager {

	/**
	 * Charge un emploi du temps en memoire depuis un fichier
	 * 
	 * @param file
	 * 			Fichier depuis lequel creer un emploi du temos
	 * @return Nouvel emploi du temps creer a partir du fichier
	 */
	public TimeTable load(File file) ;

	/**
	 * 
	 * Convertie un emploi du temps au format JSON
	 * 
	 * @param tm
	 * 		Emploi du temps a convertir
	 * @param path
	 * 		Chemin ou stocker le fichier
	 * 
	 * @return vraie si la sauvegarde s'est bien passee, faux sinon
	 */
	public boolean save( TimeTable tm ,String path);

	
}
