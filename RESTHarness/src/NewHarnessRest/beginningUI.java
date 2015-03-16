package NewHarnessRest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class beginningUI {
	static String seperator = System.getProperty("line.separator");
	JTextField name = new JTextField(35);
	private static String errMsg = "How to use: " + seperator +
	"1. You need JDK" + seperator +
	"2. In commend line cd to where the SoapTestTool.jar is located, you can launch the programe with or without GUI:"
	+ seperator + 
		"java -jar RESTHarness.jar GUI" + seperator +
		"java -jar RESTHarness.jar NGUI <folder of your test cases> <your target IP address> <your target report file path>"
		+ seperator + 
		"For example:" + seperator +
		"java -jar RESTHarness.jar NGUI C:\\Users\\MyName\\TestCases 192.168.62.111 report.txt";
	
	private String targetU = "";
	private JFrame jf = new JFrame("RestHarness");
	ListTestCaseView tcView = new ListTestCaseView();
	public static JTextArea ta;
	public static ArrayList<String> FailedResult = new ArrayList<String>();
	private int numOfT = 5;
	String textToShow = "";
	private String sessionID = "";
	
	private JFileChooser chooser = new JFileChooser(".");
	private String saveType[] = {"txt"};
	
	
	
	
	public void init(){		
		JPanel setPanel = new JPanel();		
		name.setText("Please input your target IP address");
		//name.setText("https://192.168.62.187/api/v1");
		setPanel.add(name, BorderLayout.NORTH);
		JButton addURLButton = new JButton("Set");
		addURLButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String temp = name.getText();
				if (temp.startsWith("https://")){
					targetU = name.getText() + "/api/v1";
					sessionID = new cookieGetter().getCookie(temp);
				}else{
					targetU = "https://" + name.getText() + "/api/v1";	
					sessionID = new cookieGetter().getCookie("https://" + temp);
				}
				System.out.println("session id is "+sessionID);
				tcView.setTargetURL(targetU);
			}
		});		
		setPanel.add(addURLButton);
		
		Box upAndDown = new Box(BoxLayout.Y_AXIS);
		upAndDown.add(setPanel);
		JLabel label = new JLabel("Test Cases:");
		upAndDown.add(label);		
		upAndDown.add(tcView);
		upAndDown.add(new JLabel("Report"));
		ta = new JTextArea(10,30);
		ta.setEditable(false);		
		ta.setBorder(BorderFactory.createEtchedBorder());
		upAndDown.add(new JScrollPane(ta));
		JPanel numOfThreadPane = new JPanel();
		//final JFormattedTextField numOfThread = new JFormattedTextField(NumberFormat.getIntegerInstance());		
		//numOfThread.setColumns(4);
		//numOfThread.getv
		JButton setnumOfThreadButton = new JButton("Run");
		
		
		
		setnumOfThreadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				FailedResult.clear();
				JList jl = tcView.getJList();
				int numOfT = 5;
				int size = jl.getSelectedValuesList().size();
				ArrayList<String[]> toSee;
				CountDownLatch latch = new CountDownLatch(size);
				ta.append("Let's start testing " + size +" cases......");
				new SSLVerificationDisabler().disableSslVerification();
				ExecutorService pool = Executors.newCachedThreadPool();
				RestRunnerWatcher boss = new RestRunnerWatcher(latch, ta, FailedResult, size);
				pool.execute(boss);
				//String targetUrlS = 
				for (int i = 0; i < jl.getSelectedValuesList().size(); i++) {
					pool.execute(new Thread(new RestRun(jl.getSelectedValuesList().get(i).toString(), targetU, ta, FailedResult, latch, false, false, sessionID)));
					System.out.println(targetU);
				}	
				pool.shutdown();
			}
		});		
		
		JButton saveReportButton = new JButton("Save Report");
		saveReportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				String time = df.format(new Date());
				chooser.setSelectedFile(new File(time));
            	chooser.setFileFilter(new FileNameExtensionFilter("TXT", saveType));
            	chooser.showSaveDialog(null);
            	
				if (chooser.getSelectedFile() != null) {					
					String saveFilePath = chooser.getSelectedFile().getPath();										
					try {
						if (!saveFilePath.endsWith(".txt"))
							saveFilePath += ".txt";
						FileWriter fw = new FileWriter(saveFilePath);
						if (fw != null){
								fw.append(ta.getText());
								fw.flush();
								fw.close();
						}else{							
							fw.write(ta.getText());
						}						
					} catch (IOException e1) {						
						e1.printStackTrace();
					}
				}
			}
		});
		
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {	
				textToShow = "";
				ta.setText("");
			}
		});
		
		//numOfThreadPane.add(numOfThread);
		numOfThreadPane.add(setnumOfThreadButton);
		numOfThreadPane.add(saveReportButton);
		numOfThreadPane.add(refreshButton);
		upAndDown.add(numOfThreadPane);
		jf.add(upAndDown);
		jf.setSize(800,600);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.show();
	}
	
	
	
	public static void runWithoutGUI(String folderPath, String ip, String reportPath) throws InterruptedException, ExecutionException{
		//new SSLVerificationDisabler().disableSslVerification();
		File folder = new File(folderPath);
		String[] fList = folder.list();
		List<String> toRun = new ArrayList<String>();
		for (int i=0; i<fList.length; i++){
			if (fList[i].endsWith(".tc"))
				toRun.add(folderPath+"/"+fList[i]);
		}
		ArrayList<String[]> result = new MultiThreadRest(toRun, "https://"+ ip +"/api/v1", 5, false, false).runTestCases(toRun);
		ArrayList<String> failList = new ArrayList<String>(); 
		
		String r = "";
		
		for (int i=0; i<result.size(); i++){
			r += result.get(i)[0] + seperator + seperator + seperator
				+ result.get(i)[1]+ seperator;
			if (result.get(i)[1].equals("FAILED")){
				failList.add(result.get(i)[2]);
			}
		}
		//System.out.println("texttoShow is "+textToShow);
		
		String sum = "FAILED CASES (" + failList.size() + " of " + result.size() + ") :" + seperator;
		for (int i=0; i<failList.size(); i++){
			sum += failList.get(i) + seperator;
		}
		r += seperator + seperator + sum;		
		 
		try {			
			FileWriter fw = new FileWriter(reportPath);
			if (fw != null){
				fw.append(r);
				fw.flush();
				fw.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			//System.out.println("Unfortunately you did something wrong.");			
			//System.out.println(errMsg);		
		} 
		
	}
	
	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		//new beginningUI().init();
		
		if (args.length == 0)
			new beginningUI().init();
		else if (args[0].equals("GUI"))
			new beginningUI().init();		
		else if (args[0].equals("NGUI")){
			try{
			runWithoutGUI(args[1], args[2], args[3]);
			System.out.println("FINISHED!!!!!!!");
			System.exit(0);
			} catch (ArrayIndexOutOfBoundsException e){
				System.out.println("Unfortunately you did something wrong.");
				System.out.println(errMsg);
			}
		}
		else{
			System.out.println("Unfortunately you did something wrong.");
			System.out.println(errMsg);
		}
	}
	

}
