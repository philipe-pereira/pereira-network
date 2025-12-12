package br.com.pereiraeng.network;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe do objeto que guarda o {@link Socket socket} de comunicação entre dois
 * interloucutores que trocam
 * <a href="https://tools.ietf.org/html/rfc6455" >WebSocket's</a> e também a
 * {@link Flow referência do objeto de tratamento de dados recebidos}
 * 
 * @author Philipe PEREIRA
 *
 */
public class ConnWS extends Connn<String> {

	public ConnWS(Socket connection) {
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

//			Matcher get = Pattern.compile("^GET").matcher(data);
//
//			get.find();
			Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
			match.find();
			byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n" + "Connection: Upgrade\r\n"
					+ "Upgrade: websocket\r\n" + "Sec-WebSocket-Accept: "
					+ Base64.getEncoder()
							.encodeToString(MessageDigest.getInstance("SHA-1").digest(
									(match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
					+ "\r\n\r\n").getBytes("UTF-8");
			output.write(response, 0, response.length);
		} catch (IOException | NoSuchAlgorithmException e) {
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
					super.flow.incomingData(decoding(buffer));
			}
		} catch (SocketException e) {
			System.out.println("Fim da conexão!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeObject(String s) throws IOException {
		byte[] encode = encode(s);
		output.write(encode, 0, encode.length);
	}

	// -------------------------- RFC6455 --------------------------
	// https://tools.ietf.org/html/rfc6455

	private static String decoding(byte[] buffer) {
		int length = buffer[1] + 128;
		byte[] key = Arrays.copyOfRange(buffer, 2, 6);
		byte[] encoded = new byte[length];
		System.arraycopy(buffer, 6, encoded, 0, length);

		byte[] decoded = new byte[encoded.length];
		for (int i = 0; i < encoded.length; i++)
			decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
		return new String(decoded);
	}

	private static byte[] encode(String m) {
		byte[] msg = m.getBytes();
		byte[] out = null;
		int offset = -1;
		if (msg.length < 126) {
			offset = 2;
			out = new byte[msg.length + offset];
			out[1] = (byte) (msg.length);
		} else {
			if (msg.length < 65536) {
				offset = 2 + 2;
				out = new byte[msg.length + offset];
				out[1] = (byte) (126);
				ByteBuffer.wrap(out, 2, 2).putShort((short) msg.length);
			} else {
				offset = 2 + 8;
				out = new byte[msg.length + offset];
				out[1] = (byte) (127);
				ByteBuffer.wrap(out, 2, 8).putLong((long) msg.length);
			}
		}
		out[0] = -127;

		System.arraycopy(msg, 0, out, offset, msg.length);
		return out;
	}
}
