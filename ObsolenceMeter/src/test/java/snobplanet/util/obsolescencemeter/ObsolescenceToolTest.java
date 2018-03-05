package snobplanet.util.obsolescencemeter;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.maven.model.Model;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

public class ObsolescenceToolTest extends TestCase {
	
	private ObsolescenceTool obt;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// reinitialisation de l'instance 
		 obt = new ObsolescenceTool();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindAllPOM() {

		obt.findAllPOM("./src/test/resources/datatest/");
		
		assertEquals(2, obt.getLstPom().size());
		
		assertEquals("pom.xml", obt.getLstPom().get(0).getName());
		
		assertEquals("pom.xml", obt.getLstPom().get(1).getName());
	}
	
	@Test
	public void testFindAllPOMFail() {
		
		obt.findAllPOM("./toto//test/resources/datatest/");
		
		assertEquals(0, obt.getLstPom().size());
	}



	@Test
	public void testGenerateKeyFromModel() {
		
		File file4Test = new File(".\\src\\test\\resources\\datatest\\framework\\pom.xml");
		
		Model m = obt.readApple(file4Test);
		
		String myKey = obt.generateKeyFromModel(m);
		
		assertEquals("snobtest.delivery|sas-business|1.0.0-SNAPSHOT", myKey );
		
	}

	@Test
	public void testReadApple() {
		
		File file4Test = new File(".\\src\\test\\resources\\datatest\\framework\\pom.xml");
		
		Model m = obt.readApple(file4Test);
		
		assertNotNull(m);
		
		assertEquals("sas-business",m.getArtifactId() );
		
		assertEquals(37, m.getDependencies().size());
		
	}
	
	@Test
	public void testRetrieveDependencies() {
		
		File file4Test = new File(".\\src\\test\\resources\\datatest\\framework\\pom.xml");
		Model m = obt.readApple(file4Test);
		obt.retrieveDependencies(m);
		
		File file4Test2 = new File(".\\src\\test\\resources\\datatest\\pom.xml");
		Model m2 = obt.readApple(file4Test2);
		obt.retrieveDependencies(m2);
		
		assertEquals(obt.getAllDependencies().size(), 52);
		
	}
	
	@Test
	public void testsplitDependencies() {
		
		File file4Test2 = new File(".\\src\\test\\resources\\datatest\\pom.xml");
		Model m2 = obt.readApple(file4Test2);
		obt.retrieveDependencies(m2);
		
		obt.splitDependencies(obt.getAllDependencies(), "snobtest");
		
		assertNotSame(obt.getExtDependencies().entrySet().size(), obt.getAllDependencies().size()); 
			
		
	}
	
	

}
