package bbk_beam.mtRooms.ui.model.common;

public class GenericChoiceBoxItem<T> {
    private T dto = null;
    private String text;

    /**
     * Constructor (Empty DTO)
     *
     * @param text Display text to show in ChoiceBox
     */
    public GenericChoiceBoxItem(String text) {
        this.text = text;
    }

    /**
     * Contructor
     *
     * @param dto  DTO to assign
     * @param text Display text to show in ChoiceBox
     */
    public GenericChoiceBoxItem(T dto, String text) {
        this.dto = dto;
        this.text = text;
    }

    /**
     * Gets the assigned DTO
     *
     * @return Assigned DTO
     */
    public T getDTO() {
        if (this.dto != null)
            return this.dto;
        else
            throw new NullPointerException("GenericChoiceBox<T> has no DTO assigned.");
    }

    /**
     * Check DTO assignment exists
     *
     * @return DTO existence state
     */
    public boolean hasDTO() {
        return this.dto != null;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
