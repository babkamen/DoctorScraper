package com.geymer;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.Assert.assertThat;


/**
 * Created by babkamen on 21.04.2016.
 */
public class MainTest {
    String expected = "Singh, Mandeep (#89092)\n" +
            "Room MCL 2-405\n" +
            "399 Bathurst Street\n" +
            "Toronto ON  M5T 2S8\n" +
            "\n" +
            "Singh, Mandeep (#99933)\n" +
            "Credit Valley Hospital\n" +
            "Department of Psychiatry\n" +
            "2200 Eglinton Avenue West\n" +
            "Mississauga ON  L5M 2N1\n" +
            "\n" +
            "Abadi, Babak (#67093)\n" +
            "CAMH PACE Clinic\n" +
            "Room 1326\n" +
            "80 Workman Way\n" +
            "Toronto ON  M6J 1H4\n" +
            "Phone: (416) 535-8501 Ext. 32722\n" +
            "Fax: (416) 583-1296\n" +
            "\n";
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Test
    public void testSuccess() throws Exception {
        File outputFile= folder.newFile("out.txt");
        System.out.println(outputFile.getAbsolutePath());
        String inputFile = new File("src/test/resources/doctors.xlsx").getAbsolutePath();
        String args[] = {inputFile, outputFile.getAbsolutePath()};
        Main.main(args);
        String actual=IOUtils.toString(new FileInputStream(outputFile.getAbsolutePath()));
        assertThat(actual, Matchers.equalToIgnoringWhiteSpace(expected));
    }
}