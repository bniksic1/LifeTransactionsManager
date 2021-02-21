package ba.unsa.etf.rma.rma20niksicbenjamin63.data;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable{
    public enum TYPE{
        INDIVIDUALPAYMENT("Individual payment"), REGULARPAYMENT("Regular payment"), PURCHASE("Purchase"), INDIVIDUALINCOME("Individual income"), REGULARINCOME("Regular income"), ANYTYPE("Any type");
        private String name;
        TYPE(String s) {
            name = s;
        }
        @Override
        public String toString(){
            return name;
        }
    }
    private int id;
    private LocalDate date;
    private double amount;
    private String title;
    private TYPE type;
    private String itemDescription;
    private Integer transactionInterval;
    private LocalDate endDate;

    public Transaction(int id, LocalDate date, double amount, String title, TYPE type, String itemDescription, Integer transactionInterval, LocalDate endDate) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
    }

    public Transaction(Transaction t){
        this.id = t.id;
        this.date = t.date;
        this.amount = t.amount;
        this.title = t.title;
        this.type = t.type;
        this.itemDescription = t.itemDescription;
        this.transactionInterval = t.transactionInterval;
        this.endDate = t.endDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Integer getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(Integer transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Transaction){
            return id == ((Transaction) o).getId();
        }
        return false;
    }

    @Override
    public String toString(){
        return id + ": " + title + " (" + type + ") " + amount;
    }
}
