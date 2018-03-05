package snobplanet.util.obsolescencemeter.urlaccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Classe utilitaire qui collecte en ligne les informations d'une dépendance Maven
 */
public class UrlRetriever{
	
	// TODO : Pourrait être dans un fichier de conf 
	/**
	 * Url d'accès à Mvn reposi
	 */
	private static final String BASE_URL  = "https://mvnrepository.com/artifact/";

	// TODO : A supprimer et à passer sous forme de tests
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("GO");

		
		String cmpArt = compareArtifact("org.aopalliance", "com.springsource.org.aopalliance", "1.0.0");
		
		String cmpArt0 = compareArtifact("org.springframework", "spring-core", "4.3.2.RELEASE");
		
		String cmpArt1 = compareArtifact("org.jsoup", "jsoup", "1.6.2");
		
		System.out.println("Final " + cmpArt);
		
		System.out.println("Final " + cmpArt0);
		System.out.println("Final " + cmpArt1);
	}
	
	/**
	 * Retourne les informations de la position d'une version dans son historique 
	 * API principale de la classe
	 * @param group
	 * @param artifact
	 * @param version
	 * @return
	 */
	public static String compareArtifact(String group, String artifact, String version){
		
		String toRtn = "" + group + ";" + artifact ;
		
		DependencyHistory depHist = retrieveEltsWithJSoup(makeUrlfromDep(group, artifact));
		
		String result;
		String additionalInfos;
		
		if (depHist == null) {
			result = "Not Available";
			additionalInfos = ";;;;;;;;";	
		} else {
			result =  generateCompareVersionInfo(version, depHist);
			additionalInfos = depHist.formatAdditionalInfos(); 
		}
		
		return toRtn + ";" + result +  additionalInfos;
	}
	
	/**
	 * Aggrège sur une ligne les infos sur l'age d'une version d'une dépendance
	 */
	protected static String  generateCompareVersionInfo(String version, DependencyHistory depHist) {
		
		String toRtn = "";

		String last = getLastVersionInfo(depHist);
		
		int retard = computeRetard(version, depHist, last);
		
		String curVersionInfo = findCurrenVersInfos(version, depHist, toRtn);
		
		toRtn =  curVersionInfo+";"+ retard + ";" + last;
		
		return toRtn;
	}

	/**
	 * @param version
	 * @param depHist
	 * @param toRtn
	 * @return
	 */
	private static String findCurrenVersInfos(String version, DependencyHistory depHist, String toRtn) {
		for (DependencyVersion vers : depHist.getAllVersions()){
			
			if (vers.getVersion().equalsIgnoreCase(version)){
				toRtn = vers.getVersion() + ";" +vers.getDate();
				break;
			}

		}
		return toRtn;
	}


	/**
	 * @param version
	 * @param depHist
	 * @param last
	 * @return Nombre de versions de retard
	 */
	public static int computeRetard(String version, DependencyHistory depHist, String last) {
		int retard;
		if (last.toLowerCase().contains(";central;")){ // on va calculer l'age par rapport à un repo central
			retard = findNumberOfVersionsSinceLast(depHist.getCentralVersions(), version);
		} else { // sinon on le calcule sur le totalité des historiques des versions
			retard = findNumberOfVersionsSinceLast(depHist.getAllVersions(), version);
		}
		return retard;
	}
	
	/**
	 * Calcule le nombre de version de retard
	 * @param myList liste de toutes les versiond d'une dépendance
	 * @param version courrante
	 * @return Nb de version de retard
	 */
	private static int findNumberOfVersionsSinceLast(List<DependencyVersion> myList, String version){
		int retard = 0;		
		for (DependencyVersion vers : myList){			
			if (vers.getVersion().equalsIgnoreCase(version)){
				break;
			}
			retard ++;
		}
		return retard;
	}


	/**
	 * @param depHist
	 * @return
	 */
	public static String getLastVersionInfo(DependencyHistory depHist) {
		
		DependencyVersion lastVersion = null;
		
		if (depHist.getCentralVersions().isEmpty() == false) { // cas nominal
			if (depHist.getCentralVersions().get(0) != null) { // A priori ne devrait jamais etre null
				lastVersion = depHist.getCentralVersions().get(0);
			}
		} else if (depHist.getOtherVersions().isEmpty() == false) {
			if (depHist.getOtherVersions().get(0) != null) { // A priori ne devrait jamais etre null
				lastVersion = depHist.getOtherVersions().get(0);
			}
		} else {
			System.out.println("ALERTE : Aucune derniere version de trouvée pour " + depHist.getGroupid());
			return null;
		}
		
		String last = lastVersion.getVersion() + ";" + lastVersion.getRepo() + ";" + lastVersion.getDate() + ";";
		return last;
	}

	
	public static String makeUrlfromDep(String group, String artifact){
		String toRtn = BASE_URL + group + "/" + artifact;		
		return toRtn;	
	}
	
	
	/**
	 * @param myUrl
	 * @return bean contenant les informations de dépendances
	 */
	public static DependencyHistory retrieveEltsWithJSoup(String myUrl){
		
		DependencyHistory dh  = null;
		Document doc =  null;
		
			try {
				
				doc = Jsoup.connect(myUrl).get();
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return null;
			}
			finally {
				// Pas de comportements spécifque à implémenter pour l'instant
			}
			
			if (doc.title().contains("404 - Not Found")){  // No infos available
				return null;
			}
			
			if (doc != null ){
				dh = findStuffInHtmlDoc(doc);
			}		
		
		dh.computeCentralNOtherVersions();
		
		return dh ;
	
	}


	/**
	 * @param Modèle de document
	 * @return les éléments historiques d'un artéfact
	 */
	private static DependencyHistory findStuffInHtmlDoc(Document doc) {
		DependencyHistory dh;
		dh  =  new DependencyHistory();
		
		dh.setGroupid(doc.title());
		
		Elements b_lic  = doc.getElementsByClass("b lic");
		if (b_lic != null){
			String license = b_lic.first().text();
			dh.setLicense(license);
		} else {
			dh.setLicense(null);
		}
		
		Elements b_c  = doc.getElementsByClass("b c");
		if (b_c.first() != null) {
			String cat = b_c.first().text();
			dh.setCategories(cat);
		} else {
			dh.setCategories(null);
		}
		
		Elements im_description =  doc.getElementsByClass("im-description");
		if (im_description != null){
			String desc = im_description.first().text();
			dh.setDescription(desc);
		} else {
			dh.setDescription(null);
		}
		
		Elements b_tag = doc.getElementsByClass("b tag");
		if (b_tag !=null){
			if (b_tag.first()!=null){
				String tags = b_tag.first().text();
				dh.setTags(tags);
			} else {
				dh.setTags(null);
			}
		} else {
			dh.setTags(null);
		}
				
		Element versionsTable = doc.getElementsByClass("grid versions").first();
		
		Elements tableTBobyS = versionsTable.getElementsByTag("tbody");
		
		dh.setAllVersions(new ArrayList<DependencyVersion>());
		
		for (Element tableTBoby : tableTBobyS){			
			Elements lines = tableTBoby.getElementsByTag("tr");

			for (Element line : lines){
				
				String version = "";
				String repo = "";
				String date = "";
				Elements allTDs = line.getElementsByTag("td");
				if (allTDs.size() == 5){
					version = allTDs.get(1).text();
					repo = allTDs.get(2).text();
					date = allTDs.get(4).text();
				} else if (allTDs.size() == 4){
					version = allTDs.get(0).text();
					repo = allTDs.get(1).text();
					date = allTDs.get(3).text();
				}
				
				if (version != null) {
						date = modifyDate(date);
						DependencyVersion nv = new DependencyVersion();
						nv.setVersion(version);
						nv.setRepo(repo);
						nv.setDate(date);
						dh.getAllVersions().add(nv);
				}	
			}					
		}
		return dh;
	}
	
	/**
	 * Méthode utilitaire, transformation du format d'une date
	 * @param date
	 * @return
	 */
	private static String modifyDate(String date){
		String toRtn = date.replace("(","").replace(")", "").replace(",", "").replace("-", " ");
		return "\"" + toRtn + "\"";
	}
	
}