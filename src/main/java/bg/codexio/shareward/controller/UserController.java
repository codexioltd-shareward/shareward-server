package bg.codexio.shareward.controller;

import bg.codexio.shareward.entity.User;
import bg.codexio.shareward.exception.user.DuplicateEmailException;
import bg.codexio.shareward.exception.user.PasswordMismatchException;
import bg.codexio.shareward.model.payment.AddFundsRequestModel;
import bg.codexio.shareward.model.user.UserRegisterRequestModel;
import bg.codexio.shareward.service.payment.PaymentProcessor;
import bg.codexio.shareward.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
public class UserController {

    private final UserService userService;
    private final PaymentProcessor paymentProcessor;

    public UserController(UserService userService, PaymentProcessor paymentProcessor) {
        this.userService = userService;
        this.paymentProcessor = paymentProcessor;
    }

    @GetMapping("/users/me")
    public ResponseEntity me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity register(@RequestBody @Valid UserRegisterRequestModel model) throws PasswordMismatchException, DuplicateEmailException {
        return new ResponseEntity<>(this.userService.register(model), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity getByPrefix(@RequestParam(name = "email", required = false) String emailPrefix) {
        return emailPrefix == null || emailPrefix.length() < 3 ? ResponseEntity.ok().build() : ResponseEntity.ok(this.userService.listByEmailPrefix(emailPrefix));
    }

    @GetMapping("/users/me/invitations")
    public ResponseEntity getReceivedInvites(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user.getReceivedInvitations());
    }

    @GetMapping("/users/me/receivedPaymentRequests")
    public ResponseEntity getReceivedPaymentRequests(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(this.userService.getReceivedPaymentRequests(user));
    }

    @PostMapping("/users/me/funds")
    public ResponseEntity addFunds(@RequestBody AddFundsRequestModel model, @AuthenticationPrincipal User user, HttpServletRequest request) throws IOException, InterruptedException {
        return ResponseEntity.ok(this.paymentProcessor.addFunds(model, request.getRemoteAddr(), user));
    }

}
