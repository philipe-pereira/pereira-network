package br.com.pereiraeng.network;

import java.io.Serializable;

/**
 * Classe do objeto que modela uma mensagem
 * 
 * @author Philipe PEREIRA
 *
 */
public class DefaultMsg implements Msg {
	private static final long serialVersionUID = -3195831369934217200L;

	private Object userObject;
	protected String user;
	protected boolean end = false;

	/**
	 * 
	 * @param userObject
	 */
	public DefaultMsg(Serializable userObject) {
		this.setUserObject(userObject);
	}

	/**
	 * Instancia uma mensagem de encerramento
	 */
	public DefaultMsg() {
		this.end = true;
	}

	private void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	@Override
	public boolean end() {
		return this.end;
	}

	public Object getUserObject() {
		return userObject;
	}

	@Override
	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	@Override
	public String toString() {
		return this.userObject == null ? "mensagem vazia de " + user : this.userObject.toString();
	}

}