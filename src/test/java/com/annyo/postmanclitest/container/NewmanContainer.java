package com.annyo.postmanclitest.container;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NewmanContainer extends GenericContainer<NewmanContainer> {

    private static final String IMAGE_NAME = "postman/newman";
    private static final String DEFAULT_JSON_REPORT = "json-report.json";
    private static final String DEFAULT_JUNIT_REPORT = "junit-report.xml";

    private final String collectionPath;
    private final String reportDirectory;
    private final Map<String, String> environmentVariables;

    private String jsonReportName = DEFAULT_JSON_REPORT;
    private String junitReportName = DEFAULT_JUNIT_REPORT;

    public NewmanContainer(String collectionPath, String reportDirectory) {
        super(DockerImageName.parse(IMAGE_NAME));
        this.collectionPath = collectionPath;
        this.reportDirectory = reportDirectory;
        this.environmentVariables = new HashMap<>();

        // Default Configuration
        // Using waitFor() to block the process until collection is full tested inside the container
        withCopyFileToContainer(
                MountableFile.forClasspathResource(collectionPath),
                "/" + new File(collectionPath).getName());
        waitingFor(Wait.forLogMessage(".*total run duration.*", 1));
    }

    @Override
    public void start() {
        // Create output directory if not exist
        File reportsDir = new File(reportDirectory);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        String absolutePath = reportsDir.getPath();

        // Bind container report directory with host output to access to generated reports
        withFileSystemBind(reportsDir.getPath(), "/reports", BindMode.READ_WRITE);

        // Build the command to execute in the container, then start
        String collectionFileName = new File(collectionPath).getName();
        String[] command = buildCommand(collectionFileName);
        withCommand(command);

        super.start();
    }

    private String[] buildCommand(String collectionFileName) {
        // Compute the full command
        String baseCommand = "run /" + collectionFileName + " -r cli,json,junit " +
                "--reporter-json-export /reports/" + jsonReportName + " " +
                "--reporter-junit-export /reports/" + junitReportName;

        // Add environment variables
        StringBuilder commandBuilder = new StringBuilder(baseCommand);
        for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
            commandBuilder.append(" --env-var ")
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }

        return commandBuilder.toString().split(" ");
    }

    public NewmanContainer withJsonReportName(String jsonReportName) {
        this.jsonReportName = jsonReportName;
        return this;
    }

    public NewmanContainer withJunitReportName(String junitReportName) {
        this.junitReportName = junitReportName;
        return this;
    }

    public NewmanContainer withEnvironmentVariable(String key, String value) {
        this.environmentVariables.put(key, value);
        return this;
    }

    public File getJunitReportFile() {
        return new File(reportDirectory, junitReportName);
    }

    public File getJsonReportFile() {
        return new File(reportDirectory, jsonReportName);
    }
}
