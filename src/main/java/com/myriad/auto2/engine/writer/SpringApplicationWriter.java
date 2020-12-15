package com.myriad.auto2.engine.writer;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.myriad.auto2.engine.util.ResourceLoader;
import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SpringApplicationWriter {
    public static void writeSpringApplicationClass(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,null);
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getApplication();
        template=template.replaceAll("@package",pkg);
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "Application.java"), template.getBytes());
    }

    public static void writeApp(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {


        String newPath=SpringApplicationWriter.getPath(location,artifactId,pkg,null);
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getApp();
        template=template.replaceAll("@package",pkg);
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        StringBuilder builder = new StringBuilder();
        for(TestCase test:project.getCases()) {
            builder.append("classes.add(new XmlClass(\"" + pkg+".test."+test.getName() + "\"));\n");
        }

        template = template.replaceAll("@APPLICATION_CLASSES", builder.toString());


        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "App.java"), template.getBytes());
    }

    public static void writeWebSocketMessage(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,"report");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getWebSocketMessage();
        template=template.replaceAll("@package",pkg+".report");
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "WebSocketMessage.java"), template.getBytes());
    }

    public static void writeWebSocketConfiguration(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,"conf");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getWebSocketConfiguration();
        template=template.replaceAll("@package",pkg+".conf");
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "WebSocketConfiguration.java"), template.getBytes());
    }

    public static void writeReportController(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,"report");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getReportController();
        template=template.replaceAll("@package",pkg+".report");
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "ReportController.java"), template.getBytes());
    }

    public static void writeAutoSquareReportListener(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,"report");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getAutoSquareReportListner();
        template=template.replaceAll("@package",pkg+".report");
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "AutoSquareReportListener.java"), template.getBytes());
    }
    public static void writeModelReport(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,"report.model");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getModelReport();
        template=template.replaceAll("@package",pkg+".report.model");
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "Report.java"), template.getBytes());
    }
    public static void writeModelAutoSquareTest(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,"report.model");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getModelAutoSquareTest();
        template=template.replaceAll("@package",pkg+".report.model");
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "AutoSquareTest.java"), template.getBytes());
    }

    public static void writeModelAutoSquareSuite(String location, String pkg, String artifactId, Project project, Boolean overWrite) throws IOException, FormatterException {
        String newPath=getPath(location,artifactId,pkg,"report.model");
        Files.createDirectories(Paths.get(newPath));
        String template = ResourceLoader.getModelAutoSquareSuite();
        template=template.replaceAll("@package",pkg+".report.model");
        template=template.replaceAll("@basePackage",pkg);
        Files.createDirectories(Paths.get(newPath));
        template = new Formatter().formatSource(template);
        Files.write(Paths.get(newPath + File.separator + "AutoSquareSuite.java"), template.getBytes());
    }

    public static void extractUI(String location, String artifactId) throws IOException {
        ResourceLoader.extractUI(location,artifactId);
    }
    public static String getPath(String location,String artifactId,String pkg,String endPackage){
        String newPath = location + File.separator + artifactId + File.separator + "src" + File.separator + "main" + File.separator + "java";
        if(endPackage!=null)
            pkg=pkg+"."+endPackage;
        for(String newVal : pkg.split("\\.")){
            newPath += (File.separator + newVal);
        }
        return newPath;
    }
}
