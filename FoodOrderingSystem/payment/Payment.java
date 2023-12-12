package payment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Payment {
    // UPI Payment
    public static boolean makeUPIPayment(String upiId, double amount) {
        System.out.println("Making UPI payment to " + upiId + " for amount: " + amount);
        return true; 
    }

    // Credit Card Payment Method with opt verification
    public static boolean makeCreditCardPayment(String cardNumber, String expiryDate, String cvv, double amount, String otp) {
        if (isValidCard(cardNumber, expiryDate, cvv) && validateOTP(otp)){
            System.out.println("Making credit card payment with card number " + cardNumber + " for amount: " + amount);
           
            return true; 
        } else {
            System.out.println("Invalid OTP. Payment failed.");
            return false;
        }
    }

    // Debit Card Payment Method with OTP Verification
    public static boolean makeDebitCardPayment(String cardNumber, String expiryDate, String cvv, double amount, String otp) {
        if (isValidCard(cardNumber, expiryDate, cvv) && validateOTP(otp)) {
            System.out.println("Making debit card payment with card number " + cardNumber + " for amount: " + amount);
            return true; 
        } else {
            System.out.println("Invalid OTP. Payment failed.");
            return false;
        }
    }
    // Check the OTP
    private static boolean validateOTP(String otp) {
        return otp.equals("123456");
    }
    // Check All cards Details
    public static boolean isValidCard(String cardNumber, String expiryDate, String cvv) {
        // Card Number length
        if (cardNumber.length() != 16) {
            System.out.println("Invalid card number length.");
            return false;
        }
        // Card Exipry Date
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            System.out.println("Invalid expiry date format. Use MM/YY format.");
            return false;
        }
        // Card Cvv length 
        if (cvv.length() != 3) {
            System.out.println("Invalid CVV length.");
            return false;
        }

        // Validate expiry date (month and year) from today's date
        if (!isValidExpiryDate(expiryDate)) {
            System.out.println("Invalid card expiry date.");
            return false;
        }

        return true;
    }

    private static boolean isValidExpiryDate(String expiryDate) {
        try {
            // Parse the expiry date string to a Date object
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
            Date expiry = sdf.parse(expiryDate);

            // Get today's date (only month and year)
            Calendar today = Calendar.getInstance();
            today.set(Calendar.DAY_OF_MONTH, 1); // Set day to 1
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Compare expiry date with today's date
            return expiry.after(today.getTime());
        } catch (ParseException e) {
            System.out.println("Error parsing expiry date.");
            return false;
        }
    }
}