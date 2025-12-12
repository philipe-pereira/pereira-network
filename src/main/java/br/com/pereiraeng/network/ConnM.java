package br.com.pereiraeng.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Classe do objeto que guarda o {@link Socket socket} de comunicação entre dois
 * interloucutores que trocam {@link Msg mensagens serializadas} e também a
 * {@link Flow referência do objeto de tratamento de dados recebidos}
 * 
 * @author Philipe PEREIRA
 *
 * @param <K>classe da mensagem
 */
public class ConnM<K extends Msg> extends Connn<K> {

	public ConnM(Socket connection) {
		super(connection);
		System.out.println("Connection received from: " + connection.getInetAddress().getHostName());
		try {
			// set up output stream for objects
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush(); // flush output buffer to send header information
			// set up input stream for objects
			input = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\nGot I/O streams\n");
	}

	public void writeObject(Msg msg, String user) throws IOException {
		msg.setUser(user);
		writeObject(msg);
	}

	@Override
	public void writeObject(Msg obj) throws IOException {
		((ObjectOutputStream) output).writeObject(obj);
		output.flush(); // flush output to client
	}

	@Override
	public void run() {
		K obj = null;
		while (true) { // process messages sent from client
			obj = readObject();
			if (obj != null) {
				super.flow.incomingData(obj);
				if (obj.end())
					break;
			} else
				break;
		}
	}

	@SuppressWarnings("unchecked")
	public K readObject() {
		if (input == null)
			return null;
		try {
			return (K) ((ObjectInputStream) input).readObject();
		} catch (SocketException | EOFException e) {
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}