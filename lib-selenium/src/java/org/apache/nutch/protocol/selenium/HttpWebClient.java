package org.apache.nutch.protocol.selenium;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.String;

public class HttpWebClient {

    private static final Logger LOG = LoggerFactory.getLogger("org.apache.nutch.protocol");

    public static ThreadLocal<WebDriver> threadWebDriver = new ThreadLocal<WebDriver>() {

        @Override
        protected WebDriver initialValue()
        {
//            return new FirefoxDriver(); //You can use other driver based on your requirement.
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("permissions.default.stylesheet", 2);
            WebDriver driver = new FirefoxDriver(profile);
            return driver;
        };
    };

    public static String getHtmlPage(String url, Configuration conf) {
        try {
            WebDriver driver = threadWebDriver.get();
            if (driver == null) {
                driver = new FirefoxDriver();
            }

            driver.get(url);

            // Wait for the page to load, timeout after 3 seconds
            new WebDriverWait(driver, 3);

            // Return a page maybe? Maybe just body innerHTML?
            return driver.findElement(By.tagName("body")).getText();

            //Close the browser
//            driver.quit();
            // I'm sure this catch statement is a code smell ; borrowing it from lib-htmlunit
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    public static String getHtmlPage(String url) {
        return getHtmlPage(url, null);
    }
}