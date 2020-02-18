package uk.ac.bath.compsci.server;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Payee {
    private Integer accountNumber;
    private String sortCode;
    private String payeeName;
    private String payeeBank;

    public Payee(Integer accountNumber, String sortCode, String payeeName, String payeeBank) {
        this.accountNumber = requireValidAccountNumber(accountNumber, "Invalid account number - must be 8 digits");
        this.sortCode = requireValidSortCode(sortCode, "Invalid sort code - must be in format 00-00-00");
        this.payeeName = requireNonBlank(payeeName, "Must provide payee name");
        this.payeeBank = requireNonBlank(payeeBank, "Must specify a bank name");
    }

    public String toString() {
        return (payeeName + ": " + payeeBank + ": " + accountNumber + ": " + sortCode);
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getPayeeBank() {
        return payeeBank;
    }

    public boolean isNewBankAccount() {
        return this.payeeBank.equalsIgnoreCase("NEWBANK");
    }

    private static String requireNonBlank(final String toCheck, final String message) {
        if (isBlank(toCheck))
            throw new IllegalArgumentException(message);
        return toCheck;
    }

    private static double requireNonNegative(final double toCheck, final String message) {
        if (toCheck < 0)
            throw new IllegalArgumentException(message);
        return toCheck;
    }

    private static int requireValidAccountNumber(final int toCheck, final String message) {
        int accNumLength = String.valueOf(toCheck).length();
        if (accNumLength != 8)
            throw new IllegalArgumentException(message);
        return toCheck;
    }

    private static String requireValidSortCode(final String toCheck, final String message) {
        boolean isValid = false;
        for (int i = 0; i < toCheck.length(); i++) {
            if ((i % 3 == 0) && (toCheck.charAt(i) == '-')) {
                isValid = true;
            }

            if (i % 3 != 0) {
                //must be integer
                if (Character.isDigit(toCheck.charAt(i))) {
                    isValid = true;
                }
            }
        }
        if (!isValid) {
            throw new IllegalArgumentException(message);
        }
        return toCheck;
    }
}


