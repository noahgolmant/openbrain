package wowbrain;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

public class Robotter {
	protected static OtpNode myOtpNode;
	protected static OtpMbox myOtpMbox;
	protected static OtpErlangPid erl_master;
	protected static OtpErlangPid pixel_pid;
	
	public static void doHandshake(){
		try {
			myOtpNode = new OtpNode("pxserver");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myOtpMbox = myOtpNode.createMbox("pxinbox");

		OtpErlangObject myObject = null;

		OtpErlangTuple myMsg;

		OtpErlangString theMessage;
		
		try {
			myObject = myOtpMbox.receive();
		} catch (OtpErlangExit e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OtpErlangDecodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		myMsg = (OtpErlangTuple) myObject;

        erl_master = (OtpErlangPid) myMsg.elementAt(0);

        theMessage = (OtpErlangString) myMsg.elementAt(1);
        System.out.println(theMessage);
        
        OtpErlangObject[] reply = new OtpErlangObject[2];

        reply[0] = new OtpErlangAtom("setup_java");

        reply[1] = myOtpMbox.self();

        OtpErlangTuple myTuple = new OtpErlangTuple(reply);

        myOtpMbox.send(erl_master, myTuple);
        
        OtpErlangObject pixelObject = null;
		try {
			pixelObject = myOtpMbox.receive();
		} catch (OtpErlangExit e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OtpErlangDecodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		OtpErlangTuple pixelMessage = (OtpErlangTuple) pixelObject;
		
        pixel_pid = (OtpErlangPid) pixelMessage.elementAt(1);

        System.out.println(pixelMessage.elementAt(0));
	}
	
	public static void startGame(){
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			Runtime.getRuntime().exec("java -jar /Users/philkuz/Documents/Minecraft.jar");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		robot.delay(8000);
		
		robot.mouseMove(624, 760);
		
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		robot.delay(3500);
		robot.mouseMove(346, 199);
		
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		robot.delay(3500);
		robot.mouseMove(658, 280);
		
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		robot.delay(3500);
		robot.mouseMove(606, 54);
		
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public static void delegateJobs(){
	    // start a worker thread
	    ExecutorService exec = Executors.newFixedThreadPool(2);

	    while(true){
		    // ask the worker thread to execute a task (
		    exec.submit(() -> {
		        ScreenGrabberDude.sendScreenPixels();
		    });
		    
		    exec.submit(() -> {
		        ActionDoer.doStuff();
		    });
	    }

	    // terminate the worker thread (otherwise, the thread will wait for more work)
//	    exec.shutdown();
	}

	public static void main(String[] args) {
		doHandshake();
		
//		startGame();
		
		delegateJobs();
	}

}