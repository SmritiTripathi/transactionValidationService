package com.afterpay;

import com.afterpay.model.Transaction;
import com.afterpay.util.Util;
import org.joda.money.Money;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.joda.money.CurrencyUnit.USD;

public class TransactionValidation {
    /** Per the problem statement:
     *  Transactions are to be received as a comma separated string of elements eg. '10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00'
     *
     *  **ASSUMPTION** : Input will all be string data. If needed, more overridden methods can be added.
     */

    public List<String> getFraudulentTransactions(List<String> transactions, String date,  String priceThreshold) throws Exception {
        if(null == transactions || null == date || null == priceThreshold) return new ArrayList<>();

        return getFraudulentTransactions(convertStringToTransactionList(transactions), Util.stringToDate(date), Util.stringToMoney(priceThreshold));
    }

    /**
     *
     * @param transactions
     * @param priceThreshold
     * @param date
     *
     * @return List of fraudulent credfitCard hashes
     */
    public List<String> getFraudulentTransactions(List<Transaction> transactions, DateTime date, Money priceThreshold) {
        //**ASSUMPTION** : if no priceThreshold to compare, consider all transactions as valid.
        // return immediately if any of the parameter is missing.
        if(null == transactions || null == date || null == priceThreshold) return new ArrayList<>();

        Map<String, Money> creditCardToPriceSumMap = buildCardToSumMap(transactions, date);

        return determineFradulentCards(creditCardToPriceSumMap, priceThreshold);
    }

    /**
     *
     * @param transactions - list of transactions to check for frauds
     * @param date - capture frauds that happened in this day
     *
     * @return - Map of creditcardhashes and the sum of all its transaction price.
     */
    private Map<String, Money> buildCardToSumMap(List<Transaction> transactions, DateTime date) {
        Map<String, Money> creditCardToPriceSumMap = new HashMap<>();
        boolean sameday = false;
        Money currPrice = Money.zero(USD); //**ASSUMPTION**  USD as default currency for this problem statement.

        for(Transaction transaction: transactions) {

            try {
                DateTime transactionDateTime = Util.stringToDate(transaction.getTimeStamp());

                sameday = Util.isSameDay(transactionDateTime, date);

                currPrice = Util.stringToMoney(transaction.getPrice());

            } catch (Exception e) {
                //For this use case, skipping the erroneous transaction if any in input and continue processing others
                System.out.println("Error reading transaction time/price for " + transaction);
            }

            if(sameday) {
                //**ASSUMPTION** : transaction can have negative price value too.
                creditCardToPriceSumMap.put(transaction.getCreditCardHash(),
                        creditCardToPriceSumMap.getOrDefault(transaction.getCreditCardHash(), Money.zero(USD)).plus(currPrice));
            }

        }

        return creditCardToPriceSumMap;
    }

    /**
     *
     * @param creditCardToPriceSumMap
     * @param priceThreshold - a fraud is sum of transaction price that exceeds this value
     *
     * @return List of all credit card hashes that exceed this value
     */
    private List<String> determineFradulentCards(Map<String, Money> creditCardToPriceSumMap, Money priceThreshold) {
        if(null == creditCardToPriceSumMap) return new ArrayList<>();

        List<String> fradulentCards = new ArrayList<>();
        for(Map.Entry<String,Money> entry: creditCardToPriceSumMap.entrySet()) {

            /** By definition: A credit card will be identified as fraudulent if the sum of prices for a unique hashed credit card number, for a given day,
             * exceeds the price threshold T.*/
            if(entry.getValue().isGreaterThan(priceThreshold)) {
                fradulentCards.add(entry.getKey());
            }
        }

        return fradulentCards;

    }

    private List<Transaction> convertStringToTransactionList(List<String> transactions) {
        List<Transaction> transactionList = new ArrayList<>();

        for(String transaction: transactions) {
            String[] strSplit = transaction.split(",");
            if(strSplit.length == 3) {
                transactionList.add(new Transaction(strSplit[0], strSplit[1], strSplit[2]));

            } else {
                //alternately this state can also be treated as an error and we can throw exception as well.
                // for now, misformed transaction string will simply be logged and ignored.
                System.out.println("Misformed transactions " + transaction);
            }
        }

        return transactionList;
    }
}
