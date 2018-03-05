package snobplanet.util.obsolescencemeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import snobplanet.util.obsolescencemeter.urlaccess.UrlRetriever;


/**
 * Main Class / Classe principale
 * 
 */
public class ObsolescenceTool {
	
	
		/**
		 * Contiens tout les fichiers de conf Maven trouvé dans la structure du projet
		 */
		private List<File> lstPom = new ArrayList<File>();
		
		/**
		 * Tout les modèles et Map du projet
		 */
		private Map<String, Model> allPOM = new HashMap<String,Model>();
		
		/**
		 * Toutes les dépendans identifiées, externes ou interne au projet.
		 */
		private Map<String, Dependency> allDependencies = new HashMap<String, Dependency>();  
		
		/**
		 * Toutes les dépendances externes au projet mises en commun
		 */
		private Map<String, Dependency> extDependencies = new HashMap<String, Dependency>();
		
		/**
		 * Fragment du nom du projet, permet d'identifier les composants du prj des librairies externes
		 */
		private String projectBaseName;
		

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		System.out.println("Go!");
		
		ObsolescenceTool obt = new ObsolescenceTool();
		
		if (args.length == 2) {
			
			String startLocation = args[0];
			
			obt.projectBaseName = args[1];
			
			obt.findAllPOM(startLocation);
			
			obt.mapApples();
			
		} else {
			showUsage();
		}

	}

	private static void showUsage() {
		System.out.println("parameter 1: Project root directory path");
	}
	
	// TODO : Niveau d'abstraction à rajouter à ce niveau là : le dispatch vers des factory pour pouvoir traiter d'autres 
	// moteurs de build comme gradle ou Ivy 
	// Déporter tout ce qui sera spécifique à un moteur de build dans des classes séparées
	/**
	 * @param startDir
	 * @return nb de fichier de configuration Maven trouvé
	 */
	public int findAllPOM(String startDir) {
		
		int toRtn=0;
		
		File fStart = new File(startDir);
		
		if (fStart.exists() == false) {
			System.out.println("L'emplacement de départ indiqué n'existe pas : " + startDir);
			return 0;
		}
		
		this.searchDir4EffectivePOM(fStart);
		
		toRtn = lstPom.size();
		
		System.out.println("Nb POM >\t" + toRtn);
		
		System.out.println(lstPom);
		
		return toRtn;
	}
	

	/**
	 * Identifie tout les pom.xml d'une arboresence
	 * @param fstart
	 * @return
	 */
	public void searchDir4EffectivePOM(File fstart){
		File[] rf = fstart.listFiles();
		
		for ( File f:rf) {
			if (f.isDirectory()){
				searchDir4EffectivePOM(f);
			} else 
			if (f.isFile()){
				if (f.getName().equalsIgnoreCase("pom.xml"))
					lstPom.add(f);
			}
		}
	}
	
	/**
	 * Méthode principale qui ordonnance le déroullement de l'application
	 */
	public void mapApples(){
		Model model;
		String key;
		for (File ff: lstPom ){
			model  = this.readApple(ff);
			
			key  = this.generateKeyFromModel(model);
			if (allPOM.get(key) == null)
			{
				allPOM.put(key, model);
			}
			this.retrieveDependencies(model);
		}
		
		this.splitDependencies(this.allDependencies, this.projectBaseName);
		
		this.showInfosOnExtDepInCSV(this.extDependencies);
	}
	
	/**
	 * Création d'une clé à partir d'un model Maven
	 * @param model
	 * @return
	 */
	protected String generateKeyFromModel(Model model) {
		String artifact = model.getArtifactId();
		String group = model.getGroupId();
		String v = model.getVersion();
	
		String key2Rtn = strings2key(group, artifact, v);
		return key2Rtn;
	}
	
	/** 
	 * Unique méthode pour générer une clé pour la map des artifacts
	 * @param group
	 * @param artifact
	 * @param version
	 * @return clé unique pour un artifact
	 */
	private final String strings2key(String group, String artifact, String version) {
		return  group+"|"+artifact+"|"+version;
	}

	/**
	 * Lecture d'un fichier de configuration Maven
	 * @param fichier de configucation Maven
	 */
	public Model readApple(File pom) {
			
			Reader reader = null;
			Model model =  null;
			try {
				reader = new FileReader(pom);		
				MavenXpp3Reader mxr = new MavenXpp3Reader();			
				model = mxr.read(reader);				
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch ( XmlPullParserException xppe) {
				xppe.printStackTrace();
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
			return model;
	}
	
	
	// TODO : A revoir, cette méthode doit fournir un fichier csv ou xls en sortie
	private void showInfosOnExtDepInCSV (Map<String, Dependency> deps){
		
		for (Dependency d: deps.values()){
			String versionInfos = UrlRetriever.compareArtifact(d.getGroupId(), d.getArtifactId(), d.getVersion());
			System.out.println(d.getGroupId()  + ";" + d.getArtifactId() +";" + d.getVersion() + ";"+ versionInfos);

		}
	}
	
	/**
	 * Fait le tri entre les dépendances externes et internes à l'orgnisation d'un projet
	 * Seule les dépendances externes sont prise en compte
	 * @param allDependencies
	 * @param baseProjectName
	 */
	protected void splitDependencies(Map<String, Dependency> allDependencies, String baseProjectName) {
		for (Entry<String, Dependency> e : allDependencies.entrySet()){			
			if ( e.getValue().getGroupId().contains(baseProjectName) == false) {
				this.extDependencies.put(e.getKey(), e.getValue());
			} 	
		}
	}

	/**
	 * Pour un modèle, enrichi le modèle global de ses dépendances, évite les doubons
	 * @param mod  modèle Maven
	 */
	public void retrieveDependencies(Model mod)  {
		
		List<Dependency> lst = mod.getDependencies();
		
		for (Dependency d:lst){
			String g = d.getGroupId();
			String a = d.getArtifactId();
			String v = d.getVersion();
			
			String key = strings2key(g, a, v);
			
			if (allDependencies.get(key) == null){
				allDependencies.put(key, d);
				
			}
			
		}
	}

	public List<File> getLstPom() {
		return lstPom;
	}

	public Map<String, Dependency> getAllDependencies() {
		return allDependencies;
	}

	public Map<String, Dependency> getExtDependencies() {
		return extDependencies;
	}
	
	
	
}
