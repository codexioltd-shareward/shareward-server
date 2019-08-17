package bg.codexio.shareward.service.account;

import bg.codexio.shareward.entity.*;
import bg.codexio.shareward.exception.account.AlreadyAnsweredPaymentException;
import bg.codexio.shareward.exception.account.NotEnoughMoneyException;
import bg.codexio.shareward.exception.user.NotAnOwnerException;
import bg.codexio.shareward.model.account.AccountCreateRequestModel;
import bg.codexio.shareward.model.account.PaymentRequestInputModel;

import java.util.Set;

public interface AccountService {

    Account create(User user, AccountCreateRequestModel model) throws NotEnoughMoneyException;

    Invitation invite(User inviter, Long invitedId, Long accountId) throws NotAnOwnerException;

    Account find(Long accountId);

    Set<Invitation> getInvites(Long accountId, User user) throws NotAnOwnerException;

    Invitation acceptInvitation(Long accountId, Long invitationId, User answerer) throws NotAnOwnerException;

    Invitation rejectInvitation(Long accountId, Long invitationId, User answerer) throws NotAnOwnerException;

    PaymentRequest requestPayment(Long accountId, User user, PaymentRequestInputModel model) throws NotAnOwnerException;

    Object acceptPayment(Long accountId, Long paymentRequestId, User user) throws NotAnOwnerException, AlreadyAnsweredPaymentException, NotEnoughMoneyException;

    PaymentRequest rejectPayment(Long accountId, Long paymentRequestId, User user) throws NotAnOwnerException, AlreadyAnsweredPaymentException;

    Set<PaymentRequest> getPaymentRequests(Long accountId, User user) throws NotAnOwnerException;

    Set<Payment> getPaidBills(Long accountId, User user) throws NotAnOwnerException;

    Set<Payment> getReceivedPayments(Long accountId, User user) throws NotAnOwnerException;

    Account deposit(Long accountId, Double amount, User user) throws NotAnOwnerException, NotEnoughMoneyException;

    Set<Account> listByNamePrefix(String namePrefix);

    Set<User> getParticipants(Long accountId, User user) throws NotAnOwnerException;

    Account details(Long accountId, User user) throws NotAnOwnerException;
}
