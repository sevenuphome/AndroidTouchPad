import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.InputStream;

 


import javax.microedition.io.StreamConnection;

 

public class ProcessConnectionThread implements Runnable{

 

    private StreamConnection mConnection;

     

    // Constant that indicate command from devices

    private static final int EXIT_CMD=-1;
	private static final int REQUEST_ENABLE_BT=1;
	private static final int MOUSE_LEFT = 2;
	private static final int MOUSE_RIGHT=3;
	private static final int MOUSE_MOVE=4;
	private static final int KEY_LEFT=5;
	private static final int KEY_RIGHT=6;
	private static final int SCROLL_ON=7;
	private static final int SCROLL_OFF=8;
	private static final int LEFT_PRESS=9;
	private static final int LEFT_RELEASE=10;
    private InputStream inputStream;
     

    public ProcessConnectionThread(StreamConnection connection)

    {

        mConnection = connection;

    }

     

    @Override

    public void run() {

        try {

           // prepare to receive data

            inputStream = mConnection.openInputStream();

             

            System.out.println("waiting for input");

 

            while (true) {

                int command = inputStream.read();

                 

                if (command == EXIT_CMD)

                {   

                    System.out.println("finish process");

                    break;

                }

                processCommand(command);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

     

    /**

     * Process the command from client

     * @param command the command code

     */

    private void processCommand(int command) {

        try {

            Robot robot = new Robot();

            switch (command) {

            case MOUSE_LEFT:
            		robot.mousePress(InputEvent.BUTTON1_MASK);
            		robot.mouseRelease(InputEvent.BUTTON1_MASK);
            	break;
            case MOUSE_RIGHT:
            	robot.mousePress(InputEvent.BUTTON3_MASK);
        		robot.mouseRelease(InputEvent.BUTTON3_MASK);
            	break;
            case MOUSE_MOVE:
            	int x=6*inputStream.read();
            	int y=4*inputStream.read();
            	robot.mouseMove(x, y);
            	break;
            case KEY_LEFT:
            	robot.keyPress(KeyEvent.VK_LEFT);
            	robot.keyRelease(KeyEvent.VK_LEFT);
            	break;
            case KEY_RIGHT:
            	robot.keyPress(KeyEvent.VK_RIGHT);
            	robot.keyRelease(KeyEvent.VK_RIGHT);
            	break;
            case SCROLL_ON:
            	robot.mousePress(InputEvent.BUTTON2_MASK);
            	break;
            case SCROLL_OFF:
            	robot.mouseRelease(InputEvent.BUTTON2_MASK);
            	break;
            case LEFT_PRESS:
            	robot.mousePress(InputEvent.BUTTON1_MASK);
            	break;
            case LEFT_RELEASE:
            	robot.mouseRelease(InputEvent.BUTTON1_MASK);
            	break;
           }

       } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
