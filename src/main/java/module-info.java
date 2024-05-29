module ru.vorotov.simulationslab12 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.vorotov.simulationslab12 to javafx.fxml;
    exports ru.vorotov.simulationslab12;
}