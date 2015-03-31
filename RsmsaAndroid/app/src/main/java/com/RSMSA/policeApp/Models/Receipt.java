package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 2/11/2015.
 */
public class Receipt extends Model {
    private String amount;
    private long  date;
    private String receipt_number;
    private String payment_mode="";

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getReceipt_number() {
        return receipt_number;
    }

    public void setReceipt_number(String receipt_number) {
        this.receipt_number = receipt_number;
    }
}
