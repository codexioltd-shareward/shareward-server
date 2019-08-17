package bg.codexio.shareward.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "payment_requests")
public class PaymentRequest {

    private Long id;

    private Account sender;

    private Account receiver;

    private Double amount;

    private Set<User> acceptedUsers;

    private Set<User> rejectedUsers;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = Account.class)
    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    @ManyToOne(targetEntity = Account.class)
    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @ManyToMany(targetEntity = User.class, mappedBy = "acceptedPayments")
    public Set<User> getAcceptedUsers() {
        return acceptedUsers;
    }

    public void setAcceptedUsers(Set<User> acceptedUsers) {
        this.acceptedUsers = acceptedUsers;
    }

    @ManyToMany(targetEntity = User.class, mappedBy = "rejectedPayments")
    public Set<User> getRejectedUsers() {
        return rejectedUsers;
    }

    public void setRejectedUsers(Set<User> rejectedUsers) {
        this.rejectedUsers = rejectedUsers;
    }
}
