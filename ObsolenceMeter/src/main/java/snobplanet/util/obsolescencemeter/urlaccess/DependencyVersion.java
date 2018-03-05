package snobplanet.util.obsolescencemeter.urlaccess;


/**
 * Bean définissant les infos spécifques à une version
 *
 */
public class DependencyVersion {
	
	/**
	 * Numéro de version
	 */
	private String version;
	
	/**
	 * Nom du repository
	 */
	private String repo;
	
	/**
	 * Date de publication
	 */
	private String date;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
	}

	String getDate() {
		return date;
	}

	void setDate(String date) {
		this.date = date;
	}
	
}