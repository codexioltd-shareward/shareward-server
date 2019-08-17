package bg.codexio.shareward.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "invitations")
public class Invitation {

    private Long id;

    private User inviter;

    private User invited;

    private Account account;

    private Date invitedOn;

    private Date acceptedOn;

    private Date rejectedOn;

    public Invitation() {
        this.invitedOn = new Date();
    }

    public Invitation(User invited) {
        this();
        this.invited = invited;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = User.class)
    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }

    @ManyToOne(targetEntity = User.class)
    public User getInvited() {
        return invited;
    }

    public void setInvited(User invited) {
        this.invited = invited;
    }

    public Date getInvitedOn() {
        return invitedOn;
    }

    public void setInvitedOn(Date invitedOn) {
        this.invitedOn = invitedOn;
    }

    public Date getAcceptedOn() {
        return acceptedOn;
    }

    public void setAcceptedOn(Date acceptedOn) {
        this.acceptedOn = acceptedOn;
    }

    public Date getRejectedOn() {
        return rejectedOn;
    }

    public void setRejectedOn(Date rejectedOn) {
        this.rejectedOn = rejectedOn;
    }

    @ManyToOne(targetEntity = Account.class)
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
