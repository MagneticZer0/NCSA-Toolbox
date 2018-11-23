package tech.ncsa.ncsatoolbox.toolbox.subnetter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import tech.ncsa.ncsatoolbox.R;

public class SubnetterFragment extends Fragment {

    private String defaultIP = "8.8.8.8";
    private int defaultCIDR = 24;
    private int defaultSubnets = 4;
    private ArrayList<Integer> variableSubnets = new ArrayList<>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.toolbox_subnetter, container, false);
        return view;
    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.subnet_subnetter));
        populateSubnets(256);

        setText(findViewById(R.id.ipAddress), defaultIP);
        setText(findViewById(R.id.cidrNotation),"/" + defaultCIDR);
        setText(findViewById(R.id.numberOfSubnets), String.valueOf(defaultSubnets));
        setText(findViewById(R.id.network), String.valueOf(1));
        setText(findViewById(R.id.hosts), String.valueOf(variableSubnets.get(0)));

        addActionListeners();
    }

    /**
     * This is used in order to populate the variableSubnets array
     * @param buf How many values to populate with
     */
    private void populateSubnets(int buf) {
        for(int i=0; i<buf; i++) {
            variableSubnets.add(2); // Used to populate variableSubnets
        }
    }

    /**
     * Used to set value of an EditText
     * @param ID The EditText we're going to edit
     * @param text The text to put into it
     */
    private void setText(View ID, String text) {
        EditText editText = (EditText)ID;
        editText.setText(text);
    }

    /**
     * Used to set value of an TextView
     * @param ID The TextView we're going to edit
     * @param text The text to put into it
     */
    private void setOutput(View ID, String text) {
        TextView textView = (TextView)ID;
        textView.setText(text);
    }

    /**
     * Used to get the value of an EditText
     * @param ID The EditText we're going to get the value of
     * @return The text inside of the EditText
     */
    private String getText(View ID) {
        EditText editText = (EditText)ID;
        return editText.getText().toString();
    }

    /**
     * Used to get the value of an TextView
     * @param ID The TextView we're going to get the value of
     * @return The text inside of the TextView
     */
    private String getOutput(View ID) {
        TextView textView = (TextView)ID;
        return textView.getText().toString();
    }

    /**
     * This is used to add the ActionListeners for everything
     */
    private void addActionListeners() {
        // This is the action listener for the ipAddress EditText
        findViewById(R.id.ipAddress).setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                reformatIPAddress();
            }
        });

        // This is the action listener for the cidrNotation EditText
        findViewById(R.id.cidrNotation).setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                reformatCIDR();
            }
        });

        // This is the action listener for the numberOfSubnets EditText
        findViewById(R.id.numberOfSubnets).setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                reformatSubnets();
            }
        });

        // This is the action listener for the advanced Switch
        ((Switch) findViewById(R.id.advanced)).setOnCheckedChangeListener(((compoundButton, isChecked) -> {
            if (isChecked) {
                findViewById(R.id.networkText).setVisibility(View.VISIBLE);
                findViewById(R.id.network).setVisibility(View.VISIBLE);
                findViewById(R.id.hostsText).setVisibility(View.VISIBLE);
                findViewById(R.id.hosts).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.networkText).setVisibility(View.INVISIBLE);
                findViewById(R.id.network).setVisibility(View.INVISIBLE);
                findViewById(R.id.hostsText).setVisibility(View.INVISIBLE);
                findViewById(R.id.hosts).setVisibility(View.INVISIBLE);
            }
        }));

        // This is the action listener for the network EditText
        findViewById(R.id.network).setOnFocusChangeListener((view1, hasFocus) -> {
            if (!hasFocus) {
                reformatNetwork();
                setText(findViewById(R.id.hosts), String.valueOf(variableSubnets.get(Integer.parseInt(getText(findViewById(R.id.network))))));
            }
        });

        // This is the action listener for the hosts EditText
        findViewById(R.id.hosts).setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                variableSubnets.set(Integer.parseInt(getText(findViewById(R.id.network))), Integer.parseInt(getText(findViewById(R.id.hosts))));
            }
        });

        // This is the action listener for the generate Button
        findViewById(R.id.generate).setOnClickListener(view -> {
            reformatAll();
            clearFocus();
            setOutput(findViewById(R.id.output), "");
            appendToOutput("Okay, first let's get the subnet mask! \n");
            appendToOutput("So we can look at the CIDR suffix for that \n");
            appendToOutput("The " + getText(findViewById(R.id.cidrNotation)) + " means that we have " + getCIDR() + " bits to identify our network! \n");

            StringBuilder subnetMask = new StringBuilder();
            for (int i = 0; i < 32; i++) {
                if (i % 8 == 0 && i != 0) {
                    subnetMask.append(".");
                }
                if (i < getCIDR()) {
                    subnetMask.append(1);
                } else {
                    subnetMask.append(0);
                }

            }
            appendToOutput("Meaning that our subnet mask is: " + subnetMask.toString() + "\n");

            appendToOutput("--------------------------------\n");

            if (!isAdvancedMode()) {
                int powerOfTwo = (int) Math.ceil(Math.log(Integer.parseInt(getText(findViewById(R.id.numberOfSubnets)))) / Math.log(2));
                appendToOutput("It seems that you want to create " + Integer.parseInt(getText(findViewById(R.id.numberOfSubnets))) + " subnets \n");
                appendToOutput("We need to find a power of 2 that's greater than or equal to the amount of subnets we need. \n");
                appendToOutput("In our case the closest is " + (int) Math.pow(2, powerOfTwo) + " which is 2^" + powerOfTwo + "\n");
                appendToOutput("The power tells us that we need " + powerOfTwo + " additional bits to subnet the network \n");
                int newCIDR = getCIDR() + powerOfTwo;
                StringBuilder newSubnetMask = new StringBuilder();
                for (int i = 0; i < 32; i++) {
                    if (i % 8 == 0 && i != 0) {
                        newSubnetMask.append(".");
                    }
                    if (i < newCIDR) {
                        newSubnetMask.append(1);
                    } else {
                        newSubnetMask.append(0);
                    }
                }
                appendToOutput("So our new subnet mask is: " + newSubnetMask.toString() + "\n");
                appendToOutput("Note: In CIDR notation we now have a /" + newCIDR + " network\n");

                appendToOutput("--------------------------------\n");
                appendToOutput("The remaining " + (32 - newCIDR) + " bits left for hosts \n");
                if ((32 - newCIDR) > 0) {
                    appendToOutput("Now time to list the different subnets \n\n");
                    String otherFormatIP = "";
                    if (getCIDR() != 0) {
                        otherFormatIP = String.format("%0" + getCIDR() + "d", 0).replaceAll("0", "X");
                    }
                    for (int i = 0; i < Math.pow(2, powerOfTwo); i++) {
                        String network = String.format("%0" + powerOfTwo + "d", Long.parseLong(Integer.toBinaryString(i)));
                        String networkID = fix(String.format("%s%s%" + (32 - newCIDR) + "s", otherFormatIP, network, " ").replaceAll(" ", "0"), ".", 8);
                        String broadcastAddress = fix(String.format("%s%s%" + (32 - newCIDR) + "s", otherFormatIP, network, " ").replaceAll(" ", "1"), ".", 8);
                        appendToOutput("Network " + (i + 1) + ": " + convertToDecimal(networkID) + " - " + convertToDecimal(broadcastAddress) + "\n");
                        appendToOutput("    Maximum Hosts: " + (int) (Math.pow(2, 32 - newCIDR) - 2) + "\n");
                        appendToOutput("    Network ID: " + convertToDecimal(networkID) + "\n");
                        appendToOutput("    Broadcast Address: " + convertToDecimal(broadcastAddress) + "\n\n");
                    }
                } else {
                    appendToOutput("It seems that we don't have enough bits to subnet\n");
                    appendToOutput("Try increasing the CIDR or decreasing the subnets!");
                }
            } else {
                appendToOutput("It seems that you want to create " + Integer.parseInt(getText(findViewById(R.id.numberOfSubnets))) + " subnets \n");
                appendToOutput("It also seems that each of your subnets have a different amount of hosts \n");
                ArrayList<Integer> orderedList = new ArrayList<>();
                for (int i=0; i<Integer.parseInt(getText(findViewById(R.id.numberOfSubnets))); i++) { // Making a copy of the array and calculating powers of 2
                    int powerOfTwo = (int) Math.ceil(Math.log(variableSubnets.get(i)+2) / Math.log(2));
                    orderedList.add((int) Math.pow(2, powerOfTwo));
                }
                for (int i=0; i<(Integer.parseInt(getText(findViewById(R.id.numberOfSubnets)))); i++) {
                    if (orderedList.get(i).equals(variableSubnets.get(i)+2)) {
                        appendToOutput("Note: One of your networks does not allow for expandability! \n");
                        break;
                    }
                }
                Collections.sort(orderedList); // Order the array in least to greatest
                Collections.reverse(orderedList); // Reverse the order to greatest to least

                appendToOutput("--------------------------------\n");
                String otherFormatIP = "";
                if (getCIDR() != 0) {
                    otherFormatIP = String.format("%0" + getCIDR() + "d", 0).replaceAll("0", "X");
                }
                appendToOutput("We will now create subnets with " + orderedList.toString().substring(1, orderedList.toString().length()-1) + " hosts. \n");
                int currentHostCount = 0;
                int maximumHosts = (int) Math.pow(2, 32-getCIDR());
                for (int i = 0; i < Integer.parseInt(getText(findViewById(R.id.numberOfSubnets))); i++) {
                    int leftOverBits = 32 - getCIDR();
                    StringBuilder hostPortion = new StringBuilder(Integer.toBinaryString(currentHostCount));
                    hostPortion = new StringBuilder(String.format("%0" + leftOverBits + "d", Long.parseLong(hostPortion.toString())));
                    String networkID = fix(otherFormatIP + hostPortion.toString(), ".", 8);
                    currentHostCount += orderedList.get(i);
                    maximumHosts -= orderedList.get(i);
                    hostPortion = new StringBuilder(Integer.toBinaryString(currentHostCount-1));
                    hostPortion = new StringBuilder(String.format("%0" + leftOverBits + "d", Long.parseLong(hostPortion.toString())));
                    String broadcastAddress = fix(otherFormatIP + hostPortion.toString(), ".", 8);
                    if (broadcastAddress.length()-broadcastAddress.replaceAll("\\.", "").length() > 3 ) {
                        appendToOutput("It seems we do not have enough space for the hosts you want, halting execution!\n");
                        break;
                    }
                    appendToOutput("Network " + (i + 1) + ": " + convertToDecimal(networkID) + " - " + convertToDecimal(broadcastAddress) + "\n");
                    appendToOutput("    Number Of Hosts: " + (orderedList.get(i)-2) + "\n");
                    appendToOutput("    Network ID: " + convertToDecimal(networkID) + "\n");
                    appendToOutput("    Broadcast Address: " + convertToDecimal(broadcastAddress) + "\n\n");
                }
                appendToOutput("According to all this we have " + maximumHosts + " spaces left for additional hosts!");
            }
        });
    }

    /**
     * Used to get the current value of the Advanced Mode Switch
     * @return Whether the Switch is true or false
     */
    private boolean isAdvancedMode() {
        return ((Switch) findViewById(R.id.advanced)).isChecked();
    }

    /**
     * Used to reformat the IP Address EditText if an invalid IP is entered
     */
    private void reformatIPAddress() {
        StringBuilder formattedIPAddress = new StringBuilder();
        for (String section : getText(findViewById(R.id.ipAddress)).split("\\.")) {
            if (!section.equals("x")) {
                if (Integer.parseInt(section) >= Math.pow(2, 8)) {
                    formattedIPAddress.append("255.");
                    continue;
                }
            }
            formattedIPAddress.append(section + ".");
        }
        formattedIPAddress.deleteCharAt(formattedIPAddress.length() - 1);
        setText(findViewById(R.id.ipAddress), formattedIPAddress.toString());
    }

    /**
     * Used to reformat the CIDR EditText if an invalid CIDR is entered
     */
    private void reformatCIDR() {
        // This is used to remove any invalid characters
        char[] text = getText(findViewById(R.id.cidrNotation)).toCharArray();
        setText(findViewById(R.id.cidrNotation), "");
        for(char c : text) {
            if (isValidCIDRChar(c)) {
                setText(findViewById(R.id.cidrNotation), getText(findViewById(R.id.cidrNotation)) + c);
            }
        }

        // This is used to add a / to the beginning if one is not there
        if (getText(findViewById(R.id.cidrNotation)).charAt(0) != '/') {
            setText(findViewById(R.id.cidrNotation), "/" + getText(findViewById(R.id.cidrNotation)));
        }

        // This will limit the maximum to 32
        if (getCIDR() > 32) {
            setText(findViewById(R.id.cidrNotation), "/32");
        }
    }

    /**
     * Used to reform the numberOfSubnets EditText if an invalid number of subnets is entered
     */
    private void reformatSubnets() {
        // This limits the minimum to 2
        if (Integer.parseInt(getText(findViewById(R.id.numberOfSubnets))) < 2) {
            setText(findViewById(R.id.numberOfSubnets), String.valueOf(2));
        }
    }

    /**
     * Used to reform the network EditText if an invalid network is entered
     */
    private void reformatNetwork() {
        // This limits the minimum to 1
        if (Integer.parseInt(getText(findViewById(R.id.network))) < 1) {
            setText(findViewById(R.id.network), String.valueOf(1));
        } else if (Integer.parseInt(getText(findViewById(R.id.network))) > Integer.parseInt(getText(findViewById(R.id.numberOfSubnets)))) {
            setText(findViewById(R.id.network), getText(findViewById(R.id.numberOfSubnets)));
        }
    }

    /**
     * Currently runs all the reformat*() methods
     */
    private void reformatAll() {
        reformatIPAddress();
        reformatCIDR();
        reformatSubnets();
        reformatNetwork();
    }

    /**
     * Clears the current focus and hides the keyboard
     */
    private void clearFocus() {
        View current = getActivity().getCurrentFocus();
        if (current != null) {
            current.clearFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
        }
    }

    /**
     * Used to insert a certain String every x characters
     * @param text The input text
     * @param insert The String to insert
     * @param period The period to insert to
     * @return The newly formatted String
     */
    private String fix(String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);
        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index, Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }

    /**
     * Converts a binary IP to the decimal representation
     * @param binaryIP The input binary IP
     * @return The decimal output
     */
    public String convertToDecimal(String binaryIP) {
        String[] sections = binaryIP.split("\\.");
        StringBuilder decimalVersion = new StringBuilder();
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i];
            if (section.length() == section.replaceAll("X", "").length()) {
                decimalVersion.append(Long.parseLong(section, 2) + ".");
            } else if (section.replaceAll("X", "").length() == 0) {
                decimalVersion.append(Long.parseLong(getText(findViewById(R.id.ipAddress)).split("\\.")[i]) + ".");
            } else {
                if (!isAdvancedMode()) {
                    int part1 = Integer.parseInt(getText(findViewById(R.id.ipAddress)).split("\\.")[i]);
                    int part2 = Integer.parseInt(section.replaceAll("X", "0"), 2);
                    decimalVersion.append((part1 | part2) + ".");
                } else {
                    decimalVersion.append(Integer.parseInt(section.replaceAll("X", "0"), 2) + ".");
                } // I'm not sure if the behavior I'm getting is intended, we'll see
            }
        }
        return decimalVersion.deleteCharAt(decimalVersion.length() - 1).toString();
    }

    /**
     * Appends text to the output TextView
     * @param string The string to append
     */
    private void appendToOutput(String string) {
        setOutput(findViewById(R.id.output), getOutput(findViewById(R.id.output)) + string);
    }

    /**
     * Gets the CIDR from the EditText and parses it is an Integer
     * @return The CIDR notation
     */
    private int getCIDR() {
        return Integer.parseInt(getText(findViewById(R.id.cidrNotation)).substring(1));
    }

    /**
     * Tells you if a character is a valid character for CIDR notation
     * @param c The character to check
     * @return If the character is a number or a /
     */
    private boolean isValidCIDRChar(char c) {
        if (c == '/' || (c >= '0' && c <= '9')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Used to return a view from th activity
     * @param ID The ID of the view to find
     * @return THe view with that ID
     */
    private View findViewById(int ID) {
        return getView().findViewById(ID);
    }
}
