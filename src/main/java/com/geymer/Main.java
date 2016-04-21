package com.geymer;


import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static final String URL = "http://www.cpso.on.ca/Public-Register/All-Doctors-Search";
    private static String commandDescription = "java -jr doctorscrapper.jar sourceExcelFileLocation outputFolder. \n " +
            "For example java -jr doctor-scrapper.jar C:\\in.xls D:\\folder\\out.txt";

    public static void main(String[] args) throws Exception {
        validate(args);
        List<Doctor> doctors = readXlsxFile(args[0]);
        PrintWriter printWriter = new PrintWriter(args[1]);
        WebClient webClient = new WebClient();
        WebClientOptions options = webClient.getOptions();
        options.setThrowExceptionOnScriptError(false);
        options.setRedirectEnabled(true);
        options.setThrowExceptionOnFailingStatusCode(false);
        if (doctors.size() > 0)
            for (Doctor d : doctors) {
                grab(webClient, printWriter, d.getFirstName(), d.getLastName());
            }
        printWriter.close();
    }

    private static void validate(String[] args) {
        if(args.length<2||args[0].equals("--help")) {
            System.out.println("Please provide arguments\n"+commandDescription);
            System.exit(-1);
        }
        String xlsLocation = args[0];
        File file = new File(xlsLocation);
        if(!file.exists()){
            System.out.print("Provided Xlsx file doesn't exist!"+xlsLocation);
            System.exit(-1);
        }
    }

    private static List<Doctor> readXlsxFile(String location) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(new File(location));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();


                Cell cell = cellIterator.next();
                Cell cell2 = cellIterator.next();
                doctors.add(new Doctor(cell.getStringCellValue(), cell2.getStringCellValue()));
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public static void grab(WebClient webClient, PrintWriter out, String firstName, String lastName) throws IOException {
        WebRequest requestSettings = new WebRequest(new URL(URL), HttpMethod.POST);
        requestSettings.setAdditionalHeader("content-type", "application/x-www-form-urlencoded");
        requestSettings.setAdditionalHeader("cache-control", "no-cache");

        String requestBody = "__EVENTTARGET=p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24btnSubmit&__EVENTARGUMENT=&__LASTFOCUS=&lng=en-CA&__VIEWSTATEGENERATOR=A5343185&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=1258&p%24lt%24ctl00%24SearchBox%24txtWord=Site+Search&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24txtLastName=" + lastName + "&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24txtFirstName=" + firstName + "&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24grpGender=+&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24ddLanguage=08&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24grpDocType=rdoDocTypeAll&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24grpStatus=rdoStatusActive&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24ddCity=Select+--%3E&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24txtPostalCode=&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24ddHospitalCity=Select+--%3E&p%24lt%24ctl03%24pageplaceholder%24p%24lt%24ctl03%24AllDoctorsSearch%24ddHospitalName=-1";
        requestSettings.setRequestBody(requestBody);
        HtmlPage redirectPage = webClient.getPage(requestSettings);
        List<HtmlAnchor> doctorFullNames = (List<HtmlAnchor>) redirectPage.getByXPath("//a[@class='doctor']");
        for (HtmlAnchor a : doctorFullNames) {
            DomNode td = a.getParentNode();
            out.println(td.getTextContent().trim());
            out.println(td.getNextSibling().getNextSibling().asText());
            out.println();
        }
    }


    public static class Doctor {
        private String firstName;
        private String lastName;

        public Doctor(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public String toString() {
            return "Doctor{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }

}
