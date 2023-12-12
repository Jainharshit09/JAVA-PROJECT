package mainjava;
import customer.Customer;
import customer.PremiumCustomer;
import login.Login;
import menu.MenuItem;
import billing.Billing;
import payment.Payment;
import review.Review;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import database.*;

class Main {
    private static MenuItem[] menu = new MenuItem[8];  
    static Scanner scanner=new Scanner(System.in);
    public static void initializeMenu() {
        menu[0] = new MenuItem("Pizza", 199.00);
        menu[1] = new MenuItem("Burger", 50.00);
        menu[2] = new MenuItem("Cold Coffee", 100.00);
        menu[3] = new MenuItem("Tea", 40.00);
        menu[4] = new MenuItem("Coke", 40.00);
        menu[5] = new MenuItem("Pepsi", 40.00);
        menu[6] = new MenuItem("Orange Juice", 110.00);
        menu[7] = new MenuItem("Apple Juice", 110.00);
    }
    public static void viewMenu() throws InterruptedException {
        JdbcDemo.retrieveMenuItems();
    }
    public static void displayCustomerDetails(String username) throws InterruptedException {
        Customer customer = JdbcDemo.isValidUser(username);
    
        if (customer != null) {
            System.out.println();
            Thread.sleep(100);
            System.out.println("Customer Details:");
            System.out.println();
            System.out.println("Name: " + customer.getName());
            System.out.println("Address: " + customer.getAddress());
            System.out.println("Contact: " + customer.getContact());
            System.out.println("Customer Type: " + (customer.isPremium() ? "Premium" : "Regular"));
        } else {
            System.out.println("Customer not found or an error occurred.");
        }
    }
    public static void handleMenuSelection(MenuItem[] menu, Billing billing, String username, Customer currentCustomer) throws InterruptedException {
        int maxItemId = menu.length;
        String[] orderItems = new String[100];  
    
        System.out.println("Available Food Items:");
        for (int itemId = 0; itemId < maxItemId; itemId++) {
            MenuItem item = menu[itemId];
            System.out.println((itemId + 1) + ". " + item.getName() + " - " + item.getPrice());
        }
    
        System.out.print("Enter item IDs (comma-separated) for the order: ");
        Scanner scanner = new Scanner(System.in);
        String orderInput = scanner.nextLine();
        String[] orderItemIds = orderInput.split(",");
        int orderItemCount = 0;
        Thread.sleep(100);
        for (String inputId : orderItemIds) {
            inputId = inputId.trim();
            if (!inputId.isEmpty()) {
                try {
                    int itemId = Integer.parseInt(inputId);
                    if (itemId >= 1 && itemId <= maxItemId) {
                        orderItems[orderItemCount] = inputId;
                        orderItemCount++;
                    } else {
                        System.out.println("Item with ID " + itemId + " is not available on the menu.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid item ID: " + inputId);
                }
            }
        }
    
        if (orderItemCount == 0) {
            System.out.println("No valid items selected for the order.");
            return;
        }
    
        // Creating a new array with the correct size to pass the billing methods
        String[] finalOrderItems = new String[orderItemCount];
        System.arraycopy(orderItems, 0, finalOrderItems, 0, orderItemCount);
    
        System.out.print("Enter Coupon Code: ");
        String couponCode = scanner.nextLine();
        Thread.sleep(100);
        double total = billing.calculateTotal(finalOrderItems, menu, couponCode);
        
        double total1 = billing.calculateTotal(finalOrderItems, menu,couponCode);
        int orderId;


        System.out.println();
         if (currentCustomer.isPremium()) {
        billing.generateBill(finalOrderItems, menu,total, username, couponCode, currentCustomer);}
        else{
        billing.generateBill(finalOrderItems,menu,total1,username,couponCode,currentCustomer);
        }
        double paymentTotal = currentCustomer.isPremium() ? total : total1;
        double discount = 0.0;
        if (currentCustomer.isPremium()) {
            discount = 0.10 * paymentTotal;
            System.out.println();
            paymentTotal -= discount;
        }

        double couponDiscount = 0;
        if (couponCode != null && (couponCode.equals("new10") || couponCode.equals("First10"))) {
            // Apply a 10% coupon code discount
            couponDiscount = 0.10 * paymentTotal;
            
            System.out.println();
            paymentTotal -= couponDiscount;
        }
        
        Thread.sleep(800);
        System.out.println("Payment Options:");
        System.out.println("1. UPI");
        System.out.println("2. Credit Card");
        System.out.println("3. Debit Card");
        System.out.println();
         int maxPaymentAttempts = 3; // Maximum attempts for payment details
         int paymentAttempts = 0;
         boolean paymentSuccess = false;

        do {
        try {
            System.out.print("Select a payment option: ");
            int paymentOption = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (paymentOption) {
                case 1:
                System.out.print("Enter UPI ID: ");
                Thread.sleep(800);
                String upiId = scanner.nextLine();
                if (upiId.contains("@paytm")||upiId.contains("@apl")||upiId.contains("@gpay")||upiId.contains("@phonepay")||upiId.contains("@paypal")||upiId.contains("@")) {
                paymentSuccess = Payment.makeUPIPayment(upiId, paymentTotal);
                } else {
                System.out.println("Invalid UPI ID.");
                paymentAttempts++;
            }
               break;

                case 2:
                    System.out.print("Enter Credit Card Number: ");
                    Thread.sleep(800);
                    String cardNumber = scanner.nextLine();
                    System.out.print("Enter Expiry Date (MM/YY): ");
                    Thread.sleep(800);
                    String expiryDate = scanner.nextLine();
                    Thread.sleep(800);
                    System.out.print("Enter CVV: ");
                    Thread.sleep(800);
                    String cvv = scanner.nextLine();
                    System.out.print("Enter OTP: ");
                    Thread.sleep(800);
                    String otp = scanner.nextLine();
                    paymentSuccess = Payment.makeCreditCardPayment(cardNumber, expiryDate, cvv, paymentTotal, otp);
                    break;

                case 3:
                    System.out.print("Enter Debit Card Number: ");
                    Thread.sleep(800);
                    String debitCardNumber = scanner.nextLine();
                    Thread.sleep(800);
                    System.out.print("Enter Expiry Date (MM/YY): ");
                    String debitExpiryDate = scanner.nextLine();
                    Thread.sleep(800);
                    System.out.print("Enter CVV: ");
                    String debitCvv = scanner.nextLine();
                    Thread.sleep(800);
                    System.out.print("Enter OTP: ");
                    String debitOtp = scanner.nextLine();
                    Thread.sleep(800);
                    paymentSuccess = Payment.makeDebitCardPayment(debitCardNumber, debitExpiryDate, debitCvv, paymentTotal, debitOtp);
                    break;

                default:
                    System.out.println("Invalid payment option.");
                    break;
            }

            if (paymentSuccess) {
                Thread.sleep(800);
                System.out.println("Payment successful. Enjoy your meal!");
                orderId = JdbcDemo.insertOrder(username, paymentTotal);
                saveBillingToFile(username, billing, currentCustomer,finalOrderItems,paymentTotal,paymentOption);
                break;
            } else {
                // Increment attempts only if the payment is not successful
                paymentAttempts++;
                Thread.sleep(800);
                if (paymentAttempts < maxPaymentAttempts) {
                    System.out.println("Payment failed. You have " + (maxPaymentAttempts - paymentAttempts) + " attempts remaining.");
                    System.out.println("Please try again.");
                } else {
                    System.out.println("Maximum payment attempts reached. Please order again.");
                    return;
                }
            }
    Thread.sleep(100);
        }catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); 
            paymentAttempts++;
        }
    } while (paymentAttempts <= maxPaymentAttempts);
    Thread.sleep(800);
        Review.addReviewAndRating(username);
        String userReview = Review.getReview();
        float userRating = Review.getRating(); 
        if (JdbcDemo.insertReviewAndRating(username,userReview,userRating)){
            try {
                Thread.sleep(800);
                System.out.println("Thank you for your review and rating!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to add review and rating to the database.");
        }
    }

    private static void saveBillingToFile(String username, Billing billing, Customer currentCustomer, String[] finalOrderItems, double total,int paymentOption) {
        try {
           String userFolderPath = "billing_folder/" + username + "/";
        String timestamp = java.time.LocalDateTime.now().toString().replace(":", "_").replace(".", "_");
        String fileName = userFolderPath + "order_" + timestamp + ".txt";
        File userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
            FileWriter writer = new FileWriter(fileName);
            writer.write("Username: " + username + "\n");
            writer.write("Customer Type: " + (currentCustomer.isPremium() ? "Premium" : "Regular") + "\n");
            writer.write("*********************************Billing Details********************************************\n");
            writer.write("Ordered Food Items:\n");
            for (String itemId : finalOrderItems) {
                int index = Integer.parseInt(itemId) - 1;
                MenuItem item = menu[index];
                writer.write(item.getName() + " - " + item.getPrice() + "\n");
            }
            writer.write("Total Amount: " + total + "\n");
            writer.write("Payment Option: " + getPaymentOptionName(paymentOption) + "\n");
            writer.write("Date and Time: " + java.time.LocalDateTime.now() + "\n");
            writer.write("********************************************************************************************\n");
            writer.close();
            System.out.println("Billing details saved to file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving billing information to file.");
            e.printStackTrace();
        }
    }
    private static String getPaymentOptionName(int paymentOption) {
        switch (paymentOption) {
            case 1:
                return "UPI";
            case 2:
                return "Credit Card";
            case 3:
                return "Debit Card";
            default:
                return "Unknown";
        }
    }
    private static void handleLogout(Scanner scanner, Login loginSystem, Billing billing, Customer currentCustomer) throws InterruptedException {
        int maxLogoutAttempts =3 ;
        int logoutAttempts = 0;
    
        do {
            try {
                System.out.println("Logging out...");
                currentCustomer = null;
                break;  // Break the loop after successful logout
            } catch (Exception e) {
                System.out.println("Error during logout. Please try again.");
                logoutAttempts++;
            }
    
            if (logoutAttempts >= maxLogoutAttempts) {
                System.out.println("Maximum logout attempts reached. Exiting Food Ordering System.");
                System.exit(0);
            }
        } while (true);
    
        // Check if the user needs to log in again
        if (currentCustomer == null) {
            int loginChoice;
    
            do {
                System.out.println("1. Log in");
                System.out.println("2. Exit");
    
                try {
                    loginChoice = scanner.nextInt();
                    scanner.nextLine();
    
                    if (loginChoice == 1) {
                        System.out.print("Username: ");
                        Thread.sleep(800);
                        String username = scanner.nextLine();
                        Thread.sleep(800);
                        System.out.print("Password: ");
                        String password = scanner.nextLine();
                        currentCustomer = JdbcDemo.isValidUser(username, password);
    
                        if (currentCustomer != null) {
                            Thread.sleep(800);
                            displayinfo(currentCustomer, loginSystem, billing, username, scanner);
                            break;  
                        } else {
                            Thread.sleep(800);
                            System.out.println("Invalid username or password. Please try again.");
                            System.out.println();
                            logoutAttempts++; 
                        }
                    } else if (loginChoice == 2) {
                        System.out.println("Exiting Food Ordering System.");
                        System.exit(0);
                    } else {
                        System.out.println("Invalid choice. Please enter 1 or 2.");
                        System.out.println();
                        logoutAttempts++; 
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    System.out.println();
                    scanner.nextLine();
                }
    
                if (logoutAttempts >= maxLogoutAttempts) {
                    System.out.println("Maximum logout attempts reached. Exiting Food Ordering System.");
                    System.exit(0);
                }
            } while (true);
        }
    }


    
    public static void displayinfo(Customer custoemer, Login login, Billing billing, String username, Scanner scanner) throws InterruptedException {
        while (true) {
            System.out.println();
            System.out.println("Hello " + username);
            System.out.println();
            System.out.println("1. Customer Details");
            System.out.println("2. View Menu");
            System.out.println("3. Place Order");
            System.out.println("4. Review and Rating");
            System.out.println("5. Logout");
            System.out.println("6. Exit");
            System.out.println();
            int choice;
            while (true) {
                try {
                    System.out.print("Enter your choice: ");
                    Thread.sleep(800);
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice >= 1 && choice <= 6) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please choose 1 to 6.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
            switch (choice) {
                
                case 1:
                    Thread.sleep(800);
                    displayCustomerDetails(username);
                    break;
                case 2:
                    Thread.sleep(800);
                    viewMenu();
                    break;
                case 3:
                    Thread.sleep(800);
                    handleMenuSelection(menu, billing, username, custoemer);
                    break;
                case 4:
                    Thread.sleep(800);
                    Review.addReviewAndRating(username);
                    break;
                case 5:
                    Thread.sleep(800);
                    handleLogout(scanner, login, billing, custoemer);
                    break;
                case 6:
                    Thread.sleep(800);
                    System.out.println("Exiting Food Ordering System.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    



    public static void main(String[] args) throws InterruptedException {
        initializeMenu();

        Login loginSystem = new Login();
        JdbcDemo jdbcDemo = new JdbcDemo();

        // Create a regular customer and a premium customer
        Customer regularCustomer = new Customer("Harshit", "Bapu nagar Bhilwara", "9983937973") {
            public boolean isPremium() {
                return false;
            }
        };

        Customer premiumCustomer = new PremiumCustomer("Keshav", "Kapriwas Haryana", "8575967420");

        loginSystem.addUser("Harshit", "12345", regularCustomer);
        loginSystem.addUser("Keshav", "1245", premiumCustomer);

        System.out.println("********************************************************************");
        System.out.println("*                                                                  *");
        System.out.println("*                    Welcome To Our Page                           *");
        System.out.println("*                                                                  *");
        System.out.println("********************************************************************");

        for (int i = 0; i < 4; i++) {
            System.out.println();
        }
        Thread.sleep(800);
        Scanner scanner = new Scanner(System.in);
        Billing billing = new Billing();

        System.out.println();
        System.out.println("+------------------------------------------------------------------+");
        System.out.println("+                    **************                                +");
        System.out.println("+                     PLEASE LOGIN                                 +");
        System.out.println("+                    **************                                +");
        System.out.println("+------------------------------------------------------------------+");

        for (int i = 0; i < 4; i++) {
            System.out.println();
        }
        int maxLoginAttempts = 3;
        int loginAttempts = 0;
        do{
            Thread.sleep(800);
        System.out.print("Username: ");
        Thread.sleep(800);
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        Thread.sleep(800);
        Customer currentCustomer = JdbcDemo.isValidUser(username, password) ;

          if (currentCustomer != null)  {
            Thread.sleep(800);
          displayinfo(currentCustomer,loginSystem,billing,username, scanner);  
        } 
        else {
            System.out.println("Invalid username or password. Please try again.");
            loginAttempts++;
            if (loginAttempts >= maxLoginAttempts){
            Thread.sleep(800);
            System.out.println();
            System.out.println("!!!! Please enter correct login details !!!!");
            System.out.println("Press 1 to Sign up");
            System.out.println("Press 2 to exit the system and log in again");
            int choice;
            while (true) {
                try {
                    System.out.print("Enter your choice: ");
                    Thread.sleep(800);
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice >= 1 && choice <= 2) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Please choose 1 or 2.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
            switch (choice) {
                case 1:
                    Thread.sleep(800);
                     System.out.println();
                     System.out.println("+------------------------------------------------------------------+");
                     System.out.println("+                    **************                                +");
                     System.out.println("+                        Sign UP                                   +");
                     System.out.println("+                    **************                                +");
                     System.out.println("+------------------------------------------------------------------+");

                     System.out.print("Please Enter your Name: ");
                     Thread.sleep(800);
                     String Name = scanner.nextLine();
                     while (Name.isEmpty() || !Name.matches("[a-zA-Z]+")) {
                         System.out.println("Invalid name. Please enter a valid name with only alphabetic characters.");
                         System.out.print("Please Enter your Name: ");
                         Name = scanner.nextLine();
                     }
                     
                     System.out.print("Enter Your Address: ");
                     Thread.sleep(800);
                     String Address = scanner.nextLine();
                     while (Address.isEmpty()) {
                         System.out.println("Address cannot be empty. Please enter your address.");
                         System.out.print("Enter Your Address: ");
                         Address = scanner.nextLine(); 
                     }
                     System.out.print("Enter Your Contact: ");
                     Thread.sleep(800);
                     String Contact = scanner.nextLine();
                     while (Contact.isEmpty() || !Contact.matches("\\d{10}")) {
                         System.out.println("Invalid contact number. Please enter a 10-digit number.");
                         System.out.print("Enter Your Contact: ");
                         Contact = scanner.nextLine();
                     }
                     System.out.print("Enter Your Username: ");
                     Thread.sleep(800);
                     String Username = scanner.nextLine();
                     while (Username.isEmpty()) {
                         System.out.println("Username cannot be empty. Please enter your username.");
                         System.out.print("Enter Your Username: ");
                         Username = scanner.nextLine();
                     }
                     System.out.print("Enter Password: ");
                     Thread.sleep(800);
                     String Password = scanner.nextLine();
                     while (Password.isEmpty()) {
                         System.out.println("Password cannot be empty. Please enter your password.");
                         System.out.print("Enter Password: ");
                         Password = scanner.nextLine();
                     }
                     int type;

                     while (true) {
                        Thread.sleep(800);
                         System.out.println("Press 1 for Regular Customer ");
                         System.out.println("Press 2 for Premium Customer");
                     
                         try {
                            Thread.sleep(800);
                             type = scanner.nextInt();
                             scanner.nextLine();
                     
                             if (type == 1 || type == 2) {
                                 break; // Break out of the loop if the input is 1 or 2
                             } else {
                                 System.out.println("Invalid choice. Please choose 1 or 2.");
                             }
                         } catch (InputMismatchException e) {
                             System.out.println("Invalid input. Please enter a number.");
                             scanner.nextLine(); // Consume the invalid input
                         }
                     }
                     
                     switch (type) {
                         case 1:
                             // Regular Customer registration
                             Customer newCustomer = new Customer(Name, Address, Contact) {
                                 public boolean isPremium() {
                                     return false;
                                 }
                             };
                             loginSystem.addUser(Username, Password, newCustomer);
                             int userId = jdbcDemo.insertCustomer(newCustomer);
                             if (userId != -1) {
                                 JdbcDemo.insertLoginDetails(Username, Password, userId);
                             } else {
                                 System.out.println("Failed to insert customer details.");
                             }
                             username = Username;
                             System.out.println("Welcome, " + Name + "! You are now registered.");
                             Thread.sleep(800);
                             displayinfo(newCustomer, loginSystem, billing, username, scanner);
                             break;
                     
                         case 2:
                             // Premium Customer registration
                             PremiumCustomer newPremiumCustomer = new PremiumCustomer(Name, Address, Contact);
                             loginSystem.addUser(Username, Password, newPremiumCustomer);
                             int premiumUserId = jdbcDemo.insertCustomer(newPremiumCustomer);
                             if (premiumUserId != -1) {
                                 JdbcDemo.insertLoginDetails(Username, Password, premiumUserId);
                             }
                             username = Username;
                             System.out.println("Welcome, " + Name + "! You are now registered as a premium customer.");
                             Thread.sleep(800);
                             displayinfo(newPremiumCustomer, loginSystem, billing, username, scanner);
                             break;
                     }
                    break;
                case 2:
                    System.out.println("Exiting Food Ordering System......");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
  } while(true);
 }
}
