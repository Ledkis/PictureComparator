package ledkis.module.picturecomparator.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static ledkis.module.picturecomparator.Constants.BYTES_PER_FLOAT;

public class VertexArray {

    //  The FloatBuffer will be used to store data in native memory.
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {

        //Let’s take a look at each part. First we allocated a block of native memory
        //using ByteBuffer.allocateDirect(); this memory will not be managed by the garbage
        //collector. We need to tell the method how large the block of memory should
        //be in bytes. Since our vertices are stored in an array of floats and there are
        //4 bytes per float, we pass in tableVerticesWithTriangles.length * BYTES_PER_FLOAT.

        //        The next line tells the byte buffer that it should organize its bytes in native
        //order.  When  it  comes  to  values  that  span  multiple  bytes,  such  as  32-bit
        //integers, the bytes can be ordered either from most significant to least signif -
        //        icant or from least to most. Think of this as similar to writing a number either
        //from left to right or right to left. It’s not important for us to know what that
        //order is, but it is important that we use the same order as the platform. We
        //do this by calling order(ByteOrder.nativeOrder()).

        //        Finally, we’d rather not deal with individual bytes directly. We want to work
        //with floats, so we call asFloatBuffer() to get a FloatBuffer that reflects the underlying
        //bytes. We then copy data from Dalvik’s memory to native memory by calling
        //vertexData.put(tableVerticesWithTriangles). The memory will be freed when the process
        //gets destroyed, so we don’t normally need to worry about that. If you end up
        //writing code that creates a lot of ByteBuffers and does so over time, you may
        //want to read up on heap fragmentation and memory management techniques.

        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);
    }
        
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
        int componentCount, int stride) {
        floatBuffer.position(dataOffset);

        // We then call glVertexAttribPointer() to tell OpenGL that it can find the data for
        // the attribute in the buffer vertexData.
        glVertexAttribPointer(attributeLocation, componentCount,
            GL_FLOAT, false, stride, floatBuffer);

        // Now that we’ve linked our data to the attribute, we need to enable the attribute
        // with a call to glEnableVertexAttribArray() before we can start drawing
        // With this final call, OpenGL now knows where to find all the data it needs.
        glEnableVertexAttribArray(attributeLocation);
        
        floatBuffer.position(0);
    }
}
