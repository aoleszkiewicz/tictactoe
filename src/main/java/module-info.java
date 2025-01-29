module com.example.oxgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires org.hsqldb;

    opens com.example.oxgame to javafx.fxml;
    opens com.example.model to javafx.base;
    exports com.example.oxgame;
}
