package bg.codexio.shareward.service.payment;

import bg.codexio.shareward.entity.User;
import bg.codexio.shareward.model.payment.AddFundsRequestModel;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface PaymentProcessor {

    boolean addFunds(AddFundsRequestModel model, String ip, User user) throws JsonProcessingException, IOException, InterruptedException;

}
