package ev3dev.hardware.display.spi;

import ev3dev.hardware.display.JavaFramebuffer;
import ev3dev.utils.AllImplFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;

/**
 * Framebuffer factory service provider
 *
 * @author Jakub Vaněk
 * @since 2.4.7
 */
public interface FramebufferProvider {
    /**
     * Initialize system framebuffer
     *
     * @param path Path to the framebuffer.
     * @return Initialized framebuffer for the specified path.
     * @throws RuntimeException if no suitable framebuffer is found
     */
    static JavaFramebuffer load(String path) throws AllImplFailedException {
        final Logger LOGGER = LoggerFactory.getLogger(FramebufferProvider.class);
        LOGGER.debug("Loading framebuffer for {}", path);
        ServiceLoader<FramebufferProvider> loader = ServiceLoader.load(FramebufferProvider.class);
        for (FramebufferProvider provider : loader) {
            try {
                JavaFramebuffer ok = provider.createFramebuffer(path);
                LOGGER.debug("Framebuffer '{}' is compatible", provider.getClass().getSimpleName());
                return ok;
            } catch (IllegalArgumentException ex) {
                LOGGER.debug("Framebuffer '{}' is not compatible", provider.getClass().getSimpleName());
            } catch (IOException e) {
                LOGGER.warn("Framebuffer '{}' threw IOException", provider.getClass().getSimpleName());
                LOGGER.warn("Message: {}", e.getLocalizedMessage());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try (PrintStream chos = new PrintStream(bos)) {
                    e.printStackTrace(chos);
                }
                LOGGER.warn(new String(bos.toByteArray(), StandardCharsets.UTF_8));
            }
        }
        LOGGER.error("All framebuffer implementations failed");
        throw new AllImplFailedException("No suitable framebuffer found for" + path);
    }

    /**
     * Create and initialize a new framebuffer.
     *
     * @param fbPath Path to the framebuffer device (e.g. /dev/fb0)
     * @throws IllegalArgumentException When this framebuffer is not compatible with this device.
     * @throws IOException              When there was an error accessing the device.
     */
    JavaFramebuffer createFramebuffer(String fbPath) throws IOException, IllegalArgumentException;

}
