package com.afterpay;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionValidationTest {

    @Test
    public void testFraudulentTransactions_noMatch() throws Exception {
        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> transactions = createListOfValidTransactions();

        List<String> output = transactionValidation.getFraudulentTransactions(transactions, "2014-04-29T13:15:54", "50" );

        assertNotNull(output);
        assertTrue(output.isEmpty()); // no fraudulent transaction found;
    }

    @Test
    public void testFraudulentTransactions_priceThresholdExceeded() throws Exception {
        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> transactions = createListOfValidTransactions();

        List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add("10d7ce2f43e35fa57d1bbf8b1e2");

        List<String> output = transactionValidation.getFraudulentTransactions(transactions, "2014-04-29T13:15:54", "20" );

        assertNotNull(output);
        assertEquals(1, output.size());
        assertEquals(expectedOutput, output);// 1 fraudulent transaction found;
    }

    @Test
    public void testFraudulentTransactions_priceThresholdExceededWithNegTransaction() throws Exception {

        //**ASSUMPTION** : a negative price is considered a valid transaction in this flow. Because Example, bank credit cards allow both : paying-to and withdrawing-from


        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> transactions = createListOfValidTransactions();
        transactions.add("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, -20.00"); //with this, '10d7ce2f43e35fa57d1bbf8b1e2' wont be valid for threshold 15

        List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add("10d7ce2f43e35fa57d1bbf8b1e1");

        List<String> output = transactionValidation.getFraudulentTransactions(transactions, "2014-04-29T13:15:54", "15" );

        assertNotNull(output);
        assertEquals(1, output.size());
        assertEquals(expectedOutput, output);// 1 fraudulent transaction found;
    }

    @Test
    public void testFraudulentTransactions_priceThresholdZero() throws Exception {
        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> transactions = createListOfValidTransactions();

        //**ASSUMPTION** : '0' is a valid priceThreshold to compare, consider all transactions as fradulent.
        //all credit cards should be reported as fraudulent
        List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add("10d7ce2f43e35fa57d1bbf8b1e4");
        expectedOutput.add("10d7ce2f43e35fa57d1bbf8b1e2");
        expectedOutput.add("10d7ce2f43e35fa57d1bbf8b1e1");

        List<String> output = transactionValidation.getFraudulentTransactions(transactions, "2014-04-29T13:15:54", "0" );

        assertNotNull(output);
        assertEquals(3, output.size());
        assertEquals(expectedOutput, output);// 1 fraudulent transaction found;
    }

    @Test
    public void testFraudulentTransactions_noMatchOnGivenDay() throws Exception {
        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> transactions = createListOfValidTransactions();

        List<String> output = transactionValidation.getFraudulentTransactions(transactions, "2014-05-29T13:15:54", "20" );
        assertNotNull(output);
        assertTrue(output.isEmpty());

        output = transactionValidation.getFraudulentTransactions(transactions, "2014-03-29T23:59:59", "20" );
        assertNotNull(output);
        assertTrue(output.isEmpty());
    }

    @Test
    public void testFraudulentTransactions_nullPriceThreshold() throws Exception {
        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> transactions = createListOfValidTransactions();

        List<String> output = transactionValidation.getFraudulentTransactions(transactions, "2014-04-29T13:15:54", null );

        assertNotNull(output);
        assertTrue(output.isEmpty());  //**ASSUMPTION** : if no priceThreshold to compare, consider all transactions as valid.
    }

    @Test
    public void testFraudulentTransactions_nullTransactions() throws Exception {
        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> output = transactionValidation.getFraudulentTransactions(null, "2014-04-29T13:15:54", "20" );

        assertNotNull(output);
        assertTrue(output.isEmpty());  //**ASSUMPTION** : if no priceThreshold to compare, consider all transactions as valid.
    }

    @Test
    public void testFraudulentTransactions_nullDate() throws Exception {
        TransactionValidation transactionValidation = new TransactionValidation();

        List<String> transactions = createListOfValidTransactions();

        List<String> output = transactionValidation.getFraudulentTransactions(transactions, null, "20" );

        assertNotNull(output);
        assertTrue(output.isEmpty());  //**ASSUMPTION** : if no priceThreshold to compare, consider all transactions as valid.
    }

    private List<String> createListOfValidTransactions() {
        List<String> transactions = new ArrayList<>();

        transactions.add("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00");
        transactions.add("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:16:54, 10.00");
        transactions.add("10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T14:15:54, 10.00");
        transactions.add("10d7ce2f43e35fa57d1bbf8b1e1, 2014-04-29T13:15:54, 10.00");
        transactions.add("10d7ce2f43e35fa57d1bbf8b1e4, 2014-04-29T17:18:54, 10.00");
        transactions.add("10d7ce2f43e35fa57d1bbf8b1e1, 2014-04-29T13:15:58, 10.00");

        return transactions;
    }

}
