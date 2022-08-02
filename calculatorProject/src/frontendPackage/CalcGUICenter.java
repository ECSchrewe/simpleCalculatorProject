package frontendPackage;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import javax.swing.*;

import expressionPackage.*;

public class CalcGUICenter implements ActionListener {
	private JFrame main;
	private JTextArea textArea;
	private JPanel buttonPanel;
	private JButton buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive, buttonSix, buttonSeven, buttonEight,
			buttonNine, buttonZero;
	private JButton buttonAdd, buttonSubt, buttonMult, buttonDiv, buttonEq, buttonPoint, buttonBackspace, buttonClear;
	private JButton buttonOpenParenthesis, buttonCloseParenthesis;
	private final Dimension buttonDimension = new Dimension(20, 20);
	private final Font defaultFont = new Font("LIBERATION", Font.BOLD, 14);
	private final Color buttonBGColor = new Color(245, 244, 242);
	private String previousResult;
	private boolean justReceivedAResult;
	private StringBuilder inputData = new StringBuilder();
	private int serverPort;
	private final String ip;
	private final long serverTimeOutLimit = 1000L;

	public CalcGUICenter(String ip, int port) {
		this.ip = ip;
		serverPort = port;
		if (port > 0 && ip != null) {
			System.out.println("trying to connect to server " + ip + " at tcp " + port);
			if (testServer())
				System.out.println("connection established");
			else {
				System.out.println("connection failed - doing local calculation instead");
				serverPort = 0;
			}
		} else
			System.out.println("starting without server connection");
		main = new JFrame();
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setSize(300, 200);
		main.setResizable(false);
		main.setLayout(new BorderLayout());
		textArea = new JTextArea(2, 20);
		textArea.setText("\n");
		textArea.setEditable(false);
		textArea.setBackground(new Color(152, 230, 80));
		textArea.setBorder(BorderFactory.createLineBorder(Color.black));
		textArea.setFont(defaultFont);
		main.add(textArea, BorderLayout.CENTER);
		buttonPanel = new JPanel();
		main.add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.setLayout(new GridLayout(4, 5));
		Container row1 = buttonPanel;
		Container row2 = buttonPanel;
		Container row3 = buttonPanel;
		Container row4 = buttonPanel;
		buttonOne = createButton("1");
		row1.add(buttonOne);
		buttonTwo = createButton("2");
		row1.add(buttonTwo);
		buttonThree = createButton("3");
		row1.add(buttonThree);
		buttonAdd = createButton("+");
		row1.add(buttonAdd);
		buttonBackspace = createButton("<");
		row1.add(buttonBackspace);
		buttonFour = createButton("4");
		row2.add(buttonFour);
		buttonFive = createButton("5");
		row2.add(buttonFive);
		buttonSix = createButton("6");
		row2.add(buttonSix);
		buttonSubt = createButton("-");
		row2.add(buttonSubt);
		buttonClear = createButton("C");
		row2.add(buttonClear);
		buttonSeven = createButton("7");
		row3.add(buttonSeven);
		buttonEight = createButton("8");
		row3.add(buttonEight);
		buttonNine = createButton("9");
		row3.add(buttonNine);
		buttonMult = createButton("*");
		row3.add(buttonMult);
		buttonOpenParenthesis = createButton("(");
		row3.add(buttonOpenParenthesis);
		buttonZero = createButton("0");
		row4.add(buttonZero);
		buttonPoint = createButton(".");
		row4.add(buttonPoint);
		buttonEq = createButton("=");
		row4.add(buttonEq);
		buttonDiv = createButton("/");
		row4.add(buttonDiv);
		buttonCloseParenthesis = createButton(")");
		row4.add(buttonCloseParenthesis);

		buttonZero.doClick();
		previousResult = "0";
		justReceivedAResult = true;

		main.setVisible(true);
	}

	public static void main(String[] args) {
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {

		}
		new CalcGUICenter("localhost", port);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (inputData.length() > 0 && Character.isAlphabetic(inputData.charAt(inputData.length() - 1))) {
			handleClearAll();
		}
		String input = e.getActionCommand();
		switch (input) {
		case "0":
		case "1":
		case "2":
		case "3":
		case "4":
		case "5":
		case "6":
		case "7":
		case "8":
		case "9":
		case ".":
		case "(":
		case ")":
			handleNumberInput(input);
			break;
		case "+":
		case "-":
		case "*":
		case "/":
		case "=":
			handleOperatorInput(input);
			break;
		case "C":
			handleClearAll();
			break;
		case "<":
			handleBackspace();
			break;
		}
	}

	private void handleNumberInput(String number) {
		if (justReceivedAResult) {
			handleClearAll();
		}
		inputData.append(number);
		textArea.setText(inputData.toString());
	}

	private void handleOperatorInput(String input) {
		if (justReceivedAResult) {
			inputData = new StringBuilder().append(previousResult);
			textArea.setText(inputData.toString());
			justReceivedAResult = false;
		}
		if (input.equals("=")) {
			try {
				if (serverPort == 0) {
					ComposedExpression ce = new ComposedExpression(inputData.toString());
					previousResult = ce.getStringResult();
				} else
					previousResult = askServer(inputData.toString());
			} catch (Exception e) {
				previousResult = "error";
				System.out.println("error");
			}
			inputData.append("=\n" + previousResult);
			if (!previousResult.startsWith("e")) {
				justReceivedAResult = true;
			}
		} else
			inputData.append(input);
		textArea.setText(inputData.toString());
	}

	private void handleBackspace() {
		if (justReceivedAResult) {
			inputData = new StringBuilder();
			textArea.setText(inputData.toString());
		}
		justReceivedAResult = false;
		if (inputData.length() > 0) {
			inputData = new StringBuilder().append(inputData.subSequence(0, inputData.length() - 1));
			textArea.setText(inputData.toString());
		}
	}

	private void handleClearAll() {
		inputData = new StringBuilder();
		textArea.setText(inputData.toString());
		justReceivedAResult = false;
	}

	private boolean testServer() {
		try {
			Callable<Boolean> callable = () -> askServer("1+1").equals("2");
			Future<Boolean> result = Executors.newSingleThreadExecutor().submit(callable);
			long time = System.currentTimeMillis() + serverTimeOutLimit;
			while (System.currentTimeMillis() < time || !result.isDone())
				Thread.yield();
			result.cancel(true);
			return result.isDone() && result.get();
		} catch (Exception e) {
			return false;
		}
	}

	private String askServer(String expression) {
		try (Socket sock = new Socket(ip, serverPort);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()))) {
			bw.append(expression + "\n");
			bw.flush();
			String response = br.readLine();
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private JButton createButton(String sign) {
		JButton out = new JButton(sign);
		out.setBackground(buttonBGColor);
		out.setFont(defaultFont);
		out.setSize(buttonDimension);
		out.setBorder(BorderFactory.createLineBorder(Color.black));
		out.addActionListener(this);
		out.setActionCommand(sign);
		setHotKey(sign.charAt(0));
		return out;
	}

	private void setHotKey(char key) {
		InputMap inputMap = main.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = main.getRootPane().getActionMap();
		final String keyAsString = "" + key;
		if (key == '<')
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), keyAsString);
		else {
			if (key == '=')
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), keyAsString);

			inputMap.put(KeyStroke.getKeyStroke(key), keyAsString);
		}
		Action action = new AbstractAction(keyAsString) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int id = (int) System.currentTimeMillis();
				ActionEvent outputEvent = new ActionEvent(this, id, keyAsString);
				CalcGUICenter.this.actionPerformed(outputEvent);
			}
		};
		actionMap.put(keyAsString, action);

	}

}
