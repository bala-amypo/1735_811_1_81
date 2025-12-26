package com.example.demo;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest {

    /**
     * This test ensures that the TestNG runner is correctly 
     * identifying and executing tests in your environment.
     */
    @Test(groups = "smoke")
    public void testEnvironmentReady() {
        String status = "OK";
        Assert.assertEquals(status, "OK", "The test environment should be properly initialized.");
    }

    /**
     * Verifies that basic assertions are working.
     */
    @Test(groups = "smoke")
    public void testBasicAssertion() {
        int expected = 10;
        int actual = 5 + 5;
        Assert.assertEquals(actual, expected, "Basic math operations should work in the test context.");
    }
}