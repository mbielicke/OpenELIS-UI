package org.openelis.ui.widget;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class DropDownUnit {
    
    private Dropdown<Integer> test;
    
    @Before
    public void init() {
        test = new Dropdown<Integer>();
        ArrayList<Item<Integer>> model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(0, "Item 0"));
        model.add(new Item<Integer>(1, "Item 1"));
        model.add(new Item<Integer>(2, "Item 2"));
        model.add(new Item<Integer>(3, "Item 3"));
        test.setModel(model);
    }
    
    @Test
    public void defaultTest() {
        test.setEnabled(true);
        assertNotNull(test.exceptions);
        verify(test.textbox).setEnabled(false);
    }

    @Test
    public void setDisplay() {
        test.setDisplay();
        verify(test.textbox).setText("");
        test.setSelectedIndex(0);
        test.setDisplay();
        verify(test.textbox).setText("Item 0");
    }
}
