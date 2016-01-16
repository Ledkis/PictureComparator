package ledkis.module.picturecomparator.objects;

import java.util.ArrayList;
import java.util.List;

import ledkis.module.picturecomparator.util.Geometry2D.Rect2D;
import ledkis.module.picturecomparator.util.Geometry3D;
import ledkis.module.picturecomparator.util.Geometry3D.Circle3D;
import ledkis.module.picturecomparator.util.Geometry3D.Cylinder;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }


    static GeneratedData createRect2DFrame(Rect2D rect2D) {
        ObjectBuilder builder = new ObjectBuilder(6);
        builder.appendRect2D(rect2D);
        return builder.build();
    }


    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private void appendRect2D(Rect2D rect2D) {

        // Center point of fan
        vertexData[offset++] = rect2D.center.x;
        vertexData[offset++] = rect2D.center.y;

        // 4.2     Introducing Triangle Fans

        // position 2
        vertexData[offset++] = -rect2D.width / 2;
        vertexData[offset++] = -rect2D.height / 2;

        // position 3
        vertexData[offset++] = rect2D.width / 2;
        vertexData[offset++] = -rect2D.height / 2;

        // position 4
        vertexData[offset++] = rect2D.width / 2;
        vertexData[offset++] = rect2D.height / 2;

        // position 5
        vertexData[offset++] = -rect2D.width / 2;
        vertexData[offset++] = rect2D.height / 2;

        // position 6
        vertexData[offset++] = -rect2D.width / 2;
        vertexData[offset++] = -rect2D.height / 2;

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
            }
        });
    }

    private void appendRect3D(Geometry3D.Rect3D rect3D) {

        // Center point of fan
        vertexData[offset++] = rect3D.center.x;
        vertexData[offset++] = rect3D.center.y;
        vertexData[offset++] = rect3D.center.z;

        // 4.2     Introducing Triangle Fans

        // position 2
        vertexData[offset++] = -rect3D.width / 2;
        vertexData[offset++] = -rect3D.height / 2;
        vertexData[offset++] = rect3D.center.z;

        // position 3
        vertexData[offset++] = rect3D.width / 2;
        vertexData[offset++] = -rect3D.height / 2;
        vertexData[offset++] = rect3D.center.z;

        // position 4
        vertexData[offset++] = rect3D.width / 2;
        vertexData[offset++] = rect3D.height / 2;
        vertexData[offset++] = rect3D.center.z;

        // position 5
        vertexData[offset++] = -rect3D.width / 2;
        vertexData[offset++] = rect3D.height / 2;
        vertexData[offset++] = rect3D.center.z;

        // position 6
        vertexData[offset++] = -rect3D.width / 2;
        vertexData[offset++] = -rect3D.height / 2;
        vertexData[offset++] = rect3D.center.z;


        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
            }
        });
    }

    private void appendCircle(Circle3D circle3D, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle3D.center.x;
        vertexData[offset++] = circle3D.center.y;
        vertexData[offset++] = circle3D.center.z;

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = 
                  ((float) i / (float) numPoints)
                * ((float) Math.PI * 2f);
            
            vertexData[offset++] =
                    circle3D.center.x
                            + circle3D.radius * (float) Math.cos(angleInRadians);

            vertexData[offset++] = circle3D.center.y;
            
            vertexData[offset++] =
                    circle3D.center.z
                            + circle3D.radius * (float) Math.sin(angleInRadians);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex,
                    numVertices);
            }
        });
    }

    private void appendOpenCylinder(Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);

        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = 
                  ((float) i / (float) numPoints)
                * ((float) Math.PI * 2f);
            
            float xPosition = 
                  cylinder.center.x 
                + cylinder.radius * (float) Math.cos(angleInRadians);
            
            float zPosition = 
                  cylinder.center.z 
                + cylinder.radius * (float) Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex,
                    numVertices);
            }
        });        
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }
}
