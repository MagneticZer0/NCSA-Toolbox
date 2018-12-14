package tech.ncsa.ncsatoolbox.toolbox.subnetter;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.Assert.*;

public class SubnetterTest {
    SubnetterFragment subnetterFragment;
    int iterationsPerTest = 5;

    @Before
    public void setup() {
        subnetterFragment = new SubnetterFragment();
    }

    @Test
    public void fix() throws Exception {
        Method fix = subnetterFragment.getClass().getDeclaredMethod("fix", String.class, String.class, int.class);
        fix.setAccessible(true);
        for(int lp=0; lp<iterationsPerTest; lp++) {
            String randomString = "";
            int length = new Random().nextInt(100) + 10;
            for (int i = 0; i < length; i++) {
                randomString += (char) ('A' + new Random().nextInt('z' - 'A'));
            }
            int splitter = new Random().nextInt(8)+1;
            String regex = "(?<=\\G";
            for (int i = 0; i < splitter; i++) {
                regex += ".";
            }
            regex += ")";
            String[] randomStrings = randomString.split(regex);
            String expectedOutput = "";
            for (int i = 0; i < randomStrings.length-1; i++) {
                expectedOutput += randomStrings[i] + ".";
            }
            expectedOutput += randomStrings[randomStrings.length-1];
            assertEquals(expectedOutput, fix.invoke(subnetterFragment, randomString, ".", splitter));
        }
    }

    @Test
    public void convertToDecimal() throws Exception {
        Method convertToDecimal = subnetterFragment.getClass().getDeclaredMethod("convertToDecimal", String.class);
        convertToDecimal.setAccessible(true);
        for(int lp=0; lp<iterationsPerTest; lp++) {
            String decIP = "";
            String binIP = "";
            for (int i = 0; i < new Random().nextInt(100) + 1; i++) {
                int rand = new Random().nextInt(255) + 1;
                decIP += rand + ".";
                binIP += Integer.toBinaryString(rand) + ".";
            }
            decIP = decIP.substring(0, decIP.length() - 1);
            binIP = binIP.substring(0, binIP.length() - 1);
            assertEquals(decIP, convertToDecimal.invoke(subnetterFragment, binIP));
        }
    }

    @Test
    public void isValidCIDRChar() throws Exception {
        Method isValidCIDRChar = subnetterFragment.getClass().getDeclaredMethod("isValidCIDRChar", char.class);
        isValidCIDRChar.setAccessible(true);
        for (int lp = 0; lp < iterationsPerTest; lp++) {
            assertTrue((boolean) isValidCIDRChar.invoke(subnetterFragment, '/'));
            assertTrue((boolean) isValidCIDRChar.invoke(subnetterFragment, (char) (new Random().nextInt('9'-'0')+'0')));
            assertFalse((boolean) isValidCIDRChar.invoke(subnetterFragment, (char) (new Random().nextInt('z'-'a')+'a')));
            assertFalse((boolean) isValidCIDRChar.invoke(subnetterFragment, (char) (new Random().nextInt('Z'-'A')+'A')));
        }
    }
}
