package bg.codexio.shareward.service.user;

import bg.codexio.shareward.entity.PaymentRequest;
import bg.codexio.shareward.entity.User;
import bg.codexio.shareward.exception.user.DuplicateEmailException;
import bg.codexio.shareward.exception.user.PasswordMismatchException;
import bg.codexio.shareward.model.user.UserRegisterRequestModel;
import bg.codexio.shareward.repository.PaymentRequestRepository;
import bg.codexio.shareward.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PaymentRequestRepository paymentRequestRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.paymentRequestRepository = paymentRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.userRepository.findFirstByEmail(s).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public User register(UserRegisterRequestModel request) throws PasswordMismatchException, DuplicateEmailException {
        if (!request.getPassword().equals(request.getConfirm())) {
            throw new PasswordMismatchException();
        }

        if (this.userRepository.findFirstByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException();
        }

        request.setPassword(this.passwordEncoder.encode(request.getPassword()));

        return this.userRepository.saveAndFlush(User.fromRegisterRequest(request));
    }

    @Override
    public Set<User> listByEmailPrefix(String emailPrefix) {
        return this.userRepository.findAllByEmailStartingWith(emailPrefix);
    }
}
