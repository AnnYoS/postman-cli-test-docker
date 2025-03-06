package com.annyo.postmanclitest.newman;

import com.annyo.postmanclitest.container.NewmanContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext
class NewmanDockerApiTest {

    @Value("${report.directory}")
    private String reportDirectory;

    static NewmanContainer newmanContainer = null;

    @AfterAll
    public static void afterAll() {
        if (newmanContainer != null) {
            newmanContainer.stop();
        }
    }

    @Test
    public void testApiWithNewman() throws Exception {
        // Configure the container with the collection and the output directory
        newmanContainer = new NewmanContainer("postman-collection.json", reportDirectory)
                .withEnvironmentVariable("baseUrl", InetAddress.getLocalHost().getHostAddress());

        // Start the container
        newmanContainer.start();

        // Asserting that container exit correctly
        Long exitCode = newmanContainer.getCurrentContainerInfo()
                .getState()
                .getExitCodeLong();

        assertEquals(0, exitCode, "Newman container end with code: " + exitCode);

        // Asserting that reports exist in the output directory
        File junitReport = newmanContainer.getJunitReportFile();
        File jsonReport = newmanContainer.getJsonReportFile();

        assertTrue(junitReport.exists(), "JUnit report isn't created");
        assertTrue(jsonReport.exists(), "JSON report isn't created");

        // Inside the JUnit report, searching for a <failure> tag, meaning a request failure
        // In that case, it's test failure
        Document junitDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(junitReport);
        NodeList nodeList = junitDocument.getElementsByTagName("failure");

        assertEquals(0, nodeList.getLength(), "JUnit report have " + nodeList.getLength() + " failures");
    }
}
