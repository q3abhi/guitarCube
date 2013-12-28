import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class Cube extends JFrame implements ActionListener , Runnable 
{			
	static TargetDataLine targetdataline;
	static SourceDataLine sourcedataline;
	static AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100.0F, 16, 2, 4, 44100.0F, false);
	static DataLine.Info infot,infos;
	static AudioInputStream stream;
	static int buffer = 40, count=0;	
	static byte bufferarray[] = new byte[buffer];
	static long sent,recieve;
	static String setter;
	JButton button1,button2;
	JLabel label;
	JTextField text;
	Thread capture,deliver;


	Cube()  throws LineUnavailableException, IOException
	{	   					   
		setLayout(new FlowLayout());
		button1 = new JButton("Start Microphone/Line-in");
		button2 = new JButton("Stop");
		label = new JLabel("Time lag in ms");
		text = new JTextField(3);		   
		add(button1);
		add(button2);
		add(label);
		add(text);
		button1.addActionListener(this);
		button2.addActionListener(this);		   
		// deliver= new Thread(this);

	}
	public void operation() throws LineUnavailableException
	{
		infot = new DataLine.Info(TargetDataLine.class, format);             // Dataline specifying line and format
		targetdataline = (TargetDataLine) AudioSystem.getLine(infot);        // Get Targetdataline from Audiosystem class		  		 
		targetdataline.open(format,7000);                                    // Open Targetdataline with specified format
		infos = new DataLine.Info(SourceDataLine.class, format);	
		sourcedataline = (SourceDataLine) AudioSystem.getLine(infos);		
		sourcedataline.open(format);		 
		stream = new AudioInputStream(targetdataline);                       //Create an audio input stream for target data line

	}


	public void run()
	{
		System.out.println("Capture start");
		System.out.println("Yes");


		if (Thread.currentThread()==capture)
		{
			try 
			{					
				while (count!=-1)
				{
					sent = System.currentTimeMillis();                            // Counter for calculating time lag
					count = stream.read(bufferarray);                             // Read Data from Targetdataline (via stream) and store it in an Array					    					  
					sourcedataline.write(bufferarray, 0, bufferarray.length);     // Write data to sourcedataline
					recieve = (System.currentTimeMillis()-sent);                  // Counter for calculating time lag

					if (recieve>10)
					{
						String s = recieve + "";
						text.setText(s);				  
					}
				}
			} 
			catch (IOException e) 
			{									
			}   
		}	
		/*	 if (Thread.currentThread()==deliver)
			 {
				 //Do Nothing
			 }  */
	}



	public void actionPerformed(ActionEvent action) 
	{
		if (action.getSource()==button1)
		{	
			try {
				operation();
				count=0;
				sourcedataline.start();
				targetdataline.start();
			} catch (LineUnavailableException e) {}
			capture = new Thread(this);
			capture.start();                               // Start a thread when user clicks "Start" button	
			// deliver.start();
		}
		if (action.getSource()==button2)    
		{				
			sourcedataline.stop();                           // Stop both the lines
			targetdataline.stop();
			count=-1;
		} 
	}
	public static void main(String[] args) throws LineUnavailableException, IOException 
	{
		Cube live = new Cube();
		live.setDefaultCloseOperation(EXIT_ON_CLOSE); 
		live.setSize(250,150);                               
		live.setVisible(true);			 
	}


}
