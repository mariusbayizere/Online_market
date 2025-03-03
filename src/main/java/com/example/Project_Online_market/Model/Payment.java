package com.example.Project_Online_market.Model;

import java.util.Date;

import com.example.Project_Online_market.Enum.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Payment_ID;

    @NotBlank(message = "Payment Method is Required")
    @Size(min = 3, max = 50, message = "Payment Method must be between 3 and 50 characters")
    @Column(name = "Payment_Method", nullable = false)
    private String Payment_Method;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment Status is Required")
    @Column(name = "Payment_Status", nullable = false)
    private PaymentStatus Payment_Status = PaymentStatus.PENDING;

    // @FutureOrPresent(message = "Payment Date must be today or in the future")
    @NotNull(message = "Payment Date is Required")
    @Column(name = "Payment_Date", nullable = false)
    private Date Payment_Date;
    
    
    @NotNull(message = "Payment Amount is Required")
    @Min(value = 0, message = "Payment Amount must be a positive value")
    @Column(name = "Payment_Amount", nullable = false)
    private double Payment_Amount;

    // One-to-one relationship with Order
    @OneToOne
    @JoinColumn(name = "Order_ID", nullable = false)
    private Orders order;

    public int getPayment_ID() {
        return Payment_ID;
    }

    public void setPayment_ID(int payment_ID) {
        Payment_ID = payment_ID;
    }

    public String getPayment_Method() {
        return Payment_Method;
    }

    public void setPayment_Method(String payment_Method) {
        Payment_Method = payment_Method;
    }

    public PaymentStatus getPayment_Status() {
        return Payment_Status;
    }

    public void setPayment_Status(PaymentStatus payment_Status) {
        Payment_Status = payment_Status;
    }

    public Date getPayment_Date() {
        return Payment_Date;
    }

    public void setPayment_Date(Date payment_Date) {
        Payment_Date = payment_Date;
    }

    public double getPayment_Amount() {
        return Payment_Amount;
    }

    public void setPayment_Amount(double payment_Amount) {
        Payment_Amount = payment_Amount;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }
}
