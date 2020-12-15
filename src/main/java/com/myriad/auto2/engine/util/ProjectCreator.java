package com.myriad.auto2.engine.util;

import com.myriad.auto2.model.Project;
import com.myriad.auto2.model.TestCase;



public class ProjectCreator {

	public static Project createProject(String projectName) {
		return new Project(projectName);

	}

	public static TestCase createTestCase(Project project, String testCaseName) {
		          TestCase testCase = new TestCase(testCaseName);
		testCase.setLocation(project.getHomeURL());
		project.getCases().add(testCase);
		return testCase;
	}
}
