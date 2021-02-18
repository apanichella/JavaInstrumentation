package nl.tudelft.instrumentation.fuzzing;

/**
 * Type definitions to define the type of variable in {@link MyVar}.
 */
public enum TypeEnum {
    UNKNOWN(-1),
    BOOL(1),
    INT(2),
    STRING(3),
    UNARY(4),
    BINARY(5);

    /**
     * The original int value.
     */
    private final int value;

    TypeEnum(int value) {
        this.value = value;
    }

    /**
     * Gets the original value.
     *
     * @return The original int value of the type.
     */
    public int getValue() {
        return this.value;
    }
}