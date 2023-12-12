    package customer;

    public abstract class Customer {
        private String name;
        private String address;
        private String contact;
        public Customer(String name, String address, String contact) {
            if (!name.matches("^[a-zA-Z ]+$")) {
                throw new IllegalArgumentException("Name should contain only characters.");
            }
            if (!contact.matches("\\d{10}")) {
                throw new IllegalArgumentException("Contact number must be 10 digits.");
            }
            this.name = name;
            this.address = address;
            this.contact = contact;
            
        }
        public String getName() {
            return name;
        }

        public String getContact() {
            return contact;
        }
        public String getAddress() {
            return address;
        }
        public abstract boolean isPremium();
    }

