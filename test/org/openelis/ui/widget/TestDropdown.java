package org.openelis.ui.widget;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.openelis.ui.util.TestingUtil.*;

import java.util.ArrayList;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.FieldErrorWarning;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.UIMessages;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestDropdown {
    
    Dropdown<Integer> drop;
    UIMessages        messages;
    SimpleEventBus    bus;
   
    @Before
    public void preArrange() {
        LocaleProxy.initialize();
        messages = LocaleFactory.get(UIMessages.class, "en");
        bus = new SimpleEventBus();
        
        drop = new Dropdown<Integer>() {
            protected UIMessages getMessages() {
                return messages;
            }
            
            @Override
            public void fireEvent(GwtEvent<?> event) {
                bus.fireEvent(event);
                super.fireEvent(event);
            }
        };
        
        drop.table = mock(Table.class);
    }
    
    @Test
    public void showPopup() {
        drop.showPopup();
        assertTrue(drop.showingOptions);
    }
    
    @Test
    public void setDisplay_none() {
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.setDisplay();
        
        verify(drop.textbox).setText("");
    }
    
    @Test
    public void setDisplay_single() {
        Integer[] selection = new Integer[]{1};
        Item<Integer> item1 = new Item<Integer>(1,"Item 1");
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(item1);
        
        drop.setDisplay();
        
        verify(drop.textbox).setText("Item 1");
    }
    
    @Test
    public void setDisplay_multple() {
        Integer[] selection = new Integer[]{1,2};
        Item<Integer> item1 = new Item<Integer>(1,"Item 1");
        Item<Integer> item2 = new Item<Integer>(2,"Item 2");
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(item1);
        when(drop.table.getRowAt(2)).thenReturn(item2);
        
        drop.setDisplay();
        
        verify(drop.textbox).setText("Item 1, Item 2");
    }
    
    @Test
    public void setDisplay_exceedMax() {
        Integer[] selection = new Integer[]{1,2,3,4};
        Item<Integer> item1 = new Item<Integer>(1,"Item 1");
        Item<Integer> item2 = new Item<Integer>(2,"Item 2");
        Item<Integer> item3 = new Item<Integer>(3,"Item 3");
        Item<Integer> item4 = new Item<Integer>(4,"Item 4");
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(item1);
        when(drop.table.getRowAt(2)).thenReturn(item2);
        when(drop.table.getRowAt(3)).thenReturn(item3);
        when(drop.table.getRowAt(4)).thenReturn(item4);
        
        drop.setDisplay();
        
        verify(drop.textbox).setText(4 + " " +messages.drop_optionsSelected());
    }
    
    @Test
    public void setEnabled_false() {
        drop.setEnabled(false);
        
        assertFalse(drop.enabled);
        verifyNotEnabled(drop.button);
        verify(drop.textbox,last()).setEnabled(false);
        verifyNotEnabled(drop.table);
    }
    
    @Test
    public void setEnabled_true() {
        drop.setEnabled(true);
        
        assertTrue(drop.enabled);
        verifyEnabled(drop.button);
        verify(drop.textbox,last()).setEnabled(true);
        verifyEnabled(drop.table);
    }
    
    @Test
    public void setValue() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                fail("Value change should not have fired");
            }
        });
        setModel();
        Integer[] selection = new Integer[]{1};
        Item<Integer> item1 = new Item<Integer>(1,"Item 1");
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(item1);
        
        drop.setValue(1);
        
        assertEquals(new Integer(1),drop.getValue());
        verify(drop.textbox).setText("Item 1");
    }
    
    @Test
    public void setValue_fireEvent() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                assertEquals(new Integer(1),event.getValue());
            }
        });
        setModel();
        Integer[] selection = new Integer[]{1};
        Item<Integer> item1 = new Item<Integer>(1,"Item 1");
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(item1);
        
        drop.setValue(1,true);
        
        assertEquals(new Integer(1),drop.getValue());
        verify(drop.textbox).setText("Item 1");   
    }
    
    @Test
    public void setValue_toNull() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                assertNull(event.getValue());
            }
        });
        setModel();
        drop.value = new Integer(1);
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.setValue(null,true);
        
        assertNull(drop.value);
        verify(drop.textbox).setText("");
    }
    
    @Test
    public void setValue_noChange() {
        drop.value = new Integer(1);
        
        drop.setValue(new Integer(1));
        
        verify(drop.table,never()).selectRowAt(anyInt());
    }
    
    @Test
    public void finishEding() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                assertEquals(new Integer(1),event.getValue());
            }
        });
        setModel();
        Integer[] selection = new Integer[]{1};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRow()).thenReturn(1);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(drop.model.get(1));
        
        drop.finishEditing();
        
        assertEquals(new Integer(1), drop.getValue());
        verify(drop.textbox).setText("Item 1");
    }
    
    @Test
    public void finishEditing_toNull() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                assertNull(event.getValue());
            }
        });
        setModel();
        drop.value = new Integer(1);
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.finishEditing();
        
        assertNull(drop.getValue());
        verify(drop.textbox).setText("");
    }
    
    @Test
    public void finishEditing_queryMode() {
        drop.queryMode = true;
        
        drop.finishEditing();
        
        verify(drop.table,never()).isAnyRowSelected();
    }
    
    @Test
    public void finishEditing_required() {
        drop.required = true;
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                assertNull(event.getValue());
            }
        });
        setModel();
        drop.value = new Integer(1);
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.finishEditing();
        
        assertTrue(drop.exceptions.getValidateExceptions().size() == 1);
        verify(drop.display).addStyleName(drop.css.InputError());
    }
    
    @Test
    public void setQueryMode_enterQueryMode() {
        drop.setQueryMode(true);
        
        assertTrue(drop.queryMode);
        verify(drop.table).setAllowMultipleSelection(true);
    }
    
    @Test
    public void setQueryMode_doNothing() {
        drop.setQueryMode(false);
        
        verify(drop.textbox,never()).setText("");
    }
    
    @Test
    public void setQueryMode_leaveQueryMode() {
        drop.queryMode = true;
        drop.setQueryMode(false);
        
        assertFalse(drop.queryMode);
        verify(drop.table).setAllowMultipleSelection(false);
    }
    
    @Test
    public void getQuery_returnNull() {
        when(drop.table.getSelectedRows()).thenReturn(new Integer[]{});
        
        assertNull(drop.getQuery());
    }
    
    @Test
    public void getQuery_singleItemQuery() {
        QueryData qd;
        
        setModel();
        when(drop.table.getSelectedRows()).thenReturn(new Integer[]{1});
        when(drop.table.getRowAt(1)).thenReturn(drop.getModel().get(1));
        
        qd = (QueryData)drop.getQuery();
        
        assertNotNull(qd);
        assertEquals(QueryData.Type.INTEGER, qd.getType());
        assertNull(qd.getKey());
        assertEquals("1",qd.getQuery());
    }
    
    @Test 
    public void getQuery_multipleItemQuery() {
        QueryData qd;
        
        setModel();
        when(drop.table.getSelectedRows()).thenReturn(new Integer[]{1,2});
        when(drop.table.getRowAt(1)).thenReturn(drop.getModel().get(1));
        when(drop.table.getRowAt(2)).thenReturn(drop.getModel().get(2));
        
        qd = (QueryData)drop.getQuery();
        
        assertNotNull(qd);
        assertEquals(QueryData.Type.INTEGER, qd.getType());
        assertNull(qd.getKey());
        assertEquals("1 | 2",qd.getQuery());
        
    }
    
    @Test
    public void setQuery_notInQueryMode() {
       drop.setQuery(mock(QueryData.class));
       
       verify(drop.table,never()).unselectAll();
    }
    
    @Test 
    public void setQuery_qdIsNull() {
        setModel();
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        drop.queryMode = true;
        
        drop.setQuery(null);
        
        verify(drop.table).unselectAll();
        verify(drop.textbox).setText("");
    }
    
    @Test
    public void setQuery_singleItemSelected() {
        QueryData qd;
        
        setModel();
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        qd = new QueryData();
        qd.setType(QueryData.Type.INTEGER);
        qd.setQuery("1");    
        drop.queryMode = true;
        
        drop.setQuery(qd);
        
        verify(drop.table).selectRowAt(1);
    }
    
    @Test
    public void setQuery_multipleItemSelected() {
        QueryData qd;
        
        setModel();
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        qd = new QueryData();
        qd.setType(QueryData.Type.INTEGER);
        qd.setQuery("1 | 2");    
        drop.queryMode = true;
        
        drop.setQuery(qd);
        
        verify(drop.table).selectRowAt(1);
        verify(drop.table).selectRowAt(2);
    }
    
    @Test
    public void hasExceptions() {
        assertFalse(drop.hasExceptions());
    }
    
    @Test
    public void hasExceptionsValidateExcepts() {
        drop.addValidateException(new Exception());
        assertTrue(drop.hasExceptions());
    }
    
    @Test
    public void hasExceptionsUserExcepts() {
        drop.addException(new Exception());
        assertTrue(drop.hasExceptions());
    }
    
    @Test
    public void hasExceptionsRequired() {
        drop.setRequired(true);
        drop.hasExceptions();
        assertTrue(drop.hasExceptions());
    }
    
    @Test 
    public void hasExceptionsRequiredQueryMode() {
        drop.setRequired(true);
        drop.setQueryMode(true);
        assertFalse(drop.hasExceptions());
    }
    
    @Test
    public void addException_hasStyleError() {
        drop.addException(new Exception());
        verify(drop.display).addStyleName(drop.css.InputError());
    }
    
    @Test
    public void addException_hasStyleWarning() {
        drop.addException(new FieldErrorWarning());
        verify(drop.display).addStyleName(drop.css.InputWarning());    
    } 
    
    @Test
    public void findIndexByTextValue_passEmptyString() {
        setSearchModel();
        
        assertEquals(-1,drop.findIndexByTextValue(""));
        assertNull(drop.searchText);
    }
    
    @Test 
    public void findIndexByTextValue_passSingleLetter() {
        setSearchModel();
        
        assertEquals(1,drop.findIndexByTextValue("a"));
        assertNotNull(drop.searchText);
        assertEquals(5,drop.findIndexByTextValue("z"));
        assertEquals(3,drop.findIndexByTextValue("b"));
    }
    
    @Test
    public void findIndexByTextValue_passTwoLetters() {
        setSearchModel();
        
        assertEquals(1,drop.findIndexByTextValue("al"));
        assertNotNull(drop.searchText);
        assertEquals(2,drop.findIndexByTextValue("fe"));
    }
    
    @Test
    public void findIndexByTextValue_passThreeLetters() {
        setSearchModel();
        
        assertEquals(0,drop.findIndexByTextValue("ali"));
        assertNotNull(drop.searchText);
        assertEquals(1,drop.findIndexByTextValue("ala"));
        assertEquals(4,drop.findIndexByTextValue("dou"));
    }
    
    @Test
    public void findIndexByTextValue_passOneWord() {
        setSearchModel();
        
        assertEquals(7,drop.findIndexByTextValue("mon"));
    }
    
    @Test
    public void findIndexByTextValue_passTwoWords() {
        setSearchModel();
        
        assertEquals(8,drop.findIndexByTextValue("mono d"));
    }
    
    @Test
    public void findIndexByTextValue_passThreeWords() {
        setSearchModel();
        
        assertEquals(9,drop.findIndexByTextValue("mono duo t"));
    }
    
    @Test
    public void onKeyPress_doSearch() {
        setSearchModel();  
        Integer[] selection = new Integer[]{1};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(drop.getModel().get(1));
        when(drop.textbox.getText()).thenReturn("Alabama");
        drop.keyHandler.onKeyPress(keyPress('a'));
        
        assertEquals("a",drop.searchString);
        verify(drop.table).selectRowAt(1);
        verify(drop.textbox).setSelectionRange(1, 6);
    }
    
    @Test
    public void onKeyPress_doSearchNoResult() {
        setSearchModel();  
        drop.keyHandler.onKeyPress(keyPress('k'));
        
        assertEquals("",drop.searchString);        
    }
    
    @Test
    public void onKeyPress_noSearchForQueryMode() {
        setSearchModel();  
        drop.queryMode = true;
        drop.keyHandler.onKeyPress(keyPress('a'));
        
        assertEquals("",drop.searchString);
        verify(drop.table,never()).selectRowAt(1);
    }
    
    @Test 
    public void onKeyPress_downArrowWithNoSelection() {
        setSearchModel();
        
        when(drop.table.getSelectedRow()).thenReturn(-1);
        when(drop.table.getModel()).thenReturn(getTableModel());
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        Integer[] selection = new Integer[]{0};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(0)).thenReturn(drop.getModel().get(0));
        when(drop.textbox.getText()).thenReturn((String)drop.getModel().get(0).getCell(0));
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_DOWN));
        verify(drop.table).selectRowAt(0);
        verify(drop.textbox).setText("Aligator");
    }
    
    @Test 
    public void onKeyPress_downArrowWithSelectionSet() {
        setSearchModel();
        
        when(drop.table.getSelectedRow()).thenReturn(3);
        when(drop.table.getModel()).thenReturn(getTableModel());
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        Integer[] selection = new Integer[]{4};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(4)).thenReturn(drop.getModel().get(4));
        when(drop.textbox.getText()).thenReturn((String)drop.getModel().get(4).getCell(0));
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_DOWN));
        verify(drop.table).selectRowAt(4);
        verify(drop.textbox).setText("Doughnut");
    }
    
    @Test 
    public void onKeyPress_downArrowFirstItemDisabled() {
        ArrayList<Row> tableModel = getTableModel();
        setSearchModel();
        ((Item<Integer>)tableModel.get(0)).enabled = false;
        when(drop.table.getSelectedRow()).thenReturn(-1);
        when(drop.table.getModel()).thenReturn(tableModel);
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        Integer[] selection = new Integer[]{1};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(drop.getModel().get(1));
        when(drop.textbox.getText()).thenReturn((String)drop.getModel().get(1).getCell(0));
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_DOWN));
        verify(drop.table).selectRowAt(1);
        verify(drop.textbox).setText("Alabama");
    }
    
    @Test 
    public void onKeyPress_downArrowAllRowsDisabled() {
        ArrayList<Row> tableModel = getTableModel();
        setSearchModel();
        for(Row row : tableModel)
            ((Item<Integer>)row).enabled = false;
        when(drop.table.getSelectedRow()).thenReturn(-1);
        when(drop.table.getModel()).thenReturn(tableModel);
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_DOWN));
        verify(drop.table).selectRowAt(-1);
        verify(drop.textbox).setText("");
    }
    
    @Test 
    public void onKeyPress_upArrowWithNoSelection() {
        setSearchModel();
        
        when(drop.table.getSelectedRow()).thenReturn(-1);
        when(drop.table.getModel()).thenReturn(getTableModel());
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        Integer[] selection = new Integer[]{9};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(9)).thenReturn(drop.getModel().get(9));
        when(drop.textbox.getText()).thenReturn((String)drop.getModel().get(9).getCell(0));
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_UP));
        verify(drop.table).selectRowAt(9);
        verify(drop.textbox).setText("Mono Duo Trio");
    }
    
    @Test 
    public void onKeyPress_upArrowWithSelectionSet() {
        setSearchModel();
        
        when(drop.table.getSelectedRow()).thenReturn(3);
        when(drop.table.getModel()).thenReturn(getTableModel());
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        Integer[] selection = new Integer[]{2};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(2)).thenReturn(drop.getModel().get(2));
        when(drop.textbox.getText()).thenReturn((String)drop.getModel().get(2).getCell(0));
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_UP));
        verify(drop.table).selectRowAt(2);
        verify(drop.textbox).setText("Ferret");
    }
    
    @Test 
    public void onKeyPress_upArrowFirstItemDisabled() {
        ArrayList<Row> tableModel = getTableModel();
        setSearchModel();
        ((Item<Integer>)tableModel.get(9)).enabled = false;
        when(drop.table.getSelectedRow()).thenReturn(-1);
        when(drop.table.getModel()).thenReturn(tableModel);
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        Integer[] selection = new Integer[]{8};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(8)).thenReturn(drop.getModel().get(8));
        when(drop.textbox.getText()).thenReturn((String)drop.getModel().get(8).getCell(0));
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_UP));
        verify(drop.table).selectRowAt(8);
        verify(drop.textbox).setText("Mono Duo");
    }
    
    @Test 
    public void onKeyPress_upArrowAllRowsDisabled() {
        ArrayList<Row> tableModel = getTableModel();
        setSearchModel();
        for(Row row : tableModel)
            ((Item<Integer>)row).enabled = false;
        when(drop.table.getSelectedRow()).thenReturn(-1);
        when(drop.table.getModel()).thenReturn(tableModel);
        when(drop.table.getRowCount()).thenReturn(drop.getModel().size());
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_UP));
        verify(drop.table).selectRowAt(-1);
        verify(drop.textbox).setText("");
    }

    @Test
    public void onKeyPress_backSpaceOnEmptyTextBox() {
        setSearchModel();
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_BACKSPACE));
        verify(drop.table).selectRowAt(-1);        
    }
    
    @Test
    public void onKeyPress_backspaceOnLastLetter() {
        setSearchModel();
        drop.searchString = "A";
        when(drop.table.isAnyRowSelected()).thenReturn(false);
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_BACKSPACE));
        verify(drop.table).selectRowAt(-1);
        assertEquals("",drop.searchString);
    }
    
    @Test 
    public void onKeyPress_backspaceToDifferentSelection() {
        setSearchModel();
        drop.searchString = "Ali";
        Integer[] selection = new Integer[]{1};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(1)).thenReturn(drop.getModel().get(1));
        when(drop.textbox.getText()).thenReturn("Alabama");
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_BACKSPACE));
        verify(drop.table).selectRowAt(1);
        verify(drop.textbox).setText("Alabama");
    }
    
    @Test 
    public void onKeyPress_backspaceToSameSelection() {
        setSearchModel();
        drop.searchString = "Alig";
        Integer[] selection = new Integer[]{0};
        when(drop.table.isAnyRowSelected()).thenReturn(true);
        when(drop.table.getSelectedRows()).thenReturn(selection);
        when(drop.table.getRowAt(0)).thenReturn(drop.getModel().get(0));
        when(drop.textbox.getText()).thenReturn("Aligator");
        
        drop.keyHandler.onKeyPress(keyPress(KeyCodes.KEY_BACKSPACE));
        verify(drop.table).selectRowAt(0);
        verify(drop.textbox).setText("Aligator");
    }
    
    private void setModel() {
        ArrayList<Item<Integer>> model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(0,"Item 0"));
        model.add(new Item<Integer>(1,"Item 1"));
        model.add(new Item<Integer>(2,"Item 2"));
        model.add(new Item<Integer>(3,"Item 3"));
        drop.model = model;
        drop.createKeyHash(model);  
    }
    
    private void setSearchModel() {
        ArrayList<Item<Integer>> model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(0,"Aligator"));
        model.add(new Item<Integer>(1,"Alabama"));
        model.add(new Item<Integer>(2,"Ferret"));
        model.add(new Item<Integer>(3,"Bananna"));
        model.add(new Item<Integer>(4,"Doughnut"));
        model.add(new Item<Integer>(5,"Zebra"));
        model.add(new Item<Integer>(6,"Minnesota"));
        model.add(new Item<Integer>(7,"Mono"));
        model.add(new Item<Integer>(8,"Mono Duo"));
        model.add(new Item<Integer>(9,"Mono Duo Trio"));
        drop.model = model;
        drop.createKeyHash(model);
    }
    
    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model = new ArrayList<Row>();
        model.add(new Item<Integer>(0,"Aligator"));
        model.add(new Item<Integer>(1,"Alabama"));
        model.add(new Item<Integer>(2,"Ferret"));
        model.add(new Item<Integer>(3,"Bananna"));
        model.add(new Item<Integer>(4,"Doughnut"));
        model.add(new Item<Integer>(5,"Zebra"));
        model.add(new Item<Integer>(6,"Minnesota"));
        model.add(new Item<Integer>(7,"Mono"));
        model.add(new Item<Integer>(8,"Mono Duo"));
        model.add(new Item<Integer>(9,"Mono Duo Trio"));
        return model;
    }

    private KeyPressEvent keyPress(int key) {
        KeyPressEvent keyPress = mock(KeyPressEvent.class);
        NativeEvent event = mock(NativeEvent.class);
        when(keyPress.getUnicodeCharCode()).thenReturn(0);
        when(keyPress.getNativeEvent()).thenReturn(event);
        when(event.getKeyCode()).thenReturn(key);
        return keyPress;
    }
    
    
    
    

}
