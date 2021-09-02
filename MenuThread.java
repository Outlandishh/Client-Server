package Application;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import Application.Invoice.Product;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


// Create the menu to appear as a command prompt
public class MenuThread extends Thread{

    public void run() {

        // Print a list of the commands expected from the user
        while(true) {
            System.out.println("Please select from the following:");
            System.out.println("Enter 'R' to generate report, the total of all invoices saved");
            System.out.println("Enter 'D' to delete all invoices from the server");
            System.out.println("Enter 'E' to terminate the server");

            Scanner scanner = new Scanner(System.in);
            System.out.println("\nEnter the task to perform");
            String task = scanner.nextLine().toUpperCase();

            // Depending on which selection is made, perform an action (print report, delete, quit)
            switch (task) {
                case "R":
                    System.out.println(report());
                    break;
                case "D":
                    delete();
                    break;
                case "E":
                    System.exit(0);
                default:
                    System.out.println("Invalid Task\n");
            }
        }
    }

    // If 'R' is selected, run this function that compiles and prints the report
    private String report() {

        //Reference the invoice.java file which compiles a list of products
        ArrayList<Invoice.Product> products = new ArrayList<Invoice.Product>();
        double total = 0;
        Invoice invoice = new Invoice(products, total);
        List<Invoice> invoices = new ArrayList<Invoice>();

        String name = null;
        double price = 0;

        Invoice.Product product = new Invoice.Product(name, price);

        System.out.print("INVOICE REPORT:\n");

        //Read the list of files added from the client (/home/Invoices)
        String path = System.getProperty("user.home") + File.separator + "Invoices";


        File directory = new File(path);
        File fileList[] = directory.listFiles();

        // Take all files in directory and read them based on their file type (.json or .xml)
        for (File file:fileList) {
            String readThisFile = file.getName().toLowerCase();

            // If the file type is .json, create a reader that can understand the file type, then convert
            // the JSON string to an object. Search the file for instances of 'name' and 'price' which
            // allows the invoice to be parsed and made readable in the result section further down.
            if(readThisFile.endsWith(".json")) {

                try {
                    //Create a Json instance
                    Gson gson = new Gson();

                    //Create a reader
                    URI uri = file.toURI();
                    Reader reader = Files.newBufferedReader(Paths.get(uri));

                    //Convert JSON string to object
                    JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

                    reader.close();

                    products = new ArrayList<Product>();

                    JsonArray jsonArray = jsonObject.get("invoice").getAsJsonObject().get("products").getAsJsonObject().get("product").getAsJsonArray();

                    for(JsonElement element: jsonArray) {
                        JsonObject elementAsJsonObject = element.getAsJsonObject();
                        name = elementAsJsonObject.get("name").getAsString();
                        price = elementAsJsonObject.get("price").getAsDouble();

                        product = new Product(name, price);
                        products.add(product);
                    }

                    total = jsonObject.get("invoice").getAsJsonObject().get("total").getAsDouble();

                    invoice = new Invoice(products, total);
                    invoices.add(invoice);
                    System.out.println(invoice.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }



            // Similar to the .json section above, however as .xml is in a different format, information
            // must be extracted a bit differently. This section searches for 'name' and 'price' and adds
            // them to the product list. They are then able to the called and tallied in the results.
            }  else if (readThisFile.endsWith(".xml")) {

                try {
                    //Make an XML document
                    Document xml = null;

                    xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

                    // Search the xml for occurrences of name and price
                    NodeList nameList = xml.getElementsByTagName("name");
                    NodeList priceList = xml.getElementsByTagName("price");
                    products = new ArrayList<Product>();
                    for(int i = 0; i < nameList.getLength(); i++) {
                        name = nameList.item(i).getTextContent();
                        price = Double.parseDouble(priceList.item(i).getTextContent());

                        product = new Product(name, price);
                        products.add(product);
                    }

                    NodeList nodeList = xml.getElementsByTagName("total");
                    // As there is only 1 item in the list, it is at position zero
                    total = Double.parseDouble(nodeList.item(0).getTextContent());

                    invoice = new Invoice(products, total);
                    invoices.add(invoice);
                    System.out.println(invoice.toString());

                // Exception handling (user input errors, wrong file type, etc.)
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }


            }

        }
        // Add all the invoice totals together
        total = 0;
        for(Invoice i: invoices) {
            total += i.getTotal();
        }
        // Display the result of all invoices combined
        return "The total amount for " + invoices.size() + " invoices is: $" + String.format("%.2f", total) + "\n---------------------";
  }

    // Function to delete all invoices
    private void delete() {

        // Search for the invoice location
        String path = System.getProperty("user.home") + File.separator + "Invoices";

        // Display message warning the user of their actions
        System.out.println("WARNING!!! You've chosen to DELETE ALL INVOICES!\n" +
                "This will delete all files ending with .xml or .json from " + path);

        System.out.println("The following files will be deleted: \n");

        // Make a list of the files about to be deleted
        File directory = new File(path);
        File fileList[] = directory.listFiles();

        // Print the list of the files to be deleted
        for (File file:fileList) {
            String fileToDelete = file.getName().toLowerCase();
            if (fileToDelete.endsWith(".xml")||fileToDelete.endsWith(".json")); {
                System.out.println(fileToDelete);
            }
        }

        // Prompt the user to confirm the deletion
        System.out.println("Type 'DELETE' and press enter to proceed \n" +
                "OR enter any key to cancel");

        // Delete all files in the /home/Invoices directory that end in .json or .xml
        Scanner scanner = new Scanner(System.in);
        String delete = scanner.nextLine().toUpperCase();
        // If the user has input DELETE, go ahead and delete the files
        if(delete.equals("DELETE")) {
            for (File file:fileList) {
                String fileToDelete = file.getName().toLowerCase();
                if (fileToDelete.endsWith(".xml")||fileToDelete.endsWith(".json")) {
                    file.delete();
                }
            }
            System.out.println("All invoices have been deleted\n");
        // If the user has input anything other than DELETE, return to the main menu
        } else {
            System.out.println("Operation cancelled. No invoices were deleted\n");
        }
    }
}