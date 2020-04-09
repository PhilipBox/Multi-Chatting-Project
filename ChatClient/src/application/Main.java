package application;
	
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;



public class Main extends Application {
	Socket socket;
	TextArea textArea;
	
	// 클라이언트 프로그램 동작 메소드
	public void startClient(String IP, int port) {
		//서버프로그램과 다르게 여러개의 쓰레드가 동시다발적으로 생겨나는 경우가 없기 때문에
		//굳이 ThreadPool을 사용할 필요가 없다.
		//따라서 Runnable객체 대신에 단순하게 Thread객체를 사용한다.
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					// 소켓 초기화
					socket = new Socket(IP, port);
					// 메시지를 전달받도록 receive 메소드 호출 
					receive();
					
				} catch (Exception e) {
					//오류가 발생한 경우 
					if(!socket.isClosed()) {
						//stopClient 메소드를 호출해서 클라이언트를 종료
						stopClient();
						System.out.println("[서버 접속 실패]");
						//프로그램 자체를 종료시킨다.
						Platform.exit();
					}
				}
			}
		};
		thread.start();
	}
	
	
	// 클라이언트 프로그램 종료 메소드
	public void stopClient() {
		try {
			// 소켓이 열려있는 상태라면 
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// 서버로부터 메시지를 전달받는 메소드
	// 계속 전달받기 위해서 무한loop를 돌려준다.
	public void receive() {
		while(true) {
			try {
				// 현재 서버로부터 어떠한 메시지를 전달받을 수 있도록.
				InputStream in = socket.getInputStream();
				// 512byte만큼 버퍼에 담아서 끊어서 계속 전달받을 것.
				byte[] buffer = new byte[512];
				// read함수로 실제로 입력을 받는다. 
				int length = in.read(buffer);
				// 내용을 입력받는 도중에 오류가 발생하면 IOException을 발생시킨다.
				if(length == -1 ) throw new IOException();
				// message에 버퍼에 있는 정보를 담기.
				String message = new String(buffer, 0, length, "UTF-8");
				// 화면에 출력
				Platform.runLater(()->{
					// textArea는 GUI 요소중 하나로써 화면에 출력해주는 요소
					textArea.appendText(message);
				});
				
			} catch (Exception e) {
				//오류가 발생했을 때는 stopClient 호출 후 반복문 break;
				stopClient();
				break;
			}
		}
	}
	
	
	// 서버로 메시지를 전송하는 메소드
	public void send(String message) {
		// 여기서도 메시지를 전송할 때 Thread를 이용하는데,
		// 서버로 메시지를 전송하기 위한 Thread 1개
		// 서버로부터 메시지를 전달받는 Thread 1개
		// 이렇게 총 2개의 Thread가 각각 다른 역할을 가진다.
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					// 보내고자 하는 것을 UTF-8로 인코딩을 한다.
					// 서버에서 전달받을 때 UTF-8로 인코딩된 것을 받도록 해두었기 때문.
					byte[] buffer = message.getBytes("UTF-8");
					//메시지 전송
					out.write(buffer);
					//메시지 전송의 끝을 알림.
					out.flush();
					
				} catch (Exception e) {
					//오류가 발생했다면
					stopClient();
				}
			}
		};
		thread.start();
	}
	
	
	// 실제로 프로그램을 동작시키는 메소드
	@Override
	public void start(Stage primaryStage) {
		
	}
	
	// 프로그램의 진입접입니다.
	public static void main(String[] args) {
		launch(args);
	}
}
