package nl.tudelft.instrumentation.symbolic;
import org.junit.jupiter.api.Test;

public class SymbolicExecutionLabTest {

    @Test
    public void testSymExLabAssign(){
        MyVar tmp = nl.tudelft.instrumentation.symbolic.PathTracker.myVar(10, "name");
        SymbolicExecutionLab.assign(tmp, "name", tmp.z3var, tmp.z3var.getSort());
    }

    @Test
    public void testMyAssign(){
        MyVar tmp = nl.tudelft.instrumentation.symbolic.PathTracker.myVar(10, "name");
        PathTracker.myAssign(tmp, tmp,"");
    }

    @Test
    public void testMyAssignArray(){
        MyVar tmp = nl.tudelft.instrumentation.symbolic.PathTracker.myVar(10, "name1");
        MyVar tmp2 = nl.tudelft.instrumentation.symbolic.PathTracker.myVar(10, "name2");
        MyVar[] tmpArr = {tmp, tmp2};

        PathTracker.myAssign(tmpArr, tmp, tmp2, "");
    }

}