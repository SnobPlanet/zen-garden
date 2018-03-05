package snobplanet.util.obsolescencemeter.urlaccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean définissant les éléments sur l'histoire d'une dépendance
 */
public class DependencyHistory {
	
	private String groupid;
	private String artifactid;
	private String license;	
	private String categories;
	private String tags;
	private String description;
	
	/**
	 * Toutes les versions
	 */
	private List<DependencyVersion> allVersions;
	
	/**
	 * Les versions du repo central
	 */
	private List<DependencyVersion> centralVersions = null;
	
	/**
	 * les versions spécrifiques, ne sont à prendre en compte dans les calculs que dans des cas spécifiques
	 */
	private List<DependencyVersion> otherVersions =  null;
	
	public String toString(){
		String toRtn = getGroupid() + " : " + getArtifactid() + " : " + getLicense() + " : " + getCategories() + " : " +getTags() + " : " + getDescription() + " : " + getAllVersions().size(); 				
		return toRtn;
	}
	
	/**
	 * Fais le tri entre les version du repo "central" et les autres
	 * Cela est nécessaires car dans la majorité des cas les versions ne venant pas du repo central ne sont pas à prendre en compte dans l'historique
	 */
	public void computeCentralNOtherVersions(){		
		setCentralVersions(new ArrayList<DependencyVersion>());
		setOtherVersions(new ArrayList<DependencyVersion>());
		for (DependencyVersion vh :getAllVersions()) {			
			if(vh.getRepo().equalsIgnoreCase("central")){
				getCentralVersions().add(vh);
			} else {
				getOtherVersions().add(vh);
			}			
		}		
	}
	
	// TODO Pourrait être supprimé si utilisation plus haut des getters uniquement
	/**
	 * Formate sous forme textuelle/CSV les informations additionnelles
	 * Traite les cas où pas d'infos de disponibles
	 * @return chaine contenant catégorie, tags, Description, Licence
	 */
	public String formatAdditionalInfos(){
		StringBuffer toRtn = new StringBuffer();
		
		if (getCategories() != null){
			toRtn.append(getCategories() );
		}
		toRtn.append(";");		
		if (getTags() != null){
			toRtn.append(getTags());
		}
		toRtn.append(";");
		if (getDescription() != null){
			toRtn.append(getDescription());
		}
		toRtn.append(";");
		if (getLicense() != null) {
			toRtn.append(getLicense());
		}
		toRtn.append(";");
		
		return toRtn.toString();
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getArtifactid() {
		return artifactid;
	}

	public void setArtifactid(String artifactid) {
		this.artifactid = artifactid;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DependencyVersion> getAllVersions() {
		return allVersions;
	}

	public void setAllVersions(List<DependencyVersion> allVersions) {
		this.allVersions = allVersions;
	}

	public List<DependencyVersion> getCentralVersions() {
		return centralVersions;
	}

	public void setCentralVersions(List<DependencyVersion> centralVersions) {
		this.centralVersions = centralVersions;
	}

	public List<DependencyVersion> getOtherVersions() {
		return otherVersions;
	}

	void setOtherVersions(List<DependencyVersion> otherVersions) {
		this.otherVersions = otherVersions;
	}
}