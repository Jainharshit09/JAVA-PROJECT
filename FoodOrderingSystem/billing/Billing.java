package billing;
import customer.*;
import menu.*;

public class Billing {
    
    public double calculateTotal(String[] orderItems, MenuItem[] menu,String couponCode) {
        double total = 0.0;
        System.out.println(couponCode);
        for (String itemName : orderItems) {
            int itemId = Integer.parseInt(itemName);
            total += menu[itemId - 1].getPrice();
        }
        return total;
    }

    public void generateBill(String[] orderItems, MenuItem[] menu, double total, String username, String couponCode, Customer customer) {
        System.out.println();
        System.out.println("*****************************************************************************************************************");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("------------------------- Bill -------------------------");
        System.out.println();
        System.out.println();
        System.out.println("Customer Name: " + customer.getName());
        System.out.println();
        System.out.println("Contact details: " + customer.getContact());
        System.out.println();

        double discount = 0.0;
        if (customer.isPremium()) {
            discount = 0.10 * total;
            System.out.println("Premium Customer Discount: " + discount);
            System.out.println();
            total -= discount;
        }

        double couponDiscount = 0;
        if (couponCode != null && (couponCode.equals("new10") || couponCode.equals("First10"))) {
            // Apply a 10% coupon code discount
            couponDiscount = 0.10 * total;
            
            System.out.println();
            total -= couponDiscount;
        }

        for (String itemId : orderItems) {
            int id = Integer.parseInt(itemId);
            MenuItem item = menu[id - 1];
            System.out.println(item.getName() + ": " + item.getPrice());
            System.out.println();
        }

        if (discount > 0 || couponDiscount > 0) {
            double beforetotal = total + discount + couponDiscount;
            System.out.println("Total: " + beforetotal);
            System.out.println();
            System.out.println("Coupon Code (" + couponCode + "): " + couponDiscount);
            System.out.println();
            System.out.println("Total After Discounts: " + (total));
        } else {
            System.out.println("Total: " + total);
        }
        System.out.println();
       
    }
}
