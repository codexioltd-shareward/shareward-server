package bg.codexio.shareward.service.user;

import bg.codexio.shareward.entity.PaymentRequest;
import bg.codexio.shareward.entity.User;
import bg.codexio.shareward.exception.user.DuplicateEmailException;
import bg.codexio.shareward.exception.user.PasswordMismatchException;
import bg.codexio.shareward.model.user.UserRegisterRequestModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface UserService extends UserDetailsService {

    User register(UserRegisterRequestModel request) throws PasswordMismatchException, DuplicateEmailException;

    Set<User> listByEmailPrefix(String emailPrefix);

    Set<PaymentRequest> getReceivedPaymentRequests(User user);
}
