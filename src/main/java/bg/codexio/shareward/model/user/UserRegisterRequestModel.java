package bg.codexio.shareward.model.user;

import bg.codexio.shareward.constant.ConfigurationConstants;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class UserRegisterRequestModel {

    @Email(message = ConfigurationConstants.User.ERROR_EMAIL_VALIDATION)
    private String email;

    @Pattern(regexp = ".*", message = ConfigurationConstants.User.ERROR_PASSWORD_VALIDATION)
    private String password;

    private String confirm;

    @Length(min = 10, message = ConfigurationConstants.User.ERROR_FULL_NAME_VALIDATION)
    private String fullName;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
