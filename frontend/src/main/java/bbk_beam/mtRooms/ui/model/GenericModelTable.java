package bbk_beam.mtRooms.ui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;

/**
 * Generic base table class for use with TableView
 *
 * @param <BaseType>  Base class the model uses
 * @param <ModelType> Model type
 */
public abstract class GenericModelTable<BaseType, ModelType> {
    protected SessionManager sessionManager;
    protected ObservableList<ModelType> observableList = FXCollections.observableArrayList();

    /**
     * Constructor
     *
     * @param sessionManager SessionManager instance
     */
    public GenericModelTable(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Loads the table with updated data from backend
     *
     * @param collection Collection of base types to use for modelling
     */
    public abstract void loadData(Collection<BaseType> collection);

    /**
     * Gets the list of model data
     *
     * @return Observable list
     */
    public ObservableList<ModelType> getData() {
        return this.observableList;
    }
}
