package bg.codexio.shareward.service.payment;

import bg.codexio.shareward.entity.Fund;
import bg.codexio.shareward.entity.User;
import bg.codexio.shareward.model.payment.AddFundsRequestModel;
import java.net.http.HttpClient;

import bg.codexio.shareward.repository.FundRepository;
import bg.codexio.shareward.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaysafePaymentProcessor implements PaymentProcessor {

    private final HttpClient httpClient;
    private final String accountId;
    private final String accountToken;
    private final String baseUrl;
    private final FundRepository fundRepository;
    private final UserRepository userRepository;

    public PaysafePaymentProcessor(
            @Value("${paysafe.account.id}") String accountId,
            @Value("${paysafe.account.token}") String accountToken,
            @Value("${paysafe.processor.url}") String baseUrl,
            FundRepository fundRepository,
            UserRepository userRepository) {
        this.accountId = accountId;
        this.accountToken = accountToken;
        this.baseUrl = baseUrl;
        this.fundRepository = fundRepository;
        this.userRepository = userRepository;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public boolean addFunds(AddFundsRequestModel model, String ip, User user) throws IOException, InterruptedException {
        var url = String.format("%s%s%s%s", this.baseUrl, "cardpayments/v1/accounts/", this.accountId, "/auths");
        var identification = "Codexio-" + new Date() + "-funds-" + user.getId() + "-" + UUID.randomUUID().toString();
        var response = this.httpClient.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Basic " + this.accountToken)
                        .header("Content-type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                new ObjectMapper().writeValueAsString(
                                        Map.of(
                                                "merchantRefNum", identification,
                                                "amount", model.getAmount(),
                                                "settleWithAuth", true,
                                                "dupCheck", false,
                                                "card", Map.of(
                                                        "cardNum", model.getCardNum(),
                                                        "cardExpiry", Map.of(
                                                                "month", model.getExpiryMonth(),
                                                                "year", model.getExpiryYear()
                                                        ),
                                                        "cvv", model.getCvv()
                                                ),
                                                "profile", Map.of(
                                                        "firstName", model.getFirstName(),
                                                        "lastName", model.getLastName(),
                                                        "email", model.getEmail()

                                                ),
                                                "billingDetails", Map.of(
                                                        "street", model.getBillingStreet(),
                                                        "city", model.getBillingCity(),
                                                        "state", model.getBillingState(),
                                                        "country", model.getBillingCountryCode(),
                                                        "zip", model.getBillingZipCode()
                                                ),
                                                "customerIp", ip,
                                                "description", "Funds"
                                        )
                                )
                                )
                        ).build(),
                HttpResponse.BodyHandlers.ofString()
        );

        var body = (HashMap<String, String>)new ObjectMapper().readValue(response.body(), HashMap.class);

        if (!body.containsKey("status") || !body.get("status").equals("COMPLETED")) {
            return false;
        }

        var fundTransaction = new Fund();
        fundTransaction.setBoughtOn(new Date());
        fundTransaction.setBuyer(user);
        fundTransaction.setAmount(model.getAmount() / 100.00);
        fundTransaction.setTransactionIdentification(identification);

        user.setMoney(user.getMoney() + fundTransaction.getAmount());

        this.fundRepository.saveAndFlush(fundTransaction);
        this.userRepository.saveAndFlush(user);

        return true;
    }
}
