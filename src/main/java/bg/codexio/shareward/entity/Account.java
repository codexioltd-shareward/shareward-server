package bg.codexio.shareward.entity;

import bg.codexio.shareward.model.account.AccountCreateRequestModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
public class Account {

    private Long id;

    private String name;

    private Set<User> users;

    private Double sum;

    @JsonIgnore
    private Set<Invitation> invitations;

    @JsonIgnore
    private Set<PaymentRequest> receivedRequests;

    @JsonIgnore
    private Set<PaymentRequest> initiatedRequests;

    @JsonIgnore
    private Set<Payment> receivedPayments;

    @JsonIgnore
    private Set<Payment> sentPayments;

    public static Account fromCreateRequest(AccountCreateRequestModel input) {
        return new Account(input.getName(), input.getAmount());
    }

    public Account(String name, Double sum) {
        this();
        this.name = name;
        this.sum = sum;
    }

    public Account() {
        this.users = new HashSet<>();
        this.receivedRequests = new HashSet<>();
        this.initiatedRequests = new HashSet<>();
        this.receivedPayments = new HashSet<>();
        this.sentPayments = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(targetEntity = User.class, mappedBy = "accounts", fetch = FetchType.EAGER)
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    @OneToMany(targetEntity = PaymentRequest.class, mappedBy = "receiver")
    public Set<PaymentRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(Set<PaymentRequest> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }

    @OneToMany(targetEntity = PaymentRequest.class, mappedBy = "sender")
    public Set<PaymentRequest> getInitiatedRequests() {
        return initiatedRequests;
    }

    public void setInitiatedRequests(Set<PaymentRequest> initiatedRequests) {
        this.initiatedRequests = initiatedRequests;
    }

    @OneToMany(targetEntity = Payment.class, mappedBy = "receiver")
    public Set<Payment> getReceivedPayments() {
        return receivedPayments;
    }

    public void setReceivedPayments(Set<Payment> receivedPayments) {
        this.receivedPayments = receivedPayments;
    }

    @OneToMany(targetEntity = Payment.class, mappedBy = "sender")
    public Set<Payment> getSentPayments() {
        return sentPayments;
    }

    public void setSentPayments(Set<Payment> sentPayments) {
        this.sentPayments = sentPayments;
    }

    @OneToMany(targetEntity = Invitation.class, mappedBy = "account")
    public Set<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(Set<Invitation> invitations) {
        this.invitations = invitations;
    }
}
