package mavlc.error_reporting;

import mavlc.ast.nodes.function.Function;

import java.util.Objects;

/**
 * Error class to signal a missing return statement for a non-void
 * function. For non-void functions, the last statement in the
 * function must be a return statement.
 */
public class MissingReturnError extends CompilationError{

    private final Function func;

    /**
     * Constructor
     * @param function Non-void function with missing return statement.
     */
    public MissingReturnError(Function function){
        func = function;

        StringBuilder sb = new StringBuilder();
        sb.append("Missing return statement for non-void function ").append(func.getName());
        sb.append(" defined in line ").append(func.getSrcLine()).append(", column ").append(func.getSrcColumn());
        this.message = sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissingReturnError that = (MissingReturnError) o;
        return Objects.equals(func, that.func);
    }

    @Override
    public int hashCode() {

        return Objects.hash(func);
    }
}
