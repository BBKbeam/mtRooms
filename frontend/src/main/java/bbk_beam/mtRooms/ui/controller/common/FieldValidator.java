package bbk_beam.mtRooms.ui.controller.common;

import eadjlib.logger.Logger;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class FieldValidator {
    private final Logger log = Logger.getLoggerInstance(FieldValidator.class.getName());
    private HashMap<TextField, Boolean> validationMap;

    /**
     * Constructor
     */
    public FieldValidator() {
        this.validationMap = new HashMap<>();
    }

    /**
     * Add a TextField to the validator with a check flag set to 'false'
     *
     * @param textField UI TextField
     */
    public void add(TextField textField) {
        this.validationMap.put(textField, false);
    }

    /**
     * Sets a flag for new or existing field
     *
     * @param textField UI TextField
     * @param flag      Boolean flag
     */
    public void set(TextField textField, Boolean flag) {
        this.validationMap.put(textField, flag);
    }

    /**
     * Checks the field validation HashMap
     * <p>All failing fields' background will be coloured red.</p>
     *
     * @return Field validation state (true = all valid, false = 1+ invalid)
     */
    public boolean check() {
        boolean valid_flag = true;
        for (Map.Entry<TextField, Boolean> entry : this.validationMap.entrySet()) {
            if (!entry.getValue()) {
                entry.getKey().setStyle("-fx-control-inner-background: red;");
                valid_flag = false;
            }
        }
        return valid_flag;
    }
}
