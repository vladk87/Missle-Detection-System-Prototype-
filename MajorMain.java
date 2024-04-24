import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.Timer;

public class MajorMain {


    private static int count =0;

    public static void main(String[] args) throws
            IOException, InterruptedException {


        /* Initialize the Board */
        var myUSBPort = "COM6"; // TO-DO : modify this!
        FirmataDevice arduinoObject = new FirmataDevice(myUSBPort);
        arduinoObject.start();
        arduinoObject.ensureInitializationIsDone();

        var lightsensor = arduinoObject.getPin(20);
        lightsensor.setMode(Pin.Mode.ANALOG);

        var pent = arduinoObject.getPin(14);
        pent.setMode(Pin.Mode.ANALOG);

        var button = arduinoObject.getPin(6);
        button.setMode(Pin.Mode.INPUT);

        var Led = arduinoObject.getPin(4);
        Led.setMode(Pin.Mode.OUTPUT);

        var buzzer = arduinoObject.getPin(5);
        buzzer.setMode(Pin.Mode.PWM);


        I2CDevice i2cObject = arduinoObject.getI2CDevice((byte) 0x3C);
        SSD1306 theOledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        // Initialize the OLED (SSD1306) object
        theOledObject.init();


        Timer timer = new Timer();
        var task = new LightAlarmTaskMajor(lightsensor, pent, theOledObject, button, Led, buzzer);

        timer.schedule(task, 0, 500);

        while (true) {
            if (button.getValue() > 0) {     // if button is pressed for a certain amount of time stop the system
                Thread.sleep(1000);
                count++;

            } else {
                count = 0;
            }

            if (count >= 4) {
                System.exit(0);
            }

        }
    }


}


