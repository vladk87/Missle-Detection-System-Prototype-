import edu.princeton.cs.introcs.StdDraw;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.HashMap;
import java.util.TimerTask;

public class LightAlarmTaskMajor extends TimerTask {

    private  final Pin lightsensorPin;

    private final Pin pentPin;

    private final SSD1306 display;

    private final Pin buttonPin;

    private final Pin LedPin;

    private final Pin buzzerPin;
    private double count = 0;

    private long timer = 0;

    private long timer2 = 0;

    private int prevLightVal = -1; //ChatGpt helped with the implementation of storing previous readings

    private int time = 0;





    //	class	constructor.
    LightAlarmTaskMajor(Pin lightsensorPin, Pin pentPin, SSD1306 display, Pin buttonPin, Pin LedPin, Pin buzzerPin){
        this.lightsensorPin = lightsensorPin;
        this.pentPin = pentPin;
        this.display = display;
        this.buttonPin = buttonPin;
        this.LedPin = LedPin;
        this.buzzerPin = buzzerPin;

    }




    @Override
    public void run() {

        int lightval = (int) lightsensorPin.getValue();
        int buttonstatus = (int) buttonPin.getValue();
        String SafetyStatus;
        timer++;

        if (lightval != prevLightVal) {      //ChatGpt helped with the implementation of storing previous readings

            count++;
        }
        prevLightVal = lightval;
        if(timer > 6){
            count=0;
            timer=0;
        }

        if (count >= 6) { // check if the counter has reached 6 and the time frame has not elapsed

            SafetyStatus = "WARNING";
            try {                            //set safety status, turn on Led
                LedPin.setValue(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while ((int)buttonPin.getValue()!= 1) {                   // run while loop if button is not pressed, to flash the status on display
                this.display.getCanvas().setTextsize(3);
                this.display.getCanvas().drawString(0, 30, SafetyStatus);
                this.display.display();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.display.clear();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                }
            }
       else if(buttonstatus>0){                  // if button pressed turn on Led and OLED
            try {
                LedPin.setValue(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            display.clear();
        }

       if(lightval>500 && pentPin.getValue()>512){
           timer2++;                                   // if the light value reaches a certain level and pent is the dissarm possition start the timer

       }
       else{                                  // if light value drops reset timer to 0.
           timer2=0;
       }

       if(timer2 > 4){                        // if the timer reaches 4 seconds sound the alarm

           while ((int)buttonPin.getValue()!= 1 && (int)pentPin.getValue()>512) {  // start the following loop
               try {
                   LedPin.setValue(1);              // turn on Led
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
               try {
                   buzzerPin.setValue(128);         // turn on buzzer
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }

               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);     //wait 1 sec
               }

               try {
                   LedPin.setValue(0);
               } catch (IOException e) {                // turn off Led
                   throw new RuntimeException(e);
               }
               try {
                   buzzerPin.setValue(-1);
               } catch (IOException e) {                // turn off buzzer
                   throw new RuntimeException(e);
               }

               try {
                   Thread.sleep(100);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);     // wait
               }

               this.display.getCanvas().setTextsize(2);
               this.display.getCanvas().drawString(7, 20, "WARNING!!!");
               this.display.display();
               try {                                                               // display first message
                   Thread.sleep(200);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
               this.display.clear();
               try {
                   Thread.sleep(200);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }

               this.display.getCanvas().setTextsize(2);
               this.display.getCanvas().drawString(20, 10, "SERIOUS" + "   " + "THREAT");
               this.display.display();
               try {
                   Thread.sleep(200);
               } catch (InterruptedException e) {                       // display second message
                   throw new RuntimeException(e);
               }
               this.display.clear();
               try {
                   Thread.sleep(200);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }

       }
       else if(pentPin.getValue()<512){
           try {                                               // if pent is turned all the way turn off the alarm
               LedPin.setValue(0);
           } catch (IOException e) {
               throw new RuntimeException(e);        //LED off
           }
           display.clear();                 // display cleared
           try {
               buzzerPin.setValue(-1);
           } catch (IOException e) {               // buzzer off
               throw new RuntimeException(e);
           }
       }

        HashMap<Integer, Integer> lightsensordata = new HashMap<>();

        //HashMap used for plotting

        lightsensordata.put(time, lightval);
        time=time+1;                           // Assign time with sensor readings

        if(time > 100){
            lightsensordata.clear();                 // restart graph and hashmap after 100 sec
            StdDraw.clear();
            time =0;

        }

        lightsensordata.forEach((xValue, yValue) -> StdDraw.text(xValue, yValue, "*")); //plot graph from HashMap values




        StdDraw.setXscale(0, 100);            // set up the graph
        StdDraw.setYscale(0, 800);

        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.line(0, 0, 0, 900);
        StdDraw.line(0, 0, 900, 0);

        StdDraw.text(50, -20, "[X] Time(s)");
        StdDraw.text(-3, 500, "[Y] Light(V)",90);
        StdDraw.text(50, 800, "Light vs Time");
        StdDraw.text(-3, 800, "800",90);
        StdDraw.text(-3, 700, "700",90);
        StdDraw.text(100, -20, "100");


        System.out.println("Light Value: "+lightval);
    }
}
