package bg.codexio.shareward.model.account;

public class PaymentRequestInputModel {

    private Long receiverId;

    private Double sum;

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
