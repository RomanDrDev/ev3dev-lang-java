package ev3dev.utils.io;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;

import java.io.Closeable;
import java.nio.ByteBuffer;

/**
 * <p>This class provides access to Linux files using native I/O operations. It is
 * implemented using the JNA package. The class is required because certain
 * operations (like ioctl) that are required by the Lego kernel module interface are
 * not support by standard Java methods. In addition standard Java memory mapped
 * files do not seem to function correctly when used with Linux character devices.</p>
 *
 * <p>Only JNA is used, the original interface used combination of Java and JNA.</p>
 *
 * @author andy, Jakub Vaněk
 */
public class NativeFile implements Closeable, AutoCloseable {
    private NativeLibc libc = new NativeLibc();
    protected int fd = -1;

    protected NativeFile() {

    }

    /**
     * Create a NativeFile object and open the associated file/device
     * for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @throws LastErrorException when operations fails
     */
    public NativeFile(String fname, int flags) throws LastErrorException {
        open(fname, flags);
    }

    /**
     * Create a NativeFile object and open the associated file/device
     * for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @param mode  Linux style file access mode
     * @throws LastErrorException when operations fails
     */
    public NativeFile(String fname, int flags, int mode) throws LastErrorException {
        open(fname, flags, mode);
    }

    /**
     * Open the specified file/device for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @throws LastErrorException when operations fails
     */
    public void open(String fname, int flags) throws LastErrorException {
        fd = libc.open(fname, flags);
    }

    /**
     * Open the specified file/device for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @param mode  Linux style file access mode
     * @throws LastErrorException when operations fails
     */
    public void open(String fname, int flags, int mode) throws LastErrorException {
        fd = libc.open(fname, flags, mode);
    }

    /**
     * Attempt to read the requested number of bytes from the associated file.
     *
     * @param buf location to store the read bytes
     * @param len number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int read(byte[] buf, int len) throws LastErrorException {
        return libc.read(fd, ByteBuffer.wrap(buf), len);
    }

    /**
     * Attempt to write the requested number of bytes to the associated file.
     *
     * @param buf    location to store the read bytes
     * @param offset the offset within buf to take data from for the write
     * @param len    number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int write(byte[] buf, int offset, int len) throws LastErrorException {
        return libc.write(fd, ByteBuffer.wrap(buf, offset, len), len);
    }

    /**
     * Attempt to read the requested number of byte from the associated file.
     *
     * @param buf    location to store the read bytes
     * @param offset offset with buf to start storing the read bytes
     * @param len    number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int read(byte[] buf, int offset, int len) throws LastErrorException {
        return libc.read(fd, ByteBuffer.wrap(buf, offset, len), len);
    }

    /**
     * Attempt to write the requested number of bytes to the associated file.
     *
     * @param buf location to store the read bytes
     * @param len number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int write(byte[] buf, int len) throws LastErrorException {
        return libc.write(fd, ByteBuffer.wrap(buf), len);
    }

    /**
     * Perform a Linux style ioctl operation on the associated file.
     *
     * @param req  ioctl operation to be performed
     * @param info output as integer
     * @return Linux style ioctl return
     * @throws LastErrorException when operations fails
     */
    public int ioctl(int req, IntByReference info) throws LastErrorException {
        return libc.ioctl(fd, req, info);
    }

    /**
     * Perform a Linux style ioctl operation on the associated file.
     *
     * @param req  ioctl operation to be performed
     * @param info input as integer
     * @return Linux style ioctl return
     * @throws LastErrorException when operations fails
     */
    public int ioctl(int req, int info) throws LastErrorException {
        return libc.ioctl(fd, req, info);
    }

    /**
     * Perform a Linux style ioctl operation on the associated file.
     *
     * @param req ioctl operation to be performed
     * @param buf pointer to ioctl parameters
     * @return Linux style ioctl return
     * @throws LastErrorException when operations fails
     */
    public int ioctl(int req, Pointer buf) throws LastErrorException {
        return libc.ioctl(fd, req, buf);
    }

    /**
     * Perform a Linux style ioctl operation on the associated file.
     *
     * @param req ioctl operation to be performed
     * @param buf byte array containing the ioctl parameters if any
     * @return Linux style ioctl return
     * @throws LastErrorException when operations fails
     */
    public int ioctl(int req, byte[] buf) throws LastErrorException {
        return libc.ioctl(fd, req, buf);
    }

    /**
     * Close the associated file
     *
     * @throws LastErrorException when operations fails
     */
    @Override
    public void close() throws LastErrorException {
        if (fd != -1) {
            int copy = fd;
            fd = -1;
            libc.close(copy);
        }
    }

    /**
     * Map a portion of the associated file into memory and return a pointer
     * that can be used to access that memory.
     *
     * @param len   size of the region to map
     * @param prot  protection for the memory region
     * @param flags Linux mmap flags
     * @param off   offset within the file for the start of the region
     * @return a pointer that can be used to access the mapped data
     * @throws LastErrorException when operations fails
     */
    public Pointer mmap(long len, int prot, int flags, long off) throws LastErrorException {
        Pointer p = libc.mmap(new Pointer(0), new NativeLong(len), prot, flags, fd, new NativeLong(off));
        if (p.equals(new Pointer(-1))) {
            throw new LastErrorException("mmap() failed");
        }
        return p;
    }

    /**
     * Unmap mapped memory region.
     *
     * @param addr Mapped address.
     * @param len  Region length.
     * @throws LastErrorException when operations fails
     */
    public void munmap(Pointer addr, long len) throws LastErrorException {
        libc.munmap(addr, new NativeLong(len));
    }

}
