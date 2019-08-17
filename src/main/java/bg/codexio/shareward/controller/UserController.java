package bg.codexio.shareward.controller;

import bg.codexio.shareward.exception.user.DuplicateEmailException;
import bg.codexio.shareward.exception.user.PasswordMismatchException;
import bg.codexio.shareward.model.user.UserRegisterRequestModel;
import bg.codexio.shareward.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/users")
    public ResponseEntity register(@RequestBody @Valid UserRegisterRequestModel model) throws PasswordMismatchException, DuplicateEmailException {
        return new ResponseEntity<>(this.userService.register(model), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity getByPrefix(@RequestParam(required = false) String prefix) {
        if (prefix == null || prefix.length() < 3) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok(this.userService.listByEmailPrefix(prefix));
    }

}
