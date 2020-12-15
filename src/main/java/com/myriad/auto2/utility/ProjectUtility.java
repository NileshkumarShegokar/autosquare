/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.utility;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author nshegoka
 */
public class ProjectUtility {

    public static void saveProject(Project project) {
        if (project.getLocation() != null){
            File file = new File(project.getLocation());
        if (file != null) {
            //delete existing project/folder as save only new data
            String[]entries = file.list();
            if(entries!=null) {
                for (String s : entries) {
                    File currentFile = new File(file.getPath(), s);
                    currentFile.delete();
                }
                file.delete();
            }
            file.mkdirs();
            ObjectMapper mapper = new ObjectMapper();
            try {

                String projString = mapper.writeValueAsString(project);
                Project projValue = mapper.readValue(projString, Project.class);
                projValue.setCases(null);
                try (FileWriter writer = new FileWriter(file + "//" + projValue.getName() + ".auto")) {
                    mapper.writeValue(writer, projValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            for (TestCase tCase : project.getCases()) {
                try (FileWriter writer = new FileWriter(file + "//" + tCase.getName() + ".test",false)) {
                    mapper.writeValue(writer, tCase);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    }

    public static void openProject(Window window) {
        FileChooser chooser = new FileChooser();
                chooser.setTitle("Open AutoSQUARE Projects");
                File file = chooser.showOpenDialog(window);
                chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("AutoSQUARE Project File", "*.auto")
                );
                //open file and 
                if (file != null) {
                    
                    ObjectMapper mapper = new ObjectMapper();
                    Project project = null;
                    try (FileReader reader = new FileReader(file)) {
                        JsonNode rootNode = mapper.readTree(reader);
                        project = mapper.convertValue(rootNode, Project.class);
                        String parent=file.getParent();
                        for (File test : new File(parent).listFiles()) {
                            if (!test.isDirectory()) {
                                if (getFileExtension(test).equalsIgnoreCase("test")) {
                                    JsonNode testNode = mapper.readTree(test);
                                    TestCase testCase = mapper.convertValue(testNode, TestCase.class);
                                    if (project == null) {
                                        System.out.println("Project is null");
                                    }
                                    if (testCase == null) {
                                        System.out.println("test case null");
                                    }
                                    project.addTestCase(testCase);
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(project!=null){
                        boolean addProject;
                        addProject = Resource.addProject(project);
                        Resource.updateTree();
                    }
                }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
