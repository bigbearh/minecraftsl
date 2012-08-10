package net.minecraft.console;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Formatter;
import java.util.Locale;

/**
 * PrintStream that uses one extra PrintStream argument .
 * @author Maik
 *
 */
public class PipedPrintStream extends PrintStream {
	
	PrintStream pipe;
	
	public PipedPrintStream(PrintStream pipe, OutputStream out) {
		super(out);
		this.pipe = pipe;
	}
	
	public PipedPrintStream(PrintStream pipe, String fileName) throws FileNotFoundException {
		super(fileName);
		this.pipe = pipe;
	}
	
	public PipedPrintStream(PrintStream pipe, File file) throws FileNotFoundException {
		super(file);
		this.pipe = pipe;
	}
	
	public PipedPrintStream(PrintStream pipe, OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
		this.pipe = pipe;
	}
	
	public PipedPrintStream(PrintStream pipe, String fileName, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
		this.pipe = pipe;
	}
	
	public PipedPrintStream(PrintStream pipe, File file, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
		this.pipe = pipe;
	}
	
	public PipedPrintStream(PrintStream pipe, OutputStream out, boolean autoFlush, String encoding)
			throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
		this.pipe = pipe;
	}
	
	///////////////////////////////////////////////////////////////
	
    /** Check to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
	if (out == null)
	    throw new IOException("Stream closed");
	if (pipe == null)
		throw new IOException("Pipe closed");
	try {
		//invoke(pipe, "ensureOpen");
	} catch (Exception e) {
		throw (IOException) e;
	}
    }
	
    ///////////////////////////////////////////////////////////////
    
    private Object get(Object inst, String field) {
    	try {
    		Field f = inst.getClass().getField(field);
    		f.setAccessible(true);
    		return f.get(inst);
    	} catch (Exception e) {
    	}
    	return null;
    }
    
    private void set(Object inst, String field, Object value) {
    	try {
    		Field f = inst.getClass().getField(field);
    		f.setAccessible(true);
    		f.set(inst, value);
    	} catch (Exception e) {
    	}
    }
    
    private Object invoke(Object inst, String method, Object... args) throws InvocationTargetException {
    	try {
    		Class[] classes = new Class[args.length];
    		for (int i = 0; i < args.length; i++) {
    			classes[i] = args[i].getClass();
    		}
    		Method m = inst.getClass().getMethod(method, classes);
    		m.setAccessible(true);
    		m.invoke(inst, args);
    	} catch (NoSuchMethodException e) {
    	} catch (NullPointerException e) {
    	} catch (SecurityException e) {
    	} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
			throw e;
		}
    	return null;
    }
    
    ///////////////////////////////////////////////////////////////
	
    /**
     * Writes the specified byte to this stream.  If the byte is a newline and
     * automatic flushing is enabled then the <code>flush</code> method will be
     * invoked.
     *
     * <p> Note that the byte is written as given; to write a character that
     * will be translated according to the platform's default character
     * encoding, use the <code>print(char)</code> or <code>println(char)</code>
     * methods.
     *
     * @param  b  The byte to be written
     * @see #print(char)
     * @see #println(char)
     */
    public void write(int b) {
	try {
	    synchronized (this) {
		ensureOpen();
		out.write(b);
		if ((b == '\n') && (Boolean) get(this, "autoFlush"))
		    out.flush();
		pipe.write(b);
		if ((b == '\n') && (Boolean) get(this, "autoFlush"))
		    pipe.flush();
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    set(this, "trouble", true);
	}
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this stream.  If automatic flushing is
     * enabled then the <code>flush</code> method will be invoked.
     *
     * <p> Note that the bytes will be written as given; to write characters
     * that will be translated according to the platform's default character
     * encoding, use the <code>print(char)</code> or <code>println(char)</code>
     * methods.
     *
     * @param  buf   A byte array
     * @param  off   Offset from which to start taking bytes
     * @param  len   Number of bytes to write
     */
    public void write(byte buf[], int off, int len) {
	try {
	    synchronized (this) {
		ensureOpen();
		out.write(buf, off, len);
		if ((Boolean) get(this, "autoFlush"))
		    out.flush();
		pipe.write(buf, off, len);
		if ((Boolean) get(this, "autoFlush"))
		    pipe.flush();
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    set(this, "trouble", true);
	}
    }

    /*
     * The following private methods on the text- and character-output streams
     * always flush the stream buffers, so that writes to the underlying byte
     * stream occur as promptly as with the original PrintStream.
     */

    private void write(char buf[]) {
	try {
	    synchronized (this) {
		ensureOpen();
		((BufferedWriter) get(this, "textOut")).write(buf);
		invoke(((BufferedWriter) get(this, "textOut")), "flushBuffer");
		invoke(((OutputStreamWriter) get(this, "charOut")), "flushBuffer");
		((BufferedWriter) get(pipe, "textOut")).write(buf);
		invoke(((BufferedWriter) get(pipe, "textOut")), "flushBuffer");
		invoke(((OutputStreamWriter) get(pipe, "charOut")), "flushBuffer");
		if ((Boolean) get(this, "autoFlush")) {
		    for (int i = 0; i < buf.length; i++)
			if (buf[i] == '\n') {
			    out.flush();
			    pipe.flush();
			}
		}
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    set(this, "trouble", true);
	} catch (InvocationTargetException e) {
		Throwable t = e.getCause();
		if (t instanceof InterruptedIOException) {
			Thread.currentThread().interrupt();
		} else if (t instanceof IOException){
			set(this, "trouble", true);
		} else {
			throw new Error(t);
		}
	}
    }

    private void write(String s) {
	try {
	    synchronized (this) {
		ensureOpen();
		((BufferedWriter) get(this, "textOut")).write(s);
		invoke(((BufferedWriter) get(this, "textOut")), "flushBuffer");
		invoke(((OutputStreamWriter) get(this, "charOut")), "flushBuffer");
		((BufferedWriter) get(pipe, "textOut")).write(s);
		invoke(((BufferedWriter) get(pipe, "textOut")), "flushBuffer");
		invoke(((OutputStreamWriter) get(pipe, "charOut")), "flushBuffer");
		if ((Boolean) get(this, "autoFlush") && (s.indexOf('\n') >= 0)) {
		    out.flush();
		    pipe.flush();
		}
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
	    set(this, "trouble", true);
	} catch (InvocationTargetException e) {
		Throwable t = e.getCause();
		if (t == null) {
			Thread.currentThread().interrupt();
			set(this, "trouble", true);
		}
		if (t instanceof InterruptedIOException) {
			Thread.currentThread().interrupt();
		} else if (t instanceof IOException){
			set(this, "trouble", true);
		} else {
			throw new Error(t);
		}
	}
    }

    private void newLine() {
	try {
	    synchronized (this) {
		ensureOpen();
		((BufferedWriter) get(this, "textOut")).newLine();
		invoke(((BufferedWriter) get(this, "textOut")), "flushBuffer");
		invoke(((OutputStreamWriter) get(this, "charOut")), "flushBuffer");
		((BufferedWriter) get(pipe, "textOut")).newLine();
		invoke(((BufferedWriter) get(pipe, "textOut")), "flushBuffer");
		invoke(((OutputStreamWriter) get(pipe, "charOut")), "flushBuffer");
		if ((Boolean) get(this, "autoFlush")) {
		    out.flush();
		    pipe.flush();
		}
	    }
	}
	catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	}
	catch (IOException x) {
		set(this, "trouble", true);
	} catch (InvocationTargetException e) {
		Throwable t = e.getCause();
		if (t instanceof InterruptedIOException) {
			Thread.currentThread().interrupt();
		} else if (t instanceof IOException){
			set(this, "trouble", true);
		} else {
			throw new Error(t);
		}
	}
    }

    /* Methods that do not terminate lines */

    /**
     * Prints a boolean value.  The string produced by <code>{@link
     * java.lang.String#valueOf(boolean)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      b   The <code>boolean</code> to be printed
     */
    public void print(boolean b) {
	write(b ? "true" : "false");
    }

    /**
     * Prints a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      c   The <code>char</code> to be printed
     */
    public void print(char c) {
	write(String.valueOf(c));
    }

    /**
     * Prints an integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(int)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      i   The <code>int</code> to be printed
     * @see        java.lang.Integer#toString(int)
     */
    public void print(int i) {
	write(String.valueOf(i));
    }

    /**
     * Prints a long integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      l   The <code>long</code> to be printed
     * @see        java.lang.Long#toString(long)
     */
    public void print(long l) {
	write(String.valueOf(l));
    }

    /**
     * Prints a floating-point number.  The string produced by <code>{@link
     * java.lang.String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      f   The <code>float</code> to be printed
     * @see        java.lang.Float#toString(float)
     */
    public void print(float f) {
	write(String.valueOf(f));
    }

    /**
     * Prints a double-precision floating-point number.  The string produced by
     * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      d   The <code>double</code> to be printed
     * @see        java.lang.Double#toString(double)
     */
    public void print(double d) {
	write(String.valueOf(d));
    }

    /**
     * Prints an array of characters.  The characters are converted into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      s   The array of chars to be printed
     *
     * @throws  NullPointerException  If <code>s</code> is <code>null</code>
     */
    public void print(char s[]) {
	write(s);
    }

    /**
     * Prints a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      s   The <code>String</code> to be printed
     */
    public void print(String s) {
	if (s == null) {
	    s = "null";
	}
	write(s);
    }

    /**
     * Prints an object.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      obj   The <code>Object</code> to be printed
     * @see        java.lang.Object#toString()
     */
    public void print(Object obj) {
	write(String.valueOf(obj));
    }


    /* Methods that do terminate lines */

    /**
     * Terminates the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public void println() {
	newLine();
    }

    /**
     * Prints a boolean and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  The <code>boolean</code> to be printed
     */
    public void println(boolean x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints a character and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(char)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  The <code>char</code> to be printed.
     */
    public void println(char x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints an integer and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(int)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  The <code>int</code> to be printed.
     */
    public void println(int x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints a long and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(long)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  a The <code>long</code> to be printed.
     */
    public void println(long x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints a float and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(float)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  The <code>float</code> to be printed.
     */
    public void println(float x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints a double and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(double)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  The <code>double</code> to be printed.
     */
    public void println(double x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints an array of characters and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(char[])}</code> and
     * then <code>{@link #println()}</code>.
     *
     * @param x  an array of chars to print.
     */
    public void println(char x[]) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints a String and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  The <code>String</code> to be printed.
     */
    public void println(String x) {
	synchronized (this) {
	    print(x);
	    newLine();
	}
    }

    /**
     * Prints an Object and then terminate the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x  The <code>Object</code> to be printed.
     */
    public void println(Object x) {
        String s = String.valueOf(x);
        synchronized (this) {
            print(s);
            newLine();
        }
    }


    /**
     * A convenience method to write a formatted string to this output stream
     * using the specified format string and arguments.
     *
     * <p> An invocation of this method of the form <tt>out.printf(format,
     * args)</tt> behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.format(format, args) </pre>
     *
     * @param  format
     *         A format string as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
     *         Virtual Machine Specification</a>.  The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *
     * @throws  NullPointerException
     *          If the <tt>format</tt> is <tt>null</tt>
     *
     * @return  This output stream
     *
     * @since  1.5
     */
    public PrintStream printf(String format, Object ... args) {
	return format(format, args);
    }

    /**
     * A convenience method to write a formatted string to this output stream
     * using the specified format string and arguments.
     *
     * <p> An invocation of this method of the form <tt>out.printf(l, format,
     * args)</tt> behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.format(l, format, args) </pre>
     *
     * @param  l
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
     *         is applied.
     *
     * @param  format
     *         A format string as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
     *         Virtual Machine Specification</a>.  The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *
     * @throws  NullPointerException
     *          If the <tt>format</tt> is <tt>null</tt>
     *
     * @return  This output stream
     *
     * @since  1.5
     */
    public PrintStream printf(Locale l, String format, Object ... args) {
	return format(l, format, args);
    }

    /**
     * Writes a formatted string to this output stream using the specified
     * format string and arguments.
     *
     * <p> The locale always used is the one returned by {@link
     * java.util.Locale#getDefault() Locale.getDefault()}, regardless of any
     * previous invocations of other formatting methods on this object.
     *
     * @param  format
     *         A format string as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
     *         Virtual Machine Specification</a>.  The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *
     * @throws  NullPointerException
     *          If the <tt>format</tt> is <tt>null</tt>
     *
     * @return  This output stream
     *
     * @since  1.5
     */
    public PrintStream format(String format, Object ... args) {
	try {
	    synchronized (this) {
		ensureOpen();
		if ((((Formatter) get(this, "formatter")) == null)
		    || (((Formatter) get(this, "formatter")).locale() != Locale.getDefault())) {
			set(this, "formatter", new Formatter((Appendable) this));
			set(pipe, "formatter", new Formatter((Appendable) pipe));
		}
		((Formatter) get(this, "formatter")).format(Locale.getDefault(), format, args);
		((Formatter) get(pipe, "formatter")).format(Locale.getDefault(), format, args);
	    }
	} catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	} catch (IOException x) {
		set(this, "trouble", true);
	}
	return this;
    }

    /**
     * Writes a formatted string to this output stream using the specified
     * format string and arguments.
     *
     * @param  l
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
     *         is applied.
     *
     * @param  format
     *         A format string as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
     *         Virtual Machine Specification</a>.  The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *
     * @throws  NullPointerException
     *          If the <tt>format</tt> is <tt>null</tt>
     *
     * @return  This output stream
     *
     * @since  1.5
     */
    public PrintStream format(Locale l, String format, Object ... args) {
	try {
	    synchronized (this) {
		ensureOpen();
		if ((((Formatter) get(this, "formatter")) == null)
		    || (((Formatter) get(this, "formatter")).locale() != l)) {
		    set(this, "formatter", new Formatter(this, l));
		    set(pipe, "formatter", new Formatter(pipe, l));
		}
		((Formatter) get(this, "formatter")).format(l, format, args);
		((Formatter) get(pipe, "formatter")).format(l, format, args);
	    }
	} catch (InterruptedIOException x) {
	    Thread.currentThread().interrupt();
	} catch (IOException x) {
	    set(this, "trouble", true);
	}
	return this;
    }

    /**
     * Appends the specified character sequence to this output stream.
     *
     * <p> An invocation of this method of the form <tt>out.append(csq)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.print(csq.toString()) </pre>
     *
     * <p> Depending on the specification of <tt>toString</tt> for the
     * character sequence <tt>csq</tt>, the entire sequence may not be
     * appended.  For instance, invoking then <tt>toString</tt> method of a
     * character buffer will return a subsequence whose content depends upon
     * the buffer's position and limit.
     *
     * @param  csq
     *         The character sequence to append.  If <tt>csq</tt> is
     *         <tt>null</tt>, then the four characters <tt>"null"</tt> are
     *         appended to this output stream.
     *
     * @return  This output stream
     *
     * @since  1.5
     */
    public PrintStream append(CharSequence csq) {
	if (csq == null)
	    print("null");
	else
	    print(csq.toString());
    	return this;
    }

    /**
     * Appends a subsequence of the specified character sequence to this output
     * stream. 
     * 
     * <p> An invocation of this method of the form <tt>out.append(csq, start,
     * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in
     * exactly the same way as the invocation
     *
     * <pre>
     *     out.print(csq.subSequence(start, end).toString()) </pre>
     *
     * @param  csq
     *         The character sequence from which a subsequence will be
     *         appended.  If <tt>csq</tt> is <tt>null</tt>, then characters
     *         will be appended as if <tt>csq</tt> contained the four
     *         characters <tt>"null"</tt>.
     *
     * @param  start
     *         The index of the first character in the subsequence
     *
     * @param  end
     *         The index of the character following the last character in the
     *         subsequence
     *
     * @return  This output stream
     *
     * @throws  IndexOutOfBoundsException
     *          If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
     *          is greater than <tt>end</tt>, or <tt>end</tt> is greater than
     *          <tt>csq.length()</tt>
     *
     * @since  1.5
     */
    public PrintStream append(CharSequence csq, int start, int end) {
	CharSequence cs = (csq == null ? "null" : csq);
	write(cs.subSequence(start, end).toString());
    	return this;
    }
    
    /**
     * Appends the specified character to this output stream.
     *
     * <p> An invocation of this method of the form <tt>out.append(c)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.print(c) </pre>
     *
     * @param  c
     *         The 16-bit character to append
     *
     * @return  This output stream
     *
     * @since  1.5
     */
    public PrintStream append(char c) {
	print(c);
	return this;
    }
	
}
