package com.cardiag.models.commands;

import android.graphics.Color;
import android.text.TextUtils;

import com.cardiag.models.config.ObdCommandSingleton;
import com.cardiag.models.exceptions.BadResponseException;
import com.cardiag.models.exceptions.BusInitException;
import com.cardiag.models.exceptions.MisunderstoodCommandException;
import com.cardiag.models.exceptions.NoDataException;
import com.cardiag.models.exceptions.NonNumericResponseException;
import com.cardiag.models.exceptions.ResponseException;
import com.cardiag.models.exceptions.StoppedException;
import com.cardiag.models.exceptions.UnableToConnectException;
import com.cardiag.models.exceptions.UnknownErrorException;
import com.cardiag.models.exceptions.UnsupportedCommandException;
import com.cardiag.velocimetro.Velocimetro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

/**
 * Base OBD command.
 *
 */
public abstract class ObdCommand implements Comparable<ObdCommand> {

    /**
     * Error classes to be tested in order
     */
    private final Class[] ERROR_CLASSES = {
            UnableToConnectException.class,
            BusInitException.class,
            MisunderstoodCommandException.class,
            NoDataException.class,
            StoppedException.class,
            UnknownErrorException.class,
            UnsupportedCommandException.class
    };
    protected ArrayList<Integer> buffer = null;
    protected String cmd = null;
    protected boolean useImperialUnits = false;
    protected String rawData = null;
    protected Long responseDelayInMs = ObdCommandSingleton.RESPONSE_DELAY;
    private byte[] receivedData = new byte[50];
    private Integer pos;
    private Boolean selected = true;
    private String name;
    protected Integer digitCount = 6;
    private Boolean error = false;
    private Integer timeOut = ObdCommandSingleton.TIME_OUT;
    private String res = "";

    /**
     * Default ctor to use
     *
     * @param command the command to send
     */

    public ObdCommand(String command, Integer pos) {
        this.cmd = command;
        this.buffer = new ArrayList<>();
        this.pos = pos;
    }

    @Override
    public int compareTo(ObdCommand cmd){

        if (TextUtils.equals(name, cmd.getName())) {
            return 0;
        }
        if (name.compareTo(cmd.getName()) > 0) {
            return 1;
        }
        return -1;
    }

    /**
     * Prevent empty instantiation
     */
    private ObdCommand() {
    }

    /**
     * Copy ctor.
     *
     * @param other the ObdCommand to copy.
     */
    public ObdCommand(ObdCommand other) {
        this(other.cmd, other.getPos());
    }

    /**
     * Sends the OBD-II request and deals with the response.
     * <p>
     * This method CAN be overriden in fake commands.
     *
     * @param in  a {@link InputStream} object.
     * @param out a {@link OutputStream} object.
     * @throws IOException            if any.
     * @throws InterruptedException if any.
     */
    public void run(InputStream in, OutputStream out) throws IOException,
            InterruptedException {
        synchronized (ObdCommand.class) {//Only one command can write and read a data in one time.
            try {
                sendCommand(in, out);
                readResult(in);
            } catch (TimeoutException e) {
                error = true;
            }
        }
    }

    /**
     * Sends the OBD-II request.
     * <p>
     * This method may be overriden in subclasses, such as ObMultiCommand or
     * TroubleCodesCommand.
     *
     * @param out The output stream.
     * @throws IOException            if any.
     * @throws InterruptedException if any.
     */
    protected void sendCommand(InputStream in, OutputStream out) throws IOException,
            InterruptedException, TimeoutException {
        // write to OutputStream (i.e.: a BluetoothSocket) with an added
        // Carriage return
        out.write((cmd + "\r").getBytes());
        out.flush();
        if (responseDelayInMs != null && responseDelayInMs > 0) {
            Thread.sleep(responseDelayInMs);
        }
        timeOut(in, out, true);
    }

    private void timeOut(InputStream in, OutputStream out, Boolean resend) throws IOException, InterruptedException, TimeoutException {
        Integer count = 0;
        Integer timeOutHalfTime = timeOut * 10;
        while (in.available() == 0) {
            if (count > timeOutHalfTime ) {
                if (resend) {
                    resendCommand(in, out);
                    return;
                }
                throw new TimeoutException();
            }
            count ++;
            Thread.sleep(100);
        }
    }

    /**
     * Resends this command.
     *
     * @param out a {@link OutputStream} object.
     * @throws IOException            if any.
     * @throws InterruptedException if any.
     */
    protected void resendCommand(InputStream in, OutputStream out) throws IOException,
            InterruptedException, TimeoutException {
        out.write("\r".getBytes());
        out.flush();
        if (responseDelayInMs != null && responseDelayInMs > 0) {
            Thread.sleep(responseDelayInMs);
        }
        timeOut(in, out, false);
    }



    /**
     * Reads the OBD-II response.
     * <p>
     *
     * @param in a {@link InputStream} object.
     * @throws IOException if any.
     */
    protected void readResult(InputStream in) throws IOException {
        readRawData(in);
        try {
            checkForErrors();
            fillBuffer();
            performCalculations();
        } catch (BadResponseException e)  {
            error = true;
        }
    }

    /**
     * This method exists so that for each command, there must be a method that is
     * called only once to perform calculations.
     */
    protected abstract void performCalculations();


    private static Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
    private static Pattern BUSINIT_PATTERN = Pattern.compile("(BUS INIT)|(BUSINIT)|(\\.)");
    private static Pattern SEARCHING_PATTERN = Pattern.compile("SEARCHING");
    private static Pattern DIGITS_LETTERS_PATTERN = Pattern.compile("([0-9A-F])+");

    protected String replaceAll(Pattern pattern, String input, String replacement) {
        return pattern.matcher(input).replaceAll(replacement);
    }

    protected String removeAll(Pattern pattern, String input) {
        return pattern.matcher(input).replaceAll("");
    }

    /**
     * <p>fillBuffer.</p>
     */
    protected void fillBuffer() {
        rawData = removeAll(WHITESPACE_PATTERN, rawData); //removes all [ \t\n\x0B\f\r]
        rawData = removeAll(BUSINIT_PATTERN, rawData);

        if (!DIGITS_LETTERS_PATTERN.matcher(rawData).matches()) {
            throw new NonNumericResponseException(rawData);
        }

        // read string each two chars
        buffer.clear();
        int begin = 0;
        int end = 2;
        while (end <= rawData.length()) {
            buffer.add(Integer.decode("0x" + rawData.substring(begin, end)));
            begin = end;
            end += 2;
        }
    }

    /**
     * <p>
     * readRawData.</p>
     *
     * @param in a {@link InputStream} object.
     * @throws IOException if any.
     */
    protected void readRawData(InputStream in) throws IOException {
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives OR end of stream reached
        char c;
        // -1 if the end of the stream is reached
        while (((b = (byte) in.read()) > -1)) {
            c = (char) b;
            if (c == '>') // read until '>' arrives
            {
                break;
            }
            res.append(c);
        }
    /*
     * Imagine the following response 41 0c 00 0d.
     *
     * ELM sends strings!! So, ELM puts spaces between each "byte". And pay
     * attention to the fact that I've put the word byte in quotes, because 41
     * is actually TWO bytes (two chars) in the socket. So, we must do some more
     * processing..
     */
        rawData = removeAll(SEARCHING_PATTERN, res.toString());

    /*
     * Data may have echo or informative text like "INIT BUS..." or similar.
     * The response ends with two carriage return characters. So we need to take
     * everything from the last carriage return before those two (trimmed above).
     */
        //kills multiline.. rawData = rawData.substring(rawData.lastIndexOf(13) + 1);
        rawData = removeAll(WHITESPACE_PATTERN, rawData);//removes all [ \t\n\x0B\f\r]
    }

    protected void checkForErrors() {
        rawData = rawData.replace(".", "");
        validateResponse();
        validateData();
        for (Class<? extends ResponseException> errorClass : ERROR_CLASSES) {
            ResponseException messageError;

            try {
                messageError = errorClass.newInstance();
                messageError.setCommand(this.cmd);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (messageError.isError(rawData)) {
                throw messageError;
            }
        }
    }

    protected void validateData() throws BadResponseException{
        if (TextUtils.equals(cmd.substring(0,1), "A")) {
            return;
        }
        if (rawData.length() < digitCount) {
            throw new BadResponseException();
        }
        error = false;
    }

    protected void validateResponse() throws RuntimeException{
        String responseHeader = "";
        String responseHeaderReceived = "";

        if (TextUtils.equals(cmd.substring(0,1), "A")) {
            responseHeader = "OK";
            responseHeaderReceived = rawData.substring(0,2);
        } else {
            responseHeader = "4"+cmd.substring(1,4);
            responseHeaderReceived = rawData.substring(0,4);
        }
        //Checking for the response to match with the command's
        if (!TextUtils.equals(responseHeader, responseHeaderReceived) ) {
            throw new BadResponseException();
        }
        error = false;
    }

    public void setVelocimetroProperties(Velocimetro velocimetro, double velocidadActual) {

        velocimetro.setMaxSpeed(200);
        velocimetro.setMajorTickStep(30);
        velocimetro.setMinorTicks(2);
        velocimetro.clearColoredRanges();
        velocimetro.addColoredRange(0, 60, Color.rgb(255,255,255));
        velocimetro.addColoredRange(60, 120, Color.rgb(59,131,189));
        velocimetro.addColoredRange(120, 200, Color.rgb(52,62,64));
        velocimetro.setUnitsText(getResultUnit());
        velocimetro.setUnitsTextSize(40);

        if(velocidadActual < 0) {
            velocimetro.setSpeed(0, 0, 0);
        }
        if(velocidadActual > velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocimetro.getMaxSpeed(), 0, 0);
        }
        if(velocidadActual >=  0 && velocidadActual <= velocimetro.getMaxSpeed()){
            velocimetro.setSpeed(velocidadActual, 0, 0);
        }
    }

        /**
         * <p>getResult.</p>
         *
         * @return the raw command response in string representation.
         */
    public String getResult() {
        return rawData;
    }

    /**
     * <p>getFormattedResult.</p>
     *
     * @return a formatted command response in string representation.
     */
    public abstract String getFormattedResult();

    /**
     * <p>getCalculatedResult.</p>
     *
     * @return the command response in string representation, without formatting.
     */
    public abstract String  getCalculatedResult();

    /**
     * <p>Getter for the field <code>buffer</code>.</p>
     *
     * @return a list of integers
     */
    protected ArrayList<Integer> getBuffer() {
        return buffer;
    }

    /**
     * <p>useImperialUnits.</p>
     *
     * @return true if imperial units are used, or false otherwise
     */
    public boolean useImperialUnits() {
        return useImperialUnits;
    }

    /**
     * The unit of the result, as used in {@link #getFormattedResult()}
     *
     * @return a String representing a unit or "", never null
     */
    public String getResultUnit() {
        return "";//no unit by default
    }

    /**
     * Set to 'true' if you want to use imperial units, false otherwise. By
     * default this value is set to 'false'.
     *
     * @param isImperial a boolean.
     */
    public void useImperialUnits(boolean isImperial) {
        this.useImperialUnits = isImperial;
    }

    /**
     * <p>getName.</p>
     *
     * @return the OBD command name.
     */
    public String getName() {
        return name;
    }

    /**
     * Time the command waits before returning from #sendCommand()
     *
     * @return delay in ms (may be null)
     */
    public Long getResponseTimeDelay() {
        return responseDelayInMs;
    }

    /**
     * Time the command waits before returning from #sendCommand()
     *
     * @param responseDelayInMs a Long (can be null)
     */
    public void setResponseTimeDelay(Long responseDelayInMs) {
        this.responseDelayInMs = responseDelayInMs;
    }

    /**
     * <p>getCommandPID.</p>
     *
     * @return a {@link String} object.
     * @since 1.0-RC12
     */
    public final String getCommandPID() {
        return cmd.substring(3);
    }

    /**
     * <p>getCommandMode.</p>
     *
     * @return a {@link String} object.
     */
    public final String getCommandMode() {
        if (cmd.length() >= 2) {
            return cmd.substring(0, 2);
        } else {
            return cmd;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObdCommand that = (ObdCommand) o;

        return cmd != null ? cmd.equals(that.cmd) : that.cmd == null;
    }

    @Override
    public int hashCode() {
        return cmd != null ? cmd.hashCode() : 0;
    }

    public byte[] getReceivedData() {
        return receivedData;
    }

    public Boolean isAvailable(ArrayList<Boolean> flags) {
        Boolean res;

        if (pos >= flags.size()) {
            res = false;
        } else if (pos == 1000) {
            res = true;
        } else {
            res = flags.get(pos);
        }

        return res;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCmd() {
        return cmd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}
