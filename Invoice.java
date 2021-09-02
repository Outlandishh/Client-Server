package Application;

import java.util.ArrayList;

// This function is used to take all the information gathered in the reports and convert them into
// a products list. This list is then used to create an individual total for each report, then
// used to calculate the overall value of the reports received
public class Invoice {

    //Attributes
    private ArrayList<Product> products;
    private double total;

    //Constructor
    public Invoice(ArrayList<Product> products, double total) {
        this.products = products;
        this.total = total;
    }

    //Methods - Getters and Setters
    public ArrayList<Product> getProducts() {
        return products;
    }

    //Methods - Getters and setters
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    //Methods - Getters and Setters
    public double getTotal() {
        return total;
    }

    //Methods - Getters and Setters
    public void setTotal(double total) {
        this.total = total;
    }

    //Method
    public String toString() {

        for(Product p: products) {
            System.out.println(p.toString());
        }
        return "Invoice Total: $" + String.format("%.2f", getTotal()) + "\n";
    }

    static class Product {

        //Attributes
        private String name;
        private double price;

        //Constructor
        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        //Methods - Getters and Setters
        public String getName() {
            return name;
        }

        //Methods
        public void setName(String name) {
            this.name = name;
        }

        //Methods - Getters and Setters
        public double getPrice() {
            return price;
        }

        // Methods - Getters and Setters
        public void setPrice(double price) {
            this.price = price;
        }

        //Method
        public String toString() {
            return getName() + ": " + "$" + String.format("%.2f", getPrice());
        }
    }
}