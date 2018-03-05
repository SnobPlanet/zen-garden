/**
 * 
 */
package snobplanet.util.obsolescencemeter.urlaccess;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Result;

import junit.framework.TestCase;

/**
 *
 *
 */
public class UrlRetrieverTest extends TestCase{

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link snobplanet.util.obsolescencemeter.urlaccess.UrlRetriever#compareArtifact(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCompareArtifact() {
		String cmpArt = UrlRetriever.compareArtifact("org.aopalliance", "com.springsource.org.aopalliance", "1.0.0");
		assertFalse(cmpArt.contains("Not Available"));
	}
	

	/**
	 * Test method for {@link snobplanet.util.obsolescencemeter.urlaccess.UrlRetriever#compareArtifact(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testFailCompareArtifact() {
		String cmpArt = UrlRetriever.compareArtifact("org.aopalliance", "com.springsource.org.aopatoto", "1.0.0");
		assertTrue(cmpArt.contains("Not Available"));
	}
	

	/**
	 * Test method for {@link snobplanet.util.obsolescencemeter.urlaccess.UrlRetriever#computeRetard(java.lang.String, snobplanet.util.obsolescencemeter.urlaccess.DependencyHistory, java.lang.String)}.
	 */
	@Test
	public void testComputeRetard() {
		
		DependencyHistory depHist = UrlRetriever.retrieveEltsWithJSoup(UrlRetriever.makeUrlfromDep("org.springframework", "spring-core"));
		
		String result =   UrlRetriever.generateCompareVersionInfo("4.3.2.RELEASE", depHist);
		
		assertTrue(result.length() >6); // car chaine de 6 ';' minimum
		
	}

	/**
	 * Test method for {@link snobplanet.util.obsolescencemeter.urlaccess.UrlRetriever#getLastVersionInfo(snobplanet.util.obsolescencemeter.urlaccess.DependencyHistory)}.
	 */
	@Test
	public void testGetLastVersionInfo() {
		DependencyHistory depHist = UrlRetriever.retrieveEltsWithJSoup(UrlRetriever.makeUrlfromDep("org.springframework", "spring-core"));
		
		String lastHisto = UrlRetriever.getLastVersionInfo(depHist);
		
		assertNotNull(lastHisto);
		assertTrue(lastHisto.length()>0);
		
	}

	/**
	 * Test method for {@link snobplanet.util.obsolescencemeter.urlaccess.UrlRetriever#retrieveEltsWithJSoup(java.lang.String)}.
	 */
	@Test
	public void testRetrieveEltsWithJSoup() {
		DependencyHistory depHist = UrlRetriever.retrieveEltsWithJSoup(UrlRetriever.makeUrlfromDep("org.springframework", "spring-core"));
		
		assertNotNull(depHist);
		
		assertTrue(depHist.getAllVersions().size() > 0);
	}

	
}
