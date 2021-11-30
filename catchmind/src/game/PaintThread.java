package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class PaintThread extends Thread{
	// Server에서 발신한 내용을 Receive
		@Override
		public void run() {
			ObjectInputStream ois = null;
			try {
				while(true) {
					if(isInterrupted()) {break;}
					Object obj =null;
					ois = new ObjectInputStream(
							MainController.socket.getInputStream()
						);
					if((obj = ois.readObject()) != null) {
						System.out.println(obj);
						if(obj instanceof PaintVO) {
							// 회원관련 요청 처리 결과
							PaintHandler.receiveData((PaintVO)obj);
						}
					}
				}
			} catch (Exception e) {
				
			}
		}
		
		public void sendData(Object o) {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(
						MainController.socket.getOutputStream()
					);
				oos.writeObject(o);
				oos.flush();
			} catch (IOException e) {
				
			}
		}
		

}
