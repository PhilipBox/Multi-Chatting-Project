package application;
	
import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	//ThreadPool을 통해서 다양한 클라이언트가 접속했을 때, Thread들을 효과적으로 관리할 수 있기 위한 것.
	//즉, 여러개의 쓰레드를 효율적으로 관리하기 위해 사용하는 대표적인 라이브러
	//ThreadPool로 Thread를 처리하게 되면 기본적인 Thraed숫자에 제한을 두기 떄문에, 갑작스럽게 클라이언트 숫자가 폭증하더라도 Thread의 숫자에는 제한이 있기 때문에 서버의 성능 저하를 방지할 수 있다.
	// 즉, 한정된 자원을 이용해서 안정적으로 서버를 운용하기 위해 사용하는 것이 ThreadPool 기법.
	public static ExecutorService threadPool;
	public static Vector<Client> clients = new Vector<Client>();
	
	ServerSocket serverSocket;
	
	//서버를 구동시켜서 클라이언트의 연결을 기다리는 메소드.
	// parameters : 어떠한 아이피로,어떠한 포트를 열어서 클라이언트와 통신을 할 것인지.
	public void startServer(String IP, int port) {
		
	}
	
	//서버의 작동을 중지시키는 메소드.
	public void stopServer() {
		
	}
	
	// UI를 생성하고, 실질적으로 프로그램을 동작시키는 메소드.
	@Override
	public void start(Stage primaryStage) {
		
	}
	
	// 프로그램의 진입점
	public static void main(String[] args) {
		launch(args);
	}
}