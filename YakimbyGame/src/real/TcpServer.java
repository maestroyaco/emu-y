package real;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TcpServer {
	public final static String PORT_PROP = "port";
	private final static int PORT_DEFAULT = 1234;
	private int port = PORT_DEFAULT;

	public final static String EXECUTOR_PROP = "executor";
	private final static Executor EXECUTOR_DEFAULT = Executors.newCachedThreadPool();
	private Executor executor = EXECUTOR_DEFAULT;

	public static enum State {
		STARTING, STARTED, STOPPING, STOPPED
	}

	private State currentState = State.STOPPED;
	public final static String STATE_PROP = "state";

	private Collection<Listener> listeners = new LinkedList<>(); // Event listeners
	private Event event = new Event(this); // Shared event
	private PropertyChangeSupport propSupport = new PropertyChangeSupport(this); // Properties

	@SuppressWarnings("unused")
	private TcpServer This = this; // To aid in synchronizing
	private Thread ioThread; // Performs IO
	private ServerSocket tcpServer; // The server
	private Socket socket;

	public final static String LAST_EXCEPTION_PROP = "lastException";
	private Throwable lastException;

	public TcpServer(int port) {
		this.port = port;
	}

	public synchronized void start() {
		if (currentState == State.STOPPED) {
			assert ioThread == null : ioThread;
			Runnable run = new Runnable() {
				@Override
				public void run() {
					runServer();
					ioThread = null;
					setState(State.STOPPED);
				}
			};
			ioThread = new Thread(run, this.getClass().getName());
			setState(State.STARTING);
			ioThread.start();
		}
	}

	public synchronized void stop() {
		if (currentState == State.STARTED) {
			setState(State.STOPPING);
			if (tcpServer != null) {
				try {
					tcpServer.close();
				} catch (IOException exc) {
					System.out.println("Error al cerrar el listener servidor");
					fireExceptionNotification(exc);
				}
			}
		}
	}

	public synchronized State getState() {
		return currentState;
	}

	protected synchronized void setState(State state) {
		State oldVal = this.currentState;
		this.currentState = state;
		firePropertyChange(STATE_PROP, oldVal, state);
	}

	public synchronized void reset() {
		switch (this.currentState) {
		case STARTED:
			addPropertyChangeListener(STATE_PROP, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					State newState = (State) evt.getNewValue();
					if (newState == State.STOPPED) {
						TcpServer server = (TcpServer) evt.getSource();
						server.removePropertyChangeListener(STATE_PROP, this);
						server.start();
					}
				}
			});
			stop();
			break;
		default:
			break;
		} // end switch
	}

	protected void runServer() {
		try {
			tcpServer = new ServerSocket(getPort());
			setState(State.STARTED);
			while (!tcpServer.isClosed()) {
				synchronized (this) {
					if (currentState == State.STOPPING) {
						tcpServer.close();
					}
				}
				if (!tcpServer.isClosed()) {
					socket = tcpServer.accept();
					fireTcpServerSocketReceived();
				}
			}
		} catch (Exception exc) {
			synchronized (this) {
				if (currentState == State.STOPPING) {
					try {
						tcpServer.close();
					} catch (IOException exc2) {
						System.out.println("Error al encender el servidor, estÁ en estado de apagando");
						fireExceptionNotification(exc2);
					}
				} else {
					System.out.println("El servidor se ha cerrado inesperadamente " + exc.getMessage());
				}
			}
			fireExceptionNotification(exc);
		} finally {
			setState(State.STOPPING);
			if (tcpServer != null) {
				try {
					tcpServer.close();
				} catch (IOException exc2) {
					System.out.println("El servidor se ha cerrado inesperadamente");
					fireExceptionNotification(exc2);
				}
			}
			tcpServer = null;
		}
	}

	public synchronized Socket getSocket() {
		return this.socket;
	}

	public synchronized int getPort() {
		return this.port;
	}

	public synchronized void setPort(int portx) {
		if (portx < 0 || portx > 65535) {
			throw new IllegalArgumentException("No puede usar este puerto ya que estÁ fuera de rango " + portx);
		}
		int oldVal = port;
		port = portx;
		if (getState() == State.STARTED && oldVal != portx) {
			reset();
		}
		firePropertyChange(PORT_PROP, oldVal, portx);
	}

	public synchronized Executor getExecutor() {
		return executor;
	}

	public synchronized void setExecutor(Executor exec) {
		Executor oldVal = executor;
		executor = exec;
		firePropertyChange(EXECUTOR_PROP, oldVal, exec);
	}

	public synchronized void addTcpServerListener(Listener l) {
		listeners.add(l);
	}

	public synchronized void removeTcpServerListener(Listener l) {
		listeners.remove(l);
	}

	protected synchronized void fireTcpServerSocketReceived() {
		final Listener[] ll = listeners.toArray(new Listener[listeners.size()]);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				for (Listener l : ll) {
					try {
						l.socketReceived(event);
					} catch (Exception exc) {
						System.out.println("TcpServer.Listener " + l + " threw an exception: " + exc.getMessage());
						fireExceptionNotification(exc);
					}
				}
			}
		};
		if (executor == null) {
			r.run();
		} else {
			try {
				executor.execute(r);
			} catch (Exception exc) {
				System.out.println("Supplied Executor " + this.executor + " threw an exception: " + exc.getMessage());
				fireExceptionNotification(exc);
			}
		}
	}

	public synchronized void fireProperties() {
		firePropertyChange(PORT_PROP, null, getPort());
		firePropertyChange(STATE_PROP, null, getState());
	}

	protected synchronized void firePropertyChange(final String prop, final Object oldVal, final Object newVal) {
		try {
			propSupport.firePropertyChange(prop, oldVal, newVal);
		} catch (Exception exc) {
			System.out.println("A property change listener threw an exception: " + exc.getMessage());
			fireExceptionNotification(exc);
		} // end catch
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(listener);
	}

	public synchronized void addPropertyChangeListener(String property, PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(property, listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(String property, PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(property, listener);
	}

	public synchronized Throwable getLastException() {
		return this.lastException;
	}

	protected void fireExceptionNotification(Throwable t) {
		Throwable oldVal = lastException;
		lastException = t;
		firePropertyChange(LAST_EXCEPTION_PROP, oldVal, t);
	}

	public static interface Listener extends EventListener {
		public abstract void socketReceived(Event evt);

	}

	public static class Event extends EventObject {
		private final static long serialVersionUID = 1;

		public Event(TcpServer src) {
			super(src);
		}

		public TcpServer getTcpServer() {
			return (TcpServer) getSource();
		}

		public TcpServer.State getState() {
			return getTcpServer().getState();
		}

		public Socket getSocket() {
			return getTcpServer().getSocket();
		}
	}
}