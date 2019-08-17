package bg.codexio.shareward.model;

import java.util.ArrayList;
import java.util.List;

public class ErrorViewModel {

    private final List<String> errors;

    public ErrorViewModel() {
        this.errors = new ArrayList<>();
    }

    public void add(String message) {
        this.errors.add(message);
    }

    public List<String> getErrors() {
        return errors;
    }
}
