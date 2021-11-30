package game;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;



class PaintServer {
	
	//Client Socket 연결 관리
	ServerSocket serverSocket;
	// Client thread 관리
	public static ExecutorService threadPool;//공유자원으로 활용할수 있도록 스태틱
	public static MainController mc;
	// 전체 Client 목록
	private List<PaintHandler> list;
	
	private final int port = 1593;
	//private final int port = 9950;
	
	public PaintServer() { 
		try {
			serverSocket = new ServerSocket(port);
			System.out.println(port+"포트 페인트 서버 대기중");

			list = new ArrayList<PaintHandler>();

			while (true) {
				Socket socket = serverSocket.accept();
				String client_ip = socket.getInetAddress().getHostAddress();
				System.out.println(client_ip+"연결 완료");
				PaintHandler handler = new PaintHandler(socket, list);
				handler.start();
				list.add(handler);
			} // while
		} catch (Exception e) {
		} 
	}

	public static void main(String[] args) {
		new PaintServer();
	}
}
