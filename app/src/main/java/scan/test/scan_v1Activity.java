package scan.test;
import android.hardware.barcode.*;

//import Scan.test.Scan.MainHandler;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
//import android.widget.CompoundButton.OnCheckedChangeListener;

public class scan_v1Activity extends Activity {
    /** Called when the activity is first created. */
	private Handler mHandler = new MainHandler();
	Button m_btnClear,m_btnScan;
	EditText m_edit;
	CheckBox m_chbox;
	
	/** ??????????????AudioTrack?????????????????????????????????????????????????????*/
	private final int duration = 1; // seconds
	private final int sampleRate = 2000;         
	private final int numSamples = duration * sampleRate;
	private final double sample[] = new double[numSamples];
	private final double freqOfTone = 1600; // hz    
        
	private final byte generatedSnd[] = new byte[2 * numSamples];
    @Override
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        m_btnClear=(Button)findViewById(R.id.buttonClear);  
        m_btnScan=(Button)findViewById(R.id.buttonScan);
        m_edit=(EditText)findViewById(R.id.editText1);         
        m_chbox=(CheckBox)findViewById(R.id.checkBox1);      
                
        m_btnScan.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v) {   
        		Scanner.Read();
        		//String str=Scanner.ReadSCAAuto();
			}                                        
        }                                    
        );            
 
        m_btnClear.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v) {   
        		m_edit.setText("");        
			}                                           
        }                  
        );  
        
    }
    
    /**
     * 
     * @author wyt
     *
     */
    private class MainHandler extends Handler {
    	@Override          
    	public void handleMessage(Message msg) {
    	switch (msg.what) {   
    
    	case Scanner.BARCODE_READ: {
    		m_edit.setText("");
    		//?????????????
    		m_edit.setText(m_edit.getText()+"\n"+(String)msg.obj);
    		//????????????????
    		m_edit.setSelection(m_edit.getText().length());
    		//???????????????????????
    		if(!m_chbox.isChecked())
    		play();
    		break;
    	}
    	case Scanner.BARCODE_NOREAD:{   

    		break;
    	}
 
    	default:
    	break;
    	}  
    	}
    }; 
    /**
     * 
     */
	protected void onStart() {    
		// TODO Auto-generated method stub   
		//???handle???
		Scanner.m_handler=mHandler;  
		//?????????
		Scanner.InitSCA();  
		super.onStart();   
	}
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {    	
    	if(event.getRepeatCount()==0){
    		if(keyCode==4)
    		{  
    			finish(); 
    		}     
    		else if((keyCode==220)|(keyCode==211)|(keyCode==212)|(keyCode==221))  
    		{

    			Scanner.Read(); 
    		}
		}    	
		return true;       
    }
    void genTone(){
    	// fill out the array
    	for (int i = 0; i < numSamples; ++i) {
    	sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
    	}

    	// convert to 16 bit pcm sound array
    	// assumes the sample buffer is normalised.   
    	int idx = 0;
    	for (double dVal : sample) {
    	short val = (short) (dVal * 32767);
    	generatedSnd[idx++] = (byte) (val & 0x00ff);
    	generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
    	}
 
    	}

    void playSound(){
    	
    	AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
    			8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
    			AudioFormat.ENCODING_PCM_16BIT, numSamples,
    			AudioTrack.MODE_STATIC);
    			audioTrack.write(generatedSnd, 0, numSamples);
    			audioTrack.play();
    			try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			audioTrack.release();   

    	}
    void play()
    {
 			Thread thread = new Thread(new Runnable() {
		   public void run() {
		     genTone();
		     playSound();   
		   }  
		 });   
		 thread.start();
    }      
    
    
    
}