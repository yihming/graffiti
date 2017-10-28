/**
 *	r12: 2010-03-06 10:05 John Yung: Establish the Abstract Class AsyncCallbackEx.  
 **/
package org.bnuminer.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>管理GWT-RPC函数的回调函数接口
 * @author John Yung
 *
 * @param <T>
 */
public abstract class AsyncCallbackEx<T> implements AsyncCallback<T> {
	
	/**
	 * <p>功能：实现出错时的回调函数
	 */
	@Override
	public void onFailure(Throwable caught) {
		Window.alert(caught.getMessage());
	}
	
	/**
	 * <p>功能：提供成功时的回调虚函数
	 */
	@Override
	public abstract void onSuccess(T result);
}
