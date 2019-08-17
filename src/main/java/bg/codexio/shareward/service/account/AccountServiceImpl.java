package bg.codexio.shareward.service.account;

import bg.codexio.shareward.entity.*;
import bg.codexio.shareward.exception.account.AlreadyAnsweredPaymentException;
import bg.codexio.shareward.exception.account.NotEnoughMoneyException;
import bg.codexio.shareward.exception.user.NotAnOwnerException;
import bg.codexio.shareward.model.account.AccountCreateRequestModel;
import bg.codexio.shareward.model.account.PaymentRequestInputModel;
import bg.codexio.shareward.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private static final double PAYMENT_REQUEST_SUCCESS_FACTOR = .5;
    private final AccountRepository accountRepository;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentRepository paymentRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              InvitationRepository invitationRepository,
                              UserRepository userRepository,
                              PaymentRequestRepository paymentRequestRepository,
                              PaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.paymentRequestRepository = paymentRequestRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Account create(User user, AccountCreateRequestModel model) throws NotEnoughMoneyException {
        if (user.getMoney() < model.getAmount()) {
            throw new NotEnoughMoneyException();
        }

        user.setMoney(user.getMoney() - model.getAmount());

        var account = Account.fromCreateRequest(model);
        account.getUsers().add(user);

        user.getAccounts().add(account);
        this.accountRepository.saveAndFlush(account);
        this.userRepository.saveAndFlush(user);

        return account;
    }

    @Override
    public Invitation invite(User inviter, Long invitedId, Long accountId) throws NotAnOwnerException {
        var account = this.getAccount(accountId, inviter);

        var invited = this.userRepository.findById(invitedId).orElseThrow(IllegalArgumentException::new);

        var invitation = new Invitation(invited);
        invitation.setInviter(inviter);
        invitation.setAccount(account);

        return this.invitationRepository.saveAndFlush(invitation);
    }

    @Override
    public Account find(Long accountId) {
        return this.accountRepository.findById(accountId).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Set<Invitation> getInvites(Long accountId, User user) throws NotAnOwnerException {
        this.getAccount(accountId, user);

        return this.invitationRepository.findByAccountId(accountId);
    }

    @Override
    public Invitation acceptInvitation(Long accountId, Long invitationId, User answerer) throws NotAnOwnerException {
        var account = this.accountRepository.findById(accountId).orElseThrow(IllegalArgumentException::new);

        var invitation = this.answerInvitation(
                invitationId,
                answerer,
                i -> i.setAcceptedOn(new Date())
        );

        account.getUsers().add(answerer);

        this.accountRepository.saveAndFlush(account);

        return invitation;
    }

    @Override
    public Invitation rejectInvitation(Long accountId, Long invitationId, User answerer) throws NotAnOwnerException {
        return this.answerInvitation(
                invitationId,
                answerer,
                i -> i.setRejectedOn(new Date())
        );
    }

    @Override
    public PaymentRequest requestPayment(Long accountId, User user, PaymentRequestInputModel model) throws NotAnOwnerException {
        var account = this.getAccount(accountId, user);
        var request = new PaymentRequest();
        request.setReceiver(this.accountRepository.findById(model.getReceiverId()).orElseThrow(IllegalArgumentException::new));
        request.setAmount(model.getSum());
        request.setSender(account);

        return this.paymentRequestRepository.saveAndFlush(request);
    }

    @Override
    public Object acceptPayment(Long accountId, Long paymentRequestId, User user) throws NotAnOwnerException, AlreadyAnsweredPaymentException, NotEnoughMoneyException {
        var request = this.paymentRequestRepository.findById(paymentRequestId).orElseThrow(IllegalArgumentException::new);
        if (!request.getSender().getId().equals(accountId)) {
            throw new IllegalArgumentException();
        }

        if (request.getSender().getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new NotAnOwnerException();
        }

        if (request.getRejectedUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new AlreadyAnsweredPaymentException();
        }

        request.getAcceptedUsers().add(user);
        user.getAcceptedPayments().add(request);
        this.userRepository.saveAndFlush(user);

        if ((request.getAcceptedUsers().size() * 1.0) / request.getSender().getUsers().size() > PAYMENT_REQUEST_SUCCESS_FACTOR) {
            var payment = new Payment();
            payment.setAmount(request.getAmount());
            payment.setReceiver(request.getReceiver());
            payment.setSender(request.getSender());

            request.getSender().setSum(request.getSender().getSum() - payment.getAmount());

            if (request.getSender().getSum() < 0) {
                throw new NotEnoughMoneyException();
            }

            request.getReceiver().setSum(request.getReceiver().getSum() + payment.getAmount());

            request = this.paymentRequestRepository.saveAndFlush(request);

            this.accountRepository.saveAndFlush(request.getSender());
            this.accountRepository.saveAndFlush(request.getReceiver());

            return this.paymentRepository.saveAndFlush(payment);
        }

        return request;
    }

    @Override
    public PaymentRequest rejectPayment(Long accountId, Long paymentRequestId, User user) throws NotAnOwnerException, AlreadyAnsweredPaymentException {
        var request = this.paymentRequestRepository.findById(paymentRequestId).orElseThrow(IllegalArgumentException::new);
        if (!request.getSender().getId().equals(accountId)) {
            throw new IllegalArgumentException();
        }

        if (request.getSender().getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new NotAnOwnerException();
        }

        if (request.getAcceptedUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new AlreadyAnsweredPaymentException();
        }

        request.getRejectedUsers().add(user);
        user.getRejectedPayments().add(request);

        this.userRepository.saveAndFlush(user);

        return this.paymentRequestRepository.saveAndFlush(request);
    }

    @Override
    public Set<PaymentRequest> getPaymentRequests(Long accountId, User user) throws NotAnOwnerException {
        return this.paymentRequestRepository.findAllBySenderIn(Set.of(this.getAccount(accountId, user)));
    }

    @Override
    public Set<Payment> getPaidBills(Long accountId, User user) throws NotAnOwnerException {
        return this.paymentRepository.findAllBySender(this.getAccount(accountId, user));
    }


    @Override
    public Set<Payment> getReceivedPayments(Long accountId, User user) throws NotAnOwnerException {
        return this.paymentRepository.findAllByReceiver(this.getAccount(accountId, user));
    }

    @Override
    public Account deposit(Long accountId, Double amount, User user) throws NotAnOwnerException, NotEnoughMoneyException {
        var account = this.getAccount(accountId, user);

        if (user.getMoney() < amount) {
            throw new NotEnoughMoneyException();
        }

        account.setSum(account.getSum() + amount);
        user.setMoney(user.getMoney() - amount);

        this.userRepository.saveAndFlush(user);

        return this.accountRepository.saveAndFlush(account);
    }

    @Override
    public Set<Account> listByNamePrefix(String namePrefix) {
        return this.accountRepository.findAllByNameStartingWith(namePrefix);
    }

    @Override
    public Set<User> getParticipants(Long accountId, User user) throws NotAnOwnerException {
        return this.getAccount(accountId, user).getUsers();
    }

    @Override
    public Account details(Long accountId, User user) throws NotAnOwnerException {
        return this.getAccount(accountId, user);
    }

    private Account getAccount(Long accountId, User user) throws NotAnOwnerException {
        var account = this.accountRepository.findById(accountId).orElseThrow(IllegalArgumentException::new);

        if (account.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new NotAnOwnerException();
        }

        return account;
    }

    private Invitation answerInvitation(Long invitationId, User answerer, Consumer<Invitation> invitationConsumer) throws NotAnOwnerException {
        var invitation = this.invitationRepository.findById(invitationId).orElseThrow(IllegalArgumentException::new);
        if (!invitation.getInvited().getId().equals(answerer.getId())) {
            throw new NotAnOwnerException();
        }

        if (invitation.getAcceptedOn() != null || invitation.getRejectedOn() != null) {
            throw new UnsupportedOperationException();
        }

        invitationConsumer.accept(invitation);

        return this.invitationRepository.saveAndFlush(invitation);
    }
}
