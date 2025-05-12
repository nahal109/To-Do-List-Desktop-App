package controller;

import com.sun.javafx.menu.MenuItemBase;
import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import javax.imageio.plugins.tiff.TIFFImageReadParam;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public AnchorPane root;
    public Label lblTitle;
    public Label lblUserID;
    public Pane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;
    public TextField txtSelectedToDo;
    public Button btnDelete;
    public Button btnUpdate;

    public void initialize() {
        lblTitle.setText("HI.... " + LoginFormController.loginUserName + " Welcome To Do List");
        lblUserID.setText(LoginFormController.loginUserID);

        subRoot.setVisible(false);

        loadList();

        setDisableComman(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observableValue, ToDoTM toDoTM, ToDoTM t1) {

                if(lstToDo.getSelectionModel().getSelectedItem() == null ){
                    return;
                }

                setDisableComman(false);

                subRoot.setVisible(false);

                txtSelectedToDo.setText(lstToDo.getSelectionModel().getSelectedItem().getDescription());
            }
        });
    }

    public void setDisableComman(boolean isDisable) {
        txtSelectedToDo.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);
        btnDelete.setDisable(isDisable);

        txtSelectedToDo.clear();

    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do You Want To Log Out....?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.centerOnScreen();
        }
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        lstToDo.getSelectionModel().clearSelection();

        txtDescription.requestFocus();

        subRoot.setVisible(true);

        setDisableComman(true);
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        String id = autoGenerateID();
        String description = txtDescription.getText();
        String userId = lblUserID.getText();

        if(description.trim().isEmpty()){
            txtDescription.requestFocus();
        }else {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values (?,?,?)");
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, userId);

                preparedStatement.executeUpdate();

//            System.out.println("Added");

                txtDescription.clear();
                subRoot.setVisible(false);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            loadList();
        }
    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        String id = "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist) {
                String todoID = resultSet.getString(1);

                todoID = todoID.substring(1, todoID.length());
                int intID = Integer.parseInt(todoID);
                intID++;

                if(intID < 10) {
                    id = "T00" + intID;
                }else if (intID < 100) {
                    id = "T0" + intID;
                }else {
                    id = "T" + intID;
                }

            }else {
                id = "T001";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    public void loadList(){
        ObservableList<ToDoTM> todoS = lstToDo.getItems();
        todoS.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");
            preparedStatement.setObject(1, lblUserID.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String userID = resultSet.getString(3);

                todoS.add(new ToDoTM(id,description,userID));
            }

            lstToDo.refresh();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedToDo.getText();
        String id = lstToDo.getSelectionModel().getSelectedItem().getUser_id();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1, description);
            preparedStatement.setObject(2, id);

            preparedStatement.executeUpdate();

            loadList();

            setDisableComman(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do You Want To Delete This To Do?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            String id = lstToDo.getSelectionModel().getSelectedItem().getUser_id();
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1, id);

                preparedStatement.executeUpdate();

                loadList();

                setDisableComman(true);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }


    }
}
