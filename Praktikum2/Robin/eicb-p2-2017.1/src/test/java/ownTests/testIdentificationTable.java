import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.util.EmptyStackException;

import org.junit.Before;

import mavlc.ast.nodes.statement.Declaration;
import mavlc.ast.nodes.statement.VariableDeclaration;
import mavlc.ast.type.IntType;
import mavlc.context_analysis.*;


/**
 * Test the implementet methods of 2.1 of class IdentificationTable
 */
public class testIdentificationTable {
    
    IdentificationTable table;
    VariableDeclaration decA;
    VariableDeclaration decB;

    @Before public void makeObjects() {
        table = new IdentificationTable();
        try {
            Constructor<IntType> i = IntType.class.getDeclaredConstructor();
            i.setAccessible(true);
            IntType intArg = i.newInstance();
            decA = new VariableDeclaration(1, 1, intArg, "a");
            decB = new VariableDeclaration(1, 1, intArg, "b");
        } catch (Exception e) {}
    }

    // first we just try to enter a ident whitout opening a stack
    @Test(expected=EmptyStackException.class)
    public void noScopeAddIdentifier() {
        table.addIdentifier("a", decA);
    }

    //now lets open a stack and try again
    @Test public void addIdentifier() {
        table.openNewScope();
        table.addIdentifier("a", decA);

        // lets test getting the ident here aswell
        assertTrue(table.getDeclaration("a") == decA);
    }

    //now lets test if we can make more than one scope and add idents with same name
    @Test public void openMoreScopes() {
        table.openNewScope();
        table.addIdentifier("a", decA);
        table.openNewScope();
        table.addIdentifier("a", decB);

        assertFalse(table.getDeclaration("a") == decA);
        assertTrue(table.getDeclaration("a") == decB);

        //now lets close the scope so we can test the close scope and see if our decA is at the right place
        table.closeCurrentScope();
        assertTrue(table.getDeclaration("a") == decA);  
    }
}
