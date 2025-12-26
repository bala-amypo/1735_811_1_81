package com.example.demo;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestResultListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Running: " + result.getName());
    }
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("PASS: " + result.getName());
    }
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("FAIL: " + result.getName());
    }
}