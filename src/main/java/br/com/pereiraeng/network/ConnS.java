package br.com.pereiraeng.network;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe do objeto que guarda o {@link Socket socket} de comunicação entre dois
 * interloucutores que trocam vetores de bytes e também a {@link Flow referência
 * do objeto de tratamento de dados recebidos}
 * 
 * @author Philipe PEREIRA
 *
 */
public class ConnS extends Connn<byte[]> {

	public ConnS(Socket connection) {
		super(connection);
		System.out.println("Connection received from: " + connection.getInetAddress().getHostName());
		try {
			super.input = connection.getInputStream();
			super.output = connection.getOutputStream();

			// flush output buffer to send header information
			byte[] buffer = new byte[1024];
			int size = input.read(buffer);
			if (size == -1)
				return;

			String data = new String(buffer, 0, size);
			Matcher get = Pattern.compile("^GET").matcher(data);
			if (get.find())
				System.out.println("Shakehand: " + get.group());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\nGot I/O streams\n");
	}

	@Override
	public void run() {
		try {
			byte[] buffer = new byte[1024];
			int size;
			while ((size = input.read(buffer)) >= 0) {
				if (size > 0)
					super.flow.incomingData(buffer);
			}
		} catch (SocketException e) {
			System.out.println("Fim da conexão!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeObject(byte[] s) throws IOException {
		output.write(s, 0, s.length);
	}

}
