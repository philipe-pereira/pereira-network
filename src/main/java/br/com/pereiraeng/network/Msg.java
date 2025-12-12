package br.com.pereiraeng.network;

import java.io.Serializable;

/**
 * Interface das classes que modelam mensagens trocadas entre dois
 * interloucutores. Estas mensagens pode ser {@link Serializable serializadas}.
 * 
 * @author Philipe PEREIRA
 *
 */
public interface Msg extends Serializable {

	/**
	 * Endereçamento do remetente
	 * 
	 * @param user remetente
	 */
	public void setUser(String user);

	/**
	 * Mensagem de encerramento da comunicação
	 * 
	 * @return
	 */
	public boolean end();
}
