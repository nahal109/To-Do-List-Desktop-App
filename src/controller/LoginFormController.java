package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {

    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;


    public static String loginUserID;
    public static String loginUserName;

    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../view/CreateNewAccountForm.fxml"));

        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register");
        primaryStage.centerOnScreen();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {

        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        if(userName.trim().isEmpty()) {
            txtUserName.requestFocus();
        }else if(password.trim().isEmpty()) {
            txtPassword.requestFocus();
        }else {

            Connection connection = DBConnection.getInstance().getConnection();

            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement("select * from user where name = ? and password = ?");
                preparedStatement.setObject(1, userName);
                preparedStatement.setObject(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    loginUserID = resultSet.getString(1);
                    loginUserName = resultSet.getString(2);

                    Parent parent = FXMLLoader.load(getClass().getResource("../view/ToDoForm.fxml"));

                    Scene scene = new Scene(parent);

                    Stage primaryStage = (Stage) root.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("To Do");
                    primaryStage.centerOnScreen();

                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"user name or password incorrect");
                    alert.showAndWait();

                    txtUserName.clear();
                    txtPassword.clear();
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
