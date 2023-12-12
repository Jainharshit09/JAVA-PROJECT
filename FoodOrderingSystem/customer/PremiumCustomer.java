package customer;
public class PremiumCustomer extends Customer {
    public PremiumCustomer(String name, String address, String contact) {
        super(name, address, contact);
    }

   
    public boolean isPremium() {
        return true; 
    }
}
