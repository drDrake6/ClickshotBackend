package step.learning.dao;

import com.google.inject.Inject;
import step.learning.services.DataService;

import java.util.List;

public class NotificationDAO {

    private final DataService dataService;

    @Inject
    public NotificationDAO(DataService dataService)
    {
        this.dataService = dataService;
    }
}
