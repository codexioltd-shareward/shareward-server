package bg.codexio.shareward.entity;

import bg.codexio.shareward.constant.ConfigurationConstants;
import bg.codexio.shareward.model.user.UserRegisterRequestModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    private Long id;

    private String email;

    private String password;

    private String fullName;

    private Set<Account> accounts;

    private Set<Invitation> sentInvitations;

    private Set<Invitation> receivedInvitations;

    private Set<PaymentRequest> acceptedPayments;

    private Set<PaymentRequest> rejectedPayments;

    private Set<Fund> funds;

    private Double money;

    public User() {
        this.money = ConfigurationConstants.User.INITIAL_MONEY;
        this.accounts = new HashSet<>();
        this.acceptedPayments = new HashSet<>();
        this.rejectedPayments = new HashSet<>();
    }

    public User(String email, String password, String fullName) {
        this();
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public static User fromRegisterRequest(UserRegisterRequestModel input) {
        return new User(
                input.getEmail(),
                input.getPassword(),
                input.getFullName()
        );
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToMany(targetEntity = PaymentRequest.class, fetch = FetchType.EAGER)
    public Set<PaymentRequest> getAcceptedPayments() {
        return acceptedPayments;
    }

    public void setAcceptedPayments(Set<PaymentRequest> acceptedPayments) {
        this.acceptedPayments = acceptedPayments;
    }

    @ManyToMany(targetEntity = PaymentRequest.class, fetch = FetchType.EAGER)
    public Set<PaymentRequest> getRejectedPayments() {
        return rejectedPayments;
    }

    public void setRejectedPayments(Set<PaymentRequest> rejectedPayments) {
        this.rejectedPayments = rejectedPayments;
    }

    @ManyToMany(targetEntity = Account.class, fetch = FetchType.EAGER)
    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    @OneToMany(targetEntity = Invitation.class, mappedBy = "inviter", fetch = FetchType.EAGER)
    public Set<Invitation> getSentInvitations() {
        return sentInvitations;
    }

    public void setSentInvitations(Set<Invitation> sentInvitations) {
        this.sentInvitations = sentInvitations;
    }

    @OneToMany(targetEntity = Invitation.class, mappedBy = "invited", fetch = FetchType.EAGER)
    public Set<Invitation> getReceivedInvitations() {
        return receivedInvitations;
    }

    public void setReceivedInvitations(Set<Invitation> receivedInvitations) {
        this.receivedInvitations = receivedInvitations;
    }

    @OneToMany(targetEntity = Fund.class, mappedBy = "buyer")
    public Set<Fund> getFunds() {
        return funds;
    }

    public void setFunds(Set<Fund> funds) {
        this.funds = funds;
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public String getPassword() {
        return password;
    }

    @Override
    @Transient
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) &&
                Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail());
    }
}
