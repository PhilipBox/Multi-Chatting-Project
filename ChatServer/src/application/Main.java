package application;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	// ThreadPool을 통해서 다양한 클라이언트가 접속했을 때, Thread들을 효과적으로 관리할 수 있기 위한 것.
	// 즉, 여러개의 쓰레드를 효율적으로 관리하기 위해 사용하는 대표적인 라이브러
	// ThreadPool로 Thread를 처리하게 되면 기본적인 Thraed숫자에 제한을 두기 떄문에, 갑작스럽게 클라이언트 숫자가 폭증하더라도
	// Thread의 숫자에는 제한이 있기 때문에 서버의 성능 저하를 방지할 수 있다.
	// 즉, 한정된 자원을 이용해서 안정적으로 서버를 운용하기 위해 사용하는 것이 ThreadPool 기법.
	public static ExecutorService threadPool;
	public static Vector<Client> clients = new Vector<Client>();

	ServerSocket serverSocket;

	// 서버를 구동시켜서 클라이언트의 연결을 기다리는 메소드.
	// parameters : 어떠한 아이피로,어떠한 포트를 열어서 클라이언트와 통신을 할 것인지.
	public void startServer(String IP, int port) {
		try {
			// 소켓 객체 활성화
			serverSocket = new ServerSocket();
			// bind를 통해 서버 역할을 하는 컴퓨터가 자신의 IP주소 , port번호로 특정한 클라이언트에 접속을 기다리도록 할 수 있음.
			serverSocket.bind(new InetSocketAddress(IP, port));
		} catch (Exception e) {
			e.printStackTrace();
			// 만약, 서버 소켓이 닫혀있는 상태가 아니라면 
			if(!serverSocket.isClosed()) {
				stopServer(); // 서버 종료
			}
			return;
		}
		
		// 여기까지 왔다면.
		// 오류가 발생하지 않고 성공적으로 서버가 소켓을 잘 열어서 접속을 기다리는 상태가 된 상태.
		// 클라이언트가 접속할 때까지 계속 기다리는 쓰레드.
		 
		// 쓰레들을 만들어서 하나의 클라이언트가 접속할 때까지 계속 기다리면 됨.
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						// 새로운 클라이언트가 접속할 수 있도록.
						Socket socket = serverSocket.accept();
						// 클라이언트가 접속을 했다면, 클라이언트 배열에 새롭게 접속한 클라이언트를 추가.
						clients.add(new Client(socket)); 
						System.out.println("[클라이언트 접속] "
								+ socket.getRemoteSocketAddress() 
								+ ": " + Thread.currentThread().getName());
					} catch (Exception e) {
						//오류가 발생했다면 서버를 작동중지시키고 break로 빠져나온다.
						if(!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
				
			}
		};
		
		// 쓰레드풀 초기화
		threadPool = Executors.newCachedThreadPool();
		// 그 다음, 쓰레드풀에 현재 클라이언트를 기다리는 쓰레드를 담을 수 있도록 처리.
		threadPool.submit(thread);
	}

	// 서버의 작동을 중지시키는 메소드.
	// stopServer method는 서버 작동 종료 이후에, 전체 자원을 할당해제해주는 메소드.
	// 잘 만들어진 서버프로그램이라면 stopServer도 작성되어야 한다.
	public void stopServer() {
		try {
			// stopServer는 서버를 완전하게 중지를 시키는 것이기 때문에
			// 모든 클라이언트들에 대한 정보를 끊어준다.
			
			// 현재 작동중인 모든 소켓 닫기
			Iterator<Client> iterator = clients.iterator();
			// iterator로 하나씩 접근
			while(iterator.hasNext()) {
				Client client = iterator.next();
				// 닫아줌.
				client.socket.close();
				// iterator에서도 닫아준 소켓 제거
				iterator.remove(); 
			}
			// 서버 소켓 객체 닫기
			if(serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			
			// 쓰레드 풀 종료하기
			if(threadPool != null && !threadPool.isShutdown()) {
				// shutdown으로 자원할당 해제
				threadPool.shutdown();
			}
		} catch (Exception e) {
			
			
		}
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