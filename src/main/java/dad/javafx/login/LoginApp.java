package dad.javafx.login;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {
	
	private Label userLabel, passLabel;
	private TextField userText;
	private PasswordField passText;
	private ComboBox<String> authModesCombo;
	private Button loginButton;
	
	private String auth;
	private String username;
	private String password;
	private boolean coincidence = false;
	
	private Stage primaryStage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		
		this.primaryStage = primaryStage;
		
		userLabel = new Label("Usuario:");
		userLabel.setMinWidth(80);
		passLabel = new Label("Contraseña:");
		passLabel.setMinWidth(80);
		
		userText = new TextField();
		userText.setPromptText("Nombre de usuario");
		userText.setMaxWidth(80);
		
		passText = new PasswordField();
		passText.setPromptText("Contraseña");
		passText.setMaxWidth(80);
		
		authModesCombo = new ComboBox<String>();
		authModesCombo.getItems().addAll("CuentaLocal", "LDAP", "BaseDatos");
		authModesCombo.setPromptText("Modo de autenticación");
		
		loginButton = new Button("Acceder");
		loginButton.setDefaultButton(true);
		if( authModesCombo.getSelectionModel().getSelectedItem() != "" ) {

			loginButton.setOnAction( e -> onLoginButtonAction() );
		}else {
			lanzarErrorAuth();
		}
		
		
		HBox userBox = new HBox(5, userLabel, userText);
		userBox.setAlignment(Pos.CENTER);
//		userBox.setStyle("-fx-background-color:green");
		
		HBox passBox = new HBox(5, passLabel, passText);
		passBox.setAlignment(Pos.CENTER);
//		passBox.setStyle("-fx-background-color:green");
		
		HBox authBox = new HBox(5, authModesCombo, loginButton);
		authBox.setAlignment(Pos.CENTER);
//		authBox.setStyle("-fx-background-color:purple");
		
		
		VBox root = new VBox(5, userBox, passBox, authBox);
		root.setAlignment(Pos.CENTER);
//		root.setStyle("-fx-background-color:blue");
		
		Scene scene = new Scene(root, 240, 110);
		
		primaryStage.setTitle("Iniciar sesión");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
	}

	private void onLoginButtonAction() {
		
		this.username = userText.getText();
		this. password = passText.getText();
		this.auth = authModesCombo.getSelectionModel().getSelectedItem();
		
		int codCoincidencia = compruebaContraseña(compruebaUsuario());
		
		if( codCoincidencia == -1 ) {
			lanzarError();
		}else {
			if( codCoincidencia == 0 ) {
				lanzarWrongUser();
			}else {
				lanzarBienvenida();
			}
		}
		
	}
	
	private void lanzarError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(primaryStage);
		alert.setResizable(false);
		alert.setHeaderText("Usuario no encontrado");
		alert.setContentText("El usuario: " + this.username + " no es correcto o no ha sido registrado.");
		alert.showAndWait();
		
	}
	
	private void lanzarErrorAuth() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(primaryStage);
		alert.setResizable(false);
		alert.setHeaderText("Modo autenticación no seleccionado");
		alert.setContentText("Para avanzar en el inicio de sesión debe seleccionar el modo de autenticación.");
		alert.showAndWait();
		
	}
	
	private void lanzarWrongUser() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(primaryStage);
		alert.setResizable(false);
		alert.setHeaderText("Contraseña erronea");
		alert.setContentText("La contraseña " + this.password + " no es la correcta para el usuario " + this.username + ".");
		alert.showAndWait();
		
	}
	
	private void lanzarBienvenida() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setResizable(false);
		alert.setHeaderText("Usuario encontrado");
		alert.setContentText("Bienvenido " + this.username + "!!");
		alert.showAndWait();
		
		Platform.exit();
	}
	
//	Comprueba que exista el usuario en el archivo userrs.txt y devuelve la posicion(-1 si no existe).
	private int compruebaUsuario() {
		
		ArrayList<String> usuarios = dumpData("users");
		int posMarcada = -1;
		
		for(int i = 0; i < usuarios.size(); i++) {
			if( usuarios.get(i).equals(this.username) && !coincidence ) {
				posMarcada = i;
				this.coincidence = true;
			}
		}
		
		return posMarcada;
		
	}
	
//	Comprueba si la contraseña introducida coincide con la correspondiente al usuario.
//	Devuelve -1 si no existe el usuario, 0 si la contraseña no coincide o 1 si coincide.
	private int compruebaContraseña(int pos) {

		int posMarcada = pos;
		int codReturn = -1;
		if( posMarcada > -1 ) {
			ArrayList<String> pass = dumpData("passwords");
			if( pass.get(posMarcada).equals(this.password) ) {
				codReturn = 1;
			}else {
				codReturn = 0;
			}
		}
		
		return codReturn;
	}
	
//	Vuelca los datos de los archivos users.txt o passwords.txt en ArrayList<Strings>.
	private ArrayList<String> dumpData(String usersOrPass) {
		
		ArrayList<String> usuarios = new ArrayList<String>();
		URL fileLocation = getClass().getClassLoader().getResource(this.auth + "/" + usersOrPass + ".txt");
		
		try {
			FileInputStream archivo = new FileInputStream(fileLocation.getFile());
			InputStreamReader lector = new InputStreamReader(archivo, "UTF8");
			BufferedReader datos = new BufferedReader(lector);
			
			String linea = "";
			
			while( (linea = datos.readLine()) != null ) {
				usuarios.add(linea);
			}
			
			archivo.close();
			lector.close();
			datos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return usuarios;
		
	}

}
