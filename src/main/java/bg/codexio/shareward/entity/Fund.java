package bg.codexio.shareward.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "funds")
public class Fund {

    private Long id;

    private User buyer;

    private Double amount;

    private Date boughtOn;

    private String transactionIdentification;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getBoughtOn() {
        return boughtOn;
    }

    public void setBoughtOn(Date boughtOn) {
        this.boughtOn = boughtOn;
    }

    public String getTransactionIdentification() {
        return transactionIdentification;
    }

    public void setTransactionIdentification(String transactionIdentification) {
        this.transactionIdentification = transactionIdentification;
    }
}
