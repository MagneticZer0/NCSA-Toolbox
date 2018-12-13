package tech.ncsa.ncsatoolbox.toolbox.subnetter;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class SubnetterTest {
    SubnetterFragment subnetterFragment;

    @Before
    public void setup() {
        subnetterFragment = new SubnetterFragment();
    }

    @Test
    public void fix() throws Exception {
        Method fix = subnetterFragment.getClass().getDeclaredMethod("fix", String.class, String.class, int.class);
        fix.setAccessible(true);
        assertEquals("003.000.000.001", fix.invoke(subnetterFragment, "003000000001", ".", 3));
    }

    @Test
    public void convertToDecimal() throws Exception {
        Method convertToDecimal = subnetterFragment.getClass().getDeclaredMethod("convertToDecimal", String.class);
        convertToDecimal.setAccessible(true);
        assertEquals("255.0.1.2", convertToDecimal.invoke(subnetterFragment, "11111111.00000000.00000001.00000010"));
    }

    @Test
    public void isValidCIDRChar() throws Exception {
        Method isValidCIDRChar = subnetterFragment.getClass().getDeclaredMethod("isValidCIDRChar", char.class);
        isValidCIDRChar.setAccessible(true);
        assertTrue((boolean) isValidCIDRChar.invoke(subnetterFragment, '/'));
        assertTrue((boolean) isValidCIDRChar.invoke(subnetterFragment, '5'));
        assertFalse((boolean) isValidCIDRChar.invoke(subnetterFragment, 'a'));
    }
}
