package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblPasswordNotMatch_1;
    public Label lblPasswordNotMatch_2;
    public TextField txtUserName;
    public TextField txtEMail;
    public Button btnRegister;
    public Label lblID;
    public AnchorPane root;

    public void initialize() {
        setVisible(false);

        setDisableCommon(true);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.equals(confirmPassword)) {

            setBorderColor("transparent");

            setVisible(false);

            register();

        }else {
            setBorderColor("red");

            setVisible(true);

            txtNewPassword.requestFocus();
        }

//        Connection connection = DBConnection.getInstance().getConnection();
//        System.out.println(connection);
    }

    public void setBorderColor(String color) { // set password boarder color
        txtNewPassword.setStyle("-fx-border-color: " + color);
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
    }

    public void setVisible(boolean isVisible) { // set password not match
        lblPasswordNotMatch_1.setVisible(isVisible);
        lblPasswordNotMatch_2.setVisible(isVisible);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);

        txtUserName.requestFocus();

        autoGenerateID();

    }

    public void setDisableCommon(boolean isDisable) {
        txtUserName.setDisable(isDisable);
        txtEMail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select uid from user order by uid desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist) {
                String userID = resultSet.getString(1);

                userID = userID.substring(1, userID.length());
                int intID = Integer.parseInt(userID);
                intID++;

                if(intID < 10) {
                    lblID.setText("U00" + intID);
                }else if (intID < 100) {
                    lblID.setText("U0" + intID);
                }else {
                    lblID.setText("U" + intID);
                }

            }else {
                lblID.setText("U001");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(){
        String id = lblID.getText();
        String userName = txtUserName.getText();
        String eMail = txtEMail.getText();
        String password = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if(userName.trim().isEmpty()){
            txtUserName.requestFocus();
        }else if(eMail.trim().isEmpty()) {
            txtEMail.requestFocus();
        }else if(password.trim().isEmpty()) {
            txtNewPassword.requestFocus();
        }else if(confirmPassword.trim().isEmpty()) {
            txtConfirmPassword.requestFocus();
        }else {
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?,?,?,?)");
                preparedStatement.setObject(1, id);
                preparedStatement.setObject(2, userName);
                preparedStatement.setObject(3, eMail);
                preparedStatement.setObject(4, password);

                preparedStatement.executeUpdate();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login");
                primaryStage.centerOnScreen();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }


        }


    }


}
