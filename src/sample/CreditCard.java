package sample;

import java.io.Serializable;

/**
 * Απλη Serializable κλαση που αναπαρειστα τις καρτες!!
 */
public class CreditCard implements Serializable {
    private String type;
    private String name;
    private String number;
    private String datePicker;
    private String cvv;

    public CreditCard(String type, String name, String number, String datePicker, String cvv) {
        this.type = type;
        this.name = name;
        this.number = number;
        this.datePicker = datePicker;
        this.cvv = cvv;
    }

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }


    public String getNumber() {
        return number;
    }


    public String getDatePicker() {
        return datePicker;
    }


    public String getCvv() {
        return cvv;
    }


}
