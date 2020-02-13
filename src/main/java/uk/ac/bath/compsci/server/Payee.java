package uk.ac.bath.compsci.server;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Payee {
    private Integer AccountNumber;
    private String SortCode;
    private String PayeeName;
    private String PayeeBank;

    public Payee(Integer AccNum, String SortCode, String Payee, String PayeeBank) {
        this.AccountNumber = requireValidAccountNumber(AccNum, "Invalid account number - must be 8 digits");
        this.PayeeBank = requireNonBlank(PayeeBank, "Must specify a bank name");
        this.PayeeName = requireNonBlank(Payee, "Must provide payee name");
        this.SortCode = requireValidSortCode(SortCode, "Invalid sort code - must be in format 00-00-00");
    }

    public String toString() {
        return (PayeeName + ": " + PayeeBank + ": " + AccountNumber + ": " + SortCode);
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


