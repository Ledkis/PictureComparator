package ledkis.module.picturecomparator.util;

public class Geometry2D {

    public static final Point2D CENTER_POINT_2D = new Point2D(0f, 0f);

    public static class Point2D {
        public final float x, y;

        public Point2D(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Point2D translateY(float distance) {
            return new Point2D(x, y + distance);
        }

        public Point2D translate(Vector2D vector2D) {
            return new Point2D(
                    x + vector2D.x,
                    y + vector2D.y);
        }

        @Override
        public String toString() {
            return "Point2D[" + x + "," + y + "]";
        }
    }

    public static class Vector2D {
        public final float x, y;

        public Vector2D(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        public float length() {
            return (float) Math.sqrt(
                x * x
                        + y * y);
        }
        
        // http://en.wikipedia.org/wiki/Dot_product
        public float dotProduct(Vector2D other) {
            return x * other.x
                    + y * other.y;
        }

        public Vector2D scale(float f) {
            return new Vector2D(
                x * f,
                    y * f);
        }     
    }

    public static class Circle2D {
        public final Point2D center;
        public final float radius;

        public Circle2D(Point2D center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle2D scale(float scale) {
            return new Circle2D(center, radius * scale);
        }
    }


    public static class Rect2D {
        public final Point2D center;
        public final float width;
        public final float height;

        public Rect2D(Point2D center, float width, float height) {
            this.center = center;
            this.width = width;
            this.height = height;
        }

        public Rect2D moveTo(Point2D point2D) {
            return new Rect2D(new Point2D(point2D.x, point2D.y), width, height);
        }

        @Override
        public String toString() {
            return "Rect2D[" + center.x + "," + center.y + "," + width + "," + height + "]";
        }
    }

    public static Vector2D vectorBetween2D(Point2D from, Point2D to) {
        return new Vector2D(
            to.x - from.x,
                to.y - from.y);
    }

    public static boolean intersects(Rect2D rect2D, Point2D point2D) {
        return point2D.x > (rect2D.center.x - rect2D.width / 2) &&
                point2D.x < (rect2D.center.x + rect2D.width / 2) &&
                point2D.y > (rect2D.center.y - rect2D.height / 2) &&
                point2D.y < (rect2D.center.y + rect2D.height / 2);
    }

}
