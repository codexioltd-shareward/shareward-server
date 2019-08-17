package bg.codexio.shareward.controller;


import bg.codexio.shareward.entity.User;
import bg.codexio.shareward.exception.account.AlreadyAnsweredPaymentException;
import bg.codexio.shareward.exception.account.NotEnoughMoneyException;
import bg.codexio.shareward.exception.user.NotAnOwnerException;
import bg.codexio.shareward.model.account.AccountCreateRequestModel;
import bg.codexio.shareward.model.account.InvitationCreateRequestModel;
import bg.codexio.shareward.model.account.PaymentRequestInputModel;
import bg.codexio.shareward.service.account.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public ResponseEntity myAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user.getAccounts());
    }

    @GetMapping("/accounts/filter")
    public ResponseEntity filterAccounts(@RequestParam String name) {
        return ResponseEntity.ok(this.accountService.listByNamePrefix(name));
    }

    @PostMapping("/accounts")
    public ResponseEntity create(@RequestBody @Valid AccountCreateRequestModel model, @AuthenticationPrincipal User user) throws NotEnoughMoneyException {
        return new ResponseEntity<>(this.accountService.create(user, model), HttpStatus.CREATED);
    }

    @GetMapping("/accounts/{accountId}/invitations")
    public ResponseEntity getInvitesByAccount(@PathVariable Long accountId, @AuthenticationPrincipal User user) throws NotAnOwnerException {
        return ResponseEntity.ok(this.accountService.getInvites(accountId, user));
    }

    @PostMapping("/accounts/{accountId}/invitations")
    public ResponseEntity invite(@PathVariable Long accountId, @RequestBody @Valid InvitationCreateRequestModel model, @AuthenticationPrincipal User inviter) throws NotAnOwnerException {
        return new ResponseEntity<>(this.accountService.invite(inviter, model.getInvitedId(), accountId), HttpStatus.CREATED);
    }

    @PatchMapping("/accounts/{accountId}/invitations/{invitationId}")
    public ResponseEntity answerInvitation(@PathVariable Long accountId, @PathVariable Long invitationId, @RequestParam boolean hasAccepted, @AuthenticationPrincipal User answerer) throws NotAnOwnerException {
        return hasAccepted ? ResponseEntity.ok(this.accountService.acceptInvitation(accountId, invitationId, answerer)) : ResponseEntity.ok(this.accountService.rejectInvitation(accountId, invitationId, answerer));
    }

    @PatchMapping("/accounts/{accountId/amount")
    public ResponseEntity deposit(@PathVariable Long accountId, @RequestParam Double amount, @AuthenticationPrincipal User user) throws NotEnoughMoneyException, NotAnOwnerException {
        return ResponseEntity.ok(this.accountService.deposit(accountId, amount, user));
    }

    @PostMapping("/accounts/{accountId}/initiatedRequests")
    public ResponseEntity requestPayment(@PathVariable Long accountId, @RequestBody @Valid PaymentRequestInputModel model, @AuthenticationPrincipal User user) throws NotAnOwnerException {
        return new ResponseEntity<>(this.accountService.requestPayment(accountId, user, model), HttpStatus.CREATED);
    }

    @PatchMapping("/accounts/{accountId}/initiatedRequests/{paymentRequestId}")
    public ResponseEntity answerPaymentRequest(@PathVariable Long accountId, @PathVariable Long paymentRequestId, @RequestParam boolean hasAccepted, @AuthenticationPrincipal User user) throws AlreadyAnsweredPaymentException, NotAnOwnerException, NotEnoughMoneyException {
        return hasAccepted ? ResponseEntity.ok(this.accountService.acceptPayment(accountId, paymentRequestId, user)) : ResponseEntity.ok(this.accountService.rejectPayment(accountId, paymentRequestId, user));
    }

    @GetMapping("/accounts/{accountId}/initiatedRequests")
    public ResponseEntity getPaymentRequests(@PathVariable Long accountId, @AuthenticationPrincipal User user) throws NotAnOwnerException {
        return ResponseEntity.ok(this.accountService.getPaymentRequests(accountId, user));
    }

    @GetMapping("/accounts/{accountId}/sentPayments")
    public ResponseEntity getPaidBills(@PathVariable Long accountId, @AuthenticationPrincipal User user) throws NotAnOwnerException {
        return ResponseEntity.ok(this.accountService.getPaidBills(accountId, user));
    }

    @GetMapping("/accounts/{accountId}/receivedPayments")
    public ResponseEntity getReceivedPayments(@PathVariable Long accountId, @AuthenticationPrincipal User user) throws NotAnOwnerException {
        return ResponseEntity.ok(this.accountService.getReceivedPayments(accountId, user));
    }
}
