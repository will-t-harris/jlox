package jlox;

// we use an exception class to help with control flow related to return
// statements
class Return extends RuntimeException {
    final Object value;

    Return(Object value) {
        // disables JVM exception machinery we don't need
        super(null, null, false, false);
        
        this.value = value;
    }
}