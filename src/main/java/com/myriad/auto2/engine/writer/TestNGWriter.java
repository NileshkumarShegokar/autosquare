package com.myriad.auto2.engine.writer;

import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nileshkumar_shegokar on 6/21/2017.
 */
public class TestNGWriter {

    public static void xmlWriter(String path, String artifact, String testPackage, String[] browsers, Project project,
            Boolean override) { // Create an instance on TestNG
        TestNG myTestNG = new TestNG();

        // Create an instance of XML Suite and assign a name for it.
        XmlSuite mySuite = new XmlSuite();
        mySuite.setName("MySuite");
        mySuite.setParallel(XmlSuite.ParallelMode.METHODS);
        // Create a list of XmlTests and add the Xmltest you created earlier to it.
        List<XmlTest> myTests = new ArrayList<XmlTest>();

        for (String browser : browsers) {
            // Create an instance of XmlTest and assign a name for it.
            XmlTest myTest = new XmlTest(mySuite);
            myTest.setName(browser + "Test");

            // Add any parameters that you want to set to the Test.
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("browser", browser);
            myTest.setParameters(parameters);

            // Create a list which can contain the classes that you want to run.
            List<XmlClass> myClasses = new ArrayList<XmlClass>();

            for (TestCase testCase : project.getCases()) {
                myClasses.add(new XmlClass(testPackage + "." + testCase.getName(), true));
            }

            // Assign that to the XmlTest Object created earlier.
            myTest.setXmlClasses(myClasses);
            myTests.add(myTest);
        }

        // add the list of tests to your Suite.
        mySuite.setTests(myTests);

        // Add the suite to the list of suites.
        List<XmlSuite> mySuites = new ArrayList<XmlSuite>();
        mySuites.add(mySuite);

        // Set the list of Suites to the testNG object you created earlier.
        myTestNG.setXmlSuites(mySuites);
        mySuite.setFileName(artifact + ".xml");
        mySuite.setThreadCount(3);
        myTestNG.run();

        // Create physical XML file based on the virtual XML content
        for (XmlSuite suite : mySuites) {
            createXmlFile(path, artifact, suite);
        }
        System.out.println("Filerated successfully.");
    }

    // This method will create an Xml file based on the XmlSuite data
    public static void createXmlFile(String location, String artifactId, XmlSuite mSuite) {

        FileWriter writer;
        try {

            String newPath = location + File.separator + artifactId;
            // + File.separator + "src" + File.separator + "main"+ File.separator + "resources";
            Files.createDirectories(Paths.get(newPath));
            writer = new FileWriter(new File(newPath + File.separator + artifactId + ".xml"));
            writer.write(mSuite.toXml());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
