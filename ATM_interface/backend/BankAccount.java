// ============================================
// BANK ACCOUNT CLASS
// ============================================

public class BankAccount {

    private double balance;


    // Constructor
    public BankAccount(double initialBalance) {

        balance = initialBalance;
    }


    // ==========================================
    // CHECK BALANCE
    // ==========================================

    public double checkBalance() {

        return balance;
    }


    // ==========================================
    // DEPOSIT
    // ==========================================

    public boolean deposit(double amount) {

        if (amount <= 0) {

            return false;
        }


        balance += amount;

        return true;
    }


    // ==========================================
    // WITHDRAW
    // ==========================================

    public boolean withdraw(double amount) {

        if (amount <= 0) {

            return false;
        }


        if (amount > balance) {

            return false;
        }


        balance -= amount;

        return true;
    }


    // ==========================================
    // RESET ACCOUNT
    // ==========================================

    public void reset() {

        balance = 10000;
    }
}