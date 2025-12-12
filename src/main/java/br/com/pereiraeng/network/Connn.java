package br.com.pereiraeng.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import br.com.pereiraeng.io.flow.Flow;

/**
 * Classe do objeto que guarda o {@link Socket socket} de comunicação entre dois
 * interloucutores e também a {@link Flow referência do objeto de tratamento de
 * dados recebidos}
 * 
 * @author Philipe PEREIRA
 *
 * @param <K> classe do objeto portador de informação
 */
public abstract class Connn<K> implements Runnable {

	/**
	 * connection to client
	 */
	protected Socket connection;

	/**
	 * output stream to client
	 */
	protected OutputStream output;

	/**
	 * input stream from client
	 */
	protected InputStream input;

	protected Flow<K> flow;

	public Connn(Socket connection) {
		this.connection = connection;
	}

	/**
	 * send message to client
	 * 
	 * @param obj
	 */
	public abstract void writeObject(K k) throws IOException;

	/**
	 * iniciar o processo de escuta deste
	 * 
	 * @param flow
	 */
	public void listen(Flow<K> flow) {
		this.flow = flow;
		new Thread(this).start();
	}

	/**
	 * close streams and socket
	 */
	public void close() {
		try {
			if (output != null)
				output.close(); // close output stream
			if (input != null)
				input.close(); // close input stream
			if (connection != null)
				connection.close(); // close socket
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() throws IOException {
		return connection.getInetAddress().isReachable(100);
	}
}
